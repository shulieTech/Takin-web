DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

DECLARE count INT;
SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_role' AND COLUMN_NAME = 'customer_id');

IF count = 0 THEN

alter table t_tro_role
    add column customer_id BIGINT(20) COMMENT '租户id' after features;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_role' AND COLUMN_NAME = 'application_id');

IF count > 0 THEN

alter table t_tro_role
    modify column application_id bigint(20) DEFAULT NULL COMMENT '应用id(4.5.1版本后废弃不用)';

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_role' AND COLUMN_NAME = 'alias');

IF count > 0 THEN

alter table t_tro_role
    modify column `alias` varchar(255) DEFAULT NULL COMMENT '角色别名';

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_role' AND COLUMN_NAME = 'code');

IF count > 0 THEN

alter table t_tro_role
    modify column `code` varchar(20) DEFAULT NULL COMMENT '角色编码';

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_role' AND COLUMN_NAME = 'description');

IF count > 0 THEN

alter table t_tro_role
    modify column `description` varchar(255) DEFAULT NULL COMMENT '角色描述';

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;
-- 权限相关 4.5.1
-- 角色表

-- 清除表 t_tro_role
DROP PROCEDURE IF EXISTS delete_table;
DELIMITER $$
CREATE PROCEDURE delete_table()
BEGIN

DECLARE count1 INT;
SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_role');

IF count1 > 0 THEN

-- 备份表
CREATE TABLE IF NOT EXISTS t_tro_role_copy2 LIKE t_tro_role;
INSERT IGNORE INTO t_tro_role_copy2 SELECT * FROM t_tro_role;
-- 备份表结束

delete from t_tro_role;

END IF;

END $$
DELIMITER ;
CALL delete_table();
DROP PROCEDURE IF EXISTS delete_table;
-- 清除表 t_tro_role 结束

-- 清除表 t_tro_resource
DROP PROCEDURE IF EXISTS delete_table;
DELIMITER $$
CREATE PROCEDURE delete_table()
BEGIN

DECLARE count1 INT;
SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource');

IF count1 > 0 THEN

-- 备份表
CREATE TABLE IF NOT EXISTS t_tro_resource_copy2 LIKE t_tro_resource;
INSERT IGNORE INTO t_tro_resource_copy2 SELECT * FROM t_tro_resource;
-- 备份表结束

delete from `t_tro_resource`;

END IF;

END $$
DELIMITER ;
CALL delete_table();
DROP PROCEDURE IF EXISTS delete_table;
-- 清除表 t_tro_resource 结束

DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

DECLARE count INT;
SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource' AND COLUMN_NAME = 'action');

IF count = 0 THEN

alter table t_tro_resource add column `action` varchar(255) COMMENT '操作权限: 0:all,1:query,2:create,3:update,4:delete,5:start,6:stop,7:export,8:enable,9:disable' after sequence;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource' AND COLUMN_NAME = 'customer_id');

IF count = 0 THEN

alter table t_tro_resource add column customer_id bigint(20) COMMENT '租户id' after features;
END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;

-- 授权表
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_authority' AND COLUMN_NAME = 'object_type');

IF count > 0 THEN

-- 授权表
alter table t_tro_authority
    modify column `object_type` tinyint(1) DEFAULT NULL COMMENT '对象类型:0:角色 1:用户(4.5.1版本后废弃不用)';

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_authority' AND COLUMN_NAME = 'object_id');

IF count > 0 THEN

alter table t_tro_authority
    modify column `object_id` varchar(255) DEFAULT NULL COMMENT '对象id:角色,用户(4.5.1版本后废弃不用)';
END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_authority' AND COLUMN_NAME = 'role_id');

IF count = 0 THEN

alter table t_tro_authority
    add column role_id varchar(50) DEFAULT NULL COMMENT '角色id' after id;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_authority' AND COLUMN_NAME = 'scope');

IF count = 0 THEN

alter table t_tro_authority
    add column scope varchar(255) DEFAULT NULL COMMENT '权限范围：0:全部 1:本部门 2:本部门及以下 3:自己及以下 3:仅自己';

END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_authority' AND COLUMN_NAME = 'customer_id');

IF count = 0 THEN

alter table t_tro_authority
    add column customer_id bigint(20) DEFAULT NULL COMMENT '租户id' after scope;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_authority' AND COLUMN_NAME = 'status');

IF count = 0 THEN

alter table t_tro_authority
    add column `status` tinyint(1) DEFAULT '0' COMMENT '是否启用 0:启用;1:禁用' after action;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_authority' AND COLUMN_NAME = 'role_id');

IF count > 0 THEN

delete from t_tro_authority where role_id is null;

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;



DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_mnt' AND COLUMN_NAME = 'CUSTOMER_ID');

IF count = 0 THEN

-- 应用管理
alter table t_application_mnt
    add column CUSTOMER_ID bigint(20) DEFAULT NULL COMMENT '租户id' after PRADAR_VERSION;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_mnt' AND COLUMN_NAME = 'CREATE_TIME');

IF count > 0 THEN

alter table t_application_mnt
    modify column `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_mnt' AND COLUMN_NAME = 'UPDATE_TIME');

IF count > 0 THEN

alter table t_application_mnt
    modify column `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间';

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_operation_log' AND COLUMN_NAME = 'customer_id');

IF count = 0 THEN

-- 操作日志
alter table t_operation_log
    add column `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id' after end_time;


END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_link_manage_table' AND COLUMN_NAME = 'CUSTOMER_ID');

IF count = 0 THEN

-- 系统流程
alter table t_link_manage_table
    add column CUSTOMER_ID bigint(20) DEFAULT NULL COMMENT '租户id' after IS_JOB;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_link_manage_table' AND COLUMN_NAME = 'USER_ID');

IF count = 0 THEN

alter table t_link_manage_table
    add column `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id' after CUSTOMER_ID;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_business_link_manage_table' AND COLUMN_NAME = 'CUSTOMER_ID');

IF count = 0 THEN

-- 业务活动
alter table t_business_link_manage_table
    add column CUSTOMER_ID bigint(20) DEFAULT NULL COMMENT '租户id' after IS_CORE;
END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_business_link_manage_table' AND COLUMN_NAME = 'USER_ID');

IF count = 0 THEN

-- 业务活动
alter table t_business_link_manage_table
    add column `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id' after CUSTOMER_ID;

END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_scene' AND COLUMN_NAME = 'CUSTOMER_ID');

IF count = 0 THEN

-- 业务流程
alter table t_scene
    add column CUSTOMER_ID bigint(20) DEFAULT NULL COMMENT '租户id' after IS_CHANGED;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_scene' AND COLUMN_NAME = 'USER_ID');

IF count = 0 THEN

alter table t_scene
    add column `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id' after CUSTOMER_ID;

END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_user' AND COLUMN_NAME = 'customer_id');

IF count = 0 THEN

alter table t_tro_user
    add column customer_id bigint(20) DEFAULT NULL COMMENT '租户id' after id;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'CUSTOMER_ID');

IF count = 0 THEN

-- 白名单
alter table t_white_list
    add column CUSTOMER_ID bigint(20) DEFAULT NULL COMMENT '租户id' after `USE_YN`;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'USER_ID');

IF count = 0 THEN

alter table t_white_list
    add column USER_ID bigint(20) DEFAULT NULL COMMENT '用户id' after CUSTOMER_ID;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'CREATE_TIME');

IF count > 0 THEN

alter table t_white_list
    modify column `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间';

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'UPDATE_TIME');

IF count > 0 THEN

alter table t_white_list
    modify column `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间';

END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_api_manage' AND COLUMN_NAME = 'CUSTOMER_ID');

IF count = 0 THEN

-- 入口规则
alter table t_application_api_manage
    add column CUSTOMER_ID bigint(20) DEFAULT NULL COMMENT '租户id' after APPLICATION_NAME;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_api_manage' AND COLUMN_NAME = 'USER_ID');

IF count = 0 THEN

alter table t_application_api_manage
    add column `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id' after CUSTOMER_ID;

END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_black_list' AND COLUMN_NAME = 'PRINCIPAL_NO');

IF count > 0 THEN

-- 黑名单
alter table t_black_list
    modify column `PRINCIPAL_NO` varchar(10) DEFAULT NULL COMMENT '负责人工号(废弃不用)';


END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_black_list' AND COLUMN_NAME = 'CUSTOMER_ID');

IF count = 0 THEN

alter table t_black_list
    add column CUSTOMER_ID bigint(20) DEFAULT NULL COMMENT '租户id' after USE_YN;
END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_black_list' AND COLUMN_NAME = 'USER_ID');

IF count = 0 THEN

alter table t_black_list
    add column `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id' after CUSTOMER_ID;

END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_link_guard' AND COLUMN_NAME = 'customer_id');

IF count = 0 THEN

-- 挡板
alter table t_link_guard
    add column customer_id bigint(20) DEFAULT NULL COMMENT '租户id' after groovy;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_link_guard' AND COLUMN_NAME = 'user_id');

IF count = 0 THEN

alter table t_link_guard
    add column `user_id` bigint(20) DEFAULT NULL COMMENT '用户id' after customer_id;

END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_ds_manage' AND COLUMN_NAME = 'CUSTOMER_ID');

IF count = 0 THEN

-- 影子库表
alter table t_application_ds_manage
    add column CUSTOMER_ID bigint(20) DEFAULT NULL COMMENT '租户id' after `STATUS`;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_ds_manage' AND COLUMN_NAME = 'USER_ID');

IF count = 0 THEN

alter table t_application_ds_manage
    add column USER_ID bigint(20) DEFAULT NULL COMMENT '用户id' after CUSTOMER_ID;

END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_shadow_job_config' AND COLUMN_NAME = 'customer_id');

IF count = 0 THEN

-- 影子job
alter table t_shadow_job_config
    add column customer_id bigint(20) DEFAULT NULL COMMENT '租户id' after active;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_shadow_job_config' AND COLUMN_NAME = 'user_id');

IF count = 0 THEN


alter table t_shadow_job_config
    add column user_id bigint(20) DEFAULT NULL COMMENT '用户id' after CUSTOMER_ID;

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;

-- 更新字段开始
DROP PROCEDURE IF EXISTS update_field;
DELIMITER $$
CREATE PROCEDURE update_field()
BEGIN

DECLARE count1 INT;
SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_mnt' AND COLUMN_NAME = 'customer_id');

IF count1 > 0 THEN

update t_application_mnt
set customer_id=1;

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_ds_manage' AND COLUMN_NAME = 'customer_id');

IF count1 > 0 THEN

update t_application_ds_manage
set customer_id=1;

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_link_guard' AND COLUMN_NAME = 'customer_id');

IF count1 > 0 THEN

update t_link_guard
set customer_id=1;

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'customer_id');

IF count1 > 0 THEN

update t_white_list
set customer_id=1;

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_shadow_job_config' AND COLUMN_NAME = 'customer_id');

IF count1 > 0 THEN

update t_shadow_job_config
set customer_id=1;

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_user' AND COLUMN_NAME = 'customer_id');

IF count1 > 0 THEN

update t_tro_user
set customer_id=1;

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_user' AND COLUMN_NAME = 'user_type');

IF count1 > 0 THEN

update t_tro_user
set user_type=1
where id > 1;

END IF;

END $$

DELIMITER ;

CALL update_field();

DROP PROCEDURE IF EXISTS update_field;
-- 更新字段结束


-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count3 INT;

SET count3 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_ds_manage' AND COLUMN_NAME = 'DB_TYPE');


IF count3 = 0 THEN

alter table t_application_ds_manage
    add column DB_TYPE tinyint(2) DEFAULT '0' COMMENT '存储类型 0:数据库 1:缓存' after APPLICATION_NAME;

END IF;

SET count3 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_ds_manage' AND COLUMN_NAME = 'DS_TYPE');


IF count3 > 0 THEN

alter table t_application_ds_manage
    modify column `DS_TYPE` tinyint(2) DEFAULT '0' COMMENT '方案类型 0:影子库 1:影子表 2:影子server';

END IF;

SET count3 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_base_config' AND COLUMN_NAME = 'CONFIG_VALUE');


IF count3 > 0 THEN

alter table t_base_config
    modify column `CONFIG_VALUE` LONGTEXT NOT NULL COMMENT '配置值';
END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束

-- 更新数据开始
DROP PROCEDURE IF EXISTS update_data;
DELIMITER $$
CREATE PROCEDURE update_data()
BEGIN

DECLARE count3 INT;
SET count3 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource' AND COLUMN_NAME = 'value' AND COLUMN_NAME = 'code');

IF count3 > 0 THEN

-- 更新菜单权限拦截器拦截url
UPDATE `t_tro_resource`
SET `value` = '[\"/api/user/work/bench\"]'
WHERE code = 'dashboard';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/link/tech/linkManage\"]'
WHERE code = 'systemFlow';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/link/business/manage\"]'
WHERE code = 'businessActivity';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/link/scene/manage\"]'
WHERE code = 'businessFlow';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/application/center/list\",\"/api/application/center/app/switch\",\"/api/console/switch/whitelist\"]'
WHERE code = 'appManage';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/scenemanage/list\",\"/api/application/center/app/switch\"]'
WHERE code = 'pressureTestManage_pressureTestScene';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/report/listReport\"]'
WHERE code = 'pressureTestManage_pressureTestReport';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/application/center/app/switch\"]'
WHERE code = 'configCenter_pressureMeasureSwitch';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/console/switch/whitelist\"]'
WHERE code = 'configCenter_whitelistSwitch';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/confcenter/query/blist\"]'
WHERE code = 'configCenter_blacklist';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/api/get\"]'
WHERE code = 'configCenter_entryRule';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/settle/balance/list\"]'
WHERE code = 'flowAccount';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/operation/log/list\"]'
WHERE code = 'configCenter_operationLog';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/role/list\"]'
WHERE code = 'configCenter_authorityConfig';
UPDATE `t_tro_resource`
SET `value` = '[\"/api/scriptManage\"]'
WHERE code = 'scriptManage';

END IF;

END $$
DELIMITER ;
CALL update_data();
DROP PROCEDURE IF EXISTS update_data;
-- 更新数据结束

BEGIN;
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (1, NULL, 0, 'dashboard', '系统概览', '',
        '[\"/api/scenemanage/list\",\"/api/report/listReport\",\"/api/application/center/app/switch\",\"/api/settle/accountbook\",\"/api/user/work/bench/access\",\"/api/user/work/bench\"]',
        1000, '[]', NULL, NULL, NULL, '2020-09-01 17:10:02', '2020-11-06 14:19:06', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (2, NULL, 0, 'linkTease', '链路梳理', '', '', 2400, '[]', NULL, NULL, NULL, '2020-09-01 17:16:56',
        '2020-11-12 20:12:24', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (3, 2, 0, 'systemFlow', '系统流程', '',
        '[\"/api/link/linkmanage/middleware\",\"/api/link/midlleWare/cascade\",\"/api/link/tech/linkManage\",\"/api/application/center/list\"]',
        2100, '[2,3,4]', NULL, NULL, NULL, '2020-09-01 17:20:17', '2020-11-12 16:51:46', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (4, 2, 0, 'businessActivity', '业务活动', '',
        '[\"/api/link/business/manage\",\"/api/link/linkmanage/middleware\",\"/api/link/midlleWare/cascade\"]', 2200,
        '[2,3,4]', NULL, NULL, NULL, '2020-09-01 17:26:09', '2020-11-02 20:17:57', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (5, 2, 0, 'businessFlow', '业务流程', '',
        '[\"/api/link/scene/manage\",\"/api/link/linkmanage/middleware\",\"/api/link/midlleWare/cascade\"]', 2300,
        '[2,3,4]', NULL, NULL, NULL, '2020-09-01 17:26:54', '2020-11-02 20:18:26', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (6, NULL, 0, 'appManage', '应用管理', NULL,
        '[\"/api/application/center/app/switch\",\"/api/application/center/list\",\"/api/application/center/app/info\",\"/api/link/ds/manage\",\"/api/link/ds/enable\",\"/api/link/ds/enable\",\"/api/link/ds/manage/detail\",\"/api/link/guard/guardmanage\",\"/api/link/guard/guardmanage/info\",\"/api/shadow/job/query\",\"/api/shadow/job/query/detail\",\"/api/application/whitelist\",\"/api/global/switch/whitelist\"]',
        3000, '[2,3,4,6]', NULL, NULL, NULL, '2020-09-01 17:31:32', '2020-11-06 11:59:26', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (8, NULL, 0, 'pressureTestManage', '压测管理', NULL, '', 5300, '[]', NULL, NULL, NULL, '2020-09-01 17:36:41',
        '2020-11-12 20:12:33', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (9, 8, 0, 'pressureTestManage_pressureTestScene', '压测场景', NULL,
        '[\"/api/application/center/app/switch\",\"/api/scenemanage/list\",\"/api/settle/accountbook\"]', 5100,
        '[2,3,4,5]', NULL, NULL, NULL, '2020-09-01 17:38:28', '2020-11-06 11:59:35', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (10, 8, 0, 'pressureTestManage_pressureTestReport', '压测报告', NULL, '[\"/api/report/listReport\"]', 5200, '[]',
        NULL, NULL, NULL, '2020-09-01 17:43:10', '2020-11-06 14:18:51', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (11, NULL, 0, 'configCenter', '配置中心', NULL, '', 6600, '[]', NULL, NULL, NULL, '2020-09-01 17:44:26',
        '2020-11-12 20:12:26', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (12, 11, 0, 'configCenter_pressureMeasureSwitch', '压测开关设置', NULL, '[\"/api/application/center/app/switch\"]',
        6100, '[6]', NULL, NULL, NULL, '2020-09-01 17:46:04', '2020-11-06 11:59:44', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (13, 11, 0, 'configCenter_whitelistSwitch', '白名单开关设置', NULL, '[\"/api/global/switch/whitelist\"]', 6200, '[6]',
        NULL, NULL, NULL, '2020-09-01 17:47:15', '2020-11-06 11:59:54', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (14, 11, 0, 'configCenter_blacklist', '黑名单', NULL, '[\"/api/confcenter/wbmnt/query/blist\"]', 6300, '[2,3,4,6]',
        NULL, NULL, NULL, '2020-09-01 17:48:02', '2020-11-06 12:00:05', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (15, 11, 0, 'configCenter_entryRule', '入口规则', NULL, '[\"/api/api/get\"]', 6400, '[2,3,4]', NULL, NULL, NULL,
        '2020-09-01 17:49:15', '2020-11-02 20:31:16', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (16, NULL, 0, 'flowAccount', '流量账户', NULL, '[\"/api/settle/accountbook\",\"/api/settle/balance/list\"]', 7000,
        '[]', NULL, NULL, NULL, '2020-09-01 17:51:25', '2020-11-06 14:19:30', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (307, 11, 0, 'configCenter_operationLog', '操作日志', NULL, '[\"/operation/log/list\"]', 6500, '[]', NULL, NULL,
        NULL, '2020-09-28 15:27:38', '2020-11-12 20:12:28', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (367, 11, 0, 'configCenter_authorityConfig', '权限管理', NULL,
        '[\"/api/role/list\",\"/api/role/detail\",\"/api/role/add\",\"/api/role/update\",\"/api/role/delete\",\"/api/role/clear\"]',
        6000, '[2,3,4]', NULL, NULL, NULL, '2020-11-10 11:53:09', '2020-11-16 11:35:39', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (368, NULL, 0, 'scriptManage', '脚本管理', NULL, '[\"/api/scriptManage\"]', 4000, '[2,3,4,7]', NULL, NULL, NULL,
        '2020-11-10 18:55:05', '2020-11-16 18:10:12', 0);
COMMIT;