package cn.com.cennavi.visualizer.service.parsedata;

import java.io.File;
import java.io.IOException;
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

import cn.com.cennavi.kfgis.framework.exception.NServiceInternalException;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.kfgis.util.SBase64;
import cn.com.cennavi.tpeg.decoder.parser.ITPEGDeocder;
import cn.com.cennavi.tpeg.decoder.parser.impl.TPEGDecoderImpl;
import cn.com.cennavi.tpeg.item.component.tec.TECMessage;
import cn.com.cennavi.tpeg.item.component.tfp.TFPMessage;
import cn.com.cennavi.visualizer.service.createfile.CreateFileUtil;
import cn.com.cennavi.visualizer.service.parsedata.ParserRTTDataController.OutputParams;

@Service
public class ParseTPEG2FileService implements IOutputTPEGService {

	@Override
	public JsonResult execute(OutputParams param, HttpServletRequest request, HttpServletResponse response) {
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
		
		List<TFPMessage> otfps = new ArrayList<TFPMessage>();
		List<TECMessage> otecs = new ArrayList<TECMessage>();
		
		String[] ids=param.getIds().split("#");
		for(TFPMessage tfp:tfps){
			for(String id:ids){
				if(Long.valueOf(id).equals(tfp.getMMC().getMessageID().getNumber())){
					otfps.add(tfp);
				}
			}
		}
		for(TECMessage tec:tecs){
			for(String id:ids){
				if(Long.valueOf(id).equals(tec.getMMC().getMessageID().getNumber())){
					otecs.add(tec);
				}
			}
		}
		String filepath=CreateFileUtil.saveTpegFile(otfps, otecs);
		

		JsonResult jsonResult = new JsonResult();
		jsonResult.setJsonObj(filepath);
		jsonResult.setCallback(param.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		return jsonResult;
	}

}
