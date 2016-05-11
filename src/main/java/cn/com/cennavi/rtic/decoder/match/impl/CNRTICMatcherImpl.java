package cn.com.cennavi.rtic.decoder.match.impl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.com.cennavi.rtic.decoder.bean.RTICMesage;
import cn.com.cennavi.rtic.decoder.bean.RTICMesage.ErrorLevel;
import cn.com.cennavi.rtic.decoder.bean.RTICMesage.MessageError;
import cn.com.cennavi.rtic.decoder.bean.RTICMesage.TrafficShape;
import cn.com.cennavi.rtic.decoder.match.IRTICMatcher;
import cn.com.cennavi.visualizer.common.dataloader.corresponding.CorrespondingInfo;
import cn.com.cennavi.visualizer.common.dataloader.corresponding.CorrespondingInfo.NILink;
import cn.com.cennavi.visualizer.common.dataloader.corresponding.CorrespondingTable;
import cn.com.cennavi.visualizer.common.dataloader.corresponding.CorrespondingTableParser;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;
import cn.com.cennavi.visualizer.service.parsedata.translate.CommRttData;
import cn.com.cennavi.visualizer.service.parsedata.translate.CommRttData.CNRTICRttData;
import cn.com.cennavi.visualizer.util.LosUtil;
import cn.com.cennavi.visualizer.util.MapKUtil;

public class CNRTICMatcherImpl implements IRTICMatcher {

	private CorrespondingTable ct;
	private RTable r;
	private String rticversion;
	@SuppressWarnings("unused")
	private boolean debug;

	public CNRTICMatcherImpl(CorrespondingTable ct, RTable r, String rticversion, boolean debug) {
		this.ct = ct;
		this.r = r;
		this.rticversion = rticversion;
		this.debug = debug;
	}

	@Override
	public RTICMesage match(CommRttData item) {
		CNRTICRttData data=(CNRTICRttData)item;
		RTICMesage onemessage = new RTICMesage();
		Long rticid = CorrespondingTableParser.getRticKey(data.getMeshid(), data.getId(), data.getKind());
		onemessage.setType("CN-RTIC");
		Map<String, Object> message = new HashMap<String, Object>();
		Map<String, Object> mmc = new HashMap<String, Object>();
		mmc.put("messageID", rticid);
		mmc.put("versionID", 0);
		message.put("MMC", mmc);
		onemessage.setMessage(message);
		int los = LosUtil.rtic2tpeg(data.getLos());
		
		CorrespondingInfo ci = ct.getCorrespondingMap().get(rticid);
		if (ci == null) {
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.S);
			error.setMessage("NOT Found Corresponding INFO [rticid:" + rticid + "]");
			onemessage.setError(error);
			return onemessage;
		}

		if (ci.getLinks().size() <= 0) {
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.S);
			error.setMessage("NOT Found NILinks INFO from [rticid:" + rticid + "]");
			onemessage.setError(error);
			return onemessage;
		}
		List<Point2D.Double> allshape = new ArrayList<Point2D.Double>();
		for (NILink link : ci.getLinks()) {
			List<Point2D.Double> temp = MapKUtil.getDrawDirectionShapeByCorresponding(link, r);
			validateShape(onemessage, temp, rticid, link.getLinkid(), rticversion);
			allshape.addAll(temp);
		}
		List<TrafficShape> shows = new ArrayList<TrafficShape>();
		TrafficShape ts = new TrafficShape();
		ts.setId(rticid);
		ts.setLos(los);
		ts.setDirection(0);
		ts.setShape(allshape);
		shows.add(ts);
		
		onemessage.setShowResults(shows);
		return onemessage;
	}

	private void validateShape(RTICMesage onemessage, List<Point2D.Double> shape, long rticid, long linkid, String mapversion) {
		if (onemessage.getError() == null && (shape == null || shape.size() <= 0)) {
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.S);
			error.setMessage("NO Found LINK SHAPE  [rticid:" + rticid + ", linkid:" + linkid + ", version:" + mapversion + "]");
			onemessage.setError(error);
		}
	}

}
