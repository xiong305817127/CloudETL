package com.ys.idatrix.metacube.api.beans.dataswap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @ClassName: SubscribeCrtTbResult
 * @Description:政府资源目录订阅响应接口
 * @Author: ZhouJian
 * @Date: 2018/8/8
 */
@Getter
@Setter
@ToString
public class SubscribeCrtTbResult implements Serializable {

    private static final long serialVersionUID = 1414110105950839374L;

    private int metaId;

    private boolean hasExists;

    public SubscribeCrtTbResult() {
    }


    public SubscribeCrtTbResult( int metaId) {
        this.metaId = metaId;
    }

    public SubscribeCrtTbResult(int metaId, boolean hasExists) {
        this.metaId = metaId;
        this.hasExists = hasExists;
    }

}
