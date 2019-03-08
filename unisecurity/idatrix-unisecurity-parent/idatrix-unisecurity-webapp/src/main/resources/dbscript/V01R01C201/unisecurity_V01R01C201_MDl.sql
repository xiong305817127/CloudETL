-- 元数据采集
insert  into `idatrix_unisecurity_permission` VALUES
(2531,29,'菜单','/directDataCollect/getdbinfo','外部数据源采集',1,NULL,'','idatrix-metacube-web');


-- 元数据采集角色权限
insert  into `idatrix_unisecurity_role_permission`(`rid`,`pid`,`create_time`) values (1,2531,'2017-08-22 16:48:35');


-- idatrix_unisecurity_import_msg 
ALTER TABLE idatrix_unisecurity_import_msg ADD COLUMN password VARCHAR(20) COMMENT '密码';
ALTER TABLE idatrix_unisecurity_import_msg ADD COLUMN real_name VARCHAR(20) COMMENT '真实姓名';
ALTER TABLE idatrix_unisecurity_import_msg ADD COLUMN sex INT(1) COMMENT '1-男， 2-女';
ALTER TABLE idatrix_unisecurity_import_msg ADD COLUMN age INT(2) COMMENT '年龄';
ALTER TABLE idatrix_unisecurity_import_msg ADD COLUMN email VARCHAR(100) COMMENT '邮箱';
ALTER TABLE idatrix_unisecurity_import_msg ADD COLUMN card_id VARCHAR(18) COMMENT '身份证';
ALTER TABLE idatrix_unisecurity_import_msg ADD COLUMN phone VARCHAR(20) COMMENT '手机';