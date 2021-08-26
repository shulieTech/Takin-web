CREATE TABLE IF NOT EXISTS `t_application_node_probe`  (
                                             `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
                                             `customer_id` bigint(20) UNSIGNED NOT NULL DEFAULT 1 COMMENT '租户id',
                                             `application_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '应用名称',
                                             `agent_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'agentId',
                                             `operate` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '操作类型, 1 安装, 3 升级, 2 卸载, 0 无',
                                             `operate_result` tinyint(4) UNSIGNED NULL DEFAULT 99 COMMENT '操作结果, 0 失败, 1 成功, 99 无',
                                             `probe_id` bigint(20) UNSIGNED NULL DEFAULT 0 COMMENT '对应的探针包记录id, 卸载的时候不用填',
                                             `gmt_create` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                             `gmt_update` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                             `is_deleted` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '应用节点探针操作表' ROW_FORMAT = Dynamic;

-- application 增加 id
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_node_probe' AND COLUMN_NAME = 'operate_id');

IF count = 0 THEN

ALTER TABLE `t_application_node_probe` ADD COLUMN `operate_id` bigint(20) UNSIGNED NULL DEFAULT 0 COMMENT '操作的id, 时间戳, 递增, agent 需要, 进行操作的时候会创建或更新' AFTER `operate_result`;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_mnt' AND COLUMN_NAME = 'id');

IF count = 0 THEN

ALTER TABLE `t_application_mnt` ADD COLUMN `id` bigint(20) UNSIGNED NOT NULL FIRST;

END IF;

SET count = (  SELECT COUNT(*) FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE table_schema = DATABASE () AND TABLE_NAME = 't_application_mnt' AND COLUMN_NAME = 'id' );

IF count = 0 THEN

ALTER TABLE `t_application_mnt`
    MODIFY COLUMN `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT FIRST,
DROP PRIMARY KEY,
ADD PRIMARY KEY (`id`) USING BTREE;

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;
-- application 增加 id 结束