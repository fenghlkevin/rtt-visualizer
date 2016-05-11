package cn.com.cennavi.tpeg.decoder.match.impl;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.com.cennavi.codec.Item;
import cn.com.cennavi.kfgis.util.ObjectUtil;
import cn.com.cennavi.nspatial.SpatialUtil;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.ErrorLevel;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.MessageError;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.TrafficShape;
import cn.com.cennavi.tpeg.decoder.match.IMatcher;
import cn.com.cennavi.tpeg.item.component.comm.lrc.tmc.TMCLocationReference;
import cn.com.cennavi.tpeg.item.component.tec.TECMessage;
import cn.com.cennavi.tpeg.map.dao.NRoadLoader;
import cn.com.cennavi.transform.bean.City;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTInfo;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTable;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTableParser;
import cn.com.cennavi.visualizer.common.dataloader.r.RInfo;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;

public class TmcTECMessageImplMathcerImpl implements IMatcher {
	
	private MTTable mt;
	private RTable r;
	private String lpinfoversion;
	
	public TmcTECMessageImplMathcerImpl(MTTable mt, RTable r,String lpinfoversion){
		this.mt=mt;
		this.r=r;
		this.lpinfoversion=lpinfoversion;
	}

	@Override
	public TPEGMesage match(Item item) {
		TECMessage tec=(TECMessage)item;
		TPEGMesage onemessage = new TPEGMesage();
		onemessage.setMessage(tec.toMapper(false));
		onemessage.setType("TMC-TEC");

		TMCLocationReference lrc = (TMCLocationReference) tec.getLRC().getLocationReference();
		
		int ltn = lrc.getLocationTableNumber().getNumber();
		int direction = lrc.getSelector().getLocationValue(0) ? 1 : 0;
		int locid=lrc.getLocationID().getNumber();

		City city = NRoadLoader.instance().execute(ltn, null, lpinfoversion);
		if (city == null) {
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.S);
			error.setMessage("NOT Found LPINFO[ltn:" + ltn + ", version:" + lpinfoversion + "]");
			onemessage.setError(error);
			return onemessage;
		}
		
		Long lpkey = MTTableParser.getLPKey(locid, direction, ltn);
		List<MTInfo> linkids = mt.getMtLinksMap().get(lpkey);
		// 根据每个linkid，得到mtinfo信息，并与R表关联，找到经纬度信息
		List<Point2D.Double> temp = getAllShape(linkids, mt, r);
		Collections.reverse(temp);
		
		TrafficShape sr = new TrafficShape();
		// System.out.println("before :"+shape.size());
		temp=SpatialUtil.rarefyBypassPoints(temp, 1, 20);
		// System.out.println("after :"+shape.size());
		sr.setShape(temp);
		sr.setLos(100);
		onemessage.getShowResults().add(sr);
		
		return onemessage;
	}
	
	private List<Point2D.Double> getAllShape(List<MTInfo> linkids, MTTable mt, RTable r) {
		List<Point2D.Double> allshape = new ArrayList<Point2D.Double>();
		if (linkids == null) {
			return allshape;
		}
		for (MTInfo mtinfo : linkids) {
			long linkid = new Long(mtinfo.getLink_ID());

			boolean reverse = false;
			RInfo rinfo = r.getRinfo().get(linkid);
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

}
