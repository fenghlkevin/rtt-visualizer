package cn.com.cennavi.tpeg.decoder.match.impl;

import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.BaiduMerc;
import com.jhlabs.map.util.ShapeUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.DefaultMapper;

import cn.com.cennavi.codec.Item;
import cn.com.cennavi.codec.core.base.NumberField;
import cn.com.cennavi.codec.xstream.TPEGConverter;
import cn.com.cennavi.kfgis.framework.util.ObjUtil;
import cn.com.cennavi.kfgis.util.HttpTookit;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.ErrorLevel;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.MessageError;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.TrafficShape;
import cn.com.cennavi.tpeg.decoder.match.IMatcher;
import cn.com.cennavi.tpeg.decoder.match.IOLRDecoder;
import cn.com.cennavi.tpeg.decoder.match.IOLRDecoder.LinnerLocationReferenceDecodeResponse;
import cn.com.cennavi.tpeg.decoder.match.IOLRDecoder.Response;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.AbstractLocationReference;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.OpenlrLocationReference;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.linear_lr.LinearLocationReference;
import cn.com.cennavi.tpeg.item.component.tfp.FlowVector;
import cn.com.cennavi.tpeg.item.component.tfp.FlowVectorSection;
import cn.com.cennavi.tpeg.item.component.tfp.TFPMessage;
import cn.com.cennavi.tpeg.item.enumeration.SpatialResolution;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;
import cn.com.cennavi.visualizer.util.MapKUtil;

public class OlrTFPMessageImplMathcerImpl implements IMatcher {

	private RTable r;

	private IOLRDecoder decoder;

	public OlrTFPMessageImplMathcerImpl(RTable r, String url, String mapversion) {
		this.r = r;
		decoder = new LinearLocationReferenceDecoder(url, mapversion);
	}

	@Override
	public TPEGMesage match(Item item) {
		TFPMessage tfp = (TFPMessage) item;
		TPEGMesage onemessage = new TPEGMesage();
		onemessage.setMessage(tfp.toMapper(false));
		onemessage.setType("OLR-TFP");
		OpenlrLocationReference lrc = (OpenlrLocationReference) tfp.getLRC().getLocationReference();

		LinearLocationReference linearLocationReference = (LinearLocationReference) lrc.getLocationReference();
		Response res = decoder.decode(linearLocationReference);
		if (res == null) {
			MessageError messageError = new MessageError();
			messageError.setLevel(ErrorLevel.SS);
			messageError.setMessage("No Decoder Response");
			onemessage.setError(messageError);
			return onemessage;
		}
		LinnerLocationReferenceDecodeResponse response = (LinnerLocationReferenceDecodeResponse) res;
		if (response.getRoads() == null || response.getRoads().size() <= 0 || ObjUtil.isEmpty(response.getRoads().get(0).getLine())) {
			MessageError messageError = new MessageError();
			messageError.setLevel(ErrorLevel.S);
			messageError.setMessage("No Response Line");
			onemessage.setError(messageError);
			return onemessage;
		}
		String line = response.getRoads().get(0).getLine();

		List<Long> linkids = getLinkids(line);
		/*for(Long link : linkids){
			System.out.println(link);
		}*/
		if (linkids == null || linkids.size() <= 0) {
			MessageError messageError = new MessageError();
			messageError.setLevel(ErrorLevel.S);
			messageError.setMessage("No Vilidate Links");
			onemessage.setError(messageError);
			return onemessage;
		}

		NumberField defaultSpatialResolution = tfp.getTfpData().getSpatialResolution();

		List<FlowVector> fv = tfp.getTfpData().getFlowVectors();
		for (FlowVectorSection fvs : fv.get(0).getSections()) {
			NumberField spatialResolution = fvs.getSpatialResolution() == null ? defaultSpatialResolution : fvs.getSpatialResolution();
			int offset = fvs.getSpatialOffset().getNumber();

			List<Point2D.Double> shape = new ArrayList<Point2D.Double>();
			List<Point2D.Double> temp = MapKUtil.getAllShape(linkids, r);
			Collections.reverse(temp);//从最后一个经纬度向前计算长度

			int ot = 0;
			if (spatialResolution.getNumber() == SpatialResolution.AbsoluteL10M.toInt()) {
				ot=10;
			}else if (spatialResolution.getNumber() == SpatialResolution.AbsoluteL50M.toInt()) {
				ot=50;
			}else if (spatialResolution.getNumber() == SpatialResolution.AbsoluteL100M.toInt()) {
				ot=100;
			}else if (spatialResolution.getNumber() == SpatialResolution.AbsoluteL500M.toInt()) {
				ot=500;
			}

			double distance = offset * ot;
			Point2D.Double lastPoint = null;
			int lenAll = 0;
			for (int i = 0; i < temp.size(); i++) {
				Point2D.Double nowPoint = temp.get(i);
				Point2D.Double bdp = BaiduMerc.bd_decrypt(nowPoint.getY(),nowPoint.getX());
				
				//System.out.println("temp size: " + temp.size());
				//System.out.println(bdp.getX() + "," + bdp.getY());
				
				if (lastPoint == null) {
					lastPoint = nowPoint;
					shape.add(nowPoint);
				} else {
					double len = ShapeUtil.distance(lastPoint.getY(), lastPoint.getX(), nowPoint.getY(), nowPoint.getX());
					//double len = ShapeUtil.distance(p.getX(), p.getY(), bdp.getX(), bdp.getY());
					//System.out.println(len);
					if (distance - len >= 0) {
						shape.add(nowPoint);
						distance = distance - len;
						lastPoint = nowPoint;
						lenAll += len;
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
		
			//System.out.println("lenAll" + lenAll);
			TrafficShape sr = new TrafficShape();
			// System.out.println("before :"+shape.size());
			// shape=SpatialUtil.rarefyBypassPoints(shape, 1, 20);
			// System.out.println("after :"+shape.size());
			sr.setShape(shape);
			sr.setLos(fvs.getStatus().getLos().getNumber());
			onemessage.getShowResults().add(sr);
		}
		return onemessage;
	}

	private List<Long> getLinkids(String line) {
		List<Long> ids = new ArrayList<Long>();
		try {
			String[] args = line.split(",");
			for (String arg : args) {
				ids.add(new Long(arg));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ids;
	}

	public static class LinearLocationReferenceDecoder implements IOLRDecoder {

		private String url;

		private String mapversion;

		private XStream requestXStream;

		private XStream responseXStream;

		public LinearLocationReferenceDecoder(String url, String mapversion) {
			this.url = url;
			this.mapversion = mapversion.toUpperCase();

			requestXStream = new XStream();
			requestXStream.registerConverter(new TPEGConverter(new DefaultMapper(this.getClass().getClassLoader())));
			requestXStream.alias("road", LinearLocationReferenceRoad.class);
			requestXStream.alias("request", LinnerLocationReferenceDecodeRequest.class);

			responseXStream = new XStream();
			responseXStream.alias("road", LinkRoad.class);
			responseXStream.alias("response", LinnerLocationReferenceDecodeResponse.class);
		}

		@Override
		public Response decode(AbstractLocationReference lr) {
			LinearLocationReference linear = (LinearLocationReference) lr;

			LinnerLocationReferenceDecodeRequest request = new LinnerLocationReferenceDecodeRequest();
			request.setMapversion(mapversion);
			request.setType("line");

			List<LinearLocationReferenceRoad> roads = new ArrayList<LinearLocationReferenceRoad>();
			LinearLocationReferenceRoad road = new LinearLocationReferenceRoad();
			roads.add(road);
			request.setRoads(roads);

			road.setId(1L);
			road.setResultType("decode_line");
			road.setLinearLocationReference(linear.toMapper(true));

			ByteArrayOutputStream bais = new ByteArrayOutputStream();
			requestXStream.toXML(request, bais);

			//System.out.println(requestXStream.toXML(request));// TODO debug

			HttpTookit http = null;
			byte[] responsebytes = null;
			try {
				http = new HttpTookit();
				responsebytes = http.doPost(url, null, "utf-8", bais.toByteArray());
				if (responsebytes == null) {
					return null;
				}
			} catch (Throwable e) {
				System.out.println(requestXStream.toXML(request));// TODO debug
				e.printStackTrace();
				return null;
			} finally {
				try {
					if (http != null) {
						http.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			LinnerLocationReferenceDecodeResponse response = null;
			try {
				response = (LinnerLocationReferenceDecodeResponse) responseXStream.fromXML(new ByteArrayInputStream(responsebytes));
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}

			return response;
		}

		// public static void main(String[] args) throws Throwable {
		// String path = "/home/fengheliang/temp/beijing_test_2320279.tpeg";
		// FileInputStream fis = new FileInputStream(path);
		// byte[] bs = new byte[fis.available()];
		// fis.read(bs);
		// fis.close();
		//
		// List<TFPMessage> tfps=new ArrayList<TFPMessage>();
		// TPEGDecoderImpl decoder=new TPEGDecoderImpl();
		// decoder.execute(bs, tfps, null);
		//
		// TFPMessage tfp=tfps.get(0);
		// OpenlrLocationReference olrlrc=(OpenlrLocationReference)
		// tfp.getLRC().getLocationReference();
		// LinearLocationReference linear=(LinearLocationReference)
		// olrlrc.getLocationReference();
		// LinearLocationReferenceDecoder de=new
		// LinearLocationReferenceDecoder(null,"13_Q4_G");
		// de.decode(linear);
		//
		// }

	}
	
	public static void main(String[] args){
		double len = ShapeUtil.distance(116.3872995231818,39.857260837939585,116.3868795241416,39.85726084456396);
		double len1 = ShapeUtil.distance(116.39372076975934,39.86353985833409,116.39330241489486,39.86353505343161);
		System.out.println("Len1: " + len);
		System.out.println("Len2: " + len1);
	}

}
