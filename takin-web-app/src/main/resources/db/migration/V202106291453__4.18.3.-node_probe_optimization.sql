DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

DECLARE count INT;
SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_node_probe' AND COLUMN_NAME = 'operate_id');

IF count = 0 THEN

ALTER TABLE `t_application_node_probe` ADD COLUMN `operate_id` bigint(20) UNSIGNED NULL DEFAULT 0 COMMENT '操作的id, 时间戳, 递增, agent 需要, 进行操作的时候会创建或更新' AFTER `operate_result`;

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;