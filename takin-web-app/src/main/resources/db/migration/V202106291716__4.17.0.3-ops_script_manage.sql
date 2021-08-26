-- 字段更新
DROP PROCEDURE IF EXISTS update_field;

DELIMITER $$

CREATE PROCEDURE update_field()

BEGIN

DECLARE count1 INT;
DECLARE count2 INT;
DECLARE count3 INT;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource' AND COLUMN_NAME = 'scriptManage');

SET count2 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource' AND COLUMN_NAME = 'scriptManages');

SET count3 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource' AND COLUMN_NAME = 'scriptOperation');


IF count1 > 0 THEN

UPDATE `t_tro_resource` SET `code`='scriptManage' where `code`='scriptManage_test' and name='测试脚本';

END IF;

IF count2 > 0 THEN

UPDATE `t_tro_resource` SET `code`='scriptManages' where `code`='scriptManage' and name='脚本管理';

END IF;

IF count3 > 0 THEN

UPDATE `t_tro_resource` SET `code`='scriptOperation' where `code`='scriptManage_ops' and name='运维脚本';

END IF;

END $$

DELIMITER ;

CALL update_field();

DROP PROCEDURE IF EXISTS update_field;