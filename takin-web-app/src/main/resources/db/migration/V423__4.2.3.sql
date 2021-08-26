CREATE TABLE IF NOT EXISTS `t_tro_trace_entry`
(
    `id`           int(11) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
    `app_name`     varchar(128)     NOT NULL COMMENT '应用',
    `entry`        varchar(250)     NOT NULL COMMENT '入口',
    `method`       varchar(50) DEFAULT NULL COMMENT '方法',
    `status`       varchar(20)      NOT NULL COMMENT '状态',
    `start_time`   bigint(20)       NOT NULL COMMENT '开始时间',
    `end_time`     bigint(20)       NOT NULL COMMENT '结束时间',
    `process_time` bigint(20)       NOT NULL COMMENT '耗时',
    `trace_id`     varchar(30)      NOT NULL COMMENT 'traceId',
    PRIMARY KEY (`id`),
    KEY `start_time` (`start_time`, `end_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8 COMMENT ='trace入口列表';

-- 添加索引
DROP PROCEDURE
    IF
    EXISTS change_index;

DELIMITER $$
CREATE PROCEDURE change_index () BEGIN
	DECLARE
count1 INT;

	DECLARE
count2 INT;

	SET count1 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_base_config' AND index_name = 'unique_idx_config_code' );

	SET count2 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_tro_trace_entry' AND index_name = 'entry' );


	IF
count1 = 0 THEN
ALTER TABLE `t_base_config`
    ADD UNIQUE `unique_idx_config_code` (`config_code`);

END IF;

	IF
count2 = 0 THEN
ALTER TABLE `t_tro_trace_entry`
    ADD INDEX entry (`entry`);
END IF;

END $$

DELIMITER ;
CALL change_index ();
DROP PROCEDURE
    IF
    EXISTS change_index;


-- 字段添加
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count1 INT;
DECLARE count2 INT;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_mnt' AND COLUMN_NAME = 'NODE_NUM');

SET count2 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_mnt' AND COLUMN_NAME = 'SWITCH_STATUS');

IF count1 = 0 THEN

ALTER TABLE `t_application_mnt`
    ADD COLUMN `NODE_NUM` int(4) NOT NULL DEFAULT '1' COMMENT '节点数量';

END IF;

IF count2 = 0 THEN

ALTER TABLE `t_application_mnt`
    ADD COLUMN `SWITCH_STATUS` varchar(255) NOT NULL DEFAULT 'OPENED' COMMENT 'OPENED："已开启",OPENING："开启中",OPEN_FAILING："开启异常",CLOSED："已关闭",CLOSING："关闭中",CLOSE_FAILING："关闭异常"';

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;