DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN
	DECLARE
count1 INT;
	DECLARE
count2 INT;

	SET count1 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_probe' AND index_name = 'idx_ci_v' );

	SET count2 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_probe' AND index_name = 'idx_ci_v_u' );

IF count1 > 0 THEN
ALTER TABLE `t_probe` DROP INDEX `idx_ci_v`;
END IF;

IF count2 = 0 THEN
ALTER TABLE `t_probe` ADD INDEX `idx_ci_v_u` ( `customer_id`, `version`, `gmt_update` ) USING BTREE;
END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;