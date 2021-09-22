CREATE TABLE IF NOT EXISTS `t_base_config` (
    `CONFIG_CODE` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置编码',
    `CONFIG_VALUE` longtext COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置值',
    `CONFIG_DESC` varchar(128) COLLATE utf8mb4_general_ci NOT NULL COMMENT '配置说明',
    `USE_YN` int DEFAULT '0' COMMENT '是否可用(0表示未启用,1表示启用)',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
    `UPDATE_TIME` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`CONFIG_CODE`) USING BTREE,
    UNIQUE KEY `unique_idx_config_code` (`CONFIG_CODE`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='tro配置表';

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
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN
DECLARE count1 INT;
DECLARE count2 INT;

	SET count1 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_base_config' AND index_name = 'unique_idx_config_code' );
	SET count2 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_tro_trace_entry' AND index_name = 'entry' );

	IF count1 = 0 THEN
ALTER TABLE `t_base_config` ADD UNIQUE `unique_idx_config_code` (`config_code`);
END IF;

	IF count2 = 0 THEN
ALTER TABLE `t_tro_trace_entry` ADD INDEX entry (`entry`);
END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;