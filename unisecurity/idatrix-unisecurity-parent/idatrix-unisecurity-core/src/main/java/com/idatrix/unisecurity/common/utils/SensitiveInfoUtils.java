package com.idatrix.unisecurity.common.utils;

import org.apache.commons.lang.StringUtils;

/**
 * @author Administrator
 */
public class SensitiveInfoUtils {

    public static String getSensitiveInfo(String info, int begin, int end, String symbol) {
        if (StringUtils.isBlank(info)) {
            return "";
        }

        if (StringUtils.isBlank(symbol)) {
            symbol = "*";
        }

        String biginStr = StringUtils.left(info, begin);
        String endStr = StringUtils.right(info, end);

        return StringUtils.rightPad(biginStr, end, symbol).concat(endStr);
    }

    public static void main(String[] args) {

        String str1 = "1232222222222222222222222000000000000000000000000000000000008888888888888888888888"
                + "88888888888888888888888888888888888888888888888888822222222222222222222"
                + "222222222222222222222222222222222222222222222222222222222222222222222222222222222"
                + "22222222222222222222222222222222222222222222212333333330000000000000000000000000000000";

        String str2 = "1232222222222222222222222000000000000000000000000000000000008888888888888888888888"
                + "88888888888888888888888888888888888888888888888888822222222222222222222"
                + "222222222222222222222222222222222222222222222222222222222222222222222222222222222"
                + "22222222222222222222222222222222222222222222212333333330000000000000000000000000000000" +
                "111111111111000000000000000000000000000000000000000000000000000000000000000000000000000000";
        Long begin = System.currentTimeMillis();
        String str = str1 + str2;
        Long end = System.currentTimeMillis();

        System.out.println("1st time" + (end - begin));


        Long begin2 = System.currentTimeMillis();
        String str6 = str1.concat(str2);
        Long end2 = System.currentTimeMillis();

        System.out.println("2nd time" + (end2 - begin2));
    }
}
