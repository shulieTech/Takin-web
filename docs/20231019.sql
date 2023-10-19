ALTER TABLE `t_report` ADD COLUMN `stop_time` datetime DEFAULT NULL COMMENT '停止时间' after `end_time`;
ALTER TABLE `t_report` ADD INDEX `idx_job_id` (`job_id`);
