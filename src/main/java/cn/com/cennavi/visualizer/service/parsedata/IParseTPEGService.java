package cn.com.cennavi.visualizer.service.parsedata;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.visualizer.service.parsedata.ParserRTTDataController.RTTReqParams;

public interface IParseTPEGService {
	public JsonResult execute(RTTReqParams param,HttpServletRequest request, HttpServletResponse response);
}
