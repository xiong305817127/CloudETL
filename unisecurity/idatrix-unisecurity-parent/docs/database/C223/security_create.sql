/*
Navicat MySQL Data Transfer

Source Server         : 10.0.0.85
Source Server Version : 50718
Source Host           : 10.0.0.85:3306
Source Database       : idatrix_unisecurity

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2018-12-03 16:01:52
*/

-- 建安全库
CREATE DATABASE `idatrix_unisecurity` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for idatrix_unisecurity_audit_log
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_audit_log`;
CREATE TABLE `idatrix_unisecurity_audit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `server` varchar(20) NOT NULL COMMENT '请求的服务',
  `resource` varchar(60) NOT NULL COMMENT '请求的url',
  `method_type` varchar(20) NOT NULL COMMENT '请求方式',
  `client_ip` varchar(20) NOT NULL COMMENT '客户的ip',
  `visit_time` datetime DEFAULT NULL COMMENT '请求的时间',
  `result` varchar(120) DEFAULT NULL COMMENT '结果',
  `user_id` varchar(20) DEFAULT NULL COMMENT '用户id',
  `user_name` varchar(20) DEFAULT NULL COMMENT '用户名',
  `renter_id` varchar(20) DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65593 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for idatrix_unisecurity_client_system
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_client_system`;
CREATE TABLE `idatrix_unisecurity_client_system` (
  `id` varchar(100) NOT NULL COMMENT '主键id',
  `name` varchar(256) DEFAULT NULL COMMENT '系统名称',
  `base_url` varchar(256) DEFAULT NULL COMMENT '应用访问起始点',
  `home_uri` varchar(256) DEFAULT NULL COMMENT '应用主页面URI',
  `inner_address` varchar(256) DEFAULT NULL COMMENT '系统间内部通信地址',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for idatrix_unisecurity_import_msg
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_import_msg`;
CREATE TABLE `idatrix_unisecurity_import_msg` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `batch_id` varchar(10) DEFAULT NULL,
  `msg` varchar(200) DEFAULT NULL,
  `user_name` varchar(20) DEFAULT NULL,
  `file_name` varchar(50) DEFAULT NULL,
  `importor` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `password` varchar(20) DEFAULT NULL COMMENT '密码',
  `real_name` varchar(20) DEFAULT NULL COMMENT '真实姓名',
  `sex` int(1) DEFAULT NULL COMMENT '1-男， 2-女',
  `age` int(2) DEFAULT NULL COMMENT '年龄',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `card_id` varchar(20) DEFAULT NULL COMMENT '身份证',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=367 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for idatrix_unisecurity_mail_log
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_mail_log`;
CREATE TABLE `idatrix_unisecurity_mail_log` (
  `id` int(25) NOT NULL COMMENT 'id',
  `subject` varchar(100) DEFAULT NULL COMMENT '标题',
  `send_server` varchar(100) DEFAULT NULL COMMENT '发送服务器',
  `content` varchar(200) DEFAULT NULL COMMENT '邮件内容',
  `recipient` varchar(100) DEFAULT NULL COMMENT '接收服务器',
  `status` varchar(1) DEFAULT NULL COMMENT '邮件状态 F-发送失败，S-发送成功;P-发送中',
  `msg` varchar(200) DEFAULT NULL COMMENT 'msg',
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for idatrix_unisecurity_organization
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_organization`;
CREATE TABLE `idatrix_unisecurity_organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT NULL,
  `renter_id` bigint(20) DEFAULT NULL,
  `dept_code` varchar(100) DEFAULT NULL COMMENT '部门编码',
  `dept_name` varchar(100) DEFAULT NULL COMMENT '部门名称',
  `remark` text,
  `is_active` tinyint(1) DEFAULT NULL COMMENT '是否有效(1表示有效，0表示无效)',
  `create_time` datetime DEFAULT NULL,
  `last_updated_by` datetime DEFAULT NULL,
  `unified_credit_code` varchar(30) DEFAULT NULL COMMENT '社会统一信用代码',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4678 DEFAULT CHARSET=utf8 COMMENT='部门表';

-- ----------------------------
-- Table structure for idatrix_unisecurity_permission
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_permission`;
CREATE TABLE `idatrix_unisecurity_permission` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parent_id` bigint(20) DEFAULT NULL,
  `type` varchar(20) DEFAULT NULL COMMENT '权限类型(菜单,按钮)',
  `url` varchar(256) DEFAULT NULL COMMENT 'url地址',
  `name` varchar(64) DEFAULT NULL COMMENT 'url描述',
  `is_show` tinyint(4) DEFAULT NULL,
  `show_order` int(11) DEFAULT NULL,
  `url_desc` varchar(256) DEFAULT NULL,
  `client_system_id` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2915 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for idatrix_unisecurity_pwd_question
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_pwd_question`;
CREATE TABLE `idatrix_unisecurity_pwd_question` (
  `id` bigint(20) NOT NULL COMMENT '主键id',
  `question_num` varchar(5) DEFAULT NULL COMMENT '问题序号',
  `question_name` text COMMENT '问题名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='密码问题列表';

-- ----------------------------
-- Table structure for idatrix_unisecurity_renter
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_renter`;
CREATE TABLE `idatrix_unisecurity_renter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '租户id',
  `renter_name` varchar(200) DEFAULT NULL COMMENT '租户名称',
  `admin_account` varchar(20) DEFAULT NULL COMMENT '管理员账号',
  `admin_name` varchar(20) DEFAULT NULL COMMENT '管理员姓名',
  `admin_email` varchar(512) DEFAULT NULL,
  `admin_phone` varchar(20) DEFAULT NULL COMMENT '管理员电话',
  `opened_service` varchar(1000) DEFAULT NULL COMMENT '开通服务',
  `opened_resource` varchar(500) DEFAULT NULL COMMENT '开通资源',
  `renter_status` tinyint(1) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `last_updated_by` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=488 DEFAULT CHARSET=utf8 COMMENT='租户表';

-- ----------------------------
-- Table structure for idatrix_unisecurity_role
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_role`;
CREATE TABLE `idatrix_unisecurity_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `renter_id` bigint(20) DEFAULT NULL,
  `code` varchar(20) DEFAULT NULL,
  `name` varchar(32) DEFAULT NULL COMMENT '角色名称',
  `type` varchar(10) DEFAULT NULL COMMENT '角色类型',
  `is_active` tinyint(4) DEFAULT NULL,
  `remark` text,
  `create_time` datetime DEFAULT NULL,
  `last_update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=773 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for idatrix_unisecurity_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_role_permission`;
CREATE TABLE `idatrix_unisecurity_role_permission` (
  `rid` bigint(20) DEFAULT NULL COMMENT '角色ID',
  `pid` bigint(20) DEFAULT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for idatrix_unisecurity_role_sys
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_role_sys`;
CREATE TABLE `idatrix_unisecurity_role_sys` (
  `role_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `client_system_id` varchar(100) DEFAULT NULL COMMENT '业务系统id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='色角系统表';

-- ----------------------------
-- Table structure for idatrix_unisecurity_user
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_user`;
CREATE TABLE `idatrix_unisecurity_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `renter_id` bigint(20) DEFAULT NULL,
  `dept_id` bigint(20) DEFAULT NULL,
  `username` varchar(20) DEFAULT NULL COMMENT '用户昵称',
  `real_name` varchar(128) DEFAULT NULL,
  `sex` bigint(20) DEFAULT NULL,
  `age` int(11) DEFAULT NULL,
  `email` varchar(512) DEFAULT NULL COMMENT '邮箱|登录帐号',
  `card_id` varchar(20) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `pswd` varchar(32) DEFAULT NULL COMMENT '密码',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `status` bigint(1) DEFAULT '1' COMMENT '1:有效，0:禁止登录',
  `login_token` varchar(256) DEFAULT NULL,
  `out_date` datetime DEFAULT NULL,
  `last_updated_date` datetime DEFAULT NULL,
  `q_one` text,
  `q_two` text,
  `q_three` text,
  `a_one` text,
  `a_two` text,
  `a_three` text,
  `validate_code` varchar(256) DEFAULT NULL,
  `visit_times` bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1252 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for idatrix_unisecurity_user_role
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_user_role`;
CREATE TABLE `idatrix_unisecurity_user_role` (
  `uid` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `rid` bigint(20) DEFAULT NULL COMMENT '角色ID'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for idatrix_unisecurity_user_statistic
-- ----------------------------
DROP TABLE IF EXISTS `idatrix_unisecurity_user_statistic`;
CREATE TABLE `idatrix_unisecurity_user_statistic` (
  `uid` bigint(20) DEFAULT NULL COMMENT '用户id',
  `client_system_id` varchar(100) DEFAULT NULL COMMENT '子系统id',
  `last_accessed` datetime DEFAULT NULL COMMENT '最近访问时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户统计表';

-- ----------------------------
-- Function structure for queryChildrenOrganizationInfo
-- ----------------------------
DROP FUNCTION IF EXISTS `queryChildrenOrganizationInfo`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `queryChildrenOrganizationInfo`(detpId INT) RETURNS varchar(4000) CHARSET utf8
BEGIN

DECLARE sTemp VARCHAR(4000);

DECLARE sTempChd VARCHAR(4000);


SET sTemp = '$';

SET sTempChd = cast(detpId as char);


WHILE sTempChd is not NULL DO

SET sTemp = CONCAT(sTemp,',',sTempChd);

SELECT group_concat(id) INTO sTempChd FROM idatrix_unisecurity_organization where FIND_IN_SET(parent_id,sTempChd)>0;

END WHILE;

return sTemp;

END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for queryChildrenPermitInfo
-- ----------------------------
DROP FUNCTION IF EXISTS `queryChildrenPermitInfo`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `queryChildrenPermitInfo`(permitId BIGINT) RETURNS varchar(4000) CHARSET utf8
BEGIN

DECLARE sTemp VARCHAR(4000);

DECLARE sTempChd VARCHAR(4000);


SET sTemp = '$';

SET sTempChd = cast(permitId as char);


WHILE sTempChd is not NULL DO

SET sTemp = CONCAT(sTemp,',',sTempChd);

SELECT group_concat(id) INTO sTempChd FROM idatrix_unisecurity_permission where FIND_IN_SET(parent_id,sTempChd)>0;

END WHILE;

return sTemp;

END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for queryParentIdsByDeptCode
-- ----------------------------
DROP FUNCTION IF EXISTS `queryParentIdsByDeptCode`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `queryParentIdsByDeptCode`(deptCode VARCHAR(20),renterId INT) RETURNS varchar(4000) CHARSET utf8
BEGIN
DECLARE sTemp VARCHAR(4000);
DECLARE sTempParentIds VARCHAR(4000);
SET sTemp = '$';
SET sTempParentIds = '$';
SELECT GROUP_CONCAT(id) INTO sTempParentIds FROM idatrix_unisecurity_organization WHERE dept_code =deptCode AND renter_id=renterId;
WHILE sTempParentIds IS NOT NULL DO
SET sTemp = CONCAT(sTemp,',',sTempParentIds);
SELECT GROUP_CONCAT(parent_id) INTO sTempParentIds FROM idatrix_unisecurity_organization WHERE FIND_IN_SET(id,sTempParentIds)>0;
END WHILE;
RETURN sTemp;
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for queryParentIdsByUnifiedCode
-- ----------------------------
DROP FUNCTION IF EXISTS `queryParentIdsByUnifiedCode`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` FUNCTION `queryParentIdsByUnifiedCode`(unifiedCode VARCHAR(20),renterId INT) RETURNS varchar(4000) CHARSET utf8
BEGIN
DECLARE sTemp VARCHAR(4000);
DECLARE sTempParentIds VARCHAR(4000);
SET sTemp = '$';
SET sTempParentIds = '$';
SELECT GROUP_CONCAT(id) INTO sTempParentIds FROM idatrix_unisecurity_organization WHERE unified_credit_code =unifiedCode AND renter_id=renterId;
WHILE sTempParentIds IS NOT NULL DO
SET sTemp = CONCAT(sTemp,',',sTempParentIds);
SELECT GROUP_CONCAT(parent_id) INTO sTempParentIds FROM idatrix_unisecurity_organization WHERE FIND_IN_SET(id,sTempParentIds)>0;
END WHILE;
RETURN sTemp;
END
;;
DELIMITER ;
