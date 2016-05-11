package cn.com.cennavi.visualizer.common.olr;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import cn.com.cennavi.visualizer.common.olr.OLRServerOLRHandle.FirstLocationReferencePoint;
import cn.com.cennavi.visualizer.common.olr.OLRServerOLRHandle.LastLocationReferencePoint;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class OLREventHandle {
	
	private String resxml = "<response><mapversion>13_Q1_K</mapversion><type>pointalongline</type>" +
							"	<events><event><id>13280</id><areacode>110000</areacode>" +
							"		<pointAlongLineLocationReference>" +
							"			<firstLocationReferencePoint>" +
							"				<absoluteGeoCoordinate>" +
							"					<longitude>5433986</longitude><latitude>1866264</latitude>" +
							"				</absoluteGeoCoordinate>" +
							"				<lineProperties><frc>6</frc>" +
							"					<fow>0</fow><bearing>185</bearing>" +
							"				</lineProperties>" +
							"				<pathProperties>" +
							"					<lfrncp>6</lfrncp><dnp>93</dnp>" +
							"				</pathProperties>" +
							"			</firstLocationReferencePoint>" +
							"		<lastLocationReferencePoint>" +
							"			<relativeGeoCoordinate><longitude>-108</longitude><latitude>-12</latitude></relativeGeoCoordinate>" +
							"			<lineProperties><frc>6</frc>" +
							"				<fow>0</fow><bearing>185</bearing>" +
							"			</lineProperties>" +
							"		</lastLocationReferencePoint>" +
							"		<positiveOffset>58</positiveOffset>" +
							"	</pointAlongLineLocationReference>" +
							"	<rectangle>116.599598,40.045550,116.600681,40.045672</rectangle>" +
							"</event></events></response>";
	
	private static XStream xstream;
	static {
		if(xstream == null){
			xstream=new XStream();
			xstream.alias("response", Response.class);
			xstream.alias("event", Event.class);
			xstream.alias("pointAlongLineLocationReference", PointAlongLineLocationReference.class);
		}
	}
	
	public static String createEventRequestBody(String linkIds,String messageId) {
		StringBuffer str = new StringBuffer(200);
		str.append("<request>").append("<mapversion>").append("13_Q4_G").append("</mapversion>");
		str.append("<type>pointalongline</type>");
		str.append("<events>");
		
		str.append("<event>");
		str.append("<id>").append(messageId).append("</id>");
		str.append("<resultType>tisa</resultType>");
		str.append("<pointalongline>").append(linkIds).append("</pointalongline>");
		str.append("</event>");
		str.append("</events></request>");
		
		return str.toString();
	}
	
	public Map<Long, OlrLrc> parseResponseBody(String res) {
		Response response = (Response)xstream.fromXML(res);
		Map<Long ,OlrLrc> resMap=new Long2ObjectOpenHashMap<OlrLrc>();

		for(Event event :response.getEvents()){
			OlrLrc lrc = new OlrLrc();
			
			String[] ps = event.getRectangle().split(",");
			Point2D.Double rect[]=new Point2D.Double[2];
			rect[0] = new Point2D.Double(Double.valueOf(ps[0]),Double.valueOf(ps[1]));
			rect[1] = new Point2D.Double(Double.valueOf(ps[2]),Double.valueOf(ps[3]));
			
			lrc.setPointAlineLrc(event.getPointAlongLineLocationReference());
			lrc.setRectangle(rect);
			
			resMap.put(event.getId(), lrc);
		}
		return resMap;
	}
	
	@XStreamAlias("response")
	public static class Response{
		
		@XStreamAlias("mapversion")
		private String mapversion;
		
		@XStreamAlias("type")
		private String type;
		
		@XStreamAlias("events")
		private List<Event> events;
		
		public String getMapversion() {
			return mapversion;
		}

		public void setMapversion(String mapversion) {
			this.mapversion = mapversion;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public List<Event> getEvents() {
			return events;
		}

		public void setEvents(List<Event> events) {
			this.events = events;
		}

	}
	
	@XStreamAlias("pointAlongLineLocationReference")
	public static class PointAlongLineLocationReference implements Serializable{

		private static final long serialVersionUID = -922734497035282004L;
		
		@XStreamAlias("firstLocationReferencePoint")
		private FirstLocationReferencePoint firstLocationReferencePoint;
		
		@XStreamAlias("lastLocationReferencePoint")
		private LastLocationReferencePoint lastLocationReferencePoint;
		
		private int positiveOffset;
		
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

		public int getPositiveOffset() {
			return positiveOffset;
		}

		public void setPositiveOffset(int positiveOffset) {
			this.positiveOffset = positiveOffset;
		}

	}
	
	@XStreamAlias("event")
	public static class Event{
		@XStreamAlias("id")
		private Long id;
		
		@XStreamAlias("areacode")
		private Integer areacode;
		
		@XStreamAlias("resultType")
		private String resultType;
		
		@XStreamAlias("pointAlongLineLocationReference")
		private PointAlongLineLocationReference pointAlongLineLocationReference;
		
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

		public PointAlongLineLocationReference getPointAlongLineLocationReference() {
			return pointAlongLineLocationReference;
		}

		public void setPointAlongLineLocationReference(PointAlongLineLocationReference pointAlongLineLocationReference) {
			this.pointAlongLineLocationReference = pointAlongLineLocationReference;
		}

		public String getResultType() {
			return resultType;
		}

		public void setResultType(String resultType) {
			this.resultType = resultType;
		}
		
	}
}
