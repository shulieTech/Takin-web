-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

DECLARE count INT;
SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_scene' AND COLUMN_NAME = 'features');

IF count = 0 THEN

alter table e_patrol_scene add column `features` text comment '拓展字段';

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束