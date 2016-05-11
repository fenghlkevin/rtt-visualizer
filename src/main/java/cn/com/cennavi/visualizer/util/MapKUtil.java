package cn.com.cennavi.visualizer.util;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.cennavi.kfgis.util.ObjectUtil;
import cn.com.cennavi.visualizer.common.dataloader.corresponding.CorrespondingInfo.NILink;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTInfo;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTable;
import cn.com.cennavi.visualizer.common.dataloader.r.RInfo;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;

public class MapKUtil {

	public static List<Point2D.Double> getDrawDirectionShapeByCorresponding(NILink link, RTable r) {
		List<Point2D.Double> allshape = new ArrayList<Point2D.Double>();
		if (link == null) {
			return allshape;
		}

		long linkid = link.getLinkid();
		boolean reverse = false;
		RInfo rinfo = r.getRinfo().get(linkid);
		if (rinfo == null) {
			return allshape;
		}
		if (link.getDirection() == 1) {
			reverse = true;
		} else if (link.getDirection() == 0 && rinfo.getDirection() == 3) {
			reverse = true;
		}
		@SuppressWarnings("unchecked")
		List<Point2D.Double> shape = (List<Point2D.Double>) ObjectUtil.clone(rinfo.getShape());
		allshape.addAll(shape);
		if (reverse) {
			Collections.reverse(allshape);
		}
		return allshape;
	}

	public static List<Point2D.Double> getDrawDirectionShape(List<MTInfo> linkids, MTTable mt, RTable r) {
		List<Point2D.Double> allshape = new ArrayList<Point2D.Double>();
		if (linkids == null) {
			return allshape;
		}
		for (MTInfo mtinfo : linkids) {
			long linkid = new Long(mtinfo.getLink_ID());

			boolean reverse = false;
			RInfo rinfo = r.getRinfo().get(linkid);
			if (rinfo == null) {
				return allshape;
			}
			int link_dir = new Integer(mtinfo.getLink_direction());
			int drwa_linkdir = new Integer(mtinfo.getDraw_LineDir());
			if (link_dir == 3) {
				reverse = true;
			} else if ((link_dir == 0 || link_dir == 1) && drwa_linkdir == 0) {
				reverse = true;
			}
			@SuppressWarnings("unchecked")
			List<Point2D.Double> shape = (List<Double>) ObjectUtil.clone(rinfo.getShape());
			if (reverse) {
				Collections.reverse(shape);
			}
			allshape.addAll(shape);

		}
		return allshape;
	}

	public static List<Point2D.Double> getAllShape(final Long linkid, RTable r) {
		return getAllShape(new ArrayList<Long>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7612708630052153678L;

			{
				this.add(linkid);
			}
		}, r);
	}

	public static List<Point2D.Double> getAllShape(List<Long> linkids, RTable r) {
		List<Point2D.Double> allshape = new ArrayList<Point2D.Double>();
		if (linkids == null) {
			return allshape;
		}
		for (Long linkid : linkids) {

			String temp = String.valueOf(linkid);
			int dir = new Integer(temp.substring(6, 7));
			Long id = new Long(temp.substring(7));
			boolean reverse = dir == 0 ? false : true;
			RInfo rinfo = r.getRinfo().get(id);
			if (rinfo == null) {
				return allshape;
			}
			@SuppressWarnings("unchecked")
			List<Point2D.Double> shape = (List<Point2D.Double>) ObjectUtil.clone(rinfo.getShape());

			if (reverse) {
				Collections.reverse(shape);
			}
			allshape.addAll(shape);
		}
		return allshape;
	}
}
