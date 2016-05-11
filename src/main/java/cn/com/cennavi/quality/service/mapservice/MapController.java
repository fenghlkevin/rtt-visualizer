package cn.com.cennavi.quality.service.mapservice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.com.cennavi.common.param.RectangleParams;
import cn.com.cennavi.kfgis.bean.param.IResult;
import cn.com.cennavi.kfgis.framework.annotation.RestBeanVariable;
import cn.com.cennavi.quality.common.contant.RestNameAPIContant;
import cn.com.cennavi.quality.service.mapservice.MapQueryAdminServiceImpl.AdminParams;

@Controller
public class MapController {

	@Autowired
	private IMapQueryServiceInf queryLinkByRectangle;
	
	@Autowired
	private IMapQueryServiceInf queryLPByRectangle;
	
	@Autowired
	private IMapQueryServiceInf queryadmin;

	@RequestMapping(value = RestNameAPIContant.COMMON_QUERYLINK_CIRCLE, method = RequestMethod.POST)
	public IResult getTrafficOnlineInfo(@RestBeanVariable RectangleParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return queryLinkByRectangle.query(params, request, response);
	}
	
	@RequestMapping(value = RestNameAPIContant.COMMON_QUERYLPINFO_RECTANGLE, method = RequestMethod.POST)
	public IResult querlpinfo(@RestBeanVariable RectangleParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return queryLPByRectangle.query(params, request, response);
	}
	
	@RequestMapping(value = RestNameAPIContant.COMMON_QUERYMAP_ADMIN, method = RequestMethod.POST)
	public IResult getAdmincode(@RestBeanVariable AdminParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return queryadmin.query(params, request, response);
	}
	
}
