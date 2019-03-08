package com.idatrix.unisecurity.ranger.common;

import java.util.ArrayList;
import java.util.List;

import com.idatrix.unisecurity.ranger.common.policy.vo.PolicyInfoVO;

public class RangerResourceType {
    public static final String HDFS_TYPE = "hdfs";
    
    public static final String HBASE_TYPE = "hbase";
    
    public static final String HIVE_TYPE = "hive";
    
    public static final String YARN_TYPE = "yarn";
    
    public static final String HDFS_MAP_KEY = "path";
    
    public static final String YARN_MAP_KEY = "queue";
    
    public static final String HBASE_MAP_TABLE_KEY = "table";
    
    public static final String HBASE_MAP_FAMILY_KEY = "column-family";
    
    public static final String HBASE_MAP_COLUMN_KEY = "column";
    
    public static final String HIVE_MAP_DATABASE_KEY = "database";
    
    public static final String HIVE_MAP_TABLE_KEY = "table";
    
    public static final String HIVE_MAP_COLUMN_KEY = "column";
    
}
