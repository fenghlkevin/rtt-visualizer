package cn.com.cennavi.visualizer.common.dataloader.mt;

import java.io.Serializable;

public class MTInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4962484897284909712L;
	private String locationDirection;
	private String locationCode;
	private String mesh_ID;
	private String link_ID;
	private String draw_LineDir;
	private String link_kind;
	private String rtic_kind;
	private String link_direction;
	private String link_length;
	private String ltn;
	public String getLocationDirection() {
		return locationDirection;
	}
	public void setLocationDirection(String locationDirection) {
		this.locationDirection = locationDirection;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public String getMesh_ID() {
		return mesh_ID;
	}
	public void setMesh_ID(String mesh_ID) {
		this.mesh_ID = mesh_ID;
	}
	public String getLink_ID() {
		return link_ID;
	}
	public void setLink_ID(String link_ID) {
		this.link_ID = link_ID;
	}
	public String getDraw_LineDir() {
		return draw_LineDir;
	}
	public void setDraw_LineDir(String draw_LineDir) {
		this.draw_LineDir = draw_LineDir;
	}
	public String getLink_kind() {
		return link_kind;
	}
	public void setLink_kind(String link_kind) {
		this.link_kind = link_kind;
	}
	public String getRtic_kind() {
		return rtic_kind;
	}
	public void setRtic_kind(String rtic_kind) {
		this.rtic_kind = rtic_kind;
	}
	public String getLink_direction() {
		return link_direction;
	}
	public void setLink_direction(String link_direction) {
		this.link_direction = link_direction;
	}
	public String getLink_length() {
		return link_length;
	}
	public void setLink_length(String link_length) {
		this.link_length = link_length;
	}
	public String getLtn() {
		return ltn;
	}
	public void setLtn(String ltn) {
		this.ltn = ltn;
	}
}
