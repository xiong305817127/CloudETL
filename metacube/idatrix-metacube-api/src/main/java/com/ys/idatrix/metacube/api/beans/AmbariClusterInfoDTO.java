package com.ys.idatrix.metacube.api.beans;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Ambari平台集群信息
 *
 * @author wzl
 */
@Data
@Accessors(chain = true)
public class AmbariClusterInfoDTO implements Serializable {

    private String cloudETL;

    private String elasticsearch;

    private String hbase;

    private String hdfs;

    private String hive;
}
