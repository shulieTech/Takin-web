/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
	IF NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_script_debug' AND column_name='concurrency_num')
	THEN
ALTER TABLE t_script_debug ADD COLUMN `concurrency_num` int(10) UNSIGNED NULL DEFAULT 1 COMMENT '并发数' NULL AFTER `request_num`;
END IF;
END $$
DELIMITER ;
CALL add_column;
