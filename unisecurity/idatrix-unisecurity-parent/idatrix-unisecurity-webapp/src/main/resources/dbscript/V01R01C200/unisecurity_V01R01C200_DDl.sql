-- 脱敏规则定义表
CREATE TABLE `idatrix_unisecurity_sensitive_info` (
  `id` int(10) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) DEFAULT NULL,
  `is_fixed_length` varchar(1) DEFAULT 'N',
  `begin` int(100) DEFAULT NULL,
  `end` int(100) DEFAULT NULL,
  `symbol` varchar(1) DEFAULT NULL,
  `original_info` varchar(200) DEFAULT NULL,
  `sentive_info` varchar(200) DEFAULT NULL,
  `creater` varchar(50) DEFAULT NULL,
  `dept_name` varchar(50) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='脱敏规则定义表';


-- 导入错误信息表
CREATE TABLE `idatrix_unisecurity_import_msg` (
  `id` bigint(10) NOT NULL AUTO_INCREMENT,
  `batch_id` varchar(10) DEFAULT NULL,
  `msg` varchar(200) DEFAULT NULL,
  `user_name` varchar(20) DEFAULT NULL,
  `file_name` varchar(50) DEFAULT NULL,
  `importor` varchar(20) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8;
