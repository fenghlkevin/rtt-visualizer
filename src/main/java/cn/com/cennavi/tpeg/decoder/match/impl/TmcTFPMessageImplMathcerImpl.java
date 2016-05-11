package cn.com.cennavi.tpeg.decoder.match.impl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.jhlabs.map.util.ExtinctionPoint;
import com.jhlabs.map.util.ShapeUtil;

import cn.com.cennavi.codec.Item;
import cn.com.cennavi.codec.core.base.NumberField;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.ErrorLevel;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.MessageError;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.TrafficShape;
import cn.com.cennavi.tpeg.decoder.match.IMatcher;
import cn.com.cennavi.tpeg.item.component.comm.lrc.tmc.TMCLocationReference;
import cn.com.cennavi.tpeg.item.component.tfp.FlowVector;
import cn.com.cennavi.tpeg.item.component.tfp.FlowVectorSection;
import cn.com.cennavi.tpeg.item.component.tfp.TFPMessage;
import cn.com.cennavi.tpeg.item.enumeration.SpatialResolution;
import cn.com.cennavi.tpeg.map.dao.LPInfoContainer;
import cn.com.cennavi.tpeg.map.dao.NRoadLoader;
import cn.com.cennavi.transform.bean.City;
import cn.com.cennavi.transform.bean.LPInfo;
import cn.com.cennavi.transform.bean.Road;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTInfo;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTable;
import cn.com.cennavi.visualizer.common.dataloader.mt.MTTableParser;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;
import cn.com.cennavi.visualizer.util.MapKUtil;

public class TmcTFPMessageImplMathcerImpl implements IMatcher {

	private MTTable mt;
	private RTable r;
	private String lpinfoversion;
	private boolean debug;

	public TmcTFPMessageImplMathcerImpl(MTTable mt, RTable r, String lpinfoversion, boolean debug) {
		this.mt = mt;
		this.r = r;
		this.lpinfoversion = lpinfoversion;
		this.debug = debug;
	}

	@Override
	public TPEGMesage match(Item item) {
		TFPMessage tfp = (TFPMessage) item;
		TPEGMesage onemessage = new TPEGMesage();
		onemessage.setMessage(tfp.toMapper(false));

		onemessage.setType("TMC-TFP");

		TMCLocationReference lrc = (TMCLocationReference) tfp.getLRC().getLocationReference();
		NumberField defaultSpatialResolution = tfp.getTfpData().getSpatialResolution();
		int ltn = lrc.getLocationTableNumber().getNumber();
		int locid = lrc.getLocationID().getNumber();
		int direction = lrc.getSelector().getLocationValue(0) ? 1 : 0;

		City city = NRoadLoader.instance().execute(ltn, null, lpinfoversion);
		if (city == null) {
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.S);
			error.setMessage("NOT Found LPINFO[ltn:" + ltn + ", version:" + lpinfoversion + "]");
			onemessage.setError(error);
			return onemessage;
		}

		LPInfo spointLP = LPInfoContainer.getInstance(lpinfoversion).getAlllp().get(ltn).get(locid);
		if(spointLP==null){
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.SS);
			error.setMessage("NOT Found LociD ["+locid+"] IN LPINFO[ltn:" + ltn + ", version:" + lpinfoversion + "]");
			onemessage.setError(error);
			return onemessage;
		}

		final long messageid = (spointLP.getGroupID() * 100 + ltn) * 10 + direction;// tfp.getMMC().getMessageID().getNumber();
		Road road = city.getCityTMCRoad().get(new Integer(String.valueOf(messageid)));
		if (road == null) {
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.S);
			error.setMessage("NOT Found Road Value [ltn:" + ltn + ", version:" + lpinfoversion + "]");
			onemessage.setError(error);
			return onemessage;
		}

		int tmcIndexInRoad = this.getIndex(road, lrc.getLocationID().getNumber());
		if (tmcIndexInRoad <= 0) {
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.S);
			error.setMessage("NOT Found LocID[" + lrc.getLocationID().getNumber() + "] In Road Value [ltn:" + ltn + ", version:" + lpinfoversion + "]");
			onemessage.setError(error);
			return onemessage;
		}

		List<FlowVector> fv = tfp.getTfpData().getFlowVectors();
		if (debug) {
			debug(onemessage, road, defaultSpatialResolution, lrc, ltn, fv, direction, tmcIndexInRoad);
		} else {
			// if(tfp.getMMC().getMessageID().getNumber()==2193320L){
			run(onemessage, road, defaultSpatialResolution, lrc, ltn, fv, direction, tmcIndexInRoad);
			// }

		}

		return onemessage;
	}

	private int getIndex(Road road, int locid) {
		for (int i = 1; i < road.getTmcs().size(); i++) {
			LPInfo lp = road.getTmcs().get(i);
			if (lp.getLocId() == locid) {
				return i;
			}
		}
		return -1;
	}

	private void validateShape(TPEGMesage onemessage, List<Point2D.Double> shape, int ltn) {
		if (onemessage.getError() == null && (shape == null || shape.size() <= 0)) {
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.S);
			error.setMessage("NO Found LINK SHAPE  [ltn:" + ltn + ", version:" + lpinfoversion + "]");
			onemessage.setError(error);
		}
	}

	private void run(TPEGMesage onemessage, Road road, NumberField defaultSpatialResolution, TMCLocationReference lrc, int ltn, List<FlowVector> fv, int direction,
			int tmcIndexInRoad) {
		List<LPInfo> tmcs = road.getTmcs();

		List<TrafficShape> reValue = new ArrayList<TPEGMesage.TrafficShape>();
		List<TrafficShape> shows = new ArrayList<TPEGMesage.TrafficShape>();
		int alloffset = lrc.getExtent().getNumber();

		if (alloffset > tmcs.size() - 1) {
			alloffset = tmcs.size() - 1;
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.A);
			error.setMessage("NO ENOUTH OFFSET  [ltn:" + ltn + ", version:" + lpinfoversion + "]");
			onemessage.setError(error);
		}

		for (int i = tmcIndexInRoad; i > tmcIndexInRoad - alloffset; i--) {
			LPInfo lp = null;
			if (road.isLoopRoad() && i < 1) {
				lp = tmcs.get(tmcs.size() - 1 + i);
			} else {
				lp = tmcs.get(i);
			}
			TrafficShape sr = new TrafficShape();
			sr.setId(lp.getLocId());
			sr.setDirection(direction);
			sr.setLos(1);

			Long lpkey = MTTableParser.getLPKey(lp.getLocId(), direction, ltn);
			List<MTInfo> linkids = mt.getMtLinksMap().get(lpkey);
			// 根据每个linkid，得到mtinfo信息，并与R表关联，找到经纬度信息
			List<Point2D.Double> shape = MapKUtil.getDrawDirectionShape(linkids, mt, r);
			validateShape(onemessage, shape, ltn);
			// Collections.reverse(shape);
			sr.setShape(shape);

			shows.add(sr);
		}
		Collections.reverse(shows);// 从后往前放lpinfo，反向后，达成0～n

		try {
			int lastTMCIndex = -1;
			for (FlowVectorSection fvs : fv.get(0).getSections()) {
				NumberField spatialResolution = fvs.getSpatialResolution() == null ? defaultSpatialResolution : fvs.getSpatialResolution();
				int offset = fvs.getSpatialOffset().getNumber();
				if (spatialResolution.getNumber() == SpatialResolution.TMCLocations.toInt() && offset > tmcs.size() - 1) {
					offset = tmcs.size() - 1;
					MessageError error = new MessageError();
					error.setLevel(ErrorLevel.A);
					error.setMessage("NO ENOUTH OFFSET  [ltn:" + ltn + ", version:" + lpinfoversion + "]");
					onemessage.setError(error);
				}
				int los = fvs.getStatus().getLos().getNumber();
				if (spatialResolution.getNumber() == SpatialResolution.TMCLocations.toInt()) {
					for (int i = shows.size() - 1; i >= shows.size() - offset; i--) {
						TrafficShape ts = shows.get(i);
						ts.setLos(los);
						lastTMCIndex = i;
					}
				} else if (spatialResolution.getNumber() == SpatialResolution.AbsoluteL10M.toInt() || spatialResolution.getNumber() == SpatialResolution.AbsoluteL50M.toInt()
						|| spatialResolution.getNumber() == SpatialResolution.AbsoluteL100M.toInt() || spatialResolution.getNumber() == SpatialResolution.AbsoluteL500M.toInt()) {
					double distance = offset * SpatialResolution.getResolution(spatialResolution.getNumber()).getValue();
					// absolute 不会超过tmc，所有只会出现在最后一个tmc上
					TrafficShape ts = shows.get(shows.size() - 1);
					doLengthSpatialResolution(ts, distance, los);
				} else if (spatialResolution.getNumber() == SpatialResolution.RELATIVE10M.toInt() || spatialResolution.getNumber() == SpatialResolution.RELATIVE100M.toInt()) {
					double distance = offset * SpatialResolution.getResolution(spatialResolution.getNumber()).getValue();
					TrafficShape ts = shows.get(lastTMCIndex);
					doLengthSpatialResolution(ts, distance, los);
				}
			}

			for (int i = 0; i < shows.size(); i++) {
				TrafficShape t = shows.get(i);
				if (t.getTempShape().size() <= 0) {
					//List<Point2D.Double> shape = SpatialUtil.rarefyBypassPoints(t.getShape(), 1, 20);
					List<Point2D.Double> shape = ExtinctionPoint.cutPointByAngle(t.getShape(), 175);
					t.setShape(shape);
					reValue.add(t);
				} else {
					for (TrafficShape tt : t.getTempShape()) {
						//List<Point2D.Double> shape = SpatialUtil.rarefyBypassPoints(tt.getShape(), 1, 20);
						List<Point2D.Double> shape = ExtinctionPoint.cutPointByAngle(tt.getShape(), 175);
						tt.setShape(shape);
						reValue.add(tt);
					}
				}
			}
			onemessage.setShowResults(reValue);
		} catch (Exception e) {
			MessageError error = new MessageError();
			error.setLevel(ErrorLevel.SS);
			error.setMessage("Error Decoding, may be ERROR ENCODING Value  [ltn:" + ltn + ", version:" + lpinfoversion + "]");
			onemessage.setError(error);
		}

	}

	private TrafficShape[] splitShape(TrafficShape parentShape, double absdistance, int abslos) {
		Deque<Point2D.Double> temp = new LinkedList<Point2D.Double>();
		temp.addAll(parentShape.getShape());
		Point2D.Double lastPoint = temp.pollLast();
		Deque<Point2D.Double> absshape = new LinkedList<Point2D.Double>();
		absshape.addFirst(lastPoint);

		Point2D.Double nowPoint = null;
		while ((nowPoint = temp.pollLast()) != null) {
			double len = ShapeUtil.distance(lastPoint.getY(), lastPoint.getX(), nowPoint.getY(), nowPoint.getX());
			if (absdistance - len >= 0) {
				absshape.addFirst(nowPoint);
				absdistance = absdistance - len;
				lastPoint = nowPoint;
			} else {
				double t = (len - absdistance) / len;// 得到占比
				double tx = lastPoint.getX() + (nowPoint.getX() - lastPoint.getX()) * t;
				double ty = lastPoint.getY() + (nowPoint.getY() - lastPoint.getY()) * t;
				Point2D.Double sp = new Point2D.Double(tx, ty);
				absshape.addFirst(sp);
				temp.addLast(sp);
				break;
			}
		}

		TrafficShape childShape1 = new TrafficShape();
		childShape1.setDirection(parentShape.getDirection());
		childShape1.setId(parentShape.getId());
		childShape1.setLos(parentShape.getLos());
		List<Point2D.Double> t1 = new ArrayList<Point2D.Double>();
		t1.addAll(temp);
		childShape1.setShape(t1);

		TrafficShape childShape2 = new TrafficShape();
		childShape2.setDirection(parentShape.getDirection());
		childShape2.setId(parentShape.getId());
		childShape2.setLos(abslos);
		List<Point2D.Double> t2 = new ArrayList<Point2D.Double>();
		t2.addAll(absshape);
		childShape2.setShape(t2);
		return new TrafficShape[]{childShape1, childShape2};
	}

	private void doLengthSpatialResolution(TrafficShape ts, double distance, int abslos) {
		TrafficShape[] tarray = null;
		if (ts.getTempShape().size() <= 0) {
			tarray = splitShape(ts, distance, abslos);
		} else {
			tarray = splitShape(ts.getTempShape().get(ts.getTempShape().size() - 1), distance, abslos);
			ts.getTempShape().remove(ts.getTempShape().size() - 1);
		}
		ts.getTempShape().add(tarray[0]);
		ts.getTempShape().add(tarray[1]);

	}

	private void debug(TPEGMesage onemessage, Road road, NumberField defaultSpatialResolution, TMCLocationReference lrc, int ltn, List<FlowVector> fv, int direction,
			int tmcIndexInRoad) {
		List<LPInfo> tmcs = road.getTmcs();
		Deque<Point2D.Double> lastTMCLeft = null;// 上次描绘最后1个TMC剩余的所有point

		for (FlowVectorSection fvs : fv.get(0).getSections()) {
			NumberField spatialResolution = fvs.getSpatialResolution() == null ? defaultSpatialResolution : fvs.getSpatialResolution();
			int offset = fvs.getSpatialOffset().getNumber();
			if (spatialResolution.getNumber() == SpatialResolution.TMCLocations.toInt() && offset > tmcs.size() - 1) {
				offset = tmcs.size() - 1;
				MessageError error = new MessageError();
				error.setLevel(ErrorLevel.A);
				error.setMessage("NO ENOUTH OFFSET  [ltn:" + ltn + ", version:" + lpinfoversion + "]");
				onemessage.setError(error);
			}
			List<Point2D.Double> shape = new ArrayList<Point2D.Double>();
			List<Point2D.Double> temp = null;
			if (spatialResolution.getNumber() == SpatialResolution.TMCLocations.toInt()) {
				// 根据locid和offset 得到所有的位置点对应的linkid，然后取得所有的经纬度
				// 得到所有locid，并取得经纬度序列
				// 得到画线shape后，每个tmc要把经纬度序列反序，这样才能从后往前画

				// 需要计算位置点在tmc的哪个位置for(i=tmcs.size()-1;i>= tmcs.size() -
				// offset;i--)
				for (int i = tmcIndexInRoad; i > tmcIndexInRoad - offset; i--) {
					LPInfo lp = null;
					if (road.isLoopRoad() && i < 1) {
						lp = tmcs.get(tmcs.size() - 1 + i);
					} else {
						lp = tmcs.get(i);
					}
					Long lpkey = MTTableParser.getLPKey(lp.getLocId(), direction, ltn);
					List<MTInfo> linkids = mt.getMtLinksMap().get(lpkey);
					// 根据每个linkid，得到mtinfo信息，并与R表关联，找到经纬度信息
					temp = MapKUtil.getDrawDirectionShape(linkids, mt, r);
					validateShape(onemessage, temp, ltn);
					Collections.reverse(temp);
					shape.addAll(temp);
				}
				if (temp != null) {
					// 调制了经纬度顺序，A--c--B 打开则是c画到A，关闭则是c画到B
					// Collections.reverse(temp);
					lastTMCLeft = new LinkedList<Point2D.Double>(temp);
				}
			} else if (spatialResolution.getNumber() == SpatialResolution.AbsoluteL100M.toInt()) {
				double distance = offset * 100;
				// 根据lrc终点位置点号开始，计算出长度，并得出经纬度序列
				LPInfo lastTMC = tmcs.get(tmcs.size() - 1);
				Long lpkey = MTTableParser.getLPKey(lastTMC.getLocId(), direction, ltn);
				List<MTInfo> linkids = mt.getMtLinksMap().get(lpkey);
				// 根据每个linkid，得到mtinfo信息，并与R表关联，找到经纬度信息
				temp = MapKUtil.getDrawDirectionShape(linkids, mt, r);
				validateShape(onemessage, temp, ltn);
				Collections.reverse(temp);
				Point2D.Double lastPoint = null;
				for (int i = 0; i < temp.size(); i++) {
					Point2D.Double nowPoint = temp.get(i);
					if (lastPoint == null) {
						lastPoint = nowPoint;
						shape.add(nowPoint);
					} else {
						double len = ShapeUtil.distance(lastPoint.getY(), lastPoint.getX(), nowPoint.getY(), nowPoint.getX());
						if (distance - len >= 0) {
							shape.add(nowPoint);
							distance = distance - len;
							lastPoint = nowPoint;
						} else {
							double t = (len - distance) / len;// 得到占比
							double tx = lastPoint.getX() + (nowPoint.getX() - lastPoint.getX()) * t;
							double ty = lastPoint.getY() + (nowPoint.getY() - lastPoint.getY()) * t;
							Point2D.Double sp = new Point2D.Double(tx, ty);
							shape.add(sp);
							break;
						}
					}
				}
			} else if (spatialResolution.getNumber() == SpatialResolution.RELATIVE100M.toInt()) {
				double distance = offset * 100;
				Point2D.Double lastPoint = null;
				Point2D.Double nowPoint = null;

				while (lastTMCLeft != null && (nowPoint = lastTMCLeft.poll()) != null) {
					if (lastPoint == null) {
						shape.add(nowPoint);
						lastPoint = nowPoint;
					} else {
						double len = ShapeUtil.distance(lastPoint.getY(), lastPoint.getX(), nowPoint.getY(), nowPoint.getX());
						if (distance - len >= 0) {
							shape.add(nowPoint);
							distance = distance - len;
							lastPoint = nowPoint;
						} else {
							double t = (len - distance) / len;// 得到占比
							double tx = lastPoint.getX() + (nowPoint.getX() - lastPoint.getX()) * t;
							double ty = lastPoint.getY() + (nowPoint.getY() - lastPoint.getY()) * t;
							Point2D.Double sp = new Point2D.Double(tx, ty);
							shape.add(sp);
							lastTMCLeft.addFirst(sp);
							break;
						}
					}
				}
			}

			TrafficShape sr = new TrafficShape();
			// System.out.println("before :"+shape.size());
			// shape=SpatialUtil.rarefyBypassPoints(shape, 1, 20);
			// System.out.println("after :"+shape.size());
			sr.setShape(shape);
			sr.setLos(fvs.getStatus().getLos().getNumber());
			onemessage.getShowResults().add(sr);
		}
	}
}
