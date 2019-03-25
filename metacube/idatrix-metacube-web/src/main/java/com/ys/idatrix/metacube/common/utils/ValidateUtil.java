package com.ys.idatrix.metacube.common.utils;

import com.ys.idatrix.metacube.common.exception.MetaDataException;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * 校验工具类
 */
public class ValidateUtil {

    private static final String IP_PATTERN = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
    private static final Pattern pattern = Pattern.compile(IP_PATTERN);
    private static final String HDFS_PATTERN = "^(\\/)[^\\s]+";

    public static void checkIp(String ip) {
        if (StringUtils.isNoneBlank(ip) && !verifyIp(ip)) {
            throw new MetaDataException("ip格式不正确！");
        }
    }

    /**
     * 验证ip的有效性
     */
    public static boolean verifyIp(String ip) {
        return pattern.matcher(ip).matches();
    }

    public static void checkHdfsPath(String path) {
        if (StringUtils.isNoneBlank(path) && !verifyPath(path)) {
            throw new MetaDataException("hdfs目录格式不正确！");
        }
    }

    /**
     * 验证hdfs目录有效性 以/开头
     */
    public static boolean verifyPath(String path) {
        return Pattern.compile(HDFS_PATTERN).matcher(path).matches();
    }
}
