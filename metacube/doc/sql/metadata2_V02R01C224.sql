/*
 Navicat Premium Data Transfer

 Source Server         : 10.0.0.85_root
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : 10.0.0.85:3306
 Source Schema         : metadata2

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 21/03/2019 17:10:22
*/

create database metadata2 default character set utf8 collate utf8_general_ci;
use metadata2;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bi_category
-- ----------------------------
DROP TABLE IF EXISTS `bi_category`;
CREATE TABLE `bi_category`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '目录ID',
  `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '目录名',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `renter_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bi_resource_click
-- ----------------------------
DROP TABLE IF EXISTS `bi_resource_click`;
CREATE TABLE `bi_resource_click`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资源名',
  `type` int(10) NULL DEFAULT NULL COMMENT '资源类型 1:报表分析 2:仪表盘',
  `root_directory` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '一级目录',
  `path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '资源节点路径',
  `click_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '点击时间',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `renter_id` bigint(20) NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bi_schema
-- ----------------------------
DROP TABLE IF EXISTS `bi_schema`;
CREATE TABLE `bi_schema`  (
  `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '设计表id',
  `ds_name` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'schema名称',
  `datasource` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '数据源链接数据',
  `table_relation` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表关系',
  `db_schema` mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模型',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `creator` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `updater` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `ds_id` int(10) NULL DEFAULT NULL COMMENT '数据源id',
  `renter_id` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '租户id',
  `category_id` int(10) NULL DEFAULT NULL COMMENT '目录ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_renterid_dsname`(`ds_name`, `renter_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for bi_schema_click
-- ----------------------------
DROP TABLE IF EXISTS `bi_schema_click`;
CREATE TABLE `bi_schema_click`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `click_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '点击时间',
  `schema_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模式名称',
  `schema_id` bigint(20) NULL DEFAULT NULL COMMENT '模式ID',
  `category_id` bigint(20) NULL DEFAULT NULL COMMENT '目录ID',
  `user_id` bigint(20) NULL DEFAULT NULL COMMENT '用户ID',
  `renter_id` bigint(20) NULL DEFAULT NULL COMMENT '租户ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_approval_process
-- ----------------------------
DROP TABLE IF EXISTS `mc_approval_process`;
CREATE TABLE `mc_approval_process`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请人',
  `renter_id` bigint(20) NULL DEFAULT NULL COMMENT '申请人租户ID',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '申请时间',
  `dept_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请组织code',
  `cause` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '申请原因',
  `resource_id` bigint(20) NULL DEFAULT NULL COMMENT '申请的资源id',
  `resource_type` int(10) NULL DEFAULT NULL COMMENT '资源类型 1:MYSQL 2:ORACLE 3:DM 4:POSTGRESQL 5:HIVE 6:HBASE 7:HDFS 8:ELASTICSEARCH',
  `auth_value` int(10) NULL DEFAULT NULL COMMENT '当前申请的权限值,二进制数据',
  `status` int(10) NULL DEFAULT 1 COMMENT '状态 1-申请中 2-通过 3-不通过 4-已回收 5-已撤回 6-删除',
  `approver` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审批人',
  `opinion` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '审批意见',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_database
-- ----------------------------
DROP TABLE IF EXISTS `mc_database`;
CREATE TABLE `mc_database`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `server_id` bigint(20) NOT NULL COMMENT '服务器id',
  `type` tinyint(1) UNSIGNED NOT NULL COMMENT '数据库类型 1 MySQL 2 Oracle 3 DM 4 PostgreSQL 5 Hive 6 HBase 7 HDFS 8 Elasticsearch',
  `belong` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '数据库归属 1 ODS-操作数据存储 2 DW-数据仓库3 DM-数据集市',
  `port` int(11) NULL DEFAULT NULL COMMENT '端口',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '管理员账号',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据库管理员密码',
  `is_deleted` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除字段 0未删除 1已删除',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `renter_id` bigint(20) NOT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '数据库平台实体表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_es_field
-- ----------------------------
DROP TABLE IF EXISTS `mc_es_field`;
CREATE TABLE `mc_es_field`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `index_id` bigint(20) NULL DEFAULT NULL COMMENT '索引id 关联mc_metadata中database_type=8的主键',
  `field_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段名',
  `field_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `analyzer` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分析器',
  `is_index` bit(1) NULL DEFAULT NULL COMMENT '是否索引 0:否 1:是',
  `is_store` bit(1) NULL DEFAULT NULL COMMENT '是否存储 0:否 1:是',
  `is_all` bit(1) NULL DEFAULT NULL COMMENT '是否包含在_all中 0:否 1:是',
  `is_source` bit(1) NULL DEFAULT NULL COMMENT '是否包含在_source中 0:否 1:是',
  `location` int(10) NULL DEFAULT NULL COMMENT '字段位置（当前字段的一个位置标识，每张表的字段位置都是自增，唯一的）',
  `creator` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'ES索引字段表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_md_hive_field
-- ----------------------------
DROP TABLE IF EXISTS `mc_md_hive_field`;
CREATE TABLE `mc_md_hive_field`  (
  `id` bigint(20) NOT NULL COMMENT '主键，同mc_metadata主键',
  `is_external_table` tinyint(1) NULL DEFAULT NULL COMMENT '是否外表',
  `location` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'hdfs路径',
  `fields_terminated` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '每列之间的分隔符',
  `lines_terminated` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '每行之间的分隔符',
  `null_defined` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '空值处理',
  `store_format` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储格式，TEXTFILE,SEQUENCEFILE,PARQUET,AVRO',
  `creator` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_metadata
-- ----------------------------
DROP TABLE IF EXISTS `mc_metadata`;
CREATE TABLE `mc_metadata`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'hdf目录 or es索引名称 or db实体表名',
  `identification` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'hdf子目录 or es描述 or db中文表名',
  `public_status` int(10) NULL DEFAULT NULL COMMENT '公开状态：0:不公开 1:授权访问',
  `theme_id` bigint(255) NULL DEFAULT NULL COMMENT '主题id，外键关联',
  `tags` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '标签，多个以，隔开',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `version` int(10) NULL DEFAULT NULL COMMENT '当前版本号，递增',
  `renter_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `is_gather` tinyint(1) NULL DEFAULT NULL COMMENT '是否直采，0:否 1:是',
  `status` int(20) NULL DEFAULT NULL COMMENT '当前状态：0草稿 1生效 2删除',
  `database_type` int(10) NULL DEFAULT NULL COMMENT '数据库类型,1.mysql,2.oracle,3.dm,4.postgreSQL,5.hive,6.base,7.hdfs,8.ElasticSearch',
  `resource_type` int(10) NULL DEFAULT NULL COMMENT '不同数据库下分辨不同资源 如：db 1:表 2:视图',
  `schema_id` bigint(20) NULL DEFAULT NULL COMMENT '模式id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_metadata_es
-- ----------------------------
DROP TABLE IF EXISTS `mc_metadata_es`;
CREATE TABLE `mc_metadata_es`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT 'default_type' COMMENT '类型名称',
  `identification` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  `schema_id` bigint(20) NULL DEFAULT NULL COMMENT '模型id',
  `public_status` int(10) NULL DEFAULT NULL COMMENT '公开状态：0:不公开 1:授权访问',
  `theme_id` bigint(255) NULL DEFAULT NULL COMMENT '主题id，外键关联',
  `tags` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '标签，多个以，隔开',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `version` int(10) NULL DEFAULT NULL COMMENT '当前版本号',
  `status` int(20) NULL DEFAULT NULL COMMENT '当前状态：0草稿 1生效 2删除',
  `is_open` bit(1) NULL DEFAULT NULL COMMENT '是否开启：0停用 1开启',
  `renter_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_unicode_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `max_version` int(10) NULL DEFAULT NULL COMMENT '冗余-最大版本号，递增',
  `max_location` int(10) NULL DEFAULT NULL COMMENT '冗余-最大字段位置值',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'ES索引表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_resource_auth
-- ----------------------------
DROP TABLE IF EXISTS `mc_resource_auth`;
CREATE TABLE `mc_resource_auth`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键·',
  `auth_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限名称',
  `auth_type` int(10) NULL DEFAULT NULL COMMENT '权限类型，1:读 2:写',
  `auth_value` int(20) NULL DEFAULT NULL COMMENT '权限值，二进制标识',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `modify_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_schema
-- ----------------------------
DROP TABLE IF EXISTS `mc_schema`;
CREATE TABLE `mc_schema`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键·',
  `db_id` bigint(20) NOT NULL COMMENT '数据库平台id',
  `service_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务名称 oracle实例名',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模式名称、es索引名称、hdfs目录',
  `name_cn` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模式中文名称、hdfs目录中文名',
  `username` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据库账号',
  `password` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据库密码',
  `org_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属组织编码 多个以英文逗号分隔',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `type` int(11) NULL DEFAULT 1 COMMENT '类型 1新建 2注册',
  `status` tinyint(1) NULL DEFAULT 0 COMMENT '状态 0正常 1禁用',
  `is_deleted` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '逻辑删除字段 0未删除 1已删除',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `renter_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_orgcode`(`org_code`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '模式实体表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_server
-- ----------------------------
DROP TABLE IF EXISTS `mc_server`;
CREATE TABLE `mc_server`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器名称',
  `use` tinyint(1) UNSIGNED NULL DEFAULT NULL COMMENT '服务器用途：1前置库 2平台库 3平台库-Hadoop',
  `ip` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '服务器ip地址',
  `hostname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '服务器主机名',
  `org_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组织编码',
  `location` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '位置信息',
  `contact` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人',
  `contact_number` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人电话',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '逻辑删除字段 0未删除 1已删除',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `renter_id` bigint(20) NOT NULL COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_ip_renter_id`(`ip`, `renter_id`) USING BTREE,
  FULLTEXT INDEX `ft_name`(`name`) COMMENT '服务器名称和ip全文索引'
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '服务器实体表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_server_database_change
-- ----------------------------
DROP TABLE IF EXISTS `mc_server_database_change`;
CREATE TABLE `mc_server_database_change`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `type` tinyint(1) NULL DEFAULT NULL COMMENT '变更类型 1 服务器 2 数据库 ...',
  `fk_id` bigint(20) NULL DEFAULT NULL COMMENT '逻辑外键 服务器id、数据库id...',
  `create_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '变更时间',
  `operator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '操作人',
  `content` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '变更内容',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_change_time`(`create_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_es_field
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_es_field`;
CREATE TABLE `mc_snapshot_es_field`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `index_id` bigint(20) NULL DEFAULT NULL COMMENT '索引id 关联mc_metadata中database_type=8的主键',
  `version` int(10) NULL DEFAULT NULL COMMENT '当前快照版本号',
  `field_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段名',
  `field_type` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `analyzer` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分析器',
  `is_index` bit(1) NULL DEFAULT NULL COMMENT '是否索引 0:否 1:是',
  `is_store` bit(1) NULL DEFAULT NULL COMMENT '是否存储 0:否 1:是',
  `is_all` bit(1) NULL DEFAULT NULL COMMENT '是否包含在_all中 0:否 1:是',
  `is_source` bit(1) NULL DEFAULT NULL COMMENT '是否包含在_source中 0:否 1:是',
  `location` int(10) NULL DEFAULT NULL COMMENT '字段位置（当前字段的一个位置标识，每张表的字段位置都是自增，唯一的）',
  `creator` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'ES索引字段快照表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_md_hive_field
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_md_hive_field`;
CREATE TABLE `mc_snapshot_md_hive_field`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `details` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本变更详情',
  `origin_id` bigint(20) NULL DEFAULT NULL COMMENT '快照表主键',
  `is_external_table` tinyint(1) NULL DEFAULT NULL COMMENT '是否外表',
  `location` varchar(250) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'hdfs路径',
  `fields_terminated` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '每列之间的分隔符',
  `lines_terminated` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '每行之间的分隔符',
  `null_defined` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '空值处理',
  `store_format` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '存储格式，TEXTFILE,SEQUENCEFILE,PARQUET,AVRO',
  `creator` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_metadata
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_metadata`;
CREATE TABLE `mc_snapshot_metadata`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `details` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本变更详情',
  `meta_id` bigint(20) NULL DEFAULT NULL COMMENT '元数据id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'hdf目录 or es索引名称 or db实体表名',
  `identification` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'hdf子目录 or es描述 or db中文表名',
  `public_status` int(10) NULL DEFAULT NULL COMMENT '公开状态：0:不公开 1:公开',
  `theme_id` bigint(20) NULL DEFAULT NULL COMMENT '主题',
  `tags` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '标签，多个以，隔开',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `is_gather` tinyint(1) NULL DEFAULT NULL COMMENT '是否直采，0:否 1:是',
  `status` int(10) NULL DEFAULT NULL COMMENT '当前状态：0草稿 1生效 2删除',
  `renter_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `database_type` int(10) NULL DEFAULT NULL COMMENT '数据库类型',
  `resource_type` int(10) NULL DEFAULT NULL COMMENT '不同数据库下分辨不同资源 如：db 1:表 2:视图',
  `schema_id` bigint(20) NULL DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_metadata_es
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_metadata_es`;
CREATE TABLE `mc_snapshot_metadata_es`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `details` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '版本变更详情',
  `meta_id` bigint(20) NULL DEFAULT NULL COMMENT '元数据id',
  `name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型名称',
  `identification` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'hdf子目录 or es描述 or db中文表名',
  `schema_id` bigint(20) NULL DEFAULT NULL COMMENT '模型id',
  `public_status` int(10) NULL DEFAULT NULL COMMENT '公开状态：0:不公开 1:公开',
  `theme_id` bigint(20) NULL DEFAULT NULL COMMENT '主题',
  `tags` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '标签，多个以，隔开',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int(10) NULL DEFAULT NULL COMMENT '当前状态：0草稿 1生效 2删除',
  `is_open` bit(1) NULL DEFAULT NULL COMMENT '是否启停：0停用 1开启',
  `renter_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `max_version` int(10) NULL DEFAULT NULL COMMENT '冗余-最大版本号，递增',
  `max_location` int(10) NULL DEFAULT NULL COMMENT '冗余-最大字段位置值',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = 'ES索引快照表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_table_ck_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_table_ck_oracle`;
CREATE TABLE `mc_snapshot_table_ck_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `versions` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `ch_id` bigint(20) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检查约束名',
  `check_sql` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检查语法',
  `is_enabled` tinyint(1) NULL DEFAULT NULL COMMENT 'oracle 概念，是否启用，0:未启动 1:启用',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_table_column
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_table_column`;
CREATE TABLE `mc_snapshot_table_column`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version` int(10) NULL DEFAULT NULL COMMENT '当前快照版本号',
  `column_id` bigint(20) NULL DEFAULT NULL,
  `column_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段名',
  `column_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `type_length` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型长度',
  `type_precision` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型精度',
  `is_pk` tinyint(1) NULL DEFAULT NULL COMMENT '是否为主键 0:否,1:是',
  `is_auto_increment` tinyint(1) NULL DEFAULT NULL COMMENT '是否自增，0:否  1:是',
  `is_null` tinyint(1) NULL DEFAULT NULL COMMENT '是否允许为空 0:否 1:是',
  `default_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认值',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段描述',
  `location` int(10) NULL DEFAULT NULL COMMENT '字段位置（当前字段的一个位置标识，每张表的字段位置都是自增，唯一的）',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  `is_partition` tinyint(1) NULL DEFAULT NULL COMMENT '是否是分区列(HIVE)',
  `index_partition` int(10) NULL DEFAULT NULL COMMENT '分区定义位置(HIVE)',
  `is_bucket` tinyint(1) NULL DEFAULT NULL COMMENT '是否作为分桶(HIVE)',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_table_fk_mysql
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_table_fk_mysql`;
CREATE TABLE `mc_snapshot_table_fk_mysql`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `fk_id` bigint(20) NULL DEFAULT NULL COMMENT '外键id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外键名',
  `column_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前表字段，可能有多个,以,隔开',
  `reference_schema_id` bigint(20) NULL DEFAULT NULL COMMENT '参考的模型id，在mysql中这里表示是数据库',
  `reference_table_id` bigint(20) NULL DEFAULT NULL COMMENT '参考表id',
  `reference_column` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参考列id，可能有多个，和当前表字段对应',
  `delete_trigger` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除时触发的事件',
  `update_trigger` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改时触发的事件',
  `location` int(10) NULL DEFAULT NULL COMMENT '位置（当一个位置标识，在每张表的索引位置都是自增，唯一的）',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_table_fk_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_table_fk_oracle`;
CREATE TABLE `mc_snapshot_table_fk_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `versions` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `fk_id` bigint(20) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外键名',
  `column_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前表字段，可能有多个,以,隔开',
  `reference_schema_id` bigint(20) NULL DEFAULT NULL COMMENT '参考模式id',
  `reference_table_id` bigint(20) NULL DEFAULT NULL COMMENT '参考表id',
  `reference_restrain` bigint(20) NULL DEFAULT NULL COMMENT '参考约束',
  `reference_column` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参考字段，字段是当选择约束时，自动录进去',
  `delete_trigger` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除时触发的事件',
  `is_enabled` tinyint(1) NULL DEFAULT NULL COMMENT 'oracle 概念，是否启用，0:未启动 1:启用',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_table_idx_mysql
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_table_idx_mysql`;
CREATE TABLE `mc_snapshot_table_idx_mysql`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `index_id` bigint(20) NULL DEFAULT NULL COMMENT '索引id',
  `index_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引名',
  `column_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前索引关联的字段，可能多个，以，隔开',
  `subdivision` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '子部分，对应字段，可以有多个',
  `index_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引类型',
  `index_method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引方法',
  `location` int(10) NULL DEFAULT NULL COMMENT '索引位置（当前字段的一个位置标识，每张表的索引位置都是自增，唯一的）',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_table_idx_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_table_idx_oracle`;
CREATE TABLE `mc_snapshot_table_idx_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `versions` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `index_id` bigint(20) NULL DEFAULT NULL,
  `index_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引名',
  `column_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前索引关联的字段，可能多个，以，隔开，oracle的每个字段都还对应一个排序方式：asc，desc',
  `column_sort` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段对应的排序规则（对应字段）',
  `index_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引类型',
  `location` int(10) NULL DEFAULT NULL COMMENT '索引位置（当前字段的一个位置标识，每张表的索引位置都是自增，唯一的）',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_table_pk_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_table_pk_oracle`;
CREATE TABLE `mc_snapshot_table_pk_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `versions` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `pk_id` bigint(20) NULL DEFAULT NULL COMMENT '表主键id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主键约束名',
  `sequence_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '序列名（oracle自增长，全局可以唯一，或者自己创建序列）',
  `sequence_status` int(10) NULL DEFAULT NULL COMMENT '序列状态，主要是让前端更好展示',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_table_set_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_table_set_oracle`;
CREATE TABLE `mc_snapshot_table_set_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `versions` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `set_id` bigint(20) NULL DEFAULT NULL,
  `tablespace` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表空间',
  `table_id` bigint(20) NULL DEFAULT NULL,
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_table_un_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_table_un_oracle`;
CREATE TABLE `mc_snapshot_table_un_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `versions` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `un_id` bigint(20) NULL DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一约束名',
  `column_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前表字段，可能有多个,以,隔开',
  `is_enabled` tinyint(1) NULL DEFAULT NULL COMMENT 'oracle 概念，是否启用，0:未启动 1:启用',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_snapshot_view_detail
-- ----------------------------
DROP TABLE IF EXISTS `mc_snapshot_view_detail`;
CREATE TABLE `mc_snapshot_view_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `version` int(10) NULL DEFAULT NULL COMMENT '当前快照版本',
  `view_detail_id` bigint(20) NULL DEFAULT NULL COMMENT '视图详情id',
  `view_sql` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '视图sql',
  `arithmetic` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '算法',
  `definiens` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '定义者',
  `security` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '安全性',
  `check_option` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检查选项',
  `view_id` bigint(20) NULL DEFAULT NULL COMMENT '视图基本信息id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_system_settings
-- ----------------------------
DROP TABLE IF EXISTS `mc_system_settings`;
CREATE TABLE `mc_system_settings`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `data_centre_admin` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据中心管理员，（可以修改系统参数）',
  `database_admin` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '数据库管理员，可以修改元数据',
  `root_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'hdfs根目录',
  `is_gather` tinyint(1) NULL DEFAULT NULL COMMENT '是否自动采集',
  `gather_time` datetime(0) NULL DEFAULT NULL COMMENT '上次采集时间',
  `time_type` int(10) NULL DEFAULT NULL COMMENT '时间类型，1:每月 2:每周',
  `day` int(10) NULL DEFAULT NULL COMMENT '日',
  `hour` int(10) NULL DEFAULT NULL COMMENT '时',
  `column_show_count` int(10) NULL DEFAULT NULL COMMENT '血缘分析字段显示数量',
  `renter_id` bigint(25) NULL DEFAULT NULL COMMENT '租户id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_table_ck_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_table_ck_oracle`;
CREATE TABLE `mc_table_ck_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检查约束名',
  `check_sql` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检查语法sql',
  `is_enabled` tinyint(1) NULL DEFAULT NULL COMMENT 'oracle 概念，是否启用，0:未启动 1:启用',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `location` int(10) NULL DEFAULT NULL COMMENT '位置（当前检查约束的一个位置标识，每张表的检查约束位置都是自增，唯一的）',
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除，0:未删除 1:已删除',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_table_column
-- ----------------------------
DROP TABLE IF EXISTS `mc_table_column`;
CREATE TABLE `mc_table_column`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `column_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段名',
  `column_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段类型',
  `type_length` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型长度',
  `type_precision` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型精度',
  `is_pk` tinyint(1) NULL DEFAULT NULL COMMENT '是否为主键 0:否,1:是',
  `is_auto_increment` tinyint(1) NULL DEFAULT NULL COMMENT '是否自增，0:否  1:是',
  `is_null` tinyint(1) NULL DEFAULT NULL COMMENT '是否允许为空 0:否 1:是',
  `default_value` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '默认值',
  `description` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段描述',
  `location` int(10) NULL DEFAULT NULL COMMENT '字段位置（当前字段的一个位置标识，每张表的字段位置都是自增，唯一的）',
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除，0:否 1:是',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  `is_partition` tinyint(1) NULL DEFAULT NULL COMMENT '是否是分区列(HIVE)',
  `index_partition` int(10) NULL DEFAULT NULL COMMENT '分区定义位置(HIVE)',
  `is_bucket` tinyint(1) NULL DEFAULT NULL COMMENT '是否作为分桶(HIVE)',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `table_id`(`table_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_table_fk_mysql
-- ----------------------------
DROP TABLE IF EXISTS `mc_table_fk_mysql`;
CREATE TABLE `mc_table_fk_mysql`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外键名',
  `column_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前表字段，可能有多个,以,隔开',
  `reference_schema_id` bigint(20) NULL DEFAULT NULL COMMENT '参考的模型id，在mysql中这里表示是数据库',
  `reference_table_id` bigint(20) NULL DEFAULT NULL COMMENT '参考表id',
  `reference_column` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参考列id，可能有多个，和当前表字段对应',
  `delete_trigger` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除时触发的事件',
  `update_trigger` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改时触发的事件',
  `location` int(10) NULL DEFAULT NULL COMMENT '位置（当一个位置标识，在每张表的索引位置都是自增，唯一的）',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除，0:未删除 1:已删除',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_table_fk_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_table_fk_oracle`;
CREATE TABLE `mc_table_fk_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外键名',
  `column_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前表字段，可能有多个,以,隔开',
  `reference_schema_id` bigint(20) NULL DEFAULT NULL COMMENT '参考模式id',
  `reference_table_id` bigint(20) NULL DEFAULT NULL COMMENT '参考表id',
  `reference_restrain` bigint(20) NULL DEFAULT NULL COMMENT '参考约束',
  `reference_restrain_type` int(10) NULL DEFAULT NULL COMMENT '参考约束类型',
  `reference_column` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '参考字段，字段是当选择约束时，自动录进去',
  `delete_trigger` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '删除时触发的事件',
  `is_enabled` tinyint(1) NULL DEFAULT NULL COMMENT 'oracle 概念，是否启用，0:未启动 1:启用',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `location` int(10) NULL DEFAULT NULL,
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除，0:未删除 1:已删除',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_table_idx_mysql
-- ----------------------------
DROP TABLE IF EXISTS `mc_table_idx_mysql`;
CREATE TABLE `mc_table_idx_mysql`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `index_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引名',
  `column_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前索引关联的字段，可能多个，以，隔开',
  `subdivision` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '子部分，对应字段，可以有多个',
  `index_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引类型',
  `index_method` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引方法',
  `location` int(10) NULL DEFAULT NULL COMMENT '索引位置（当前字段的一个位置标识，每张表的索引位置都是自增，唯一的）',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除，0:否 1:是',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_table_idx_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_table_idx_oracle`;
CREATE TABLE `mc_table_idx_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `index_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引名',
  `column_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前索引关联的字段，可能多个，以，隔开，oracle的每个字段都还对应一个排序方式：asc，desc',
  `column_sort` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '字段对应的排序规则（对应字段）',
  `index_type` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '索引类型',
  `tablespace` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表空间',
  `schema_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模式',
  `location` int(10) NULL DEFAULT NULL COMMENT '索引位置（当前字段的一个位置标识，每张表的索引位置都是自增，唯一的）',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除，0:否 1:是',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_table_pk_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_table_pk_oracle`;
CREATE TABLE `mc_table_pk_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主键约束名',
  `sequence_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '序列名（oracle自增长，全局可以唯一，或者自己创建序列）',
  `sequence_status` int(10) NULL DEFAULT NULL COMMENT '序列状态，1:无主键 2:未填充 3:从新序列填充 4:从已有序列填充',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除，0:未删除 1:已删除',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_table_set_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_table_set_oracle`;
CREATE TABLE `mc_table_set_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tablespace` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '表空间',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_table_un_oracle
-- ----------------------------
DROP TABLE IF EXISTS `mc_table_un_oracle`;
CREATE TABLE `mc_table_un_oracle`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '唯一约束名',
  `column_ids` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前表字段，可能有多个,以,隔开',
  `is_enabled` tinyint(1) NULL DEFAULT NULL COMMENT 'oracle 概念，是否启用，0:未启动 1:启用',
  `table_id` bigint(20) NULL DEFAULT NULL COMMENT '表id',
  `location` int(10) NULL DEFAULT NULL COMMENT '位置（当前检查约束的一个位置标识，每张表的检查约束位置都是自增，唯一的）',
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除，0:未删除 1:已删除',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_tag
-- ----------------------------
DROP TABLE IF EXISTS `mc_tag`;
CREATE TABLE `mc_tag`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `tag_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '标签名',
  `renter_id` bigint(20) NULL DEFAULT NULL COMMENT '租户ID',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前标签所属人id',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '当前标签录入时间',
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除，0:未删除 1:已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_theme
-- ----------------------------
DROP TABLE IF EXISTS `mc_theme`;
CREATE TABLE `mc_theme`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主题名',
  `theme_code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '主题代码',
  `use_count` int(10) NULL DEFAULT NULL COMMENT '当前主题使用次数',
  `renter_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id',
  `is_deleted` tinyint(1) NULL DEFAULT NULL COMMENT '是否删除 0:未删除 1:已删除',
  `creator` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `modifier` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '修改人',
  `modify_time` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for mc_view_detail
-- ----------------------------
DROP TABLE IF EXISTS `mc_view_detail`;
CREATE TABLE `mc_view_detail`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `view_sql` varchar(5000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '视图sql',
  `arithmetic` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '算法',
  `definiens` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '定义者',
  `security` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '安全性',
  `check_option` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '检查选项',
  `view_id` bigint(20) NULL DEFAULT NULL COMMENT '视图基本信息id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
