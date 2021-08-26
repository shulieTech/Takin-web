DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count INT;
DECLARE count2 INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_node_probe' AND COLUMN_NAME = 'remark');

SET count2 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_node_probe' AND COLUMN_NAME = 'operate_id');

IF count = 0 THEN

ALTER TABLE `t_application_node_probe` ADD COLUMN `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '备注信息' AFTER `probe_id`;

END IF;

IF count2 = 0 THEN

ALTER TABLE `t_application_node_probe` ADD COLUMN `operate_id` bigint(20) UNSIGNED NULL DEFAULT 0 COMMENT '操作的id, 时间戳, 递增, agent 需要, 进行操作的时候会创建或更新\n' AFTER `operate_result`;

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;