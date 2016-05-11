package cn.com.cennavi.visualizer.service.createfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import cn.com.cennavi.kfgis.framework.exception.NServiceInternalException;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.kfgis.util.HttpTookit;
import cn.com.cennavi.tpeg.item.component.comm.MMC;
import cn.com.cennavi.tpeg.item.component.comm.lrc.LRC;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.OpenlrLocationReference;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.common.AbsoluteGeoCoordinate;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.common.FirstLocationReferencePoint;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.common.LastLocationReferencePoint;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.common.LineProperties;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.common.PathProperties;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.common.PointLocationLineReferenceData;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.common.RelativeGeoCoordinate;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.linear_lr.IntermediateLocationReferencePoints;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.linear_lr.LinearLocationReference;
import cn.com.cennavi.tpeg.item.component.comm.lrc.olr.point_along_line_lr.PointAlongLineLocationReference;
import cn.com.cennavi.tpeg.item.component.comm.lrc.tmc.TMCLocationReference;
import cn.com.cennavi.tpeg.item.component.tec.Advice;
import cn.com.cennavi.tpeg.item.component.tec.Cause;
import cn.com.cennavi.tpeg.item.component.tec.RestrictionType;
import cn.com.cennavi.tpeg.item.component.tec.TECData;
import cn.com.cennavi.tpeg.item.component.tec.TECMessage;
import cn.com.cennavi.tpeg.item.component.tec.VehicleRestriction;
import cn.com.cennavi.tpeg.item.component.tfp.FlowVector;
import cn.com.cennavi.tpeg.item.component.tfp.FlowVectorSection;
import cn.com.cennavi.tpeg.item.component.tfp.Status;
import cn.com.cennavi.tpeg.item.component.tfp.TFPData;
import cn.com.cennavi.tpeg.item.component.tfp.TFPMessage;
import cn.com.cennavi.tpeg.item.enumeration.SpatialResolution;
import cn.com.cennavi.transform.bean.TrafficFlowInfo;
import cn.com.cennavi.visualizer.common.olr.Http4Olr;
import cn.com.cennavi.visualizer.common.olr.OLREventHandle;
import cn.com.cennavi.visualizer.common.olr.OLRServerOLRHandle;
import cn.com.cennavi.visualizer.common.olr.OLRServerOLRHandle.IntermediateLocationReferencePoint;
import cn.com.cennavi.visualizer.common.olr.OlrLrc;
import cn.com.cennavi.visualizer.service.createfile.CreateFileController.CreateReqParams;

import com.jhlabs.map.util.ShapeUtil;

@Service
public class GetRoadServiceImpl implements IGetRoadService {

	@Override
	public JsonResult create(CreateReqParams params, HttpServletRequest request, HttpServletResponse response) {
		String data = params.getData();

		List<TFPMessage> tfpList = new ArrayList<TFPMessage>();
		List<TECMessage> tecList = new ArrayList<TECMessage>();
		String filePath = "";

		try {
			JSONArray arr = new JSONArray(data);
			for (int i = 0; i < arr.length(); i++) {
				JSONObject json = arr.getJSONObject(i);
				String messageType = json.getString("messagetype");
				String messageLrc = json.getString("messagelrc");
				JSONObject mmc= json.getJSONObject("mmc");
				if ("tec".equals(messageType)) {
					createTecMsg(tecList, json,mmc);
				} else {
					if ("olr".equals(messageLrc)) {
						createTfpOlrMsg(tfpList, json,mmc);
					} else {
						createTfpMsg(tfpList, json,mmc);
					}
				}
			}

//			filePath = saveTpegFile(tfpList, tecList);
			filePath=CreateFileUtil.saveTpegFile(tfpList, tecList);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JsonResult jsonResult = new JsonResult();

		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		jsonResult.setJsonObj(filePath);

		return jsonResult;
	}

	/**
	 * 构建tfpmessage
	 * 
	 * @param tfpList
	 * @param json
	 * @throws Exception
	 */
	private void createTfpMsg(List<TFPMessage> tfpList, JSONObject json,JSONObject mmcmap) throws Exception {
		String messageId = json.getString("messageid");
		messageId = messageId.substring(0, 10);

		TFPMessage message = new TFPMessage();
		// 构建MMC结构
		MMC mmc = createMMC(Long.valueOf(messageId), mmcmap);

		TFPData tfpData = new TFPData();
		tfpData.setId(6);
		tfpData.setLengthComp(0);
		tfpData.setLengthAttr(0);
		tfpData.setStartTIme(this.parseData2Timestamp(mmcmap.getString("starttime")));
		tfpData.setSelector();
		tfpData.setSpatialResolution(SpatialResolution.TMCLocations.toInt());

		List<FlowVector> flowVectors = new ArrayList<FlowVector>();
		
		FlowVector flowVector = new FlowVector();
		flowVector.setId(7);
		flowVector.setLengthComp(0);
		flowVector.setLengthAttr(0);
		flowVector.setTimeOffset(0);
		flowVector.setSelector();

		String plocId = "";
		String direction = "";

		JSONArray roadArr = json.getJSONArray("roaditems");
		JSONObject roadJson = (JSONObject) roadArr.get(roadArr.length() - 1);

		List<FlowVectorSection> sections = new ArrayList<FlowVectorSection>();
		// 构建section
		structSection(roadArr, sections);

		plocId = roadJson.getString("id");
		direction = roadJson.getString("direction");

		flowVector.setSections(sections);
		flowVector.setSectionCOunt(sections.size());
		flowVectors.add(flowVector);

		// 创建lrc
		TrafficFlowInfo flow = new TrafficFlowInfo();
		flow.setLpCountryCode(12);
		flow.setLpDirection(Integer.valueOf(direction));
		flow.setLpLocationTableNumber(roadJson.getInt("areacode"));
		flow.setLpExtent(roadArr.length());
		flow.setLpID(Integer.valueOf(plocId));
		LRC lrc = createLRC(flow);

		message.setId(0);
		message.setLengthComp(0);
		message.setLengthAttr(0);
		message.setLRC(lrc);
		message.setMMC(mmc);

		tfpData.setFlowVectors(flowVectors);
		message.setTfpData(tfpData);

		tfpList.add(message);
	}

	/**
	 * 创建OLR的tfp编码数据
	 * 
	 * @param tfpList
	 * @param json
	 * @throws Exception
	 */
	private void createTfpOlrMsg(List<TFPMessage> tfpList, JSONObject json,JSONObject mmcmap) throws Exception {

		String messageId = json.getString("messageid");
		messageId = messageId.substring(0, 10);

		TFPMessage message = new TFPMessage();

		// 构建MMC结构
		MMC mmc = createMMC(Long.valueOf(messageId), mmcmap);

		TFPData tfpData = new TFPData();
		tfpData.setId(6);
		tfpData.setLengthComp(0);
		tfpData.setLengthAttr(0);
		tfpData.setStartTIme(this.parseData2Timestamp(mmcmap.getString("starttime")));
		tfpData.setSelector();
		tfpData.setSpatialResolution(SpatialResolution.AbsoluteL100M.toInt());

		List<FlowVector> flowVectors = new ArrayList<FlowVector>();

		FlowVector flowVector = new FlowVector();
		flowVector.setId(7);
		flowVector.setLengthComp(0);
		flowVector.setLengthAttr(0);
		flowVector.setTimeOffset(0);
		flowVector.setSelector();

		JSONArray roadArr = json.getJSONArray("roaditems");
		JSONObject roadJson = (JSONObject) roadArr.get(roadArr.length() - 1);
		JSONObject lrcJson = roadJson.getJSONObject("lrc");
		String linkStr = lrcJson.getJSONArray("value").toString();
		linkStr = linkStr.substring(1, linkStr.length() - 1);

		List<FlowVectorSection> sections = new ArrayList<FlowVectorSection>();
		// 构建 OLR section
		structOlrSection(roadArr, sections);

		flowVector.setSections(sections);
		flowVector.setSectionCOunt(sections.size());
		flowVectors.add(flowVector);

		message.setId(0);
		message.setLengthComp(0);
		message.setLengthAttr(0);
		message.setLRC(createOLRLRC(linkStr, messageId));
		message.setMMC(mmc);

		tfpData.setFlowVectors(flowVectors);
		message.setTfpData(tfpData);

		tfpList.add(message);
	}

	/**
	 * 构建TECMessgae
	 * 
	 * @param tecList
	 * @param json
	 * @throws Exception
	 */
	private void createTecMsg(List<TECMessage> tecList, JSONObject json,JSONObject mmcmap) throws Exception {
		String messageId = json.getString("messageid");
		messageId = messageId.substring(0, 10);
		String messageLrc = json.getString("messagelrc");

		TECMessage tecMessage = new TECMessage();
		tecMessage.setId(0); // id先不计数，在分配帧时统一计数
		tecMessage.setLengthComp(0);
		tecMessage.setLengthAttr(0);

		TECData tecData = new TECData();
		tecData.setId(3);
		tecData.setLengthComp(0);
		tecData.setLengthAttr(0);
		tecData.setSelector();

		JSONArray roadArr = json.getJSONArray("roaditems");
		JSONObject roadJson = (JSONObject) roadArr.get(roadArr.length() - 1);

		String roadEventStr = json.getString("event");
		JSONObject roadEvent = new JSONObject(roadEventStr);

		if (roadEvent.has("effectcode")) {
			tecData.setEffectCode(roadEvent.getInt("effectcode"));
		}

		if (roadEvent.has("lengthaffected")) {
			tecData.setLengthAffected(roadEvent.getInt("lengthaffected"));
		}
		
		if (roadEvent.has("averagespeedabsolute")) {
			tecData.setAverageSpeedAbsolute(roadEvent.getInt("averagespeedabsolute"));
		}

		if (roadEvent.has("starttime")) {
			tecData.setStartTIme((int) (getTime(roadEvent.getString("starttime"), "yyyy-mm-dd hh:mm") / 1000));
		} else {
			tecData.setStartTIme((int) System.currentTimeMillis() / 1000);
		}

		int stopTime = (int) (System.currentTimeMillis() / 1000 + 864000);
		if (roadEvent.has("stoptime")) {
			stopTime = (int) (getTime(roadEvent.getString("stoptime"), "yyyy-mm-dd hh:mm") / 1000);
			tecData.setStopTIme(stopTime);
		} else {
			tecData.setStopTIme(stopTime);
		}

		// 构建MMC结构
		MMC mmc = createMMC(Long.valueOf(messageId), mmcmap);

		List<Cause> causes=new ArrayList<Cause>();
		if (roadEvent.has("causes") && roadEvent.getJSONArray("causes").length() > 0) {
			causes.add(createCause(roadEvent));
			tecData.setCauses(causes);
		}

		List<Advice> advices=new ArrayList<Advice>();
		if (roadEvent.has("advices") && roadEvent.getJSONArray("advices").length() > 0) {
			advices.add(createAdvice(roadEvent));
			tecData.setAdvices(advices);
		}

		if ("olr".equals(messageLrc)) {
			String url = System.getProperty("RPE_URL") + "/getnearestlink?bear=0&roadtype=15";
			JSONObject lrcJson = roadJson.getJSONObject("lrc");
			JSONObject valJson = lrcJson.getJSONObject("value");
			double x = valJson.getDouble("x");
			double y = valJson.getDouble("y");
			String[] sp = ShapeUtil.to128S(x, y);

			url += "&point=" + sp[0] + "," + sp[1];

			HttpTookit ht = new HttpTookit();
			byte[] bs = ht.doGet(url, "UTF-8");
			String result = new String(bs, "UTF-8");
			ht.close();

			int start = result.indexOf("<linkpid>");
			int end = result.indexOf("</linkpid>");
			if (start > 0) {
				String linkId = result.substring(start + 9, end);
				String dir = linkId.substring(6, 7);

				if ("2".equals(dir)) {
					linkId = linkId.substring(0, 6) + "0" + linkId.substring(7, 13);
				}

				int pStart = result.indexOf("<clist>") + 7;
				int pEnd = result.indexOf("</clist>");
				String pointStr = result.substring(pStart, pEnd);
				String[] points = pointStr.split(",");
				String[] spTemp = new String[2];
				if (points.length > 4) {
					spTemp = ShapeUtil.toWGS84(points[2], points[3]);
				} else {
					spTemp = ShapeUtil.toWGS84(points[0], points[1]);
				}

				linkId = linkId + "," + spTemp[0] + "," + spTemp[1];
				LRC lrc = createTECOLRLRC(linkId, messageId);
				tecMessage.setLRC(lrc);
			}
		} else {
			// 创建TMC lrc
			String plocId = roadJson.getString("id");
			String direction = roadJson.getString("direction");

			TrafficFlowInfo flow = new TrafficFlowInfo();
			flow.setLpCountryCode(12);
			flow.setLpDirection(Integer.valueOf(direction));
			flow.setLpLocationTableNumber(roadJson.getInt("areacode"));
			flow.setLpExtent(roadArr.length());
			flow.setLpID(Integer.valueOf(plocId));

			LRC lrc = createLRC(flow);
			tecMessage.setLRC(lrc);
		}

		tecMessage.setMMC(mmc);
		tecMessage.setTecData(tecData);
		tecList.add(tecMessage);
	}

	/**
	 * 创建Cause结构
	 * 
	 * @param roadEvent
	 * @return
	 * @throws Exception
	 */
	private Cause createCause(JSONObject roadEvent) throws Exception {
		JSONArray causeArr = roadEvent.getJSONArray("causes");
		JSONObject causeJson = (JSONObject) causeArr.get(0);

		Cause cause = new Cause();
		cause.setId(4);// 固定值为4
		cause.setLengthComp(0);
		cause.setLengthAttr(0);

		if (causeJson.has("maincause")) {
			cause.setMainCause(causeJson.getInt("maincause"));
		}

		if (causeJson.has("warninglevel")) {
			cause.setWarningLevel(causeJson.getInt("warninglevel"));
		}
		cause.setSelector();

		if (causeJson.has("subcause")) {
			cause.setSubCause(causeJson.getInt("subcause"));
		}

		if (causeJson.has("lengthaffected")) {
			cause.setLengthAffected(causeJson.getInt("lengthaffected"));
		}

		if (causeJson.has("lanerestrictiontype")) {
			cause.setLaneRestrictionType(causeJson.getInt("lanerestrictiontype"));
		}

		if (causeJson.has("lanenumber")) {
			cause.setNumberOfLanes(causeJson.getInt("lanenumber"));
		}

		return cause;
	}

	private Advice createAdvice(JSONObject roadEvent) throws Exception {
		JSONArray adviceArr = roadEvent.getJSONArray("advices");
		JSONObject adviceJson = (JSONObject) adviceArr.get(0);

		Advice ad = new Advice();
		ad.setId(6);
		ad.setLengthComp(0);
		ad.setLengthAttr(0);

		if (adviceJson.has("restrictiontypes")) {
			JSONArray restrictionTypeArr = adviceJson.getJSONArray("restrictiontypes");
			if (restrictionTypeArr.length() > 0) {
				List<RestrictionType> restrictionTypes = new ArrayList<RestrictionType>();
				for (int i = 0; i < restrictionTypeArr.length(); i++) {
					JSONObject restrictionTypeJson = (JSONObject) restrictionTypeArr.get(0);
					RestrictionType type = new RestrictionType();
					type.setRestrictionType(restrictionTypeJson.getInt("restrictiontype"));
					type.setSelector();
					type.setRestrictionValue(restrictionTypeJson.getInt("restrictionvalue"));
					restrictionTypes.add(type);
				}
				VehicleRestriction vehicleRestriction = new VehicleRestriction();
				vehicleRestriction.setId(7);
				vehicleRestriction.setLengthComp(0);
				vehicleRestriction.setLengthAttr(0);
				vehicleRestriction.setSelector();

				if (adviceJson.has("vehicletype")) {
					vehicleRestriction.setVehicleType(adviceJson.getInt("vehicletype"));
				} else {
					vehicleRestriction.setVehicleType(1);
				}

//				vehicleRestriction.set(restrictionTypes.size());
				vehicleRestriction.setRestrictionTypes(restrictionTypes);

				List<VehicleRestriction> list = new ArrayList<VehicleRestriction>();
				list.add(vehicleRestriction);
				ad.setVehicleRestrictions(list);
			}
		}

		ad.setSelector();
		ad.setAdviceCode(adviceJson.getInt("advicecode"));
		ad.setSubAdviceCode(adviceJson.getInt("subadvicecode"));

		return ad;
	}

	/**
	 * 构建section结构
	 * 
	 * @param roadArr
	 * @param sections
	 */
	private void structSection(JSONArray roadArr, List<FlowVectorSection> sections) {
		try {
			int lastLos = -1;
			for (int j = 0; j < roadArr.length(); j++) {
				JSONObject roadJson = roadArr.getJSONObject(j);
				JSONArray sectionArr = roadJson.getJSONArray("sections");

				// 将section按照先后顺序进行排列
				List<RoadSection> sectionList = new ArrayList<RoadSection>();
				sortList(sectionList, sectionArr);

				for (int n = 0; n < sectionList.size(); n++) {
					RoadSection roadSection = sectionList.get(n);
					int los = roadSection.getLos();
					int length = roadSection.getLength();

					if (lastLos == los) {
						continue;
					}

					FlowVectorSection section = new FlowVectorSection();

					if (n == 0) {
						section.setSpatialOffset(roadArr.length() - j);
						lastLos = los;
					} else {
						section.setSpatialOffset(length / 100);
						section.setSpatialResolution(SpatialResolution.RELATIVE100M.toInt());
					}

					Status status = new Status();
					status.setSelector();
					status.setLos(los);
					status.setAverageSpeed(roadSection.getSpeed());
					status.setDelay(0);
					section.setStatus(status);
					section.setSelector();
					sections.add(section);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构建OLR 的section结构
	 * 
	 * @param roadArr
	 * @param sections
	 * @throws Exception
	 */
	private void structOlrSection(JSONArray roadArr, List<FlowVectorSection> sections) throws Exception {
		int lastLos = -1;
		JSONObject roadJson = roadArr.getJSONObject(0);
		JSONArray sectionArr = roadJson.getJSONArray("sections");

		// 将section按照先后顺序进行排列
		List<RoadSection> sectionList = new ArrayList<RoadSection>();
		sortList(sectionList, sectionArr);

		for (int n = 0; n < sectionList.size(); n++) {
			RoadSection roadSection = sectionList.get(n);
			int los = roadSection.getLos();
			int length = roadSection.getLength();

			if (lastLos == los) {
				continue;
			}

			FlowVectorSection section = new FlowVectorSection();

			if (n == 0) {
				section.setSpatialOffset(length / 10);
				section.setSpatialResolution(SpatialResolution.AbsoluteL10M.toInt());
			} else {
				section.setSpatialOffset(length / 100);
			}

			Status status = new Status();
			status.setSelector();
			status.setLos(los);
			status.setAverageSpeed(30);
			status.setDelay(0);
			section.setStatus(status);
			section.setSelector();
			sections.add(section);
		}
	}

	/**
	 * 对section list 进行排序，按照length大小排序
	 * 
	 * @param sectionList
	 * @param sectionArr
	 */
	private void sortList(List<RoadSection> sectionList, JSONArray sectionArr) {

		try {
			int length = 0;
			int allpz=0;
			int lastLos = 0;
			for (int i = sectionArr.length() - 1; i >= 0; i--) {
				JSONObject sectionJson = sectionArr.getJSONObject(i);
				RoadSection section = new RoadSection();

				length += sectionJson.getInt("length");

				section.setLength(length);
				section.setLos(sectionJson.getInt("los"));
				allpz+=sectionJson.getInt("length")*sectionJson.getInt("speed");

				if (lastLos ==section.getLos()) {
					continue;
				}
				section.setSpeed(allpz/length);
				lastLos = section.getLos();
				sectionList.add(section);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Collections.reverse(sectionList);
	}

	/**
	 * Tpeg编码并写入文件
	 * 
	 * @param tfpList
	 * @param tecList
	 * @return
	 */
//	private String saveTpegFile(List<TFPMessage> tfpList, List<TECMessage> tecList) {
//		Long time = System.currentTimeMillis();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.S");
//		String fileDate = sdf.format(time);
//
//		String filename = "rtt_test_" + fileDate + ".tpg";
//
//		String url = System.getProperty("TEMP_TPG_DOWNLOAD_URL");
//		String path = System.getProperty("TEMP_TPG_PATH");
//		path += filename;
//		url += filename;
//
//		System.out.println(path);
//		try {
//			TPEGBuilder b = new TPEGBuilder();
//			cn.com.cennavi.tpeg.builder.Configure c = new cn.com.cennavi.tpeg.builder.Configure();
//			c.setSidA(10);
//			c.setSidB(20);
//			c.setSidC(30);
//			b.setConfigure(c);
//
//			TPEG tpeg = b.execute(tfpList, tecList);
//			tpeg.encoding();
//
//			File file = new File(path);
//			// 如果文件夹不存在则创建
//			if (!file.getParentFile().exists()) {
//				file.mkdirs();
//			}
//			file.delete();
//			file.createNewFile();
//			FileOutputStream fos = new FileOutputStream(file);
//			fos.write(tpeg.getEncodedStream());
//			fos.flush();
//			fos.close();
//			System.out.println("tpeg file write over.");
//
//		} catch (Throwable e) {
//			e.printStackTrace();
//			return "";
//		}
//
//		return url;
//	}

	/**
	 * 创建LRC
	 * 
	 * @param flow
	 * @return
	 */
	private LRC createLRC(TrafficFlowInfo flow) {
		TMCLocationReference reference = new TMCLocationReference();
		reference.setId(2);
		reference.setLengthComp(0);
		reference.setLengthAttr(0);
		reference.setLocationID(flow.getLpID());
		reference.setCountryCode(flow.getLpCountryCode());
		reference.setLocationTableNumber(32);
		reference.setSelector();
		reference.getSelector().changeLocationValue(flow.getLpDirection(), 0);
		reference.setExtent(flow.getLpExtent());

		LRC lrc = new LRC();
		lrc.setId(2);
		lrc.setLengthComp(0);
		lrc.setLengthAttr(0);
		lrc.setLocationReference(reference);
		return lrc;
	}

	/**
	 * 创建OLR的LRC结构
	 * 
	 * @param linkIds
	 * @param areaCode
	 * @param messageId
	 * @return
	 * @throws Exception
	 */
	private LRC createOLRLRC(String linkIds, String messageId) throws Exception {

		OLRServerOLRHandle olrHandle = new OLRServerOLRHandle();
		String serverurl = System.getProperty("OLR_ENCODER_URL") + "t=" + System.currentTimeMillis();
		String req = olrHandle.createRequestBody(linkIds, messageId);
		System.out.println("serverurl: " + serverurl);
		System.out.println("req: " + req);
		Map<Long, OlrLrc> lrcMap = null;
		Http4Olr ht = null;

		ht = new Http4Olr();
		byte[] res = ht.doPost(serverurl, null, "UTF-8", req);
		if (res == null || res.length <= 0) {
			throw new NServiceInternalException("无法获取olr结果");
		}
		System.out.println("response: " + new String(res, "UTF-8"));
		lrcMap = olrHandle.parseResponseBody(new String(res, "UTF-8"));

		cn.com.cennavi.visualizer.common.olr.OLRServerOLRHandle.LinearLocationReference olr = new cn.com.cennavi.visualizer.common.olr.OLRServerOLRHandle.LinearLocationReference();
		for (Long key : lrcMap.keySet()) {
			olr = lrcMap.get(key).getLrc();
		}

		LRC lrc = new LRC();
		lrc.setId(2);
		lrc.setLengthComp(0);
		lrc.setLengthAttr(0);

		OpenlrLocationReference openlrLocationReference = new OpenlrLocationReference();
		lrc.setLocationReference(openlrLocationReference);

		openlrLocationReference.setVersion(16);

		LinearLocationReference linearLocationReference = new LinearLocationReference();
		openlrLocationReference.setLocationReference(linearLocationReference);

		FirstLocationReferencePoint firstLocationReferencePoint = new FirstLocationReferencePoint();
		linearLocationReference.setFirstLocationReferencePoint(firstLocationReferencePoint);

		AbsoluteGeoCoordinate absoluteGeoCoordinate = new AbsoluteGeoCoordinate();
		absoluteGeoCoordinate.setLongitude(olr.getFirstLocationReferencePoint().getAbsoluteGeoCoordinate().getLongitude());
		absoluteGeoCoordinate.setLatitude(olr.getFirstLocationReferencePoint().getAbsoluteGeoCoordinate().getLatitude());
		firstLocationReferencePoint.setAbsoluteGeoCoordinate(absoluteGeoCoordinate);

		firstLocationReferencePoint.setLineProperties(createLineProperties(olr.getFirstLocationReferencePoint().getLineProperties()));
		firstLocationReferencePoint.setPathProperties(createPathProperties(olr.getFirstLocationReferencePoint().getPathProperties()));

		LastLocationReferencePoint lastLocationReferencePoint = new LastLocationReferencePoint();
		linearLocationReference.setLastLocationReferencePoint(lastLocationReferencePoint);

		RelativeGeoCoordinate relativeGeoCoordinate = new RelativeGeoCoordinate();
		relativeGeoCoordinate.setLongitude(olr.getLastLocationReferencePoint().getRelativeGeoCoordinate().getLongitude());
		relativeGeoCoordinate.setLatitude(olr.getLastLocationReferencePoint().getRelativeGeoCoordinate().getLatitude());
		lastLocationReferencePoint.setRelativeGeoCoordinate(relativeGeoCoordinate);

		lastLocationReferencePoint.setLineProperties(createLineProperties(olr.getLastLocationReferencePoint().getLineProperties()));

		if (olr.getIntermediateLocationReferencePoints() != null && olr.getIntermediateLocationReferencePoints().size() > 0) {
			linearLocationReference.setN(olr.getIntermediateLocationReferencePoints().size());
			for (IntermediateLocationReferencePoint ip : olr.getIntermediateLocationReferencePoints()) {
				// IntermediateLocationReference ir=new
				// IntermediateLocationReference();
				IntermediateLocationReferencePoints ir = new IntermediateLocationReferencePoints();

				relativeGeoCoordinate = new RelativeGeoCoordinate();
				relativeGeoCoordinate.setLongitude(ip.getRelativeGeoCoordinate().getLongitude());
				relativeGeoCoordinate.setLatitude(ip.getRelativeGeoCoordinate().getLatitude());

				ir.setRelativeGeoCoordinate(relativeGeoCoordinate);

				ir.setLineProperties(this.createLineProperties(ip.getLineProperties()));
				ir.setPathProperties(this.createPathProperties(ip.getPathProperties()));
				linearLocationReference.addIntermediateLocationReference(ir);
			}
		}
		linearLocationReference.setPositiveOffset(Math.abs(olr.getPositiveOffset()));
		linearLocationReference.setNegativeOffset(Math.abs(olr.getNegativeOffset()));
		return lrc;
	}

	private LRC createTECOLRLRC(String linkIds, String messageId) throws Exception {

		Map<Long, OlrLrc> lrcMap = null;
		Http4Olr ht = null;
		try {
			// 构建olr xml
			OLREventHandle xmlhandle = new OLREventHandle();
			String req = xmlhandle.createEventRequestBody(linkIds, messageId);
			String serverurl = System.getProperty("OLR_ENCODER_URL") + "t=" + System.currentTimeMillis();
			System.out.println("serverurl: " + serverurl);
			System.out.println("request body: " + req);

			ht = new Http4Olr();
			byte[] res = ht.doPost(serverurl, null, "UTF-8", req);

			if (res == null || res.length <= 0) {
				throw new NServiceInternalException("无法获取olr结果");
			}
			System.out.println("http返回结果：" + new String(res, "UTF-8"));
			lrcMap = xmlhandle.parseResponseBody(new String(res, "UTF-8"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new NServiceInternalException("Parsing xml Response ERROR", e);
		}

		cn.com.cennavi.visualizer.common.olr.OLREventHandle.PointAlongLineLocationReference olr = new cn.com.cennavi.visualizer.common.olr.OLREventHandle.PointAlongLineLocationReference();
		for (Long key : lrcMap.keySet()) {
			olr = lrcMap.get(key).getPointAlineLrc();
		}

		LRC lrc = new LRC();
		lrc.setId(2);
		lrc.setLengthComp(0);
		lrc.setLengthAttr(0);

		OpenlrLocationReference reference1 = new OpenlrLocationReference();
		lrc.setLocationReference(reference1);

		PointAlongLineLocationReference pointRef = new PointAlongLineLocationReference();
		reference1.setLocationReference(pointRef);
		reference1.setVersion(16);

		PointLocationLineReferenceData refData = new PointLocationLineReferenceData();
		pointRef.setPointLocationLineReferenceData(refData);

		FirstLocationReferencePoint firstLocationReferencePoint = new FirstLocationReferencePoint();
		refData.setFirstLocationReferencePoint(firstLocationReferencePoint);
		AbsoluteGeoCoordinate absoluteGeoCoordinate = new AbsoluteGeoCoordinate();

		absoluteGeoCoordinate.setLongitude(olr.getFirstLocationReferencePoint().getAbsoluteGeoCoordinate().getLongitude());
		absoluteGeoCoordinate.setLatitude(olr.getFirstLocationReferencePoint().getAbsoluteGeoCoordinate().getLatitude());

		firstLocationReferencePoint.setAbsoluteGeoCoordinate(absoluteGeoCoordinate);
		firstLocationReferencePoint.setLineProperties(createLineProperties(olr.getFirstLocationReferencePoint().getLineProperties()));
		firstLocationReferencePoint.setPathProperties(createPathProperties(olr.getFirstLocationReferencePoint().getPathProperties()));

		LastLocationReferencePoint lastLocationReferencePoint = new LastLocationReferencePoint();
		refData.setLastLocationReferencePoint(lastLocationReferencePoint);
		RelativeGeoCoordinate relativeGeoCoordinate = new RelativeGeoCoordinate();
		relativeGeoCoordinate.setLongitude(olr.getLastLocationReferencePoint().getRelativeGeoCoordinate().getLongitude());
		relativeGeoCoordinate.setLatitude(olr.getLastLocationReferencePoint().getRelativeGeoCoordinate().getLatitude());

		lastLocationReferencePoint.setRelativeGeoCoordinate(relativeGeoCoordinate);
		lastLocationReferencePoint.setLineProperties(createLineProperties(olr.getLastLocationReferencePoint().getLineProperties()));

		refData.setPositiveOffset(olr.getPositiveOffset());
		refData.setOrientation(0);
		refData.setSideOfRoad(0);

		return lrc;
	}

	private int parseData2Timestamp(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm");
		try {
			return (int) (sdf.parse(time).getTime() / 1000);
		} catch (ParseException e) {
			e.printStackTrace();
			return (int) (System.currentTimeMillis() / 1000);
		}
	}

	private MMC createMMC(long messageId, JSONObject mmcmap) throws JSONException {
		MMC mmc = new MMC();
		mmc.setId(1);
		mmc.setLengthComp(0);
		mmc.setLengthAttr(0);
		mmc.setMessageID(messageId);
		mmc.setVersionID(mmcmap.getInt("versionid"));
		mmc.setMessageExpiryTime(parseData2Timestamp(mmcmap.getString("messageExpiryTime")));
		mmc.setMessageGenerationTime(parseData2Timestamp( mmcmap.getString("messageGenerationTime")));
		mmc.setSelector();
		mmc.getSelector().changeLocationValue((mmcmap.getBoolean("cancelflag")), 0);
		return mmc;
	}

	private LineProperties createLineProperties(cn.com.cennavi.visualizer.common.olr.OLRServerOLRHandle.LineProperties linepro) {
		LineProperties lineProperties = new LineProperties();
		lineProperties.setId(9);
		lineProperties.setLengthComp(0);
		lineProperties.setLengthAttr(0);
		lineProperties.setFrc(linepro.getFrc());
		lineProperties.setFow(linepro.getFow());
		lineProperties.setBearing(linepro.getBearing());
		lineProperties.setSelector();
		return lineProperties;
	}

	private PathProperties createPathProperties(cn.com.cennavi.visualizer.common.olr.OLRServerOLRHandle.PathProperties pathpro) {
		PathProperties pathProperties = new PathProperties();
		pathProperties.setId(10);
		pathProperties.setLengthComp(0);
		pathProperties.setLengthAttr(0);
		pathProperties.setLfrncp(pathpro.getLfrncp());
		pathProperties.setDnp(pathpro.getDnp());
		pathProperties.setSelector();
		return pathProperties;
	}

	private Long getTime(String timeStamp, String format) throws Exception {
		Long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		time = sdf.parse(timeStamp).getTime();

		return time;
	}

	public static void main(String[] args) {
		/*
		 * RoadSection r1 = new RoadSection(); r1.setLength(500);
		 * 
		 * RoadSection r2 = new RoadSection(); r2.setLength(300);
		 * 
		 * RoadSection r3 = new RoadSection(); r3.setLength(800);
		 * 
		 * List<RoadSection> sectionList = new ArrayList<RoadSection>();
		 * sectionList.add(r1); sectionList.add(r2); sectionList.add(r3);
		 * 
		 * Collections.reverse(sectionList);
		 * 
		 * for(RoadSection r : sectionList){ System.out.println(r.getLength());
		 * }
		 */
		/*
		 * String result = "111<linkpid>123456789012</linkpid>"; int start =
		 * result.indexOf("<linkpid>");
		 * 
		 * String linkId = result.substring(start+9, start + 21);
		 * 
		 * System.out.println(linkId); System.out.println(linkId.substring(0,
		 * 6)); System.out.println(linkId.substring(6, 12));
		 */

		Long time = System.currentTimeMillis();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss.S");
		String date = sdf.format(time);
		System.out.println(date);
		// time = sdf.parse(time).getTime();
	}

}
