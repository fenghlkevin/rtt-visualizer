package cn.com.cennavi.visualizer.service.createfile;

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
import cn.com.cennavi.kfgis.framework.annotation.RestBeanVariable;
import cn.com.cennavi.visualizer.common.contant.RestNameAPIContant;
import cn.com.cennavi.visualizer.service.createfile.query.IRoadQueryInf;

@Controller
public class CreateFileController {

	@Autowired
	private IGetRoadService service;

	@Autowired
	private IRoadQueryInf circle;// =new RoadByCircleQuery() ;

	@Autowired
	private IRoadQueryInf roadbyid;// =new RoadByIDQuery();

	@Autowired
	private IRoadQueryInf route;// =new RouteQuery();

	@Autowired
	private IRoadQueryInf createfile;

	@RequestMapping(value = RestNameAPIContant.CREATEFILE_QUERY_CIRCLE, method = RequestMethod.POST)
	public IResult getTrafficOnlineInfo(@RestBeanVariable CircleParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return circle.query(params, request, response);
	}

	@RequestMapping(value = RestNameAPIContant.CREATEFILE_BYJSON, method = RequestMethod.POST)
	public IResult createfilebyjson(@RestBeanVariable CreateJSONParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return createfile.query(params, request, response);
	}

	@RequestMapping(value = RestNameAPIContant.CREATEFILE_QUERY_ROADITEM, method = RequestMethod.POST)
	public IResult getRoadItemInfo(@RestBeanVariable QueryRoadItemReqParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return roadbyid.query(params, request, response);
	}

	@RequestMapping(value = RestNameAPIContant.CREATEFILE_CREATE, method = RequestMethod.POST)
	public IResult createfile(@RestBeanVariable CreateReqParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return service.create(params, request, response);
	}

	@RequestMapping(value = RestNameAPIContant.CREATEFILE_QUERY_ROUTE, method = RequestMethod.POST)
	public IResult queryByRoute(@RestBeanVariable RouteParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return route.query(params, request, response);
	}

	public abstract static class AbstractReqParams extends AbstractParams {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7215673980919035636L;
		private String callback;

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

	public static class CreateJSONParams extends AbstractReqParams {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3143696637476559289L;
		
		private String data;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

	public abstract static class QueryReqParams extends AbstractReqParams {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3597699255848231143L;

		private String lpinfo_version;

		private String olr_map_version;

		private String tmc_map_version;

		private String messagetype;

		private String lrctype;

		public String getLpinfo_version() {
			return lpinfo_version;
		}

		public void setLpinfo_version(String lpinfo_version) {
			this.lpinfo_version = lpinfo_version;
		}

		public String getMessagetype() {
			return messagetype;
		}

		public void setMessagetype(String messagetype) {
			this.messagetype = messagetype;
		}

		public String getLrctype() {
			return lrctype;
		}

		public void setLrctype(String lrctype) {
			this.lrctype = lrctype;
		}

		public String getOlr_map_version() {
			return olr_map_version;
		}

		public void setOlr_map_version(String olr_map_version) {
			this.olr_map_version = olr_map_version;
		}

		public String getTmc_map_version() {
			return tmc_map_version;
		}

		public void setTmc_map_version(String tmc_map_version) {
			this.tmc_map_version = tmc_map_version;
		}
	}

	public static class CreateReqParams extends AbstractReqParams {

		private String data;

		/**
		 * 
		 */
		private static final long serialVersionUID = -752688336015867021L;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

	public static class QueryRoadItemReqParams extends QueryReqParams {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8466985134330584341L;

		private String roaditemid;

		private Integer roaditem_areacode;

		private Integer direction;

		public String getRoaditemid() {
			return roaditemid;
		}

		public void setRoaditemid(String roaditemid) {
			this.roaditemid = roaditemid;
		}

		public Integer getRoaditem_areacode() {
			return roaditem_areacode;
		}

		public void setRoaditem_areacode(Integer roaditem_areacode) {
			this.roaditem_areacode = roaditem_areacode;
		}

		public Integer getDirection() {
			return direction;
		}

		public void setDirection(Integer direction) {
			this.direction = direction;
		}

	}

	public static class CircleParams extends QueryReqParams {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8466985134330584341L;

		private Double center_lng;

		private Double center_lat;

		private Double radius;

		public Double getCenter_lng() {
			return center_lng;
		}

		public void setCenter_lng(Double center_lng) {
			this.center_lng = center_lng;
		}

		public Double getCenter_lat() {
			return center_lat;
		}

		public void setCenter_lat(Double center_lat) {
			this.center_lat = center_lat;
		}

		public Double getRadius() {
			return radius;
		}

		public void setRadius(Double radius) {
			this.radius = radius;
		}
	}

	public static class RouteParams extends QueryReqParams {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private String spoint;

		private String epoint;

		public String getSpoint() {
			return spoint;
		}

		public void setSpoint(String spoint) {
			this.spoint = spoint;
		}

		public String getEpoint() {
			return epoint;
		}

		public void setEpoint(String epoint) {
			this.epoint = epoint;
		}

	}
}
