ALTER TABLE `trodb`.`t_report_business_activity_detail`
    MODIFY COLUMN `rt_distribute` text CHARACTER SET utf8 COLLATE utf8_bin NULL COMMENT '分布范围，格式json' AFTER `target_rt`;