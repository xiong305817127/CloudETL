package com.idatrix.resource.common.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;


public class HttpUtils {

	private static final CloseableHttpClient httpClient;
	private static RequestConfig requestConfig;

	private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);

	static {
		//设置HTTP状态参数
		requestConfig = RequestConfig.custom()
				.setSocketTimeout(5000).setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).build();

		httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
	}

	public static HttpUriRequest getRequestMethod(Map<String, String> map, String url, String method) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Set<Map.Entry<String, String>> entrySet = map.entrySet();
		for (Map.Entry<String, String> e : entrySet) {
			String name = e.getKey();
			String value = e.getValue();
			NameValuePair pair = new BasicNameValuePair(name, value);
			params.add(pair);
		}
		HttpUriRequest reqMethod = null;
		if (CommonConstants.REQUEST_POST_METHOD.equals(method.toUpperCase())) {
			reqMethod = RequestBuilder.post().setUri(url)
					.addParameters(params.toArray(new BasicNameValuePair[params.size()]))
					.setConfig(requestConfig).build();
		} else if (CommonConstants.REQUEST_GET_METHOD.equals(method.toUpperCase())) {
			reqMethod = RequestBuilder.get().setUri(url)
					.addParameters(params.toArray(new BasicNameValuePair[params.size()]))
					.setConfig(requestConfig).build();
		}
		return reqMethod;
	}

	public static HttpEntity getRequestEntity(String url, String requestMethod) throws IOException {

			Map<String, String> map = new HashMap<String, String>();
			map.put("account", "");
			map.put("password", "");

			HttpUriRequest request = getRequestMethod(map, url, requestMethod);
			HttpResponse response = httpClient.execute(request);

			if (response.getStatusLine().getStatusCode() == 200) {
				return response.getEntity();
			} else {
				return null;
			}
	}

	//测试方法 已通过
	public static void main(String args[]) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		map.put("account", "");
		map.put("password", "");

		String url = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx?WSDL";

		HttpUriRequest get = getRequestMethod(map, url, "get");
		HttpResponse response = httpClient.execute(get);

		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			String message = EntityUtils.toString(entity, "utf-8");
			System.out.println(message);
		} else {
			System.out.println("请求失败");
		}
	}
}
