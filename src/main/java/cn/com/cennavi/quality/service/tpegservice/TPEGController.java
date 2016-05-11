package cn.com.cennavi.quality.service.tpegservice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.com.cennavi.kfgis.bean.param.IResult;
import cn.com.cennavi.kfgis.framework.annotation.RestBeanVariable;
import cn.com.cennavi.quality.common.contant.RestNameAPIContant;

public class TPEGController {
	
	@Autowired
	private TPEGCompareService service;
	
	@RequestMapping(value = RestNameAPIContant.TPEG_QUERY_COMPARE, method = RequestMethod.POST)
	public IResult comare(@RestBeanVariable TPEGParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return service.compare(params, request, response);
	}
	
}
