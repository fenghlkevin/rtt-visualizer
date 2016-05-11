package cn.com.cennavi.visualizer.service.parsedata.translate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommRttData implements Serializable {

	/**
		 * 
		 */
	private static final long serialVersionUID = -794270797660199762L;

	public static class CNRTICRttData extends CommRttData{
		/**
		 * 
		 */
		private static final long serialVersionUID = -6339010374419337336L;

		protected long meshid;

		protected int kind;

		protected long id;

		protected int direction;

		protected int los;

		public long getMeshid() {
			return meshid;
		}

		public void setMeshid(long meshid) {
			this.meshid = meshid;
		}

		public int getKind() {
			return kind;
		}

		public void setKind(int kind) {
			this.kind = kind;
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public int getDirection() {
			return direction;
		}

		public void setDirection(int direction) {
			this.direction = direction;
		}

		public int getLos() {
			return los;
		}

		public void setLos(int los) {
			this.los = los;
		}
	}

	public static class CNRticEventRttData extends CommRttData {

		/**
		 * 
		 */
		private static final long serialVersionUID = -5268221094211217672L;

		private Long meshid;
		
		private int startLength;

		private int endLength;

		private int eventRestrictType;

		private int eventRestrict;

		private int eventReasonType;

		private int eventReason;

		private String StartTime;

		private String EndTime;
		
		private List<CNRTICRttData> eventrtic;
		
		{
			eventrtic=new ArrayList<CNRTICRttData>();
		}

		public int getStartLength() {
			return startLength;
		}

		public void setStartLength(int startLength) {
			this.startLength = startLength;
		}

		public int getEndLength() {
			return endLength;
		}

		public void setEndLength(int endLength) {
			this.endLength = endLength;
		}

		public int getEventRestrictType() {
			return eventRestrictType;
		}

		public void setEventRestrictType(int eventRestrictType) {
			this.eventRestrictType = eventRestrictType;
		}

		public int getEventRestrict() {
			return eventRestrict;
		}

		public void setEventRestrict(int eventRestrict) {
			this.eventRestrict = eventRestrict;
		}

		public int getEventReasonType() {
			return eventReasonType;
		}

		public void setEventReasonType(int eventReasonType) {
			this.eventReasonType = eventReasonType;
		}

		public int getEventReason() {
			return eventReason;
		}

		public void setEventReason(int eventReason) {
			this.eventReason = eventReason;
		}

		public String getStartTime() {
			return StartTime;
		}

		public void setStartTime(String startTime) {
			StartTime = startTime;
		}

		public String getEndTime() {
			return EndTime;
		}

		public void setEndTime(String endTime) {
			EndTime = endTime;
		}

		public List<CNRTICRttData> getEventrtic() {
			return eventrtic;
		}

		public void setEventrtic(List<CNRTICRttData> eventrtic) {
			this.eventrtic = eventrtic;
		}

		public Long getMeshid() {
			return meshid;
		}

		public void setMeshid(Long meshid) {
			this.meshid = meshid;
		}

	}
}