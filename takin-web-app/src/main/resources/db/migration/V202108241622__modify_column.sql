-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

    DECLARE count1 INT;

    SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
                  WHERE table_schema = DATABASE() AND TABLE_NAME = 't_app_remote_call' AND COLUMN_NAME = 'mock_return_value');

    IF count1 > 0 THEN
        ALTER TABLE t_app_remote_call MODIFY `mock_return_value` longtext COLLATE utf8_bin COMMENT 'mock返回值' AFTER `type`;
    END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束