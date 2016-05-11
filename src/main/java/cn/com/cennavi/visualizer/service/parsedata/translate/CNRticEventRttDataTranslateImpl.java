package cn.com.cennavi.visualizer.service.parsedata.translate;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.com.cennavi.kfgis.framework.exception.NServiceInternalException;
import cn.com.cennavi.kfgis.util.SBase64;
import cn.com.cennavi.visualizer.service.parsedata.translate.CommRttData.CNRTICRttData;
import cn.com.cennavi.visualizer.service.parsedata.translate.CommRttData.CNRticEventRttData;

public class CNRticEventRttDataTranslateImpl implements IRttDataTranslate {

	@SuppressWarnings("rawtypes")
	@Override
	public List<CommRttData> translate(String database64) {

		List<CommRttData> all = new ArrayList<CommRttData>();

		String data = database64;
		byte[] item = null;
		try {
			item = SBase64.decode(data);
		} catch (UnsupportedEncodingException e) {
			throw new NServiceInternalException("Base64解析失败", e);
		}
		String rticstr = new String(item);
		SAXReader reader = new SAXReader();
		try {
			Document document = reader.read(new StringReader(rticstr));
			List nodes = document.getRootElement().element("DATA").elements("MESH");
			for (Iterator it = nodes.iterator(); it.hasNext();) {
				Element elm = (Element) it.next();
				Long meshid=Long.valueOf(elm.element("MeshID").getStringValue());
				List events =elm.elements("Event");
				for (Iterator ite = events.iterator(); ite.hasNext();) {
					Element event = (Element) ite.next();
					CNRticEventRttData cnrticevent=new CNRticEventRttData();
					all.add(cnrticevent);
					cnrticevent.setMeshid(meshid);
					cnrticevent.setStartLength(Integer.valueOf(event.element("StartLength").getStringValue()));
					cnrticevent.setEndLength(Integer.valueOf(event.element("EndLength").getStringValue()));
					cnrticevent.setEventRestrictType(Integer.valueOf(event.element("EventRestrictType").getStringValue()));
					cnrticevent.setEventRestrict(Integer.valueOf(event.element("EventRestrict").getStringValue()));
					cnrticevent.setEventReasonType(Integer.valueOf(event.element("EventReasonType").getStringValue()));
					cnrticevent.setEventReason(Integer.valueOf(event.element("EventReason").getStringValue()));
					cnrticevent.setStartTime(event.element("StartTime").getStringValue());
					cnrticevent.setEndTime(event.element("EndTime").getStringValue());
					
					List rtics =event.elements("RTIC");
					for (Iterator rit = rtics.iterator(); rit.hasNext();) {
						Element rtic = (Element) rit.next();
						CNRTICRttData cnrtic=new CNRTICRttData();
						cnrticevent.getEventrtic().add(cnrtic);
						cnrtic.setMeshid(Long.valueOf(rtic.element("MeshId").getStringValue()));
						cnrtic.setKind(Integer.valueOf(rtic.element("Kind").getStringValue()));
						cnrtic.setId(Long.valueOf(rtic.element("Id").getStringValue()));
					}
					
				}
			}
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		return all;
	}

}
