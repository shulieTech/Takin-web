/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
	IF NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_quick_access' AND column_name='customer_id')
	THEN
ALTER TABLE t_quick_access ADD customer_id bigint(20) COMMENT '租户字段，customer_id,custom_id已废弃';
END IF;
END $$
DELIMITER ;
CALL add_column;


update t_quick_access set customer_id = custom_id;


alter table t_ops_script_manage MODIFY customer_id BIGINT(20) COMMENT '租户字段';