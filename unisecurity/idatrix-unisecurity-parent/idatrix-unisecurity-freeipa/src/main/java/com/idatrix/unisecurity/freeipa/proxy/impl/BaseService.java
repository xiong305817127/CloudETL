package com.idatrix.unisecurity.freeipa.proxy.impl;

import com.google.gson.Gson;
import com.idatrix.unisecurity.freeipa.model.FreeIPATemplate;
import com.idatrix.unisecurity.freeipa.proxy.factory.LdapHttpDataBuilder;
import com.idatrix.unisecurity.freeipa.util.SessionManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;


public abstract class BaseService {

	private static final Logger logger = Logger.getLogger(BaseService.class);
	
	
	protected static final RequestConfig requestConfig = RequestConfig.custom()
			.setConnectTimeout(60000).setConnectionRequestTimeout(30000)
			.setSocketTimeout(60000).build();

	protected Gson gson = new Gson();

	@Autowired
	protected FreeIPATemplate ipaTmpl;

	@Autowired
	protected LdapHttpDataBuilder ldapHttpDataBuilder;
	
	public BaseService(FreeIPATemplate rjdata, com.idatrix.unisecurity.freeipa.proxy.factory.LdapHttpDataBuilder builder){
		this.ipaTmpl = rjdata;
		this.ldapHttpDataBuilder = builder;
	}
	

	/**
	 * 向freeipa发送消息。但在发送消息之前会判断是否已经执行login动作
	 * @param username : 执行login()的用户名，如果为空，则使用默认的管理员用户。
	 * @param password
	 * @param request  : 需要向freeipa发送的消息
	 * @return
	 * @throws Exception
	 */
	protected CloseableHttpResponse excute(String username, String password,
			HttpRequestBase request) throws Exception {
		request.setConfig(requestConfig);
		try {
			//判断是否已经调用过login()函数，, 每次连接ipa都重新连接，获取cookie
//			if (StringUtils.isEmpty(SessionManager.getInstance()
//					.getIpa_session())) {
				this.synLogins(username == null ? ipaTmpl.getAdminUser()
						: username,
						password == null ? ipaTmpl.getAdminPwd()
								: password);
//			}
			//设置消息头
			setHeader(request);
			CloseableHttpClient httpclient = BaseService
					.createSSLClientDefault();
			CloseableHttpResponse response = httpclient.execute(request);
			
			if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
				this.synLogins(username == null ? ipaTmpl.getAdminUser()
						: username,
						password == null ? ipaTmpl.getAdminPwd()
								: password);
				setHeader(request);
				response = httpclient.execute(request);
			}
			return response;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new Exception( "Ldap requst error!");
		}
	}

	protected CloseableHttpResponse excute(HttpRequestBase request)
			throws Exception {
		return this.excute(null, null, request);
	}

	/**
	 * freeipa全部是通过rest的方式进行操作。但是freeipa会对消息头进行校验，这其中特别是ipa_session
	 * 用户执行login()之后，可以获取此参数。freeipa会将此保存在cookie中,会对此参数进行校验
	 * @param request
	 */
	private void setHeader(HttpRequestBase request) {
		request.setHeader("Content-Type", "application/json;charset=UTF-8");
		request.setHeader("Cookie", "ipa_session="
				+ SessionManager.getInstance().getIpa_session());
		request.setHeader("Referer", ipaTmpl.getReferUrl());
	}


	public synchronized boolean synLogins(String username, String password)
			throws Exception {
		return logins(username, password);
	}

	/**
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public boolean logins(String username, String password) throws Exception {
		logger.info("ldap login ...");

		String loginUrl = ipaTmpl.getLogin_url();

		CloseableHttpClient httpsclient = createSSLClientDefault();
		HttpPost httpPost = new HttpPost(loginUrl);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", username));
		nvps.add(new BasicNameValuePair("password", password));
		System.out.println("loginUrl:" + loginUrl + " " + username);
		logger.info("loginUrl:" + loginUrl);
		logger.info("TTTTTTTTTTTTTT --------- user:" + username +", passowrd:" + password);

		HttpEntity reqEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
		httpPost.setEntity(reqEntity);

		CloseableHttpResponse response = httpsclient.execute(httpPost);
//		System.out.println(response);
		if (response.getStatusLine().getStatusCode() == HttpServletResponse.SC_UNAUTHORIZED) {
			throw new Exception(
					"Ldap login failed!user not exist or password error or password exipire." );

		}

		String ipa_session = getHeader(response, "ipa_session");

		if (StringUtils.isEmpty(ipa_session)) {
			throw new Exception("Ldap login failed!");
		}
		//保存ipa_session，后续的执行操作需要使用此参数
		SessionManager.getInstance().setIpa_session(ipa_session);

		HttpEntity responseEntity = response.getEntity();
		String retStr = EntityUtils.toString(responseEntity);
		logger.info("ldap login success:" + retStr);
		EntityUtils.consume(responseEntity);
		// httpclient.close();
		return true;
	}
	
	public boolean changePasswd(String username, String oldPwd, String newPwd) throws ClientProtocolException, IOException {
		logger.info("Begin to change password....");
		String changeUrl = this.ipaTmpl.getChangePwdUrl();// "https://freeipa56.example.com/ipa/session/change_password";
		CloseableHttpClient httpsclient = createSSLClientDefault();
		HttpPost httPost =  new HttpPost(changeUrl);
		List<NameValuePair> nvps =  new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("user", username));
		nvps.add(new BasicNameValuePair("old_password", oldPwd));
		nvps.add(new BasicNameValuePair("new_password", newPwd));
		HttpEntity reqEntity = new UrlEncodedFormEntity(nvps, Consts.UTF_8);
		httPost.setEntity(reqEntity);
		
		CloseableHttpResponse response = httpsclient.execute(httPost);
		
		HttpEntity responseEntity = response.getEntity();
		String retStr = EntityUtils.toString(responseEntity);
		logger.info("password: " + retStr);
		EntityUtils.consume(responseEntity);
		httPost.releaseConnection();
		httpsclient.close();
		
		return true;
	}
	

	@SuppressWarnings("deprecation")
	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						// 信任所有
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}
					}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}

	private String getHeader(CloseableHttpResponse response, String name) {
		String sessionID = null;
		Header header = response.getFirstHeader("Set-Cookie");
		if (null != header) {
			String value = header.getValue();
			if (StringUtils.isNotEmpty(value)) {
				String[] values = value.split(";", value.length());
				for (String headerValue : values) {
					String[] headerKV = headerValue.split("=");
					if (name.equals(headerKV[0])) {
						sessionID = headerKV[1];
						break;
					}
				}
			}
		}

		return sessionID;
	}

}
