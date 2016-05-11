package cn.com.cennavi.visualizer.service.simulaterequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import sun.misc.BASE64Encoder;
import cn.com.cennavi.kfgis.framework.view.resultobj.JsonResult;
import cn.com.cennavi.visualizer.service.simulaterequest.SimulateRequestController.GetMessageParams;
import cn.com.cennavi.visualizer.service.simulaterequest.SimulateRequestController.SimulateReqMIB2Params;
import cn.com.cennavi.visualizer.service.simulaterequest.SimulateRequestController.SimulateReqParams;

@Service
public class SimulateRequestServiceImpl implements ISimulateRequestService {
		
	public JsonResult initSessionDailmer(SimulateReqParams params,HttpServletRequest request, HttpServletResponse response){
		
		String postStr = "";
		JSONObject json = new JSONObject();
		Integer[] ltns = new Integer[]{32,1,3,4,5,6,7,8,9,10,11,12,14,15,16,18,20,23,24,25,26,27,28,29,30,31};
		try {
			List<String> locationTables = new ArrayList<String>();
			
			String lpVersion = params.getLpinfoVersion();
			lpVersion = lpVersion.substring(0,2) + ".0";
			
			for(int i = 0;i < ltns.length;i++){
				locationTables.add("C_"+ ltns[i] +"_" + lpVersion);
			}
			
			//json.put("locationTables", new String[]{"C_32_13.0"});
			json.put("innerRadius", params.getInnerRadius());
			json.put("outerRadius", params.getOuterRadius());
			json.put("tmcPlr", params.getTmcPlr());
			json.put("tmcFreeFlow", params.getTmcFreeFlow());
			json.put("jamfrontwarning", params.getJamfrontWarning());
			json.put("openLR", params.getOpenlr());
			json.put("maxMessages", 2000);
			json.put("locationTables",locationTables);
			json.put("tfp",true);
			
			postStr = json.toString();
			System.out.println("request json: " + json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String serviceUrl = params.getServiceUrl();
		
		HttpClient client = new HttpClient();
		//client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);
        //client.getHttpConnectionManager().getParams().setSoTimeout(10000);
		PostMethod post = new PostMethod(serviceUrl);
		post.setRequestHeader("Connection", "close");
		post.addRequestHeader("Content-Type", "application/json");
		
		StringRequestEntity entiey = new StringRequestEntity(postStr);
	    post.setRequestEntity(entiey);
	    String postResult = "";
	    
	    try{
	    	client.executeMethod(post);
	    	if (post.getStatusCode() == HttpStatus.SC_OK || post.getStatusCode() == 201) {
	    		postResult = post.getResponseBodyAsString();
	    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	    JsonResult jsonResult = new JsonResult();
		jsonResult.setJsonObj(postResult);
		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		
		return jsonResult;
	}
	
	public JsonResult initSessionMIB2(SimulateReqMIB2Params params,HttpServletRequest request, HttpServletResponse response){
		String serviceUrl = params.getServiceUrl();
		String postStr = params.getPostXml();
		
		HttpClient client = new HttpClient();
		
		//client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        //client.getHttpConnectionManager().getParams().setSoTimeout(10000);
        
		PostMethod post = new PostMethod(serviceUrl);
		post.setRequestHeader("Connection", "close");
		post.addRequestHeader("Content-Type", "application/xml");
		
		StringRequestEntity entiey = new StringRequestEntity(postStr);
	    post.setRequestEntity(entiey);
	    String postResult = "";
	    JSONObject json = new JSONObject();
	    try{
	    	client.executeMethod(post);
	    	if (post.getStatusCode() == HttpStatus.SC_OK || post.getStatusCode() == 201) {
	    		postResult = post.getResponseBodyAsString();
	    		System.out.println(postResult);
	    		
	    		Document doc = DocumentHelper.parseText(postResult);
    	        Element rootElement = doc.getRootElement();
    	        
    	        String url = rootElement.elementTextTrim("url");
    	        String sessionId = rootElement.elementTextTrim("sessionId");
    	        
    	        json.put("url", url);
    	        json.put("sessionId", sessionId);
	    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		JsonResult jsonResult = new JsonResult();
		jsonResult.setJsonObj(json.toString());
		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		
		return jsonResult;
	}
	
	public JsonResult getMessageDailmer(GetMessageParams params,HttpServletRequest request, HttpServletResponse response){
		
		String postStr = "";
		JSONObject json = new JSONObject();
		try {
			Map<String,Double> posMap = new HashMap<String,Double>();
			posMap.put("lon", params.getLon());
			posMap.put("lat", params.getLat());
			json.put("pos", posMap);
			
			postStr = json.toString();
			System.out.println("request json: " + json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String serviceUrl = params.getGetMessageUrl();
		serviceUrl = serviceUrl.replace("https", "http");
		serviceUrl = serviceUrl.replace(":42843", ":10080");
		
		HttpClient client = new HttpClient();
		//client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        //client.getHttpConnectionManager().getParams().setSoTimeout(10000);
		BASE64Encoder encoder = new BASE64Encoder();
		PostMethod post = new PostMethod(serviceUrl);
		post.setRequestHeader("Connection", "close");
		post.addRequestHeader("Content-Type", "application/json");
		//post.addRequestHeader("X-Session-ID", params.getSessionId());
		//post.addRequestHeader("Authorization", encoder.encode((params.getSessionId() + ":").getBytes()));
		//System.out.println("Authorization : " + encoder.encode((params.getSessionId() + ":").getBytes()));
		//System.out.println(params.getSessionId());
		StringRequestEntity entiey = new StringRequestEntity(postStr);
	    post.setRequestEntity(entiey);
	    String postResult = "";
	    
	    InputStream stream = null;
	    ByteArrayOutputStream swapStream = null;
	    try{
	    	client.executeMethod(post);
	    	if (post.getStatusCode() == HttpStatus.SC_OK || post.getStatusCode() == 201) {
	    		
	    		stream = post.getResponseBodyAsStream();
	    		
	    		swapStream = new ByteArrayOutputStream(); 
	    		byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据 
	    		int rc = 0;
	    		
	    		while ((rc = stream.read(buff, 0, 100)) > 0) { 
	    			swapStream.write(buff, 0, rc); 
	    		} 
	    		byte[] in_b = swapStream.toByteArray(); //in_b为转换之后的结果 
	    		
	    		//将返回结果转为base64编码数据
	    		postResult = encoder.encode(in_b);
	    		
	    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	if(stream != null){
	    		try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    	if(swapStream != null){
	    		try {
	    			swapStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	    
	    JsonResult jsonResult = new JsonResult();
		jsonResult.setJsonObj(postResult);
		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		
		return jsonResult;
	}
	
	
	public JsonResult getMessageMib2(GetMessageParams params,HttpServletRequest request, HttpServletResponse response){
		
		String serviceUrl = params.getGetMessageUrl();
		
		StringBuffer postXml = new StringBuffer(200);
		postXml.append("<getMessages version=\"1\">");
		postXml.append("<sessionId>" + params.getSessionId() + "</sessionId>");
		postXml.append("<locations><loc><order>0</order>");
		postXml.append("<lat>" + params.getLat() + "</lat>");
		postXml.append("<lon>" + params.getLon() + "</lon>");
		postXml.append("<bearing>0</bearing>");
		postXml.append("<isStopOver>false</isStopOver>");
		postXml.append("</loc></locations>");
		postXml.append("<heading>47</heading><roaming>false</roaming>");
		postXml.append("<fcdList><fcd><timestamp>20100505T134711.3Z</timestamp><lat>48.141111</lat><lon>11.569167</lon><heading>192</heading>");
		postXml.append("<velocity unit=\"kmh\">53</velocity>");
		postXml.append("<temp unit=\"C\">18</temp>");
		postXml.append("<rain>4</rain><frc>2</frc><fow>2</fow></fcd></fcdList>");
		postXml.append("</getMessages> ");
		
		System.out.println(serviceUrl);
		System.out.println(postXml);
		
		HttpClient client = new HttpClient();
		//client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        //client.getHttpConnectionManager().getParams().setSoTimeout(10000);
        
		PostMethod post = new PostMethod(serviceUrl);
		post.setRequestHeader("Connection", "close");
		post.addRequestHeader("Content-Type", "application/xml");
		//post.addRequestHeader("X-Session-ID", params.getSessionId());
		
		StringRequestEntity entiey = new StringRequestEntity(postXml.toString());
	    post.setRequestEntity(entiey);
	    String postResult = "";
	    
	    InputStream stream = null;
	    ByteArrayOutputStream swapStream = null;
	    try{
	    	client.executeMethod(post);
	    	System.out.println(post.getStatusCode());
	    	if (post.getStatusCode() == HttpStatus.SC_OK || post.getStatusCode() == 201) {
	    		
	    		//将返回结果转为base64编码数据
	    		stream = post.getResponseBodyAsStream();
	    		swapStream = new ByteArrayOutputStream(); 
	    		byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据 
	    		int rc = 0;
	    		
	    		while ((rc = stream.read(buff, 0, 100)) > 0) { 
	    			swapStream.write(buff, 0, rc); 
	    		} 
	    		byte[] in_b = swapStream.toByteArray(); //in_b为转换之后的结果 
	    		
	    		BASE64Encoder encoder = new BASE64Encoder();
	    		postResult = encoder.encode(in_b);
	    		
	    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	if(stream != null){
	    		try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    	if(swapStream != null){
	    		try {
	    			swapStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	    
	    JsonResult jsonResult = new JsonResult();
		jsonResult.setJsonObj(postResult);
		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		
		return jsonResult;
	}

	@Override
	public JsonResult initSessionRenault(SimulateReqMIB2Params params, HttpServletRequest request, HttpServletResponse response) {
		String serviceUrl = params.getServiceUrl();
		String postStr = params.getPostXml();
		
		HttpClient client = new HttpClient();
		
		//client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        //client.getHttpConnectionManager().getParams().setSoTimeout(10000);
        
		PostMethod post = new PostMethod(serviceUrl);
		post.setRequestHeader("Connection", "close");
		post.addRequestHeader("Content-Type", "application/xml");
		
		StringRequestEntity entiey = new StringRequestEntity(postStr);
	    post.setRequestEntity(entiey);
	    String postResult = "";
	    JSONObject json = new JSONObject();
	    try{
	    	client.executeMethod(post);
	    	if (post.getStatusCode() == HttpStatus.SC_OK || post.getStatusCode() == 201) {
	    		postResult = post.getResponseBodyAsString();
	    		System.out.println(postResult);
	    		
	    		JSONObject j=new JSONObject(postResult);
    	        
    	        String url = j.getString("getMessagesURL");
    	        String sessionId = j.getString("sessionID");
    	        
    	        json.put("url", url);
    	        json.put("sessionId", sessionId);
	    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		
		JsonResult jsonResult = new JsonResult();
		jsonResult.setJsonObj(json.toString());
		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		
		return jsonResult;
	}

	@Override
	public JsonResult getMessageRenault(GetMessageParams params, HttpServletRequest request, HttpServletResponse response) {
		String postStr = "";
		JSONObject json = new JSONObject();
		try {
			Map<String,Double> posMap = new HashMap<String,Double>();
			posMap.put("lon", params.getLon());
			posMap.put("lat", params.getLat());
			json.put("pos", posMap);
			
			postStr = json.toString();
			System.out.println("request json: " + json.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String serviceUrl = params.getGetMessageUrl();
		//serviceUrl = serviceUrl.replace("https", "http");
		//serviceUrl = serviceUrl.replace(":42843", ":10080");
		
		HttpClient client = new HttpClient();
		//client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        //client.getHttpConnectionManager().getParams().setSoTimeout(10000);
		BASE64Encoder encoder = new BASE64Encoder();
		PostMethod post = new PostMethod(serviceUrl);
		post.setRequestHeader("Connection", "close");
		post.addRequestHeader("Content-Type", "application/json");
		post.addRequestHeader("tID", "1");
		//post.addRequestHeader("X-Session-ID", params.getSessionId());
		//post.addRequestHeader("Authorization", encoder.encode((params.getSessionId() + ":").getBytes()));
		//System.out.println("Authorization : " + encoder.encode((params.getSessionId() + ":").getBytes()));
		//System.out.println(params.getSessionId());
		StringRequestEntity entiey = new StringRequestEntity(postStr);
	    post.setRequestEntity(entiey);
	    String postResult = "";
	    
	    InputStream stream = null;
	    ByteArrayOutputStream swapStream = null;
	    try{
	    	client.executeMethod(post);
	    	if (post.getStatusCode() == HttpStatus.SC_OK || post.getStatusCode() == 201) {
	    		
	    		//将返回结果转为base64编码数据
	    		stream = post.getResponseBodyAsStream();
	    		
	    		swapStream = new ByteArrayOutputStream(); 
	    		byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据 
	    		int rc = 0;
	    		
	    		while ((rc = stream.read(buff, 0, 100)) > 0) { 
	    			swapStream.write(buff, 0, rc); 
	    		} 
	    		byte[] in_b = swapStream.toByteArray(); //in_b为转换之后的结果 
	    		
	    		postResult = encoder.encode(in_b);
	    		
	    	}else{
	    		//将返回结果转为base64编码数据
	    		stream = post.getResponseBodyAsStream();
	    		
	    		swapStream = new ByteArrayOutputStream(); 
	    		byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据 
	    		int rc = 0;
	    		
	    		while ((rc = stream.read(buff, 0, 100)) > 0) { 
	    			swapStream.write(buff, 0, rc); 
	    		} 
	    		byte[] in_b = swapStream.toByteArray(); //in_b为转换之后的结果 
	    		
	    		postResult = encoder.encode(in_b);
	    		String s=new String(postResult);
	    		System.out.println(s);
	    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	if(stream != null){
	    		try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    	if(swapStream != null){
	    		try {
	    			swapStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	    
	    JsonResult jsonResult = new JsonResult();
		jsonResult.setJsonObj(postResult);
		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		
		return jsonResult;
	}

	@Override
	public JsonResult getMessageBmw(GetMessageParams params, HttpServletRequest request, HttpServletResponse response) {
		
		String serviceUrl = params.getGetMessageUrl();
		
		HttpClient client = new HttpClient();
		//client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
        //client.getHttpConnectionManager().getParams().setSoTimeout(10000);
		
		GetMethod get=new GetMethod(serviceUrl);
		
//		PostMethod post = new PostMethod(serviceUrl);
		get.setRequestHeader("Connection", "close");
		get.addRequestHeader("Content-Type", "application/json");
		
	    String xml = "";
	    InputStream stream = null;
	    ByteArrayOutputStream swapStream = null;
	    try{
	    	client.executeMethod(get);
	    	if (get.getStatusCode() == HttpStatus.SC_OK || get.getStatusCode() == 201) {
	    		
	    		stream = get.getResponseBodyAsStream();
	    		
	    		swapStream = new ByteArrayOutputStream(); 
	    		byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据 
	    		int rc = 0;
	    		
	    		while ((rc = stream.read(buff, 0, 100)) > 0) { 
	    			swapStream.write(buff, 0, rc); 
	    		} 
	    		byte[] in_b = swapStream.toByteArray(); //in_b为转换之后的结果 
	    		xml = new String(in_b,"UTF-8");
	    		 SAXReader reader = new SAXReader();
	    		 Document doc= reader.read(new ByteArrayInputStream(xml.getBytes("UTF-8")));
	    		 xml= doc.getRootElement().element("TransportFrame").getTextTrim();
	    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }finally{
	    	if(stream != null){
	    		try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    	if(swapStream != null){
	    		try {
	    			swapStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    	}
	    }
	    
	    JsonResult jsonResult = new JsonResult();
		jsonResult.setJsonObj(xml);
		jsonResult.setCallback(params.getCallback());
		jsonResult.setContent_type("application/json");
		jsonResult.setEncoding("utf-8");
		
		return jsonResult;
	}
}
