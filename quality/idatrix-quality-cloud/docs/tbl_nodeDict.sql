/*
Navicat MySQL Data Transfer

Source Server         : 10.0.0.88
Source Server Version : 50621
Source Host           : 10.0.0.88:3306
Source Database       : qualityDB

Target Server Type    : MYSQL
Target Server Version : 50621
File Encoding         : 65001

Date: 2018-10-09 15:43:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_nodeDict
-- ----------------------------
DROP TABLE IF EXISTS `tbl_nodeDict`;
CREATE TABLE `tbl_nodeDict` (
  `ID` varchar(255) NOT NULL,
  `DICTNAME` varchar(255) DEFAULT NULL,
  `SPLINDEX` varchar(255) DEFAULT NULL,
  `STDVAL1` varchar(255) DEFAULT NULL,
  `SIMVAL2` varchar(255) DEFAULT NULL,
  `SIMVAL3` varchar(255) DEFAULT NULL,
  `SIMVAL4` varchar(255) DEFAULT NULL,
  `SIMVAL5` varchar(255) DEFAULT NULL,
  `SIMVAL6` varchar(255) DEFAULT NULL,
  `SIMVAL7` varchar(255) DEFAULT NULL,
  `SIMVAL8` varchar(255) DEFAULT NULL,
  `SIMVAL9` varchar(255) DEFAULT NULL,
  `SIMVAL10` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbl_nodeDict
-- ----------------------------
INSERT INTO `tbl_nodeDict` VALUES ('1', '电话号码格式', ',', '(NNN)NNNNNNNNNNN', 'NNNNNNNNNNN', '-NNN-NNN-NNN', '--- NNNNNNNNNNN', 'NNN-NNNNNNNNNNN', 'NN-NNNNNNNNNNN', 'N---NNNNNNNNNNN', '-NN-NNNNNNNNNNN', '--NNNNNNNNNNN', null);
INSERT INTO `tbl_nodeDict` VALUES ('2', '日期格式', ',', 'YYYY-MM-DD', 'YYYY-MMDD', 'YY-MM-DD', 'YYYY/MM/DD', 'YYYY/MMDD', 'YY/MM/DD', 'YYYYMMDD', 'YYMMDD', null, null);
INSERT INTO `tbl_nodeDict` VALUES ('3', '证件类', ',', '18位身份证号规则', '旧身份证15位规则', null, null, null, null, null, null, null, null);
