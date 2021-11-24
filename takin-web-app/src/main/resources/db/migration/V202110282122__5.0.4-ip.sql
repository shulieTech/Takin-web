
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
	IF NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_operation_log' AND column_name='ip')
	THEN
alter table t_operation_log
    add column ip VARCHAR(128) COMMENT '登录ip';
END IF;
END $$
DELIMITER ;
CALL add_column;



-- 插入开始
DROP PROCEDURE IF EXISTS insert_data;
DELIMITER $$
CREATE PROCEDURE insert_data()
BEGIN

DECLARE count1 INT;
SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'traceLogData');

IF count1 = 0 THEN

-- 添加菜单
INSERT INTO `t_tro_resource` ( `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES ( NULL, 0, 'traceLogData', 'trace数据审计', NULL, '[\"/api/trace/log/list\"]', 33000, '[]', NULL, NULL, NULL, '2021-10-12 21:06:35', '2021-10-13 18:51:45', 0);

END IF;

END $$
DELIMITER ;
CALL insert_data();
DROP PROCEDURE IF EXISTS insert_data;



