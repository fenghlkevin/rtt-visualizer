package cn.com.cennavi.visualizer.service.createfile.query;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;

public interface IRoadQueryInf {
	
	public JsonResult query(AbstractParams params, HttpServletRequest request, HttpServletResponse response);
	
}
