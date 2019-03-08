package com.ys.idatrix.metacube.common.utils;

import java.io.UnsupportedEncodingException;
import org.apache.commons.codec.binary.Base64;

/**
 * Base64编码解码
 *
 * @author wzl
 */
public class Base64Utils {

    public static String encode(String str) {
        String result = null;
        try {
            result = new Base64().encodeToString(str.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String decode(String str) {
        return new String(Base64.decodeBase64(str));
    }

    public static void main(String[] args) {
        String s1 = "Basic ";
        String encodeResult = "cXVlcnlfY2x1c3RlcmluZm86cXVlcnlfY2x1c3RlcmluZm8=";
        String decodeResult = "query_clusterinfo:query_clusterinfo";

        System.out.println(Base64Utils.decode(encodeResult));
        System.out.println(s1 + Base64Utils.encode(decodeResult));
    }
}