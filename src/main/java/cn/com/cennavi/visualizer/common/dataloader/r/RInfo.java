package cn.com.cennavi.visualizer.common.dataloader.r;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RInfo extends Rectangle2D.Double implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2507036205101251306L;
	
	private String[] midvalues=null;
	
	//index=9
	private Long snodeID;
	
	//index=10
	private Long enodeID;
	
	//index=5
	private int direction=-1;
	
	//index=1
	private Long linkid;
	
	//index=3 只取字符串的前兩位即可
	private Integer kind=Integer.MAX_VALUE;
	
	public void setRect(double xmin, double ymin,double xmax,double ymax){
		super.setRect(xmin, ymin, xmax-xmin, ymax-ymin);
	}
	
	public void setRect(double xmin, double ymin){
		super.setRect(xmin, ymin, 0,0);
	}
	
	public String[] getMidvalues() {
		return midvalues;
	}
	public void setMidvalues(String[] midvalues) {
		this.midvalues = midvalues;
	}
	public List<Point2D.Double> getShape() {
		return shape;
	}
	public void setShape(List<Point2D.Double> shape) {
		this.shape = shape;
	}
	private List<Point2D.Double> shape=new ArrayList<Point2D.Double>();
	public int getDirection() {
		return direction;
	}
	public void setDirection(int direction) {
		this.direction = direction;
	}
	public Long getSnodeID() {
		return snodeID;
	}
	public void setSnodeID(Long snodeID) {
		this.snodeID = snodeID;
	}
	public Long getEnodeID() {
		return enodeID;
	}
	public void setEnodeID(Long enodeID) {
		this.enodeID = enodeID;
	}

	public Long getLinkid() {
		return linkid;
	}

	public void setLinkid(Long linkid) {
		this.linkid = linkid;
	}

	public Integer getKind() {
		return kind;
	}

	public void setKind(Integer kind) {
		this.kind = kind;
	}
}
