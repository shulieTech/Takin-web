-- pt_t_application_mnt 索引优化
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 'pt_t_application_mnt' AND index_name = 'T_APLICATION_MNT_INDEX1') THEN

ALTER TABLE `pt_t_application_mnt` DROP INDEX `T_APLICATION_MNT_INDEX1`;

END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;

-- t_scene 索引优化
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 't_scene' AND index_name = 'T_LINK_MNT_INDEX2') THEN

ALTER TABLE `t_scene` DROP INDEX `T_LINK_MNT_INDEX2`;

END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;

-- t_application_mnt 索引优化
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 't_application_mnt' AND index_name = 'T_APLICATION_MNT_INDEX1') THEN

ALTER TABLE `t_application_mnt` DROP INDEX `T_APLICATION_MNT_INDEX1`;

END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;

-- t_tc_sequence 索引优化
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 't_tc_sequence' AND index_name = 'IDX_SEQUENCE_NAME') THEN

ALTER TABLE `t_tc_sequence` DROP INDEX `IDX_SEQUENCE_NAME`;

END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;

-- t_report_machine 索引优化
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 't_report_machine' AND index_name = 'idx_report_id_application_name') THEN

ALTER TABLE `t_report_machine` DROP INDEX `idx_report_id_application_name`;

END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;

-- t_report_application_summary 索引优化
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 't_report_application_summary' AND index_name = 'idx_report_id') THEN

ALTER TABLE `t_report_application_summary` DROP INDEX `idx_report_id`;

END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;