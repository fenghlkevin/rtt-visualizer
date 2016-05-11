package cn.com.cennavi.common.param;

public  class RectangleParams extends AbstractReqParams {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8466985134330584341L;

		private Double min_lng;

		private Double min_lat;
		
		private Double max_lng;

		private Double max_lat;

		private String mapversion;

		public String getMapversion() {
			return mapversion;
		}

		public void setMapversion(String mapversion) {
			this.mapversion = mapversion;
		}

		public Double getMin_lng() {
			return min_lng;
		}

		public void setMin_lng(Double min_lng) {
			this.min_lng = min_lng;
		}

		public Double getMin_lat() {
			return min_lat;
		}

		public void setMin_lat(Double min_lat) {
			this.min_lat = min_lat;
		}

		public Double getMax_lng() {
			return max_lng;
		}

		public void setMax_lng(Double max_lng) {
			this.max_lng = max_lng;
		}

		public Double getMax_lat() {
			return max_lat;
		}

		public void setMax_lat(Double max_lat) {
			this.max_lat = max_lat;
		}
	}