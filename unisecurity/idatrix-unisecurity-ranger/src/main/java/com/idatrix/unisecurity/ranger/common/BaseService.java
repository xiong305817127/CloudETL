package com.idatrix.unisecurity.ranger.common;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.idatrix.unisecurity.ranger.common.vo.RangerBaseVO;

public abstract class BaseService {
	private static final Logger logger = Logger.getLogger(BaseService.class);
	protected static RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(60000)
			.setConnectionRequestTimeout(30000).setSocketTimeout(60000).build();

	private String username;
	private String passwd; 

	private String url;

	private String RangerAdminSession; // RANGERADMINSESSIONID;

	protected Gson gson = new Gson();

	public BaseService(String username, String passwd, String url) {
		this.url = url;
		this.username = username;
		this.passwd = passwd;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	protected CloseableHttpResponse excute(HttpRequestBase request) throws Exception {
		request.setConfig(requestConfig);
		try {
			if (StringUtils.isEmpty(this.RangerAdminSession)) {
				// 使用管理员帐号与密码，登录ranger
				this.login(this.username, this.passwd);
			}

			setHeader(request);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse response = httpclient.execute(request);

			if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {

				this.login(this.username, this.passwd);
				setHeader(request);
				response = httpclient.execute(request);
			}

			return response;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new Exception("Ranger requst error!");
		}
	}

	protected void setHeader(HttpRequestBase request) {
		request.setHeader("Content-Type", "application/json;charset=UTF-8");
		request.setHeader("Cookie", "RANGERADMINSESSIONID=" + this.RangerAdminSession);
	}

	public synchronized boolean login(String username, String password) throws Exception {
		logger.info("ranger login ...");
		String loginUrl = this.url + RangerStaticInfo.RANGER_LOGIN_URL;

		try {

			HttpPost httpPost = new HttpPost(loginUrl);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("j_username", username));
			nvps.add(new BasicNameValuePair("j_password", password));
			HttpEntity reqEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
			httpPost.setEntity(reqEntity);
			CloseableHttpClient httpclient = HttpClients.createDefault();
			CloseableHttpResponse response = httpclient.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
				throw new Exception("Ranger login failed!");
			}

			String RANGERADMINSESSIONID = getHeader(response, "RANGERADMINSESSIONID");

			if (StringUtils.isEmpty(RANGERADMINSESSIONID)) {
				throw new Exception("Ranger login failed!");
			}

			HttpEntity responseEntity = response.getEntity();
			String retStr = EntityUtils.toString(responseEntity);
			RangerBaseVO retObject = gson.fromJson(retStr, RangerBaseVO.class);
			if (retObject.getStatusCode() != HttpServletResponse.SC_OK) {
				throw new Exception("Ranger login failed!");
			}
			this.RangerAdminSession = RANGERADMINSESSIONID;

			logger.info("ranger login success:" + retStr);
			EntityUtils.consume(responseEntity);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new Exception("Ranger login error!");
		}

		return true;
	}

	protected String getHeader(CloseableHttpResponse response, String name) {
		String RANGERADMINSESSIONID = null;
		Header header = response.getFirstHeader("Set-Cookie");
		if (null != header) {
			String value = header.getValue();
			if (StringUtils.isNotEmpty(value)) {
				String[] values = value.split(";", value.length());
				for (String headerValue : values) {
					String[] headerKV = headerValue.split("=");
					if (name.equals(headerKV[0])) {
						RANGERADMINSESSIONID = headerKV[1];
						break;
					}
				}
			}
		}

		return RANGERADMINSESSIONID;
	}

	protected void checkResult(RangerBaseVO baseVO) throws Exception {

		if (null == baseVO.getStatusCode() || baseVO.getStatusCode() == 0) {
			return;
		}
		throw new Exception(baseVO.getMsgDesc());
	}

	protected RangerBaseVO getResultVO(HttpEntity responseEntity, String retStr) {
		if (StringUtils.isEmpty(retStr)) {
			return null;
		}

		if (responseEntity.getContentType() != null
				&& responseEntity.getContentType().toString().toLowerCase().indexOf("application/xml") >= 0) {
			RangerBaseVO retVO = new RangerBaseVO();
			try {
				StringReader sr = new StringReader(retStr);
				InputSource is = new InputSource(sr);
				Document doc = (new SAXBuilder()).build(is);
				Element rootElement = doc.getRootElement();
				String statusCode = rootElement.getChildText("statusCode");
				if (StringUtils.isNotEmpty(statusCode)) {
					retVO.setStatusCode(Integer.valueOf(statusCode));
				}

				String msgDesc = rootElement.getChildText("msgDesc");
				if (StringUtils.isNotEmpty(msgDesc)) {
					retVO.setMsgDesc(msgDesc);
				}

				Element messageList = rootElement.getChild("messageList");
				if (null != messageList) {
					String name = messageList.getChildText("name");
					retVO.setMsgName(name);
				}

				return retVO;

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		if (responseEntity.getContentType() != null
				&& responseEntity.getContentType().toString().toLowerCase().indexOf("application/json") >= 0) {
			return gson.fromJson(retStr, RangerBaseVO.class);
		}

		return null;
	}

}
