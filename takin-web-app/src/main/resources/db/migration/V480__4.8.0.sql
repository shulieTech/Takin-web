DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

DECLARE count INT;
SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = DATABASE() AND TABLE_NAME = 't_link_manage_table' AND COLUMN_NAME = 'features');

IF count = 0 THEN

alter table t_link_manage_table add column features text;

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;

-- 清除表开始
DROP PROCEDURE IF EXISTS delete_table;
DELIMITER $$
CREATE PROCEDURE delete_table()
BEGIN

DECLARE count3 INT;
DECLARE count1 INT;
DECLARE count2 INT;

SET count3 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource' AND COLUMN_NAME = 'code');


IF count3 > 0 THEN

    SET count2 = (SELECT COUNT(*) FROM t_tro_resource WHERE EXISTS (select id from t_tro_resource where `code` = 'systemFlow'));
    IF count2 > 0 THEN

        -- 备份表
CREATE TABLE IF NOT EXISTS t_tro_resource_copy_4 LIKE t_tro_resource;
INSERT IGNORE INTO t_tro_resource_copy_4 SELECT * from t_tro_resource where `code` = 'systemFlow';
-- 备份表结束



SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_authority');

        IF count1 > 0 THEN
            -- 备份表
CREATE TABLE IF NOT EXISTS t_tro_authority_copy3 LIKE t_tro_authority;
INSERT IGNORE INTO t_tro_authority_copy3 SELECT * from t_tro_authority where resource_id IN (select id from t_tro_resource where `code` = 'systemFlow');
-- 备份表结束

delete from t_tro_authority where resource_id IN (select id from t_tro_resource where `code` = 'systemFlow');

END IF;

delete from t_tro_resource where `code` = 'systemFlow';

END IF;

END IF;

END $$
DELIMITER ;
CALL delete_table();
DROP PROCEDURE IF EXISTS delete_table;
-- 清除表结束

BEGIN;
UPDATE `t_tro_resource` SET `value` = '[\"/api/user/work/bench\"]' WHERE `code` = 'dashboard';
UPDATE `t_tro_resource` SET `value` = '[\"/api/activities\"]' WHERE `code` = 'businessActivity';
UPDATE `t_tro_resource` SET `value` = '[\"/api/link/scene/manage\"]' WHERE `code` = 'businessFlow';
UPDATE `t_tro_resource` SET `value` = '[\"/api/application/center/list\",\"/api/application/center/app/switch\",\"/api/console/switch/whitelist\"]' WHERE `code` = 'appManage';
UPDATE `t_tro_resource` SET `value` = '[\"/api/scenemanage/list\",\"/api/application/center/app/switch\",\"/api/console/switch/whitelist\"]' WHERE `code` = 'pressureTestManage_pressureTestScene';
UPDATE `t_tro_resource` SET `value` = '[\"/api/report/listReport\"]' WHERE `code` = 'pressureTestManage_pressureTestReport';
UPDATE `t_tro_resource` SET `value` = '[\"/api/application/center/app/switch\"]' WHERE `code` = 'configCenter_pressureMeasureSwitch';
UPDATE `t_tro_resource` SET `value` = '[\"/api/console/switch/whitelist\"]' WHERE `code` = 'configCenter_whitelistSwitch';
UPDATE `t_tro_resource` SET `value` = '[\"/api/confcenter/query/blist\"]' WHERE `code` = 'configCenter_blacklist';
UPDATE `t_tro_resource` SET `value` = '[\"/api/api/get\"]' WHERE `code` = 'configCenter_entryRule';
UPDATE `t_tro_resource` SET `value` = '[\"/api/operation/log/list\"]' WHERE `code` = 'configCenter_operationLog';
UPDATE `t_tro_resource` SET `value` = '[\"/api/statistic\"]' WHERE `code` = 'pressureTestManage_pressureTestStatistic';
UPDATE `t_tro_resource` SET `value` = '[\"/api/role/list\"]' WHERE `code` = 'configCenter_authorityConfig';
UPDATE `t_tro_resource` SET `value` = '[\"/debugTool/linkDebug\",\"/api/fastdebug/config/list\"]' WHERE `code` = 'debugTool_linkDebug';
UPDATE `t_tro_resource` SET `value` = '[\"/debugTool/linkDebug/detail\"]' WHERE `code` = 'debugTool_linkDebug_detail';
UPDATE `t_tro_resource` SET `value` = '[\"/api/settle/balance/list\"]' WHERE `code` = 'flowAccount';
UPDATE `t_tro_resource` SET `value` = '[\"/api/scriptManage\"]' WHERE `code` = 'scriptManage';
UPDATE `t_tro_resource` SET `value` = '[\"/api/shellManage\"]' WHERE `code` = 'shellManage';
COMMIT;