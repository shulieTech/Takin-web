/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
	IF NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_business_link_manage_table' AND column_name='SERVER_MIDDLEWARE_TYPE')
	THEN
ALTER TABLE t_business_link_manage_table ADD SERVER_MIDDLEWARE_TYPE VARCHAR ( 20 ) COMMENT '中间件类型（KAFKA,RABBITMQ,....）';
END IF;
END $$
DELIMITER ;
CALL add_column;
