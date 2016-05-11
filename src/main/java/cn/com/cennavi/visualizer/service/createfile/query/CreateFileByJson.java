package cn.com.cennavi.visualizer.service.createfile.query;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.stereotype.Service;

import cn.com.cennavi.kfgis.bean.param.AbstractParams;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.tpeg.item.component.comm.MMC;
import cn.com.cennavi.tpeg.item.component.comm.lrc.LRC;
import cn.com.cennavi.tpeg.item.component.comm.lrc.tmc.TMCLocationReference;
import cn.com.cennavi.tpeg.item.component.tec.Advice;
import cn.com.cennavi.tpeg.item.component.tec.Cause;
import cn.com.cennavi.tpeg.item.component.tec.LocalisedShortString;
import cn.com.cennavi.tpeg.item.component.tec.ShortString;
import cn.com.cennavi.tpeg.item.component.tec.TECData;
import cn.com.cennavi.tpeg.item.component.tec.TECMessage;
import cn.com.cennavi.tpeg.item.component.tec.VehicleRestriction;
import cn.com.cennavi.tpeg.item.component.tfp.FlowVector;
import cn.com.cennavi.tpeg.item.component.tfp.FlowVectorSection;
import cn.com.cennavi.tpeg.item.component.tfp.Status;
import cn.com.cennavi.tpeg.item.component.tfp.TFPData;
import cn.com.cennavi.tpeg.item.component.tfp.TFPMessage;
import cn.com.cennavi.visualizer.service.createfile.CreateFileUtil;
import cn.com.cennavi.visualizer.service.createfile.CreateFileController.CreateJSONParams;

@Service("createfile")
public class CreateFileByJson implements IRoadQueryInf {

	@Override
	public JsonResult query(AbstractParams params, HttpServletRequest request, HttpServletResponse response) {

		CreateJSONParams jsonparams = (CreateJSONParams) params;
		List<TFPMessage> tfps = new ArrayList<TFPMessage>();
		List<TECMessage> tecs = new ArrayList<TECMessage>();
		try {
			JSONArray messages = new JSONArray(jsonparams.getData());
			for (int i = 0; i < messages.length(); i++) {
				JSONObject msg = messages.getJSONObject(i);
				String type = msg.getString("type");
				JSONObject value = msg.getJSONObject("value");

				MMC mmc = this.createMMC(value.getJSONObject("mmc"));
				LRC lrc = this.createLRC(value.getJSONObject("lrc"));
				if ("tfp".equalsIgnoreCase(type)) {
					TFPData data = this.createTFPData(value.getJSONObject("tfpdata"));
					TFPMessage one = new TFPMessage();
					one.setMMC(mmc);
					one.setLRC(lrc);
					one.setTfpData(data);
					tfps.add(one);
				} else {
					TECData data = this.createTECData(value.getJSONObject("tecdata"));
					TECMessage one = new TECMessage();
					one.setMMC(mmc);
					one.setLRC(lrc);
					one.setTecData(data);
					tecs.add(one);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String filepath = CreateFileUtil.saveTpegFile(tfps, tecs);
		JsonResult jsonResult = new JsonResult();
		jsonResult.setCallback(jsonparams.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		jsonResult.setJsonObj(filepath);

		return jsonResult;
	}

	private MMC createMMC(JSONObject mmc) throws JSONException {
		MMC remmc = new MMC();
		remmc.setMessageID(mmc.getLong("messageid"));
		remmc.setVersionID(mmc.getInt("versionid"));
		remmc.setMessageExpiryTime(mmc.getInt("messageexpirytime"));
		if (!mmc.isNull("selector_0_cancel")) {
			remmc.getSelector().changeLocationValue(mmc.getInt("selector_0_cancel"), 0);
		}
		if (!mmc.isNull("messagegenerationtime")) {
			remmc.setMessageGenerationTime(mmc.getInt("messagegenerationtime"));
		}
		return remmc;
	}

	private LRC createLRC(JSONObject lrc) throws JSONException {
		LRC relrc = new LRC();
		TMCLocationReference tmc = new TMCLocationReference();
		relrc.setLocationReference(tmc);
		tmc.setLocationID(lrc.getInt("locationid"));
		tmc.setCountryCode(lrc.getInt("countrycode"));
		tmc.setLocationTableNumber(lrc.getInt("locationtablenumber"));

		if (!lrc.isNull("extent")) {
			tmc.setExtent(lrc.getInt("extent"));
		}
		if (!lrc.isNull("extendedcountrycode")) {
			tmc.setExtendedCountryCode(lrc.getInt("extendedcountrycode"));
		}
		if (!lrc.isNull("locationtableversion")) {
			tmc.setLocationTableVersion(lrc.getString("locationtableversion"));
		}
		if (!lrc.isNull("selector_0_direction")) {
			tmc.getSelector().changeLocationValue(lrc.getInt("selector_0_direction"), 0);
		}
		return relrc;
	}

	private TFPData createTFPData(JSONObject tfpdata) throws JSONException {
		TFPData redata = new TFPData();
		redata.setStartTIme(tfpdata.getInt("starttime"));
		if (!tfpdata.isNull("duration")) {
			redata.setDuration(tfpdata.getInt("duration"));
		}
		redata.setSpatialResolution(tfpdata.getInt("spatialresolution"));
		List<FlowVector> flowVectors = new ArrayList<FlowVector>();
		redata.setFlowVectors(flowVectors);
		JSONArray _flowvectors = tfpdata.getJSONArray("flowvectors");
		for (int i = 0; i < _flowvectors.length(); i++) {
			JSONObject _fv = _flowvectors.getJSONObject(i);
			FlowVector fv = new FlowVector();
			flowVectors.add(fv);
			fv.setTimeOffset(_fv.getInt("timeoffset"));
			if (!_fv.isNull("spatialresolution")) {
				fv.setSpatialResolution(_fv.getInt("spatialresolution"));
			}
			List<FlowVectorSection> sections = new ArrayList<FlowVectorSection>();
			JSONArray _flowvectorsections = _fv.getJSONArray("flowvectorsections");
			for (int j = 0; j < _flowvectorsections.length(); j++) {
				JSONObject _section = _flowvectorsections.getJSONObject(j);
				FlowVectorSection section = new FlowVectorSection();
				sections.add(section);

				section.setSpatialOffset(_section.getInt("spatialoffset"));
				if (!_section.isNull("spatialresolution")) {
					section.setSpatialResolution(_section.getInt("spatialresolution"));
				}

				Status status = new Status();
				JSONObject _status = _section.getJSONObject("status");
				section.setStatus(status);
				if (!_status.isNull("los")) {
					status.setLos(_status.getInt("los"));
				}
				if (!_status.isNull("averagespeed")) {
					status.setAverageSpeed(_status.getInt("averagespeed"));
				}
				if (!_status.isNull("delay")) {
					status.setDelay(_status.getInt("delay"));
				}

			}
			fv.setSections(sections);
		}
		return redata;
	}

	private TECData createTECData(JSONObject tecdata) throws JSONException {
		TECData redata = new TECData();
		redata.setEffectCode(tecdata.getInt("effectcode"));
		if (!tecdata.isNull("starttime")) {
			redata.setStartTIme(tecdata.getInt("starttime"));
		}
		if (!tecdata.isNull("stoptime")) {
			redata.setStopTIme(tecdata.getInt("stoptime"));
		}
		if (!tecdata.isNull("lengthaffected")) {
			redata.setLengthAffected(tecdata.getInt("lengthaffected"));
		}
		if (!tecdata.isNull("averagespeedabsolute")) {
			redata.setAverageSpeedAbsolute(tecdata.getInt("averagespeedabsolute"));
		}

		JSONArray _causes = tecdata.getJSONArray("causes");
		List<Cause> causes = new ArrayList<Cause>();
		redata.setCauses(causes);
		for (int i = 0; i < _causes.length(); i++) {
			JSONObject _cause = _causes.getJSONObject(i);
			Cause cause = new Cause();
			causes.add(cause);

			cause.setMainCause(_cause.getInt("maincause"));
			cause.setWarningLevel(_cause.getInt("warninglevel"));
			if (!_cause.isNull("subcause")) {
				cause.setSubCause(_cause.getInt("subcause"));
			}
			if (!_cause.isNull("lengthaffected")) {
				cause.setLengthAffected(_cause.getInt("lengthaffected"));
			}
			if (!_cause.isNull("lanerestrictiontype")) {
				cause.setLaneRestrictionType(_cause.getInt("lanerestrictiontype"));
			}
			if (!_cause.isNull("numberoflanes")) {
				cause.setNumberOfLanes(_cause.getInt("numberoflanes"));
			}
			
			if (!_cause.isNull("freetexts")) {
				JSONArray _freeTexts=_cause.getJSONArray("freetexts");
				List<LocalisedShortString> freeTexts=new ArrayList<LocalisedShortString>();
				for(int j=0;j<_freeTexts.length();j++){
					JSONObject _freeText=_freeTexts.getJSONObject(j);
					
					LocalisedShortString lss=new LocalisedShortString();
					freeTexts.add(lss);
					
					lss.setLanguageCode(_freeText.getInt("languagecode"));
					ShortString ss=new ShortString();
					ss.setBytes(_freeText.getString("freetext"));
					lss.setFreeText(ss);
				}
				cause.setFreeText(freeTexts);
			}
		}

		if (!tecdata.isNull("advices")) {
			JSONArray _advices = tecdata.getJSONArray("advices");
			List<Advice> advices = new ArrayList<Advice>();

			for (int i = 0; i < _advices.length(); i++) {
				JSONObject _advice = _advices.getJSONObject(i);
				Advice advice = new Advice();
				advices.add(advice);

				if (!_advice.isNull("advicecode")) {
					advice.setAdviceCode(_advice.getInt("advicecode"));
				}
				if (!_advice.isNull("subadvicecode")) {
					advice.setSubAdviceCode(_advice.getInt("subadvicecode"));
				}

				if (!_advice.isNull("freetexts")) {
					JSONArray _freeTexts=_advice.getJSONArray("freetexts");
					List<LocalisedShortString> freeTexts=new ArrayList<LocalisedShortString>();
					for(int j=0;j<_freeTexts.length();j++){
						JSONObject _freeText=_freeTexts.getJSONObject(j);
						LocalisedShortString lss=new LocalisedShortString();
						freeTexts.add(lss);
						
						lss.setLanguageCode(_freeText.getInt("languagecode"));
						ShortString ss=new ShortString();
						ss.setBytes(_freeText.getString("freetext"));
						lss.setFreeText(ss);
					}
					advice.setFreeTexts(freeTexts);
				}

				if (!_advice.isNull("vehiclerestrictions")) {
					JSONArray _vehiclerestrictions = _advice.getJSONArray("vehiclerestrictions");
					List<VehicleRestriction> vehiclerestrictions=new ArrayList<VehicleRestriction>();
					for (int j = 0; j < _vehiclerestrictions.length(); j++) {
						JSONObject _vehicleRestriction=_vehiclerestrictions.getJSONObject(j);
						VehicleRestriction vr=new VehicleRestriction();
						if (!_vehicleRestriction.isNull("vehicletype")) {
							vr.setVehicleType(_vehicleRestriction.getInt("vehicletype"));
							vehiclerestrictions.add(vr);
						}
					}
					advice.setVehicleRestrictions(vehiclerestrictions);
				}
			}
			
			redata.setAdvices(advices);
		}
		
		if (!tecdata.isNull("vehiclerestrictions")) {
			JSONArray _vehiclerestrictions = tecdata.getJSONArray("vehiclerestrictions");
			List<VehicleRestriction> vehiclerestrictions=new ArrayList<VehicleRestriction>();
			for (int j = 0; j < _vehiclerestrictions.length(); j++) {
				JSONObject _vehicleRestriction=_vehiclerestrictions.getJSONObject(j);
				VehicleRestriction vr=new VehicleRestriction();
				if (!_vehicleRestriction.isNull("vehicletype")) {
					vr.setVehicleType(_vehicleRestriction.getInt("vehicletype"));
					vehiclerestrictions.add(vr);
				}
			}
			redata.setVehicleRestrictions(vehiclerestrictions);
		}

		// JSONObject _cause = tecdata.getJSONObject("cause");
		// Cause cause = new Cause();
		// redata.setCauses(cause);
		//
		// cause.setMainCause(_cause.getInt("maincause"));
		// cause.setWarningLevel(_cause.getInt("warninglevel"));
		// if (!_cause.isNull("subcause") ) {
		// cause.setSubCause(_cause.getInt("subcause"));
		// }
		// if (!_cause.isNull("lengthaffected") ) {
		// cause.setLengthAffected(_cause.getInt("lengthaffected"));
		// }
		// if (!_cause.isNull("lanerestrictiontype") ) {
		// cause.setLaneRestrictionType(_cause.getInt("lanerestrictiontype"));
		// }
		// if (!_cause.isNull("numberoflanes") ) {
		// cause.setNumberOfLanes(_cause.getInt("numberoflanes"));
		// }
		return redata;
	}

}
