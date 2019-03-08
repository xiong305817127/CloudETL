package com.hdfs;

import java.io.IOException;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class HDFSLoginContext {
	private static LoginContext context = null;

	public static Subject getSubject() {
		if(context == null){
			HDFSLoginContext.login();
		}
		return context.getSubject();
	}

	public static void login() {
		System.setProperty("java.security.krb5.realm", "CMSZ.COM");
		System.setProperty("java.security.krb5.kdc", "hdp19.cmsz.com");

		Krb5Configuration conf = new Krb5Configuration();
		try {
			context = new LoginContext("myKerberosLogin", new Subject(),
					HDFSLoginContext.createJaasCallbackHandler("huf@CMSZ.COM",
							"huf"), conf);
			context.login();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	public static CallbackHandler createJaasCallbackHandler(
			final String principal, final String password) {
		return new CallbackHandler() {
			@Override
			public void handle(Callback[] callbacks) throws IOException,
					UnsupportedCallbackException {

				for (Callback callback : callbacks) {
					if (callback instanceof NameCallback) {
						NameCallback nameCallback = (NameCallback) callback;
						nameCallback.setName(principal);
					} else if (callback instanceof PasswordCallback) {
						PasswordCallback passwordCallback = (PasswordCallback) callback;
						passwordCallback.setPassword(password.toCharArray());
					} else {
						throw new UnsupportedCallbackException(callback,
								"Unsupported callback: "
										+ callback.getClass()
												.getCanonicalName());
					}
				}
			}
		};
	}

}
