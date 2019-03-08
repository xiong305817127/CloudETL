
SELECT
	*
FROM
	idatrix_unisecurity_permission
WHERE
	NAME IN (
		'数据库查询',
		'过滤记录',
		'空操作',
		'生成记录',
		'合并加入',
		'记录集连接',
		'排序合并',
		'获取变量',
		'记录关联(输出)',
		'获取系统信息',
		'设置变量',
		'Parquet输出',
		'Parquet输入',
		'公式',
		'读取内容'
		) and id>=2691;


UPDATE idatrix_unisecurity_permission
SET type = '按钮'
WHERE
	id IN (2691, 2701, 2731, 2741, 2751, 2761, 2771, 2781, 2791, 2801, 2811, 2856, 2857, 2858, 2861);



-- 神算子修改成大数据应用开发环境
update idatrix_unisecurity_permission set name='大数据应用开发环境' where id = 27;


-- 监控管理改成运维监控
update idatrix_unisecurity_permission set name='运维监控' where id = 30;




