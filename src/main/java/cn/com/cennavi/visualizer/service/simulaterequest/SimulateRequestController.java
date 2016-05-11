package cn.com.cennavi.visualizer.service.simulaterequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.bean.param.IResult;
import cn.com.cennavi.kfgis.bean.param.IXmlParams;
import cn.com.cennavi.kfgis.framework.annotation.NotNullValid;
import cn.com.cennavi.kfgis.framework.annotation.RestBeanVariable;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.visualizer.common.contant.RestNameAPIContant;

@Controller
public class SimulateRequestController {

	@Autowired
	private ISimulateRequestService service;

	@RequestMapping(value = RestNameAPIContant.SIMULATE_REQUEST, method = RequestMethod.POST)
	public IResult initSessionDaimler(@RestBeanVariable SimulateReqParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonResult jsonResult = service.initSessionDailmer(params, request, response);
		return jsonResult;
	}

	@RequestMapping(value = RestNameAPIContant.GET_MESSAGE, method = RequestMethod.POST)
	public IResult getMessage(@RestBeanVariable GetMessageParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonResult jsonResult = new JsonResult();
		if("mib2".equalsIgnoreCase(params.getType())){
			jsonResult = service.getMessageMib2(params, request, response);
		}else if("daimler".equalsIgnoreCase(params.getType())){
			jsonResult = service.getMessageDailmer(params, request, response);
		}else if("renault".equalsIgnoreCase(params.getType())){
			jsonResult = service.getMessageRenault(params, request, response);
		}else if("bmw".equalsIgnoreCase(params.getType())){
			jsonResult = service.getMessageBmw(params, request, response);
		}
		
		return jsonResult;
	}
	
	@RequestMapping(value = RestNameAPIContant.SIMULATE_REQUEST_MIB2, method = RequestMethod.POST)
	public IResult initSessionMIB2(@RestBeanVariable SimulateReqMIB2Params params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonResult jsonResult = service.initSessionMIB2(params, request, response);
		return jsonResult;
	}
	
	@RequestMapping(value = RestNameAPIContant.SIMULATE_REQUEST_RENAULT, method = RequestMethod.POST)
	public IResult initSessionRenault(@RestBeanVariable SimulateReqMIB2Params params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		JsonResult jsonResult = service.initSessionRenault(params, request, response);
		return jsonResult;
	}

	public static class SimulateReqMIB2Params extends AbstractParams {

		private static final long serialVersionUID = 8466985134330584341L;

		private String serviceUrl;

		private String postXml;
		
		private String callback;

		public String getServiceUrl() {
			return serviceUrl;
		}

		public void setServiceUrl(String serviceUrl) {
			this.serviceUrl = serviceUrl;
		}

		public String getPostXml() {
			return postXml;
		}

		public void setPostXml(String postXml) {
			this.postXml = postXml;
		}
		
		public String getCallback() {
			return callback;
		}

		public void setCallback(String callback) {
			this.callback = callback;
		}

		@Override
		public Validator getValidator() {
			return null;
		}

		@Override
		public Class<? extends IXmlParams> getXmlClass() {
			return null;
		}

	}

	public static class SimulateReqParams extends AbstractParams {

		private static final long serialVersionUID = 8466985134330584341L;

		private String serviceUrl;

		@NotNullValid(false)
		private String lpinfoVersion;

		@NotNullValid(false)
		private String mapVersion;

		private String callback;

		private String tmcPlr;

		private String tmcFreeFlow;

		private String jamfrontWarning;

		private String openlr;

		private String innerRadius;

		private String outerRadius;

		public String getServiceUrl() {
			return serviceUrl;
		}

		public void setServiceUrl(String serviceUrl) {
			this.serviceUrl = serviceUrl;
		}

		public String getLpinfoVersion() {
			return lpinfoVersion;
		}

		public void setLpinfoVersion(String lpinfoVersion) {
			this.lpinfoVersion = lpinfoVersion;
		}

		public String getMapVersion() {
			return mapVersion;
		}

		public void setMapVersion(String mapVersion) {
			this.mapVersion = mapVersion;
		}

		public String getCallback() {
			return callback;
		}

		public void setCallback(String callback) {
			this.callback = callback;
		}

		public String getTmcPlr() {
			return tmcPlr;
		}

		public void setTmcPlr(String tmcPlr) {
			this.tmcPlr = tmcPlr;
		}

		public String getTmcFreeFlow() {
			return tmcFreeFlow;
		}

		public void setTmcFreeFlow(String tmcFreeFlow) {
			this.tmcFreeFlow = tmcFreeFlow;
		}

		public String getJamfrontWarning() {
			return jamfrontWarning;
		}

		public void setJamfrontWarning(String jamfrontWarning) {
			this.jamfrontWarning = jamfrontWarning;
		}

		public String getOpenlr() {
			return openlr;
		}

		public void setOpenlr(String openlr) {
			this.openlr = openlr;
		}

		public String getInnerRadius() {
			return innerRadius;
		}

		public void setInnerRadius(String innerRadius) {
			this.innerRadius = innerRadius;
		}

		public String getOuterRadius() {
			return outerRadius;
		}

		public void setOuterRadius(String outerRadius) {
			this.outerRadius = outerRadius;
		}

		@Override
		public Validator getValidator() {
			return null;
		}

		@Override
		public Class<? extends IXmlParams> getXmlClass() {
			return null;
		}

	}

	public static class GetMessageParams extends AbstractParams {

		private static final long serialVersionUID = 8466985134330584341L;

		private String getMessageUrl;

		private Double lon;

		private Double lat;

		private String callback;

		private String sessionId;
		
		private String type;
		
		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getGetMessageUrl() {
			return getMessageUrl;
		}

		public void setGetMessageUrl(String getMessageUrl) {
			this.getMessageUrl = getMessageUrl;
		}

		public Double getLon() {
			return lon;
		}

		public void setLon(Double lon) {
			this.lon = lon;
		}

		public Double getLat() {
			return lat;
		}

		public void setLat(Double lat) {
			this.lat = lat;
		}

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

		public String getCallback() {
			return callback;
		}

		public void setCallback(String callback) {
			this.callback = callback;
		}

		@Override
		public Validator getValidator() {
			return null;
		}

		@Override
		public Class<? extends IXmlParams> getXmlClass() {
			return null;
		}

	}

}
