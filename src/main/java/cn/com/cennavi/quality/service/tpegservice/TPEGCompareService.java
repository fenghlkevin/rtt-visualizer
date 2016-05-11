package cn.com.cennavi.quality.service.tpegservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import cn.com.cennavi.framework.httpput.param.SessionParam;
import cn.com.cennavi.kfgis.framework.configure.ConfigureFactory;
import cn.com.cennavi.kfgis.framework.couchbase.CBClusterManager;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;

@Service
public class TPEGCompareService {

	public JsonResult compare(TPEGParams params, HttpServletRequest request, HttpServletResponse response) {

		SessionParam sessionParam = (SessionParam) ConfigureFactory.getInstance().getConfigure(SessionParam.class);
		CBClusterManager manager = CBClusterManager.getInstance(sessionParam.getSession_db());

		String[] keys = params.getLockeys().split(",");

		List<TPEGCompareBean> revalues = new ArrayList<TPEGCompareBean>();
		for (String _key : keys) {
			//String[] temp = _key.split("_");
			// time_locid_adcode_direction//+version
			String cbkey = params.getTimestamp() + "_" + _key;
			//TODO 处理每行内容
			
			String[][][] data = (String[][][]) manager.get(cbkey);
			if(data!=null){
				TPEGCompareBean tcb=new TPEGCompareBean();
				tcb.setLocid(_key);
				for(int i=0;i<data.length;i++){
					String[][] onedata=data[0];
					tcb.getEncoder_tfps().add(this.getTfp(onedata[0]));
					tcb.getTe_tfps().add(this.getTfp(onedata[1]));
					if(i==0){
						//i=0时候，有cntf
						String[] allcntf=onedata[2];
						for(String onecntf:allcntf){
							String[] cntf=onecntf.split("@");
							tcb.getTe_tfps().add(this.getCNTF(cntf));
						}
					}
				}
				revalues.add(tcb);
			}
		}
		JsonResult jsonResult = new JsonResult();

		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		jsonResult.setJsonObj(revalues);
		return jsonResult;

	}
	
	private Map<String,Object> getTfp(String[] args){
		Map<String,Object> revalue=new HashMap<String, Object>();
		revalue.put("los", args[0]);
		revalue.put("speed", args[1]);
		revalue.put("length", args[2]);
		return revalue;
	}
	
	private Map<String,Object> getCNTF(String[] args){
		Map<String,Object> revalue=new HashMap<String, Object>();
		revalue.put("linkid", args[0]);
		revalue.put("los", args[1]);
		revalue.put("speed", args[2]);
		revalue.put("length", args[3]);
		return revalue;
	}
	
	public static class TPEGCompareBean{
		
		private String locid;
		
		/**
		 * los,speed,length,shape
		 */
		private List<Map<String,Object>> encoder_tfps=new ArrayList<Map<String,Object>>();
		/**
		 * los,speed,length,shape
		 */
		private List<Map<String,Object>> te_tfps=new ArrayList<Map<String,Object>>();
		
		/**
		 * linkid,los,speed,length,shape
		 */
		private List<Map<String,Object>> te_cntfs=new ArrayList<Map<String,Object>>();

		public String getLocid() {
			return locid;
		}

		public void setLocid(String locid) {
			this.locid = locid;
		}

		public List<Map<String, Object>> getEncoder_tfps() {
			return encoder_tfps;
		}

		public void setEncoder_tfps(List<Map<String, Object>> encoder_tfps) {
			this.encoder_tfps = encoder_tfps;
		}

		public List<Map<String, Object>> getTe_tfps() {
			return te_tfps;
		}

		public void setTe_tfps(List<Map<String, Object>> te_tfps) {
			this.te_tfps = te_tfps;
		}

		public List<Map<String, Object>> getTe_cntfs() {
			return te_cntfs;
		}

		public void setTe_cntfs(List<Map<String, Object>> te_cntfs) {
			this.te_cntfs = te_cntfs;
		}
	}
}
/**
 * { "locid": 11, "tpeg": [ { "los": 1, "speed": 1, "length": 1 }, { "los": 2,
 * "speed": 2, "length": 2 } ], "te_tfp": [ { "los": 1, "speed": 1, "length": 1
 * }, { "los": 2, "speed": 2, "length": 2 } ], "te_cntf": [ { "linkid": 1,
 * "los": 1, "speed": "1", "length": 1 } ] }
 */

