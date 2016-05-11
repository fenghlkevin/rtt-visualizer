package cn.com.cennavi.quality.service.tpegservice;

import cn.com.cennavi.common.param.AbstractReqParams;

public  class TPEGParams extends AbstractReqParams {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8466985134330584341L;

		private String lockeys;

		private String mapversion;
		
		private String timestamp;

		public String getMapversion() {
			return mapversion;
		}

		public void setMapversion(String mapversion) {
			this.mapversion = mapversion;
		}

		public String getLockeys() {
			return lockeys;
		}

		public void setLockeys(String lockeys) {
			this.lockeys = lockeys;
		}

		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

	
	}