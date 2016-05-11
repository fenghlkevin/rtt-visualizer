package cn.com.cennavi.quality.service.mapservice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;

public interface IMapQueryServiceInf {
	
	public JsonResult query(AbstractParams params, HttpServletRequest request, HttpServletResponse response) ;
}
