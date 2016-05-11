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
import cn.com.cennavi.nspatial.SpatialUtil;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.ErrorLevel;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.MessageError;
import cn.com.cennavi.tpeg.decoder.bean.TPEGMesage.TrafficShape;
import cn.com.cennavi.tpeg.decoder.match.IMatcher;
import cn.com.cennavi.tpeg.decoder.match.IOLRDecoder;
import cn.com.cennavi.tpeg.decoder.match.IOLRDecoder.LinnerLocationReferenceDecodeResponse;
import cn.com.cennavi.tpeg.decoder.match.IOLRDecoder.PointAlongLineLocationReferenceDecodeResponse;
import cn.com.cennavi.tpeg.decoder.match.IOLRDecoder.Response;
import cn.com.cennavi.tpeg.decoder.match.impl.OlrTFPMessageImplMathcerImpl.LinearLocationReferenceDecoder;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.AbstractLocationReference;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.OpenlrLocationReference;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.linear_lr.LinearLocationReference;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.point_along_line_lr.PointAlongLineLocationReference;
import cn.com.cennavi.tpeg.item.component.tec.TECMessage;
import cn.com.cennavi.tpeg.item.component.tfp.FlowVector;
import cn.com.cennavi.tpeg.item.component.tfp.FlowVectorSection;
import cn.com.cennavi.tpeg.item.enumeration.SpatialResolution;
import cn.com.cennavi.visualizer.common.dataloader.r.RTable;
import cn.com.cennavi.visualizer.util.MapKUtil;

public class OlrTECMessageImplMathcerImpl implements IMatcher {

	private RTable r;

	private IOLRDecoder decoder;

	public OlrTECMessageImplMathcerImpl(RTable r, String url, String mapversion) {
		this.r = r;
		decoder = new LinearLocationReferenceDecoder(url, mapversion);
		//decoder = new PointAlongLineLocationReferenceDecoder(url, mapversion);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TPEGMesage match(Item item) {
		TECMessage tfp = (TECMessage) item;
		TPEGMesage onemessage = new TPEGMesage();
		onemessage.setMessage(tfp.toMapper(false));
		onemessage.setType("OLR-TEC");
		OpenlrLocationReference lrc = (OpenlrLocationReference) tfp.getLRC().getLocationReference();

		LinearLocationReference linearLocationReference = (LinearLocationReference) lrc.getLocationReference();
		//PointAlongLineLocationReference pointAlongLineLocationReference = (PointAlongLineLocationReference) lrc.getLocationReference();
		//Response res = decoder.decode(pointAlongLineLocationReference);
		Response res = decoder.decode(linearLocationReference);
		/*if (res == null) {
			MessageError messageError = new MessageError();
			messageError.setLevel(ErrorLevel.S);
			messageError.setMessage("No Decoder Response");
			onemessage.setError(messageError);
			return onemessage;
		}
		PointAlongLineLocationReferenceDecodeResponse response = (PointAlongLineLocationReferenceDecodeResponse) res;
		if (response.getEvents() == null || response.getEvents().size() <= 0 || ObjUtil.isEmpty(response.getEvents().get(0).getPointAlongLine())) {
			MessageError messageError = new MessageError();
			messageError.setLevel(ErrorLevel.S);
			messageError.setMessage("No Response Line");
			onemessage.setError(messageError);
			return onemessage;
		}
		String line = response.getEvents().get(0).getPointAlongLine();
		
		Object[] objs=getLinkids(line);
		List<Long> linkids = (List<Long>) objs[0];
		Point2D.Double point=(java.awt.geom.Point2D.Double) objs[1];
		if (linkids == null || linkids.size() <= 0) {
			MessageError messageError = new MessageError();
			messageError.setLevel(ErrorLevel.S);
			messageError.setMessage("No Vilidate Links");
			onemessage.setError(messageError);
			return onemessage;
		}
		
		List<Point2D.Double> shape = MapKUtil.getAllShape(linkids, r);
		Collections.reverse(shape);
		
		TrafficShape sr = new TrafficShape();
		shape=SpatialUtil.rarefyBypassPoints(shape, 1, 20);
		sr.setShape(shape);
		sr.getMarkers().add(point);
		sr.setLos(100);
		onemessage.getShowResults().add(sr);*/
		
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

		List<Long> linkids = getLinkidsLine(line);
		if (linkids == null || linkids.size() <= 0) {
			MessageError messageError = new MessageError();
			messageError.setLevel(ErrorLevel.S);
			messageError.setMessage("No Vilidate Links");
			onemessage.setError(messageError);
			return onemessage;
		}
		
		List<Point2D.Double> shape = MapKUtil.getAllShape(linkids, r);
		Point2D.Double nowPoint = shape.get(0);
		Collections.reverse(shape);
		
		TrafficShape sr = new TrafficShape();
		shape=SpatialUtil.rarefyBypassPoints(shape, 1, 20);
		sr.setShape(shape);
		sr.getMarkers().add(nowPoint);
		sr.setLos(100);
		onemessage.getShowResults().add(sr);

		return onemessage;
	}

	private List<Long> getLinkidsLine(String line) {
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
	
	private Object[] getLinkids(String line) {
		List<Long> ids = new ArrayList<Long>();
		Point2D.Double point=null;
		try {
			String[] args = line.split(",");
			for(int i=0;i<args.length;i++){
				if(i==args.length-2){
					point=new Point2D.Double(Double.valueOf(args[i]), Double.valueOf(args[i+1]));
					break;
				}else{
					ids.add(new Long(args[i]));
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Object[]{ids,BaiduMerc.bd_encrypt(point.getY(),point.getX())};
	}

	public static class PointAlongLineLocationReferenceDecoder implements IOLRDecoder {

		private String url;

		private String mapversion;

		private XStream requestXStream;

		private XStream responseXStream;

		public PointAlongLineLocationReferenceDecoder(String url, String mapversion) {
			this.url = url;
			this.mapversion = mapversion.toUpperCase();

			requestXStream = new XStream();
			requestXStream.registerConverter(new TPEGConverter(new DefaultMapper(this.getClass().getClassLoader())));
			requestXStream.alias("event", PointAlongLineLocationReferenceEvent.class);
			requestXStream.alias("request", PointAlongLineDecodeRequest.class);

			responseXStream = new XStream();
			responseXStream.alias("event",EventRoad.class);
			responseXStream.alias("response", PointAlongLineLocationReferenceDecodeResponse.class);
		}

		@Override
		public Response decode(AbstractLocationReference lr) {
			PointAlongLineLocationReference pointAlongLine = (PointAlongLineLocationReference) lr;

			PointAlongLineDecodeRequest request = new PointAlongLineDecodeRequest();
			request.setMapversion(mapversion);
			request.setType("pointalongline");

			List<PointAlongLineLocationReferenceEvent> events = new ArrayList<PointAlongLineLocationReferenceEvent>();
			PointAlongLineLocationReferenceEvent event = new PointAlongLineLocationReferenceEvent();
			events.add(event);
			request.setEvents(events);

			event.setId(1L);
			event.setResultType("decode_pointalongline");
			event.setPointAlongLineLocationReference(pointAlongLine.getPointLocationLineReferenceData().toMapper(true));//.setLinearLocationReference(linear.toMapper(true));

			ByteArrayOutputStream bais = new ByteArrayOutputStream();
			requestXStream.toXML(request, bais);

			HttpTookit http = null;
			byte[] responsebytes = null;
			try {
				http = new HttpTookit();
				responsebytes = http.doPost(url, null, "utf-8", bais.toByteArray());
				if (responsebytes == null) {
					return null;
				}
			}catch(Throwable e){
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
			PointAlongLineLocationReferenceDecodeResponse response =null;
			try {
				 response = (PointAlongLineLocationReferenceDecodeResponse) responseXStream.fromXML(new ByteArrayInputStream(responsebytes));
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}

			return response;
		}
	}

}
