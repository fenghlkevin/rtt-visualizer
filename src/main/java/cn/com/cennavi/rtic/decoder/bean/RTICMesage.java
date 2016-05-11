package cn.com.cennavi.rtic.decoder.bean;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RTICMesage {
	
	private String type;

	private Map<String,Object> message;
	
	private MessageError error;
	
	private List<TrafficShape> showResults=new ArrayList<TrafficShape>();

	public List<TrafficShape> getShowResults() {
		return showResults;
	}

	public Map<String,Object> getMessage() {
		return message;
	}

	public void setMessage(Map<String,Object> message) {
		this.message = message;
	}

	public MessageError getError() {
		return error;
	}

	public void setError(MessageError error) {
		this.error = error;
	}

	public void setShowResults(List<TrafficShape> showResults) {
		this.showResults = showResults;
	}
	
	public static class TrafficShape {
		
		//该数组用于存储临时的变量，如果该值有内容，则把原路况替换成这个对象内容
		private List<TrafficShape> tempShape=new ArrayList<TrafficShape>();
		
		private List<Point2D.Double> shape = new ArrayList<Point2D.Double>();
		
		private List<Point2D.Double> markers=new ArrayList<Point2D.Double>();
		
		private long id;
		
		private int direction;
		
		private Integer los = 0;
		
		private String message;

		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public List<Point2D.Double> getShape() {
			return shape;
		}
		public void setShape(List<Point2D.Double> shape) {
			this.shape = shape;
		}
		public Integer getLos() {
			return los;
		}
		public void setLos(Integer los) {
			this.los = los;
		}
		public List<Point2D.Double> getMarkers() {
			return markers;
		}
		public void setMarkers(List<Point2D.Double> markers) {
			this.markers = markers;
		}
		
		public int getDirection() {
			return direction;
		}
		public void setDirection(int direction) {
			this.direction = direction;
		}
		public List<TrafficShape> getTempShape() {
			return tempShape;
		}
		public void setTempShape(List<TrafficShape> tempShape) {
			this.tempShape = tempShape;
		}
		public long getId() {
			return id;
		}
		public void setId(long id) {
			this.id = id;
		}
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public static class MessageError{
		
		private ErrorLevel level;
		
		private String message;

		public ErrorLevel getLevel() {
			return level;
		}

		public void setLevel(ErrorLevel level) {
			this.level = level;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
	
	public enum ErrorLevel{
		SS("SS"),S("S"),A("A"),B("B"),C("C"),D("D"),OK("OK");
		private String value;
		
		private ErrorLevel(String value){
			this.value=value;
		}

		@Override
		public String toString() {
			return this.value;
		}
		
		
	}

}