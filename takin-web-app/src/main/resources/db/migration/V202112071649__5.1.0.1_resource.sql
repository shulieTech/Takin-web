/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
	IF NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_tro_resource' AND column_name='IS_SUPER')
	THEN
ALTER TABLE t_tro_resource ADD IS_SUPER tinyint(1) default false COMMENT '只有超级管理员才能看到';
END IF;
END $$
DELIMITER ;
CALL add_column;
