package cn.com.cennavi.visualizer.service.parsedata;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import cn.com.cennavi.codec.Item;
import cn.com.cennavi.kfgis.framework.exception.NServiceInternalException;
import cn.com.cennavi.kfgis.framework.file.CommonFileMapContainer;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.kfgis.util.SBase64;
import cn.com.cennavi.kfgis.util.ThreadPool;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage;
import cn.com.cennavi.tpeg.decoder.match.IMatcher;
import cn.com.cennavi.tpeg.decoder.match.impl.OlrTECMessageImplMathcerImpl;
import cn.com.cennavi.tpeg.decoder.match.impl.OlrTFPMessageImplMathcerImpl;
import cn.com.cennavi.tpeg.decoder.match.impl.TmcTECMessageImplMathcerImpl;
import cn.com.cennavi.tpeg.decoder.match.impl.TmcTFPMessageImplMathcerImpl;
import cn.com.cennavi.tpeg.decoder.parser.ITPEGDeocder;
import cn.com.cennavi.tpeg.decoder.parser.impl.TPEGDecoderImpl;
import cn.com.cennavi.tpeg.item.component.comm.lrc.LocationReference;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.OpenlrLocationReference;
import cn.com.cennavi.tpeg.item.component.comm.lrc.tmc.TMCLocationReference;
import cn.com.cennavi.tpeg.item.component.tec.TECMessage;
import cn.com.cennavi.tpeg.item.component.tfp.TFPMessage;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTable;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;
import cn.com.cennavi.visualizer.service.parsedata.ParserRTTDataController.RTTReqParams;

@Service("tpegservice")
public class ParseTPEGNoTMCService implements IParseTPEGService {

	@Override
	public JsonResult execute(RTTReqParams param, HttpServletRequest request, HttpServletResponse response) {
		String data = param.getData();
		byte[] item = null;
		try {
			item = SBase64.decode(data);
		} catch (UnsupportedEncodingException e) {
			throw new NServiceInternalException("Base64解析失败", e);
		}
		
		ITPEGDeocder decoder = new TPEGDecoderImpl();
		List<TFPMessage> tfps = new ArrayList<TFPMessage>();
		List<TECMessage> tecs = new ArrayList<TECMessage>();

		try {
			decoder.execute(item, tfps, tecs);
		} catch (Throwable e) {
			throw new NServiceInternalException("TPEG还原异常", e);
		}
		List<TPEGMesage> result = new ArrayList<TPEGMesage>();
		String lpinfoversion = param.getLpinfo_version();
		String tmc_mapversion = param.getTmc_map_version();
		String olr_mapversion = param.getOlr_map_version();
		String olrurl = System.getProperty("OLR_DECODER_URL");

		MTTable mt = (MTTable) CommonFileMapContainer.getInstance().getFileMap("mt." + lpinfoversion);
		RTable tmcr = (RTable) CommonFileMapContainer.getInstance().getFileMap("map." + tmc_mapversion);
		RTable olrr = (RTable) CommonFileMapContainer.getInstance().getFileMap("map." + olr_mapversion);
		IMatcher tmcTFPMatcher = new TmcTFPMessageImplMathcerImpl(mt, tmcr, lpinfoversion,param.getDebug());
		IMatcher olrTFPMatcher = new OlrTFPMessageImplMathcerImpl(olrr, olrurl, olr_mapversion);
		IMatcher tmcTECMatcher = new TmcTECMessageImplMathcerImpl(mt, tmcr, lpinfoversion);
		IMatcher olrTECMatcher = new OlrTECMessageImplMathcerImpl(olrr, olrurl, olr_mapversion);

		ThreadPool<TPEGMesage> pool=null;
		int datacount=tfps.size() + tecs.size();
		boolean usePool=false;
		CountDownLatch end=null;
		if(datacount>50){
			usePool=true;
			end = new CountDownLatch(datacount);
			pool = new ThreadPool<TPEGMesage>();
			pool.startExecutors(20);
		}
		
		List<Future<TPEGMesage>> futures = new ArrayList<Future<TPEGMesage>>();
		for (TFPMessage tfp : tfps) {
			if(tfp.getLRC()!=null){
				LocationReference lrc = tfp.getLRC().getLocationReference();
				if (lrc instanceof TMCLocationReference) {
					this.todo(pool, usePool, end, tmcTFPMatcher, tfp, futures, result);
				} else if (lrc instanceof OpenlrLocationReference) {
					this.todo(pool, usePool, end, olrTFPMatcher, tfp, futures, result);
				}
			}
			
		}

		for (TECMessage tec : tecs) {
			if(tec.getLRC()!=null){
				LocationReference lrc = tec.getLRC().getLocationReference();
				if (lrc instanceof TMCLocationReference) {
					this.todo(pool, usePool, end, tmcTECMatcher, tec, futures, result);
				} else if (lrc instanceof OpenlrLocationReference) {
					this.todo(pool, usePool, end, olrTECMatcher, tec, futures, result);
				}
			}
		}
		while (true&&usePool) {
			if (end.getCount() > 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			for (Future<TPEGMesage> msgf : futures) {
				try {
					result.add(msgf.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}
			break;
		}

		JsonResult jsonResult = new JsonResult();
		jsonResult.setJsonObj(result);
		jsonResult.setCallback(param.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		return jsonResult;
	}
	
	private void todo(ThreadPool<TPEGMesage> pool,boolean usepool,CountDownLatch end,IMatcher matcher,Item item,List<Future<TPEGMesage>> futures,List<TPEGMesage> result){
		if(usepool){
			Command c = new Command(matcher, item, end);
			futures.add(pool.addCommand(c));
		}else{
			result.add(matcher.match(item));
		}
	}

	public static class Command implements Callable<TPEGMesage> {

		private IMatcher matcher;

		private Item item;

		private CountDownLatch end;

		public Command(IMatcher matcher, Item item, CountDownLatch end) {
			this.matcher = matcher;
			this.item = item;
			this.end = end;
		}
		@Override
		public TPEGMesage call() throws Exception {
			try{
				TPEGMesage msg = matcher.match(item);
				return msg;
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}finally{
				end.countDown();
			}
		}

	}

}
