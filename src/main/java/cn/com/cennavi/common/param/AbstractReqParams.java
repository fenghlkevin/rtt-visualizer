package cn.com.cennavi.common.param;

import org.springframework.validation.Validator;

import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.bean.param.IXmlParams;

public abstract class AbstractReqParams extends AbstractParams {
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