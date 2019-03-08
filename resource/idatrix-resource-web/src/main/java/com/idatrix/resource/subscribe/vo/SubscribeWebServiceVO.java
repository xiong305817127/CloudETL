package com.idatrix.resource.subscribe.vo;

/**
 * Created by Administrator on 2018/9/13.
 */
public class SubscribeWebServiceVO {

    /**/
    private String subKey;

    private String webUrl;

    public String getSubKey() {
        return subKey;
    }

    public void setSubKey(String subKey) {
        this.subKey = subKey;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    @Override
    public String toString() {
        return "SubscribeWebServiceVO{" +
                "subKey='" + subKey + '\'' +
                ", webUrl='" + webUrl + '\'' +
                '}';
    }
}
