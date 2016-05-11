package cn.com.cennavi.visualizer.service.parsedata;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import cn.com.cennavi.kfgis.framework.file.CommonFileMapContainer;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.kfgis.util.ThreadPool;
import cn.com.cennavi.rtic.decoder.bean.RTICMesage;
import cn.com.cennavi.rtic.decoder.match.IRTICMatcher;
import cn.com.cennavi.rtic.decoder.match.impl.CNRTICMatcherImpl;
import cn.com.cennavi.rtic.decoder.match.impl.CNRticEventMatcherImpl;
import cn.com.cennavi.visualizer.common.dataloader.corresponding.CorrespondingTable;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;
import cn.com.cennavi.visualizer.service.parsedata.ParserRTTDataController.RTTReqParams;
import cn.com.cennavi.visualizer.service.parsedata.translate.CNRticRttDataTranslateImpl;
import cn.com.cennavi.visualizer.service.parsedata.translate.CNRticEventRttDataTranslateImpl;
import cn.com.cennavi.visualizer.service.parsedata.translate.CommRttData;
import cn.com.cennavi.visualizer.service.parsedata.translate.IRttDataTranslate;

@Service("rticservice")
public class ParseRTICService implements IParseTPEGService {

	@Override
	public JsonResult execute(RTTReqParams param, HttpServletRequest request, HttpServletResponse response) {
		JsonResult jsonResult = new JsonResult();

		String data = param.getData();

		String lpinfoversion = param.getLpinfo_version();
		String tmc_mapversion = param.getTmc_map_version();
		CorrespondingTable ct = (CorrespondingTable) CommonFileMapContainer.getInstance().getFileMap("ct." + lpinfoversion);
		RTable rticr = (RTable) CommonFileMapContainer.getInstance().getFileMap("map." + tmc_mapversion);

		IRttDataTranslate translate = null;
		IRTICMatcher matcher = null;
		if ("crcsv".equalsIgnoreCase(param.getFiletype())) {
			translate = new CNRticRttDataTranslateImpl();
			matcher = new CNRTICMatcherImpl(ct, rticr, lpinfoversion, false);
		} else if ("crecsv".equalsIgnoreCase(param.getFiletype())) {

		} else if ("crexml".equalsIgnoreCase(param.getFiletype())) {
			translate = new CNRticEventRttDataTranslateImpl();
			matcher = new CNRticEventMatcherImpl(ct, rticr, lpinfoversion, false);
		} else {
			jsonResult.setJsonObj("{error:'no validate file translate'}");
		}
		List<CommRttData> datalist = translate.translate(data);
		List<RTICMesage> result = new ArrayList<RTICMesage>();

		ThreadPool<RTICMesage> pool = null;
		List<Future<RTICMesage>> futures = new ArrayList<Future<RTICMesage>>();
		int datacount = datalist.size();
		boolean usepool = false;
		CountDownLatch end = null;
		if (datacount > 50) {
			usepool = true;
			end = new CountDownLatch(datacount);
			pool = new ThreadPool<RTICMesage>();
			pool.startExecutors(20);
		}
		for (CommRttData crd : datalist) {
			todo(pool, usepool, end, matcher, crd, futures, result);
		}
		while (true && usepool) {
			if (end.getCount() > 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			for (Future<RTICMesage> msgf : futures) {
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

		jsonResult.setJsonObj(result);
		jsonResult.setCallback(param.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		return jsonResult;
	}

	private void todo(ThreadPool<RTICMesage> pool, boolean usepool, CountDownLatch end, IRTICMatcher matcher, CommRttData crd, List<Future<RTICMesage>> futures,
			List<RTICMesage> result) {
		if (usepool) {
			Command c = new Command(matcher, crd, end);
			futures.add(pool.addCommand(c));
		} else {
			result.add(matcher.match(crd));
		}
	}

	public static class Command implements Callable<RTICMesage> {

		private IRTICMatcher matcher;

		private CommRttData item;

		private CountDownLatch end;

		public Command(IRTICMatcher matcher, CommRttData item, CountDownLatch end) {
			this.matcher = matcher;
			this.item = item;
			this.end = end;
		}
		@Override
		public RTICMesage call() throws Exception {
			try {
				RTICMesage msg = matcher.match(item);
				return msg;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				end.countDown();
			}
		}
	}

}
