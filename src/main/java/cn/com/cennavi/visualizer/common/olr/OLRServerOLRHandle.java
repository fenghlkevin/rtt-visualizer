package cn.com.cennavi.visualizer.common.olr;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class OLRServerOLRHandle{

	/**
	 * <request>
	 * 	<mapversion>13_Q1_K</mapversion>
	 * 	<road>
	 *  		<id>1</id>
	 *  		<areacode>310000</areacode>
	 * 		<line>123456789012,123456789012,123456789012</line>
	 *  	</road>
	 *  	 <road>
	 *  		<id>2</id>
	 *  		<areacode>310000</areacode>
	 * 		<line>123456789012,123456789012,123456789012</line>
	 *  	</road>
	 *  	 <road>
	 *  		<id>3</id>
	 *  		<areacode>310000</areacode>
	 * 		<line>123456789012,123456789012,123456789012</line>
	 *  	</road>
	 * </request>
	 */
	
	public static String createRequestBody(String linkIds,String messageId) {
		StringBuffer str=new StringBuffer();
		str.append("<request>").append("<mapversion>").append("13_Q4_G").append("</mapversion>");
		str.append("<type>line</type>");
		str.append("<roads>");
		
		str.append("<road>");
		str.append("<id>").append(messageId).append("</id>");
		str.append("<resultType>tisa</resultType>");
		str.append("<line>").append(linkIds).append("</line>");
		str.append("</road>");
		
		str.append("</roads></request>");
		return str.toString();
	}
	
	private static XStream xstream;
	static {
		if(xstream == null){
			xstream=new XStream();
			xstream.alias("response", Response.class);
			xstream.alias("road", Road.class);
			xstream.alias("IntermediateLocationReferencePoint", IntermediateLocationReferencePoint.class);
		}
	}


	/**
	 * <response>
	 * 	<mapversion>13_Q1_K</mapversion>
	 * 	<roads>
	 * 	<road>
	 *  		<id>1</id>
	 *  		<areacode>310000</areacode>
	 * 		<linearLocationReference>
			<firstLocationReferencePoint>
				<absoluteGeoCoordinate>
					<longitude>11660068</longitude>
					<latitude>4004567</latitude>
				</absoluteGeoCoordinate>
				<lineProperties>
				<frc>6</frc>
				<fow>0</fow>
				<bearing>261</bearing>
				</lineProperties>
				<pathProperties>
				<lfrncp>6</lfrncp>
				<dnp>93</dnp>
				</pathProperties>
			</firstLocationReferencePoint>
			<IntermediateLocationReferencePoints>
			<IntermediateLocationReferencePoint>
				<relativeGeoCoordinate>
				<longitude>-108</longitude>
				<latitude>-12</latitude>
				</relativeGeoCoordinate>
				<lineProperties>
				<frc>6</frc>
				<fow>0</fow>
				<bearing>261</bearing>
				</lineProperties>
				<pathProperties>
				<lfrncp>6</lfrncp>
				<dnp>93</dnp>
				</pathProperties>
			</IntermediateLocationReferencePoint>
			</IntermediateLocationReferencePoints>
			<lastLocationReferencePoint>
				<relativeGeoCoordinate>
				<longitude>-294</longitude>
				<latitude>-29</latitude>
				</relativeGeoCoordinate>
				<lineProperties>
				<frc>6</frc>
				<fow>0</fow>
				<bearing>261</bearing>
				</lineProperties>
			</lastLocationReferencePoint>
			<positiveOffset>0</positiveOffset>
			<negativeOffset>0</negativeOffset>
			</linearLocationReference>
	 * 		<rectangle>lbx02秒,lby02秒,rtx02秒,rty02秒</rectangle>
	 *  	</road>
	 *  	<roads>
	 * </response>
	 */
	public static Map<Long, OlrLrc> parseResponseBody(String res) {
		Response response = (Response)xstream.fromXML(res);
		Map<Long ,OlrLrc> resMap=new Long2ObjectOpenHashMap<OlrLrc>();

		for(Road road:response.getRoads()){
			OlrLrc lrc=new OlrLrc();
			
			String[] ps=road.getRectangle().split(",");
			Point2D.Double rect[]=new Point2D.Double[2];
			rect[0]=new Point2D.Double(Double.valueOf(ps[0]),Double.valueOf(ps[1]));
			rect[1]=new Point2D.Double(Double.valueOf(ps[2]),Double.valueOf(ps[3]));
			lrc.setLrc(road.getLinearLocationReference());
			lrc.setRectangle(rect);
			
			resMap.put(road.getId(), lrc);
		}
		return resMap;
	}
	
	@XStreamAlias("response")
	public static class Response{
		
		@XStreamAlias("mapversion")
		private String mapversion;
		
		@XStreamAlias("type")
		private String type;
		
		@XStreamAlias("roads")
		private List<Road> roads;

		public String getMapversion() {
			return mapversion;
		}

		public void setMapversion(String mapversion) {
			this.mapversion = mapversion;
		}

		public List<Road> getRoads() {
			return roads;
		}

		public void setRoads(List<Road> roads) {
			this.roads = roads;
		} 
	}
	
	@XStreamAlias("linearLocationReference")
	public static class LinearLocationReference implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = -922734497035282004L;
		
		@XStreamAlias("firstLocationReferencePoint")
		private FirstLocationReferencePoint firstLocationReferencePoint;
		
		@XStreamAlias("IntermediateLocationReferencePoints")
		private List<IntermediateLocationReferencePoint> IntermediateLocationReferencePoints;
		
		@XStreamAlias("lastLocationReferencePoint")
		private LastLocationReferencePoint lastLocationReferencePoint;
		
		private int positiveOffset;
		
		private int negativeOffset;

		public FirstLocationReferencePoint getFirstLocationReferencePoint() {
			return firstLocationReferencePoint;
		}

		public void setFirstLocationReferencePoint(FirstLocationReferencePoint firstLocationReferencePoint) {
			this.firstLocationReferencePoint = firstLocationReferencePoint;
		}

		public LastLocationReferencePoint getLastLocationReferencePoint() {
			return lastLocationReferencePoint;
		}

		public void setLastLocationReferencePoint(LastLocationReferencePoint lastLocationReferencePoint) {
			this.lastLocationReferencePoint = lastLocationReferencePoint;
		}

		public List<IntermediateLocationReferencePoint> getIntermediateLocationReferencePoints() {
			return IntermediateLocationReferencePoints;
		}

		public void setIntermediateLocationReferencePoints(List<IntermediateLocationReferencePoint> intermediateLocationReferencePoints) {
			IntermediateLocationReferencePoints = intermediateLocationReferencePoints;
		}

		public int getPositiveOffset() {
			return positiveOffset;
		}

		public void setPositiveOffset(int positiveOffset) {
			this.positiveOffset = positiveOffset;
		}

		public int getNegativeOffset() {
			return negativeOffset;
		}

		public void setNegativeOffset(int negativeOffset) {
			this.negativeOffset = negativeOffset;
		}
		
	}
	
	@XStreamAlias("firstLocationReferencePoint")
	public static class FirstLocationReferencePoint implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -4653839491714808000L;

		@XStreamAlias("absoluteGeoCoordinate")
		private AbsoluteGeoCoordinate absoluteGeoCoordinate;
		
		@XStreamAlias("lineProperties")
		private LineProperties lineProperties;
		
		@XStreamAlias("pathProperties")
		private PathProperties pathProperties;

		public AbsoluteGeoCoordinate getAbsoluteGeoCoordinate() {
			return absoluteGeoCoordinate;
		}

		public void setAbsoluteGeoCoordinate(AbsoluteGeoCoordinate absoluteGeoCoordinate) {
			this.absoluteGeoCoordinate = absoluteGeoCoordinate;
		}

		public LineProperties getLineProperties() {
			return lineProperties;
		}

		public void setLineProperties(LineProperties lineProperties) {
			this.lineProperties = lineProperties;
		}

		public PathProperties getPathProperties() {
			return pathProperties;
		}

		public void setPathProperties(PathProperties pathProperties) {
			this.pathProperties = pathProperties;
		}
	}
	
	@XStreamAlias("IntermediateLocationReferencePoint")
	public static class IntermediateLocationReferencePoint implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 4731772164548680673L;
		
		@XStreamAlias("relativeGeoCoordinate")
		private RelativeGeoCoordinate relativeGeoCoordinate;
		
		@XStreamAlias("lineProperties")
		private LineProperties lineProperties;
		
		@XStreamAlias("pathProperties")
		private PathProperties pathProperties;


		public RelativeGeoCoordinate getRelativeGeoCoordinate() {
			return relativeGeoCoordinate;
		}

		public void setRelativeGeoCoordinate(RelativeGeoCoordinate relativeGeoCoordinate) {
			this.relativeGeoCoordinate = relativeGeoCoordinate;
		}

		public LineProperties getLineProperties() {
			return lineProperties;
		}

		public void setLineProperties(LineProperties lineProperties) {
			this.lineProperties = lineProperties;
		}

		public PathProperties getPathProperties() {
			return pathProperties;
		}

		public void setPathProperties(PathProperties pathProperties) {
			this.pathProperties = pathProperties;
		}
		
	}
	
	@XStreamAlias("lastLocationReferencePoint")
	public static class LastLocationReferencePoint implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6391933138621128908L;
		
		@XStreamAlias("relativeGeoCoordinate")
		private RelativeGeoCoordinate relativeGeoCoordinate;
		
		@XStreamAlias("lineProperties")
		private LineProperties lineProperties;
		
		@XStreamAlias("pathProperties")
		private PathProperties pathProperties;

		public RelativeGeoCoordinate getRelativeGeoCoordinate() {
			return relativeGeoCoordinate;
		}

		public void setRelativeGeoCoordinate(RelativeGeoCoordinate relativeGeoCoordinate) {
			this.relativeGeoCoordinate = relativeGeoCoordinate;
		}

		public LineProperties getLineProperties() {
			return lineProperties;
		}

		public void setLineProperties(LineProperties lineProperties) {
			this.lineProperties = lineProperties;
		}

		public PathProperties getPathProperties() {
			return pathProperties;
		}

		public void setPathProperties(PathProperties pathProperties) {
			this.pathProperties = pathProperties;
		}
		
	}
	
	@XStreamAlias("lineProperties")
	public static class LineProperties implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 5871997737881870659L;
		
		private int frc;
		private int fow;
		private int bearing;
		public int getFrc() {
			return frc;
		}
		public void setFrc(int frc) {
			this.frc = frc;
		}
		public int getFow() {
			return fow;
		}
		public void setFow(int fow) {
			this.fow = fow;
		}
		public int getBearing() {
			return bearing;
		}
		public void setBearing(int bearing) {
			this.bearing = bearing;
		}
	}
	
	@XStreamAlias("pathProperties")
	public static class PathProperties implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 9189031982890308836L;
		private int lfrncp;
		private int dnp;
		public int getLfrncp() {
			return lfrncp;
		}
		public void setLfrncp(int lfrncp) {
			this.lfrncp = lfrncp;
		}
		public int getDnp() {
			return dnp;
		}
		public void setDnp(int dnp) {
			this.dnp = dnp;
		}
	}
	
public static class AbsoluteGeoCoordinate implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -4802441999054719815L;

		private int longitude;
		
		private int latitude;

		public int getLongitude() {
			return longitude;
		}

		public void setLongitude(int longitude) {
			this.longitude = longitude;
		}

		public int getLatitude() {
			return latitude;
		}

		public void setLatitude(int latitude) {
			this.latitude = latitude;
		}
	}
	
	
	public static class RelativeGeoCoordinate implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -4802441999054719815L;

		private int longitude;
		
		private int latitude;

		public int getLongitude() {
			return longitude;
		}

		public void setLongitude(int longitude) {
			this.longitude = longitude;
		}

		public int getLatitude() {
			return latitude;
		}

		public void setLatitude(int latitude) {
			this.latitude = latitude;
		}
	}
	
	@XStreamAlias("road")
	public static class Road{
		@XStreamAlias("id")
		private Long id;
		
		@XStreamAlias("areacode")
		private Integer areacode;
		
		@XStreamAlias("resultType")
		private String resultType;
		
		@XStreamAlias("linearLocationReference")
		private LinearLocationReference linearLocationReference;
		
		@XStreamAlias("rectangle")
		private String rectangle;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Integer getAreacode() {
			return areacode;
		}

		public void setAreacode(Integer areacode) {
			this.areacode = areacode;
		}

		public String getRectangle() {
			return rectangle;
		}

		public void setRectangle(String rectangle) {
			this.rectangle = rectangle;
		}

		public LinearLocationReference getLinearLocationReference() {
			return linearLocationReference;
		}

		public void setLinearLocationReference(LinearLocationReference linearLocationReference) {
			this.linearLocationReference = linearLocationReference;
		}

		public String getResultType() {
			return resultType;
		}

		public void setResultType(String resultType) {
			this.resultType = resultType;
		}
		
	}
	


}
