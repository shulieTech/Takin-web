-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;
DELIMITER
$$
CREATE PROCEDURE change_field()
BEGIN

DECLARE
count1 INT;

SET
count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_scene_check' AND COLUMN_NAME = 'error_detail');

IF
count1 > 0 THEN

ALTER TABLE `e_patrol_scene_check`
    MODIFY COLUMN `error_detail` varchar (2200) CHARACTER SET utf8 COLLATE utf8_bin NULL DEFAULT NULL COMMENT '错误详情' AFTER `error_description`;

END IF;

SET
count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception');

IF
count1 > 0 THEN

ALTER TABLE `e_patrol_exception` CONVERT TO CHARACTER SET utf8 COLLATE utf8_bin;

END IF;


SET
count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception_config');

IF
count1 > 0 THEN

ALTER TABLE `e_patrol_exception_config` CONVERT TO CHARACTER SET utf8 COLLATE utf8_bin;

END IF;

SET
count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception_notice_config');

IF
count1 > 0 THEN

ALTER TABLE `e_patrol_exception_notice_config` CONVERT TO CHARACTER SET utf8 COLLATE utf8_bin;

END IF;

SET
count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception_notice_config');

IF
count1 > 0 THEN

ALTER TABLE `e_patrol_exception_status_change_log` CONVERT TO CHARACTER SET utf8 COLLATE utf8_bin;

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束