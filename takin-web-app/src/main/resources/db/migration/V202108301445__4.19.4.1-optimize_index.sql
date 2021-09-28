-- t_shadow_table_datasource 索引优化
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 't_shadow_table_datasource' AND index_name = 'SHADOW_DATASOURCE_INDEX1') THEN
ALTER TABLE `t_shadow_table_datasource` DROP INDEX `SHADOW_DATASOURCE_INDEX1`;
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

-- t_application_ip 索引优化
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 't_application_ip' AND index_name = 'IDX_T_APP_ID2') THEN
ALTER TABLE `t_application_ip` DROP INDEX `IDX_T_APP_ID2`;
END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;

-- t_database_conf 索引优化
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 't_database_conf' AND index_name = 'TDC_INDEX4') THEN
ALTER TABLE `t_database_conf` DROP INDEX `TDC_INDEX4`;
END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;