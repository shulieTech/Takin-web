ALTER TABLE `trodb`.`t_scene_manage`
    ADD COLUMN `auto_start_sla_flag` tinyint  default 1 NULL COMMENT '是否自动设置sre的sla' AFTER `business_flow_id`;


ALTER TABLE `trodb`.`t_report_business_activity_detail`
    ADD COLUMN `diagnosis_id` bigint NULL COMMENT 'sre任务ID' AFTER `report_json`;

ALTER TABLE `trodb`.`t_report_business_activity_detail`
    ADD COLUMN `chain_code` varchar(64) NULL COMMENT 'sre的链路code' AFTER `diagnosis_id`;