CREATE TABLE IF NOT EXISTS `t_application_plugins_config` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `application_id` bigint(20) NOT NULL COMMENT '应用id',
    `application_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '应用名称',
    `config_item` varchar(256) NOT NULL COMMENT '配置项',
    `config_key` varchar(255) DEFAULT NULL COMMENT '配置项key',
    `config_desc` varchar(256) NOT NULL COMMENT '配置说明',
    `config_value` varchar(256) DEFAULT NULL COMMENT '配置值',
    `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
    `customer_id` bigint(20) DEFAULT NULL COMMENT '租户Id',
    `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
    `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modifie_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `creator_id` bigint(20) DEFAULT NULL COMMENT '创建人',
    `modifier_id` bigint(20) DEFAULT NULL COMMENT '更新人',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 修改索引开始
DROP PROCEDURE
    IF
    EXISTS change_index;

DELIMITER $$
CREATE PROCEDURE change_index () BEGIN
	DECLARE
count1 INT;
	DECLARE
count2 INT;

	SET count1 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_tro_user' AND index_name = 'idx_name' );

	SET count2 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_tro_user' AND index_name = 'idx_name' );
	IF
count1 > 0 THEN

ALTER TABLE `t_tro_user` DROP INDEX `idx_name`;

IF
count2 = 0 THEN
ALTER TABLE `t_tro_user` ADD UNIQUE INDEX `idx_name`(`name`, `customer_id`) USING BTREE;

END IF;
ELSE
		IF
			count2 = 0 THEN
ALTER TABLE `t_probe` ADD INDEX `idx_ci_v_u` ( `customer_id`, `version`, `gmt_update` ) USING BTREE;

END IF;

END IF;

END $$

DELIMITER ;
CALL change_index ();
DROP PROCEDURE
    IF
    EXISTS change_index;
-- 修改索引结束