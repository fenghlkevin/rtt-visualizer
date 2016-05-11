package cn.com.cennavi.visualizer.service.parsedata;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.com.cennavi.common.param.AbstractReqParams;
import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.bean.param.IResult;
import cn.com.cennavi.kfgis.bean.param.IXmlParams;
import cn.com.cennavi.kfgis.framework.annotation.NotNullValid;
import cn.com.cennavi.kfgis.framework.annotation.RestBeanVariable;
import cn.com.cennavi.visualizer.common.contant.RestNameAPIContant;


@Controller
public class ParserRTTDataController {

	// private Logger logger =
	// LoggerFactory.getLogger(GetMessagesController.class);

	@Autowired
	private IParseTPEGService tpegservice;
	
	@Autowired
	private IParseTPEGService rticservice;
	
	@Autowired
	private IOutputTPEGService outputservice;

	@RequestMapping(value = RestNameAPIContant.PARSER_TPEG_OUTPUT, method = RequestMethod.POST)
	public IResult outputTPEG2File(@RestBeanVariable OutputParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return  outputservice.execute(params, request, response);
	}
	
	@RequestMapping(value = RestNameAPIContant.PARSER_TPEG_DATA, method = RequestMethod.POST)
	public IResult parseTPEG(@RestBeanVariable RTTReqParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return  tpegservice.execute(params, request, response);
	}
	
	@RequestMapping(value = RestNameAPIContant.PARSER_RTIC_DATA, method = RequestMethod.POST)
	public IResult parseRTIC(@RestBeanVariable RTTReqParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return  rticservice.execute(params, request, response);
	}
	
	public static class OutputParams extends AbstractReqParams {
		/**
		 * 
		 */
		private static final long serialVersionUID = 6340118949144789534L;

		private String data;
		
		private String ids;
		

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getIds() {
			return ids;
		}

		public void setIds(String ids) {
			this.ids = ids;
		}
	}

	public static class RTTReqParams extends AbstractParams {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8466985134330584341L;

		private String lpinfo_version;

		private String tmc_map_version;
		
		private String olr_map_version;
		
		private String callback;
		
		private String data;
		
		private Boolean debug;
		
		@NotNullValid(false)
		private String filetype;

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getCallback() {
			return callback;
		}

		public void setCallback(String callback) {
			this.callback = callback;
		}

		public String getLpinfo_version() {
			return lpinfo_version;
		}

		public void setLpinfo_version(String lpinfo_version) {
			this.lpinfo_version = lpinfo_version;
		}
		
		@Override
		public Validator getValidator() {
			return null;
		}

		@Override
		public Class<? extends IXmlParams> getXmlClass() {
			return null;
		}

		public String getTmc_map_version() {
			return tmc_map_version;
		}

		public void setTmc_map_version(String tmc_map_version) {
			this.tmc_map_version = tmc_map_version;
		}

		public String getOlr_map_version() {
			return olr_map_version;
		}

		public void setOlr_map_version(String olr_map_version) {
			this.olr_map_version = olr_map_version;
		}

		public Boolean getDebug() {
			return debug;
		}

		public void setDebug(Boolean debug) {
			this.debug = debug;
		}

		public String getFiletype() {
			return filetype;
		}

		public void setFiletype(String filetype) {
			this.filetype = filetype;
		}

	}
}
