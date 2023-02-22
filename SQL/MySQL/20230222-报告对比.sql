ALTER TABLE `trodb`.`t_report`
    ADD COLUMN `report_remarks` varchar(255) NULL COMMENT '压测报告备注' AFTER `pt_config`;

ALTER TABLE `trodb`.`t_report_business_activity_detail`
    ADD COLUMN `report_json` text NULL COMMENT '压测报告json持久化数据' AFTER `gmt_update`;