package cn.com.cennavi.visualizer.service.parsedata;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.visualizer.service.parsedata.ParserRTTDataController.OutputParams;
import cn.com.cennavi.visualizer.service.parsedata.ParserRTTDataController.RTTReqParams;

public interface IOutputTPEGService {
	public JsonResult execute(OutputParams param,HttpServletRequest request, HttpServletResponse response);
}
