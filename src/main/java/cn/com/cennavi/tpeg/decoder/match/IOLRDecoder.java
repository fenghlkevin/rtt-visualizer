package cn.com.cennavi.tpeg.decoder.match;

import java.util.List;
import java.util.Map;

import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.AbstractLocationReference;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public interface IOLRDecoder {

	public Response decode(AbstractLocationReference lr);

	public static class Response {
		@XStreamAlias("mapversion")
		private String mapversion;

		@XStreamAlias("type")
		private String type;

		public String getMapversion() {
			return mapversion;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public void setMapversion(String mapversion) {
			this.mapversion = mapversion;
		}
	}

	@XStreamAlias("response")
	public static class LinnerLocationReferenceDecodeResponse extends Response {
		@XStreamAlias("roads")
		private List<LinkRoad> roads;

		public List<LinkRoad> getRoads() {
			return roads;
		}

		public void setRoads(List<LinkRoad> roads) {
			this.roads = roads;
		}
	}

	@XStreamAlias("response")
	public static class PointAlongLineLocationReferenceDecodeResponse extends Response {
		@XStreamAlias("events")
		private List<EventRoad> events;

		public List<EventRoad> getEvents() {
			return events;
		}

		public void setEvents(List<EventRoad> events) {
			this.events = events;
		}
	}

	@XStreamAlias("request")
	public static class LinnerLocationReferenceDecodeRequest extends Response {

		@XStreamAlias("roads")
		private List<LinearLocationReferenceRoad> roads;

		public List<LinearLocationReferenceRoad> getRoads() {
			return roads;
		}

		public void setRoads(List<LinearLocationReferenceRoad> roads) {
			this.roads = roads;
		}
	}

	@XStreamAlias("request")
	public static class PointAlongLineDecodeRequest extends Response {

		@XStreamAlias("events")
		private List<PointAlongLineLocationReferenceEvent> events;

		public List<PointAlongLineLocationReferenceEvent> getEvents() {
			return events;
		}

		public void setEvents(List<PointAlongLineLocationReferenceEvent> events) {
			this.events = events;
		}
	}

	public static class Road {
		@XStreamAlias("id")
		private Long id;

		@XStreamAlias("resultType")
		private String resultType;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getResultType() {
			return resultType;
		}

		public void setResultType(String resultType) {
			this.resultType = resultType;
		}
	}

	@XStreamAlias("event")
	public static class EventRoad extends Road {

		@XStreamAlias("pointAlongLine")
		private String pointAlongLine;

		public String getPointAlongLine() {
			return pointAlongLine;
		}

		public void setPointAlongLine(String pointAlongLine) {
			this.pointAlongLine = pointAlongLine;
		}
	}

	@XStreamAlias("road")
	public static class LinkRoad extends Road {
		@XStreamAlias("line")
		private String line;

		public String getLine() {
			return line;
		}

		public void setLine(String line) {
			this.line = line;
		}
	}

	@XStreamAlias("road")
	public static class LinearLocationReferenceRoad extends Road {

		@XStreamAlias("linearLocationReference")
		private Map<String, Object> linearLocationReference;

		@XStreamAlias("rectangle")
		private String rectangle;

		public String getRectangle() {
			return rectangle;
		}

		public void setRectangle(String rectangle) {
			this.rectangle = rectangle;
		}

		public Map<String, Object> getLinearLocationReference() {
			return linearLocationReference;
		}

		public void setLinearLocationReference(Map<String, Object> linearLocationReference) {
			this.linearLocationReference = linearLocationReference;
		}
	}

	@XStreamAlias("event")
	public static class PointAlongLineLocationReferenceEvent extends Road {

		@XStreamAlias("pointAlongLineLocationReference")
		private Map<String, Object> pointAlongLineLocationReference;

		@XStreamAlias("rectangle")
		private String rectangle;

		public String getRectangle() {
			return rectangle;
		}

		public void setRectangle(String rectangle) {
			this.rectangle = rectangle;
		}

		public Map<String, Object> getPointAlongLineLocationReference() {
			return pointAlongLineLocationReference;
		}

		public void setPointAlongLineLocationReference(Map<String, Object> pointAlongLineLocationReference) {
			this.pointAlongLineLocationReference = pointAlongLineLocationReference;
		}
	}

}
