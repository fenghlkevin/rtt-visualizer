package cn.com.cennavi.visualizer.common.olr;

import java.awt.geom.Point2D;

import cn.com.cennavi.visualizer.common.olr.OLREventHandle.PointAlongLineLocationReference;
import cn.com.cennavi.visualizer.common.olr.OLRServerOLRHandle.LinearLocationReference;


public class OlrLrc {

	private LinearLocationReference lrc;

	private PointAlongLineLocationReference pointAlineLrc;

	private Point2D.Double[] rectangle;

	public PointAlongLineLocationReference getPointAlineLrc() {
		return pointAlineLrc;
	}

	public void setPointAlineLrc(PointAlongLineLocationReference pointAlineLrc) {
		this.pointAlineLrc = pointAlineLrc;
	}

	public Point2D.Double[] getRectangle() {
		return rectangle;
	}

	public void setRectangle(Point2D.Double[] rectangle) {
		this.rectangle = rectangle;
	}

	public LinearLocationReference getLrc() {
		return lrc;
	}

	public void setLrc(LinearLocationReference lrc) {
		this.lrc = lrc;
	}

}