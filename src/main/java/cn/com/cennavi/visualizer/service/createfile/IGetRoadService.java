package cn.com.cennavi.visualizer.service.createfile;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.visualizer.service.createfile.CreateFileController.CreateReqParams;

public interface IGetRoadService {
	
	
	public abstract JsonResult create(CreateReqParams params, HttpServletRequest request, HttpServletResponse response);
	
	public static class Road implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 7920772507400693960L;
		
		private Map<Long,RoadItem> roaditems=new HashMap<Long,RoadItem>();
		
		private int direction;
		
		private long roadid;

		public Map<Long, RoadItem> getRoaditems() {
			return roaditems;
		}

		public void setRoaditems(Map<Long, RoadItem> roaditems) {
			this.roaditems = roaditems;
		}

		public int getDirection() {
			return direction;
		}

		public void setDirection(int direction) {
			this.direction = direction;
		}

		public long getRoadid() {
			return roadid;
		}

		public void setRoadid(long roadid) {
			this.roadid = roadid;
		}

		public String getRoadname() {
			return roadname;
		}

		public void setRoadname(String roadname) {
			this.roadname = roadname;
		}

		private String roadname;
		
	}
	
	public static class RoadItem implements Serializable{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -5867006671101176773L;

		private long id;
		
		private List<Long> itemids=new ArrayList<Long>();
		
		private List<Point2D.Double> shape;
		
		private int groupid;
		
		private double length;
		
		private long nextItemId;
		
		private long prevItemId;
		
		private boolean inOverlay;
		
		private RoadItemType type;
		
		private int direction;
		
		private int areacode;
		
		public List<Point2D.Double> getShape() {
			return shape;
		}

		public void setShape(List<Point2D.Double> shape) {
			this.shape = shape;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public RoadItemType getType() {
			return type;
		}

		public void setType(RoadItemType type) {
			this.type = type;
		}

		public boolean isInOverlay() {
			return inOverlay;
		}

		public void setInOverlay(boolean inOverlay) {
			this.inOverlay = inOverlay;
		}

		public long getNextItemId() {
			return nextItemId;
		}

		public void setNextItemId(long nextItemId) {
			this.nextItemId = nextItemId;
		}

		public long getPrevItemId() {
			return prevItemId;
		}

		public void setPrevItemId(long prevItemId) {
			this.prevItemId = prevItemId;
		}

		public double getLength() {
			return length;
		}

		public void setLength(double length) {
			this.length = length;
		}

		public int getDirection() {
			return direction;
		}

		public void setDirection(int direction) {
			this.direction = direction;
		}

		public int getAreacode() {
			return areacode;
		}

		public void setAreacode(int areacode) {
			this.areacode = areacode;
		}

		public int getGroupid() {
			return groupid;
		}

		public void setGroupid(int groupid) {
			this.groupid = groupid;
		}

		public List<Long> getItemids() {
			return itemids;
		}

		public void setItemids(List<Long> itemids) {
			this.itemids = itemids;
		}

	}
	
	public static enum RoadItemType{
		TMC("tmc"),LINK("link");
		
		private String typename;
		
		private RoadItemType(String typename){
			this.typename=typename;
		}
		
		@Override
		public String toString(){
			return typename;
		}
	}
	
}
