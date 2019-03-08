package com.ys.idatrix.metacube.api.beans;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetaEsDTO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/2/21
 */
@Data
public class MetaEsDTO implements Serializable {

    private static final long serialVersionUID = -3872678844023457607L;

    /**
     * 索引id
     */
    private Long id;

    /**
     * 索引名称
     */
    private String indexName;

    /**
     * 索引版本
     */
    private Integer version;

    /**
     * 操作权限
     */
    private ActionTypeEnum actionTypeEnum;

}
