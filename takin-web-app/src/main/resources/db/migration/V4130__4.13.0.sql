-- 重复数据删除开始
DROP PROCEDURE
    IF
    EXISTS change_data;
DELIMITER $$
CREATE PROCEDURE change_data() BEGIN
	DECLARE
count1 INT;
SET count1 = (SELECT COUNT(*) FROM t_tro_user WHERE EXISTS (select id from t_tro_user group by `name` having count(*)>1));

IF count1 > 0 THEN

delete from t_tro_user where id in (select id from t_tro_user group by `name` having count(*)>1);

END IF;


SET count1 = (SELECT COUNT(*) FROM t_tro_dept WHERE EXISTS (select id from t_tro_dept group by `name` having count(*)>1));


IF count1 > 0 THEN

delete from t_tro_dept where id in (select id from t_tro_dept group by `name` having count(*)>1);

END IF;

END $$
DELIMITER ;
CALL change_data();
DROP PROCEDURE
    IF
    EXISTS change_data;
-- 重复数据删除结束

-- 索引创建开始
DROP PROCEDURE
    IF
    EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN
	DECLARE
count1 INT;
	SET count1 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_tro_dept' AND index_name = 'idx_name' );

IF count1 = 0 THEN

CREATE UNIQUE INDEX `idx_name` ON t_tro_dept(`name`);

END IF;

	SET count1 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_tro_user' AND index_name = 'idx_name' );

IF count1 = 0 THEN

CREATE UNIQUE INDEX `idx_name` ON t_tro_user(`name`);

END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE
    IF
    EXISTS change_index;
-- 索引创建结束

-- 字段添加开始
DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

DECLARE count INT;
SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_trace_node_info' AND COLUMN_NAME = 'rpc_type');

IF count = 0 THEN

alter table t_trace_node_info
    add column `rpc_type` tinyint(4) DEFAULT NULL COMMENT 'rpc类型';

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_trace_node_info' AND COLUMN_NAME = 'port');

IF count = 0 THEN

alter table t_trace_node_info
    add column `port` tinyint(4) DEFAULT NULL COMMENT '端口';

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_trace_node_info' AND COLUMN_NAME = 'is_upper_unknown_node');

IF count = 0 THEN

alter table t_trace_node_info
    add column `is_upper_unknown_node` tinyint(1) DEFAULT NULL COMMENT '是否下游有未知节点';

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;
-- 字段添加结束