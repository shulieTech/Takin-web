-- 插入记录开始
DROP PROCEDURE IF EXISTS insert_record;
DELIMITER $$
CREATE PROCEDURE insert_record()
BEGIN

IF NOT EXISTS (SELECT * FROM t_tro_resource WHERE `code` = 'traceQuery') THEN

INSERT INTO `t_tro_resource` (`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (NULL, 0, 'traceQuery', '链路查询', '', '["/api/apm/traceFlowManage/list"]', 2500, '[]', NULL, NULL, '2021-08-25 02:16:56', '2021-08-25 02:16:56', 0);

END IF;

END $$
DELIMITER ;
CALL insert_record();
DROP PROCEDURE IF EXISTS insert_record;
-- 插入记录结束