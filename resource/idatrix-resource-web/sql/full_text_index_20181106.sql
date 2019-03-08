-- 用于资源目录数据库创建索引
-- 1. 目标：数据库使用索引提高查询效率
-- 2. 使用步骤
--    a. 数据库属性配置：
--        [mysqld]
--        ft_min_word_len=1
--        ngram_token_size=1
--   查询确认配置
--        SHOW GLOBAL VARIABLES LIKE '%ft_%';
--        SHOW GLOBAL VARIABLES LIKE 'ngram_token_size';
--
--    b.运行以下索引：
--
-- 制作 : robin
-- 日期 ：2018/11/06



ALTER TABLE rc_catalog_resource ADD INDEX `idx_catalog_id_resource_id` (`catalog_id`, `resource_id`) USING BTREE;
ALTER TABLE rc_data_upload ADD INDEX `idx_resource_id` (`resource_id`) USING BTREE;
ALTER TABLE rc_data_upload_detail ADD INDEX `idx_data_upload_id` (`parent_id`) USING BTREE;
ALTER TABLE rc_dept_limited ADD INDEX `idx_resource_id_dept_id` (`resource_id`,`dept_id`) USING BTREE;
ALTER TABLE rc_resource ADD INDEX `idx_dept_code` (`dept_code`) USING BTREE;
ALTER TABLE rc_resource ADD INDEX `idx_catalog_code` (`catalog_code`) USING BTREE;
ALTER TABLE rc_resource ADD INDEX `idx_code` (`code`) USING BTREE;
ALTER TABLE `rc_resource` ADD FULLTEXT INDEX `ft_name_dept_name` (`name`, `dept_name`) COMMENT '资源名称和提供方名称建立全文检索字段' WITH PARSER ngram;
ALTER TABLE rc_resource_approve ADD INDEX `idx_resource_id` (`resource_id`) USING BTREE;
ALTER TABLE rc_resource_column ADD INDEX `idx_resource_id` (`resource_id`) USING BTREE;
ALTER TABLE rc_resource_file ADD INDEX `idx_resource_id` (`resource_id`) USING BTREE;
ALTER TABLE rc_resource_history ADD INDEX `idx_resource_id` (`resource_id`) USING BTREE;
ALTER TABLE rc_service_log_detail ADD INDEX `idx_service_log_id` (`parent_id`) USING BTREE;
ALTER TABLE rc_subscribe ADD INDEX `idx_resource_id` (`resource_id`) USING BTREE;
ALTER TABLE rc_subscribe_dbio ADD INDEX `idx_subscribe_id` (`subscribe_id`) USING BTREE;