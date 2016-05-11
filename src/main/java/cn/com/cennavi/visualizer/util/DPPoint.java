package cn.com.cennavi.visualizer.util;

import java.awt.geom.Point2D;

public class DPPoint extends Point2D.Double{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8645756814264055607L;
	
	   /** 
     * 点所属的曲线的索引 
     */  
    private int index = 0;

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	} 

}
