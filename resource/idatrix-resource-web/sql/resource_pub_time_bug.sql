-- 资源发布的时候存在Bug 资源下架后重新发布，资源发布时间为空
-- 查询有问题资源语句：
-- SELECT
-- 	*
-- FROM
-- 	rc_resource rr
-- WHERE
-- 	rr.`status` = "pub_success"
-- 	AND rr.pub_date IS NULL;


-- 处理问题语句
UPDATE rc_resource rr,
(
SELECT
	rra.resource_id AS resource_id,
	MAX( rra.approve_time ) AS pub_time
FROM
	rc_resource_approve rra
WHERE
	rra.next_status = "pub_success"
GROUP BY
	rra.resource_id
	) id_time_table
	SET rr.pub_date = id_time_table.pub_time
WHERE
	rr.STATUS = "pub_success"
	AND id_time_table.resource_id = rr.id
	AND rr.pub_date IS NULL;
