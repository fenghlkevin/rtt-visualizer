package cn.com.cennavi.visualizer.common.olr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Http4Olr {

	//private CloseableHttpClient httpClient;
	public static final String CHARSET = "UTF-8";

	/**
	 * HTTP Post 获取内容
	 * 
	 * @param url
	 *            请求的url地址 ?之前的地址
	 * @param params
	 *            请求的参数
	 * @param charset
	 *            编码格式
	 * @return 页面内容
	 */
	public byte[] doPost(String url, Map<String, String> params, String charset, String xml) {

		List<NameValuePair> pairs = null;
		if (params != null && !params.isEmpty()) {
			pairs = new ArrayList<NameValuePair>(params.size());
			for (Map.Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();
				if (value != null) {
					pairs.add(new BasicNameValuePair(entry.getKey(), value));
				}
			}
		}
		HttpClient client = new HttpClient();   
		PostMethod post = new PostMethod(url);
		post.setRequestHeader("Connection", "close"); 
		StringRequestEntity sen = new StringRequestEntity(xml);
		post.setRequestEntity(sen);
		byte[] bs = null;
		try {
			int statusCode = client.executeMethod(post);
			if (statusCode != 200 && statusCode != 201) {
				throw new RuntimeException("HttpClient,error status code :" + statusCode);
			}
			bs = post.getResponseBody();
			System.out.println("orl http 执行完毕." );
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bs;
	}

}