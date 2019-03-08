-- 运行此脚本可以查看统计数据
-- 需要强制更新时候，运行此脚本
-- 统计方式： 
-- 1. data_count 表示上传数据，数据库类型资源表示资源条数，文件类型表示文件个数
-- 2. data_update_time 表示数据更新时间主要是数据上报时间
-- 3. sub_count 表示资源订阅次数，订阅审核成功时，次数才加1
-- 4. share_data_count 表示资源被交换数据 条数
-- 制作 : robin
-- 日期 ：2018/09/05

SELECT
	orgg_id.id AS id,
	orgg_id.resource_id AS resource_id,
	ifnull( orgg_id.data_count, 0 ) AS data_count,
	orgg_id.data_update_time AS data_update_time,
	orgg_id.import_status AS import_status,
	st.sub_key AS sub_key,
	st.STATUS AS sub_status,
	st.resource_id AS sub_resource_id,
	ifnull( st.sub_count, 0 ) AS sub_count,
	ifnull( st.share_data_count, 0 ) AS share_data_count 
FROM
	(
SELECT
	org_id.resource_id AS id,
	ut.resource_id AS resource_id,
	ut.data_count AS data_count,
	ut.data_update_time AS data_update_time,
	ut.import_status AS import_status 
FROM
	( SELECT resource_id FROM rc_data_upload t_rdu WHERE t_rdu.STATUS = "IMPORT_COMPLETE" UNION SELECT resource_id FROM rc_subscribe t_rs WHERE t_rs.STATUS = "success" ) org_id
	LEFT JOIN (
SELECT
	rdu.resource_id AS resource_id,
	SUM( rdu.import_count ) AS data_count,
	rdu.import_time AS data_update_time,
	rdu.STATUS AS import_status 
FROM
	rc_data_upload rdu 
WHERE
	rdu.STATUS = "IMPORT_COMPLETE" 
GROUP BY
	rdu.resource_id 
	) ut ON org_id.resource_id = ut.resource_id 
	) orgg_id
	LEFT JOIN (
SELECT
	rs.sub_no AS sub_key,
	rs.STATUS AS STATUS,
	rs.resource_id AS resource_id,
	count( resource_id ) AS sub_count,
	ifnull( sum( rst.import_count ), 0 ) AS share_data_count 
FROM
	rc_subscribe rs
	LEFT JOIN rc_sub_task rst ON rs.id = rst.id 
WHERE
	rs.STATUS = "success" 
GROUP BY
	rs.resource_id 
	) st ON orgg_id.id = st.resource_id