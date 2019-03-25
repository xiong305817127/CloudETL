package com.ys.idatrix.metacube.metamanage.vo.response;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: tableInfo
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: chl
 * @date: 2017年6月7日 下午1:57:48
 */
@Data
public class DbTableInfo implements Serializable {

    /**
     * 数据库 id
     */
    private Integer id;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * 表名称列表
     */
    private List<MetaDefOverviewVO> tableList;



}
