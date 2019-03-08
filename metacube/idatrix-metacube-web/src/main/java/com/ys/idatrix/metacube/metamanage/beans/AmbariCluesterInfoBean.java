package com.ys.idatrix.metacube.metamanage.beans;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AmbariCluesterInfoBean {


    /**
     * href : http://ysbdh03.gdbd.com:8080/api/v1/clusters/sz/configurations/service_config_versions?service_name.in(CLUSTERINFO)&is_current=true
     * items : [{"href":"http://ysbdh03.gdbd.com:8080/api/v1/clusters/sz/configurations/service_config_versions?service_name=CLUSTERINFO&service_config_version=2","cluster_name":"sz","configurations":[{"Config":{"cluster_name":"sz","stack_id":"HDP-2.5"},"type":"clusterinfo","tag":"version1547806827164","version":3,"properties":{"CLOUDETL":"master1:ysbdh04.gdbd.com:60090:Y:root:12345678,slave1:ysbdh03.gdbd.com:60090:N:root:12345678,slave2:ysbdh05.gdbd.com:60090:N:root:12345678","ELASTICSEARCH":"ysbdh05.gdbd.com:9305","HBASE":"jdbc:phoenix:ysbdh04.gdbd.com,ysbdh03.gdbd.com,ysbdh05.gdbd.com:2181","HDFS":"ysbdh03.gdbd.com:8020","HIVE":"jdbc:hive2://ysbdh04.gdbd.com:10000","MYSQL":"ysbdh05.gdbd.com:3306","hbase.rootdir":"hdfs://ysbdh03.gdbd.com:8020/apps/hbase/data","hive.metastore.warehouse.dir":"/apps/hive/warehouse"},"properties_attributes":{}}],"createtime":1547807645744,"group_id":-1,"group_name":"Default","hosts":[],"is_cluster_compatible":true,"is_current":true,"service_config_version":2,"service_config_version_note":"ysbdh05.gdbd.com","service_name":"CLUSTERINFO","stack_id":"HDP-2.5","user":"admin"}]
     */

    private String href;
    private List<ItemsBean> items;

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public List<ItemsBean> getItems() {
        return items;
    }

    public void setItems(List<ItemsBean> items) {
        this.items = items;
    }

    public static class ItemsBean {

        /**
         * href : http://ysbdh03.gdbd.com:8080/api/v1/clusters/sz/configurations/service_config_versions?service_name=CLUSTERINFO&service_config_version=2
         * cluster_name : sz
         * configurations : [{"Config":{"cluster_name":"sz","stack_id":"HDP-2.5"},"type":"clusterinfo","tag":"version1547806827164","version":3,"properties":{"CLOUDETL":"master1:ysbdh04.gdbd.com:60090:Y:root:12345678,slave1:ysbdh03.gdbd.com:60090:N:root:12345678,slave2:ysbdh05.gdbd.com:60090:N:root:12345678","ELASTICSEARCH":"ysbdh05.gdbd.com:9305","HBASE":"jdbc:phoenix:ysbdh04.gdbd.com,ysbdh03.gdbd.com,ysbdh05.gdbd.com:2181","HDFS":"ysbdh03.gdbd.com:8020","HIVE":"jdbc:hive2://ysbdh04.gdbd.com:10000","MYSQL":"ysbdh05.gdbd.com:3306","hbase.rootdir":"hdfs://ysbdh03.gdbd.com:8020/apps/hbase/data","hive.metastore.warehouse.dir":"/apps/hive/warehouse"},"properties_attributes":{}}]
         * createtime : 1547807645744
         * group_id : -1
         * group_name : Default
         * hosts : []
         * is_cluster_compatible : true
         * is_current : true
         * service_config_version : 2
         * service_config_version_note : ysbdh05.gdbd.com
         * service_name : CLUSTERINFO
         * stack_id : HDP-2.5
         * user : admin
         */

        private String href;
        private String cluster_name;
        private long createtime;
        private int group_id;
        private String group_name;
        private boolean is_cluster_compatible;
        private boolean is_current;
        private int service_config_version;
        private String service_config_version_note;
        private String service_name;
        private String stack_id;
        private String user;
        private List<ConfigurationsBean> configurations;
        private List<?> hosts;

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getCluster_name() {
            return cluster_name;
        }

        public void setCluster_name(String cluster_name) {
            this.cluster_name = cluster_name;
        }

        public long getCreatetime() {
            return createtime;
        }

        public void setCreatetime(long createtime) {
            this.createtime = createtime;
        }

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public boolean isIs_cluster_compatible() {
            return is_cluster_compatible;
        }

        public void setIs_cluster_compatible(boolean is_cluster_compatible) {
            this.is_cluster_compatible = is_cluster_compatible;
        }

        public boolean isIs_current() {
            return is_current;
        }

        public void setIs_current(boolean is_current) {
            this.is_current = is_current;
        }

        public int getService_config_version() {
            return service_config_version;
        }

        public void setService_config_version(int service_config_version) {
            this.service_config_version = service_config_version;
        }

        public String getService_config_version_note() {
            return service_config_version_note;
        }

        public void setService_config_version_note(String service_config_version_note) {
            this.service_config_version_note = service_config_version_note;
        }

        public String getService_name() {
            return service_name;
        }

        public void setService_name(String service_name) {
            this.service_name = service_name;
        }

        public String getStack_id() {
            return stack_id;
        }

        public void setStack_id(String stack_id) {
            this.stack_id = stack_id;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public List<ConfigurationsBean> getConfigurations() {
            return configurations;
        }

        public void setConfigurations(List<ConfigurationsBean> configurations) {
            this.configurations = configurations;
        }

        public List<?> getHosts() {
            return hosts;
        }

        public void setHosts(List<?> hosts) {
            this.hosts = hosts;
        }

        public static class ConfigurationsBean {

            /**
             * Config : {"cluster_name":"sz","stack_id":"HDP-2.5"}
             * type : clusterinfo
             * tag : version1547806827164
             * version : 3
             * properties : {"CLOUDETL":"master1:ysbdh04.gdbd.com:60090:Y:root:12345678,slave1:ysbdh03.gdbd.com:60090:N:root:12345678,slave2:ysbdh05.gdbd.com:60090:N:root:12345678","ELASTICSEARCH":"ysbdh05.gdbd.com:9305","HBASE":"jdbc:phoenix:ysbdh04.gdbd.com,ysbdh03.gdbd.com,ysbdh05.gdbd.com:2181","HDFS":"ysbdh03.gdbd.com:8020","HIVE":"jdbc:hive2://ysbdh04.gdbd.com:10000","MYSQL":"ysbdh05.gdbd.com:3306","hbase.rootdir":"hdfs://ysbdh03.gdbd.com:8020/apps/hbase/data","hive.metastore.warehouse.dir":"/apps/hive/warehouse"}
             * properties_attributes : {}
             */

            private ConfigBean Config;
            private String type;
            private String tag;
            private int version;
            private PropertiesBean properties;
//            private PropertiesAttributesBean properties_attributes;

            public ConfigBean getConfig() {
                return Config;
            }

            public void setConfig(ConfigBean Config) {
                this.Config = Config;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getTag() {
                return tag;
            }

            public void setTag(String tag) {
                this.tag = tag;
            }

            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }

            public PropertiesBean getProperties() {
                return properties;
            }

            public void setProperties(PropertiesBean properties) {
                this.properties = properties;
            }

//            public PropertiesAttributesBean getProperties_attributes() {
//                return properties_attributes;
//            }
//
//            public void setProperties_attributes(PropertiesAttributesBean properties_attributes) {
//                this.properties_attributes = properties_attributes;
//            }

            public static class ConfigBean {

                /**
                 * cluster_name : sz
                 * stack_id : HDP-2.5
                 */

                private String cluster_name;
                private String stack_id;

                public String getCluster_name() {
                    return cluster_name;
                }

                public void setCluster_name(String cluster_name) {
                    this.cluster_name = cluster_name;
                }

                public String getStack_id() {
                    return stack_id;
                }

                public void setStack_id(String stack_id) {
                    this.stack_id = stack_id;
                }
            }

            public static class PropertiesBean {

                /**
                 * CLOUDETL : master1:ysbdh04.gdbd.com:60090:Y:root:12345678,slave1:ysbdh03.gdbd.com:60090:N:root:12345678,slave2:ysbdh05.gdbd.com:60090:N:root:12345678
                 * ELASTICSEARCH : ysbdh05.gdbd.com:9305
                 * HBASE : jdbc:phoenix:ysbdh04.gdbd.com,ysbdh03.gdbd.com,ysbdh05.gdbd.com:2181
                 * HDFS : ysbdh03.gdbd.com:8020
                 * HIVE : jdbc:hive2://ysbdh04.gdbd.com:10000
                 * MYSQL : ysbdh05.gdbd.com:3306
                 * hbase.rootdir : hdfs://ysbdh03.gdbd.com:8020/apps/hbase/data
                 * hive.metastore.warehouse.dir : /apps/hive/warehouse
                 */

                private String CLOUDETL;
                private String ELASTICSEARCH;
                private String HBASE;
                private String HDFS;
                private String HIVE;
                private String MYSQL;
                @SerializedName("hbase.rootdir")
                private String hbaseRootDir;
                @SerializedName("hive.metastore.warehouse.dir")
                private String hiveMetastoreWarehouseDir;

                public String getCLOUDETL() {
                    return CLOUDETL;
                }

                public void setCLOUDETL(String CLOUDETL) {
                    this.CLOUDETL = CLOUDETL;
                }

                public String getELASTICSEARCH() {
                    return ELASTICSEARCH;
                }

                public void setELASTICSEARCH(String ELASTICSEARCH) {
                    this.ELASTICSEARCH = ELASTICSEARCH;
                }

                public String getHBASE() {
                    return HBASE;
                }

                public void setHBASE(String HBASE) {
                    this.HBASE = HBASE;
                }

                public String getHDFS() {
                    return HDFS;
                }

                public void setHDFS(String HDFS) {
                    this.HDFS = HDFS;
                }

                public String getHIVE() {
                    return HIVE;
                }

                public void setHIVE(String HIVE) {
                    this.HIVE = HIVE;
                }

                public String getMYSQL() {
                    return MYSQL;
                }

                public void setMYSQL(String MYSQL) {
                    this.MYSQL = MYSQL;
                }

                public String getHbaseRootDir() {
                    return hbaseRootDir;
                }

                public void setHbaseRootDir(String hbaseRootDir) {
                    this.hbaseRootDir = hbaseRootDir;
                }

                public String getHiveMetastoreWarehouseDir() {
                    return hiveMetastoreWarehouseDir;
                }

                public void setHiveMetastoreWarehouseDir(String hiveMetastoreWarehouseDir) {
                    this.hiveMetastoreWarehouseDir = hiveMetastoreWarehouseDir;
                }
            }

//            public static class PropertiesAttributesBean {
//
//            }
        }
    }
}
