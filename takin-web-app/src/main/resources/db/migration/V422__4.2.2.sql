-- 报告汇总表
CREATE TABLE IF NOT EXISTS `t_report_summary`
(
    `id`                                bigint(20) NOT NULL AUTO_INCREMENT,
    `report_id`                         bigint(20) NOT NULL,
    `bottleneck_interface_count`        int(11) DEFAULT NULL COMMENT '瓶颈接口',
    `risk_machine_count`                int(11) DEFAULT NULL COMMENT '风险机器数',
    `business_activity_count`           int(11) DEFAULT NULL COMMENT '业务活动数',
    `unachieve_business_activity_count` int(11) DEFAULT NULL COMMENT '未达标业务活动数',
    `application_count`                 int(11) DEFAULT NULL COMMENT '应用数',
    `machine_count`                     int(11) DEFAULT NULL COMMENT '机器数',
    `warn_count`                        int(11) DEFAULT NULL COMMENT '告警次数',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_report_id` (`report_id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COLLATE = utf8_bin;

-- 报告机器列表
CREATE TABLE IF NOT EXISTS `t_report_machine`
(
    `id`                        bigint(20) NOT NULL AUTO_INCREMENT,
    `report_id`                 bigint(20) NOT NULL,
    `application_name`          varchar(64) COLLATE utf8_bin  DEFAULT NULL COMMENT '应用名称',
    `machine_ip`                varchar(32) COLLATE utf8_bin  DEFAULT NULL COMMENT '机器ip',
    `machine_base_config`       varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '机器基本信息',
    `machine_tps_target_config` text COLLATE utf8_bin COMMENT '机器tps对应指标信息',
    `risk_value`                decimal(16, 6)                DEFAULT NULL COMMENT '风险计算值',
    `risk_flag`                 tinyint(4)                    DEFAULT NULL COMMENT '是否风险机器(0-否，1-是)',
    `risk_content`              varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '风险提示内容',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_report_application_machine` (`report_id`, `application_name`, `machine_ip`) USING BTREE,
    KEY `idx_report_id_application_name` (`report_id`, `application_name`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COLLATE = utf8_bin;

-- 报告瓶颈列表
CREATE TABLE IF NOT EXISTS `t_report_bottleneck_interface`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `report_id`         bigint(20) NOT NULL,
    `application_name`  varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `sort_no`           int(11)                       DEFAULT NULL,
    `interface_type`    varchar(32) COLLATE utf8_bin  DEFAULT NULL COMMENT '接口类型',
    `interface_name`    varchar(512) COLLATE utf8_bin DEFAULT NULL,
    `tps`               decimal(10, 2)                DEFAULT NULL,
    `rt`                decimal(10, 2)                DEFAULT NULL,
    `node_count`        int(11)                       DEFAULT NULL,
    `error_reqs`        int(11)                       DEFAULT NULL,
    `bottleneck_weight` decimal(16, 10)               DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_report_id` (`report_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COLLATE = utf8_bin;

-- 应用汇总表
CREATE TABLE IF NOT EXISTS `t_report_application_summary`
(
    `id`                  bigint(20)                   NOT NULL AUTO_INCREMENT,
    `report_id`           bigint(20)                   NOT NULL,
    `application_name`    varchar(64) COLLATE utf8_bin NOT NULL,
    `machine_total_count` int(11) DEFAULT NULL COMMENT '总机器数',
    `machine_risk_count`  int(11) DEFAULT NULL COMMENT '风险机器数',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_idx_report_appliacation` (`report_id`, `application_name`),
    KEY `idx_report_id` (`report_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8 COLLATE = utf8_bin;

-- 应用表添加开关状态字段
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_mnt' AND COLUMN_NAME = 'SWITCH_STATUS');

IF count = 0 THEN

ALTER TABLE t_application_mnt ADD COLUMN `SWITCH_STATUS` varchar(255) NOT NULL DEFAULT 'OPENED' COMMENT 'OPENED："已开启",OPENING："开启中",OPEN_FAILING："开启异常",CLOSED："已关闭",CLOSING："关闭中",CLOSE_FAILING："关闭异常"' after ACCESS_STATUS;

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;
