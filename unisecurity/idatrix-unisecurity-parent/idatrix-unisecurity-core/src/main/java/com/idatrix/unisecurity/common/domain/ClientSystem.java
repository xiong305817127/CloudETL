package com.idatrix.unisecurity.common.domain;

import com.idatrix.unisecurity.common.sso.StringUtil;
import com.idatrix.unisecurity.common.utils.SecurityStringUtils;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * 客户端系统
 */
public class ClientSystem implements Serializable {

    protected final static Logger logger = Logger.getLogger(ClientSystem.class);

    private String id; // 唯一标识

    private String name; // 系统名称

    private String baseUrl; // 应用基路径，代表应用访问起始点

    private String homeUri; // 应用主页面URI，baseUrl + homeUri = 主页URL

    private String innerAddress; // 系统间内部通信地址

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getHomeUri() {
        return homeUri;
    }

    public void setHomeUri(String homeUri) {
        this.homeUri = homeUri;
    }

    public String getInnerAddress() {
        return innerAddress;
    }

    public void setInnerAddress(String innerAddress) {
        this.innerAddress = innerAddress;
    }

    public String getHomeUrl() {
        return baseUrl + homeUri;
    }

    /**
     * 服务端与客户端系统通信，通知客户端token过期
     * @param tokenTimeout
     * @return 延期的有效期
     */
    public Date noticeTimeout(String vt, int tokenTimeout) {
        try {
            String url = baseUrl + "/notice/timeout?vt=" + vt + "&tokenTimeout=" + tokenTimeout;
            logger.debug("noticeTimeout url:" + url);
            logger.debug("noticeTimeout vt :" + vt);
            String ret = httpAccess(url);
            if (StringUtil.isEmpty(ret)) {
                return null;
            } else {
                if (SecurityStringUtils.isNumeric(ret))
                    return new Date(Long.parseLong(ret));
                else
                    return null;
            }
        } catch (Exception e) {
            logger.error("noticeTimeout " + baseUrl + " error >>>");
            return null;
        }
    }

    /**
     * 服务端通知客户端用户退出
     */
    public boolean noticeLogout(String vt) {
        try {
            String url = baseUrl + "/notice/logout?vt=" + vt;
            logger.info("noticeLogout url：" + url);
            return postHttp(url);
        } catch (Exception e) {
            logger.error("noticeLogout " + baseUrl + " error >>>" + e.getMessage());
            return false;
        }
    }

    private boolean postHttp(String url) {
        String ret = "";
        Request request = new Request();
        Response response = request.post(url);
        ret = response.getResult();
        return Boolean.parseBoolean(ret);
    }

    /**
     * 通知客户端服务端关闭，客户端收到信息后执行清除缓存操作
     */
    public boolean noticeShutdown() {
        try {
            String url = baseUrl + "/notice/shutdown";
            logger.debug("noticeShutdown url url >>>" + url);
            return postHttp(url);
        } catch (Exception e) {
            return false;
        }
    }

    private String httpAccess(String theUrl) throws Exception {
        URL url = new URL(theUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(500);
        InputStream is = conn.getInputStream();
        conn.connect();
        byte[] buff = new byte[is.available()];
        is.read(buff);
        String ret = new String(buff, "utf-8");
        conn.disconnect();
        is.close();
        return ret;
    }

    class FurtureData implements Response {

        RealRequest real;
        protected boolean isReady = false;

        @Override
        public synchronized String getResult() {
            while (!isReady) {
                try {
                    wait();
                } catch (Exception e) {
                    logger.error("get result error :" + e.getMessage());
                    e.printStackTrace();
                }
            }
            return real.getResult();
        }

        public synchronized void setRealRequest(RealRequest real) {
            if (isReady)
                return;

            this.real = real;
            isReady = true;
            notifyAll();
        }
    }

    class Request {
        public Response post(String url) {
            FurtureData data = new FurtureData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RealRequest real = new RealRequest(url);
                    data.setRealRequest(real);
                }
            }).start();
            return data;
        }
    }


    class RealRequest implements Response {
        String ret;

        public RealRequest(String url) {
            try {
                ret = httpAccess(url);
            } catch (Exception e) {
            }
        }

        @Override
        public String getResult() {
            return ret;
        }
    }


    interface Response {
        public String getResult();
    }
}
