-- ES索引权限
insert  into `idatrix_unisecurity_permission` VALUES
(2502,731,'菜单','/EsIndexController/search','ES索引',1,NULL,'','idatrix-metacube-web'),
(2503,2502,'按钮','/EsIndexController/IndexAndType','新增',1,NULL,'','idatrix-metacube-web'),
(2504,2502,'按钮','/EsIndexController/Modify','修改',1,NULL,'','idatrix-metacube-web'),
(2505,2502,'按钮','/EsIndexController/updateStatus','修改状态',1,NULL,'','idatrix-metacube-web'),
(2506,2502,'按钮','/EsIndexController/history','历史版本',1,NULL,'','idatrix-metacube-web'),
(2507,2502,'按钮','/EsIndexController/switchVersion','切换版本',1,NULL,'','idatrix-metacube-web'),
(2511,24,'菜单','/es/index/list','全文检索',1,NULL,'','datalab'),
(2521,2511,'按钮','/es/search/custom','自定义搜索',1,NULL,'','datalab');


-- ES索引 角色权限
insert  into `idatrix_unisecurity_role_permission`(`rid`,`pid`,`create_time`) values (1,2502,'2017-08-22 16:48:35');
insert  into `idatrix_unisecurity_role_permission`(`rid`,`pid`,`create_time`) values (1,2503,'2017-08-22 16:48:35');
insert  into `idatrix_unisecurity_role_permission`(`rid`,`pid`,`create_time`) values (1,2504,'2017-08-22 16:48:35');
insert  into `idatrix_unisecurity_role_permission`(`rid`,`pid`,`create_time`) values (1,2505,'2017-08-22 16:48:35');
insert  into `idatrix_unisecurity_role_permission`(`rid`,`pid`,`create_time`) values (1,2506,'2017-08-22 16:48:35');
insert  into `idatrix_unisecurity_role_permission`(`rid`,`pid`,`create_time`) values (1,2507,'2017-08-22 16:48:35');
insert  into `idatrix_unisecurity_role_permission`(`rid`,`pid`,`create_time`) values (1,2511,'2017-08-22 16:48:35');
insert  into `idatrix_unisecurity_role_permission`(`rid`,`pid`,`create_time`) values (1,2521,'2017-08-22 16:48:35');