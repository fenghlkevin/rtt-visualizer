package cn.com.cennavi.visualizer.common.dataloader.n;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NInfo  extends Rectangle2D.Double implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2507036205101251306L;
	
	/**
	 * @param x 墨卡託投影座標
	 * @param y 墨卡託投影座標
	 */
	public NInfo() {
		
	}
	
	public void setRect(double x, double y){
		this.setRect(x, y, 0, 0);
	}
	
	private String[] midvalues=null;
	
	private Long id;
	/**
	 * 路口標識
	 * index=4
	 */
	private int cross_flag=-1;

	/**
	 * 路口連續link號碼
	 * index=6
	 */
	private String cross_lid="";
	
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

	public int getCross_flag() {
		return cross_flag;
	}
	public void setCross_flag(int cross_flag) {
		this.cross_flag = cross_flag;
	}
	public String getCross_lid() {
		return cross_lid;
	}
	public void setCross_lid(String cross_lid) {
		this.cross_lid = cross_lid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
