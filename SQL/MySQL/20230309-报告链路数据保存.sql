ALTER TABLE `trodb`.`t_pressure_resource_relate_shadow_mq_consumer`
    ADD COLUMN `relate_ds_manage_id` bigint(0) NULL COMMENT '关联application_ds_manage表的字段' AFTER `gmt_modified`;


ALTER TABLE `trodb`.`t_report_business_activity_detail`
    MODIFY COLUMN `report_json` longtext CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '压测报告json持久化数据' AFTER `gmt_update`;
