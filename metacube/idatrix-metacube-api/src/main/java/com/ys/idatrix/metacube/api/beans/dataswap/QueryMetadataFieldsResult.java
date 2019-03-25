package com.ys.idatrix.metacube.api.beans.dataswap;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: QueryMetadataFieldsResult
 * @Description: 查询元数据字段结果
 * @Author: ZhouJian
 * @Date: 2018/8/10
 */
@Getter
@Setter
@ToString
public class QueryMetadataFieldsResult implements Serializable {

    private static final long serialVersionUID = 6371677759863143195L;

    private List<MetadataField> metadataField;


    public QueryMetadataFieldsResult() {
    }

    public QueryMetadataFieldsResult(List<MetadataField> metadataField) {

        this.metadataField = metadataField;
    }


}
