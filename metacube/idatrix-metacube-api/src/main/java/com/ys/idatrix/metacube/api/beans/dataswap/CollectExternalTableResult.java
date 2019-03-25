package com.ys.idatrix.metacube.api.beans.dataswap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: CollectExternalTableResult
 * @Description:
 * @Author: ZhouJian
 * @Date: 2018/11/8
 */
@Getter
@Setter
@ToString
public class CollectExternalTableResult implements Serializable {

    /**
     * 生成的元数据表id
     */
    private int metaId;

    /**
     * 表字段
     */
    private List<String> fieldNames;

    public CollectExternalTableResult() {
    }


    public CollectExternalTableResult(List<String> fieldNames) {
        this.metaId = metaId;
        this.fieldNames = fieldNames;
    }

}
