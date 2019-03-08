package com.idatrix.unisecurity.user.service;

/**
 * 登录页前置处理器
 */
public interface IPreLoginHandler {

	// 验证码的key
	String SESSION_ATTR_NAME = "login_session_attr_name";

	/**
	 * 前置处理，获取验证码
	 */
	String handle() throws Exception;

	/**
	 * 校验验证码
	 * @param code
	 * @return
	 */
	Boolean verifyCode(String code);

}
