package com.idatrix.unisecurity.common.sso;

import sun.misc.BASE64Decoder;

import java.io.UnsupportedEncodingException;

@SuppressWarnings("restriction")
public class SecurityUtils {

	/**
     * 将 BASE64 编码的字符串 s 进行加密
     **/
    public static String encrypt(String str, boolean... bf) {
        if (str==null || str=="")
        	return null;
        
        String base64 = null;
        try {
            base64 = new sun.misc.BASE64Encoder().encode(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        
        //去掉 '='
        if (isBlank(bf)  && bf[0]) {
            base64 = base64.replaceAll("=", "");
        }
        return base64;
    }
    
    public static boolean isBlank(Object... objects) {
        Boolean result = false;
        for (Object object : objects) {
            if (null == object || "".equals(object.toString().trim())
                    || "null".equals(object.toString().trim())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 将 BASE64 编码的字符串 s 进行解码
     **/
    public static String  decrypt(String s) {
        if (s==null ||s=="") 
        	return "";
        
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b,"UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
    
    public static void main(String[] args) {
		String result = "Root_12345678";
		result = encrypt(result);
		System.out.println(result);
	}
}