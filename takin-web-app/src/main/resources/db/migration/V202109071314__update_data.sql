-- 字段更新开始
DROP PROCEDURE IF EXISTS update_field;
DELIMITER $$
CREATE PROCEDURE update_field()
BEGIN

    DECLARE count INT;
    SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                 WHERE table_schema = DATABASE() AND LOWER(TABLE_NAME) = 't_tro_resource' AND (LOWER(COLUMN_NAME) = 'action' OR LOWER(COLUMN_NAME) = 'value'));
    IF count = 2 THEN
        UPDATE `t_tro_resource` SET `action` = '[2,3,4]' WHERE  `value` = '[\"/api/pradar/switch/list\"]';
    END IF;

END $$
DELIMITER ;
CALL update_field();
DROP PROCEDURE IF EXISTS update_field;
-- 字段更新结束