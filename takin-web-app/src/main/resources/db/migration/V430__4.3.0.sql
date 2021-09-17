CREATE TABLE IF NOT EXISTS `t_tro_user` (
                                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'id',
                                            `name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '登录账号',
    `nick` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '用户名称',
    `key` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '用户key',
    `salt` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '盐值',
    `password` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '登录密码',
    `status` tinyint(1) DEFAULT '0' COMMENT '状态 0:启用  1： 冻结',
    `user_type` int DEFAULT '0' COMMENT '用户类型，0:系统管理员，1:其他',
    `model` tinyint(1) DEFAULT '0' COMMENT '模式 0:体验模式，1:正式模式',
    `role` tinyint(1) DEFAULT '0' COMMENT '角色 0:管理员，1:体验用户 2:正式用户',
    `customer_id` bigint DEFAULT NULL,
    `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `idx_name` (`name`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- 用户表（t_tro_user）
-- 字段添加
DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

DECLARE count1 INT;
SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_user' AND COLUMN_NAME = 'user_type');

IF count1 = 0 THEN

ALTER TABLE t_tro_user
    ADD COLUMN `user_type` int(1) DEFAULT '0' COMMENT '用户类型，0:系统管理员，1:其他';

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;

-- 部门表（t_tro_dept）
CREATE TABLE IF NOT EXISTS `t_tro_dept`
(
    `id`          bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '部门id主键',
    `name`        varchar(20) NOT NULL COMMENT '部门名称',
    `code`        varchar(50)  DEFAULT NULL COMMENT '部门编码',
    `parent_id`   bigint(11)   DEFAULT NULL COMMENT '上级部门id',
    `level`       varchar(50)  DEFAULT NULL COMMENT '部门层级',
    `path`        varchar(50)  DEFAULT NULL COMMENT '部门路径',
    `sequence`    int(5)       DEFAULT '0' COMMENT '排序',
    `ref_id`      varchar(255) DEFAULT NULL COMMENT '第三方平台唯一标识',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 用户部门关系表（t_tro_user_dept_relation）
CREATE TABLE IF NOT EXISTS `t_tro_user_dept_relation`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'id主键',
    `user_id`     varchar(255) NOT NULL COMMENT '用户id',
    `dept_id`     varchar(255) NOT NULL COMMENT '部门id',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`(191)),
    KEY `idx_dept_id` (`dept_id`(191))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


-- 角色表（t_tro_role）
CREATE TABLE IF NOT EXISTS `t_tro_role`
(
    `id`             bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '角色id主键',
    `application_id` bigint(20)   NOT NULL COMMENT '应用id',
    `name`           varchar(20)  NOT NULL COMMENT '角色名称',
    `alias`          varchar(255) NOT NULL COMMENT '角色别名',
    `code`           varchar(20)  NOT NULL COMMENT '角色编码',
    `description`    varchar(255) NOT NULL COMMENT '角色描述',
    `status`         tinyint(1)   DEFAULT '0' COMMENT '状态(0:启用 1:禁用)',
    `features`       longtext COMMENT '扩展字段，k-v形式存在',
    `remark`         varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time`    datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`     tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`),
    KEY `idx_application_id` (`application_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='角色表';

-- 基础角色表（t_tro_base_role）
CREATE TABLE IF NOT EXISTS `t_tro_base_role`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT '角色id主键',
    `name`        varchar(20)  NOT NULL COMMENT '角色名称',
    `alias`       varchar(20)  NOT NULL COMMENT '角色别名',
    `code`        varchar(20)  NOT NULL COMMENT '角色编码',
    `description` varchar(255) NOT NULL COMMENT '角色描述',
    `status`      tinyint(1) DEFAULT '0' COMMENT '状态(0:启用 1:禁用)',
    `action`      varchar(255) NOT NULL COMMENT '操作类型(0:all,1:query,2:create,3:update,4:delete,5:start,6:stop,7:export,8:enable,9:disable,10:auth)',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='基础角色表';

-- 资源表（t_tro_resource）
CREATE TABLE IF NOT EXISTS `t_tro_resource`
(
    `id`          bigint(20)    NOT NULL AUTO_INCREMENT COMMENT '资源id主键',
    `parent_id`   bigint(20)   DEFAULT NULL COMMENT '父资源id',
    `type`        tinyint(1)    NOT NULL COMMENT '资源类型(0:菜单1:按钮 2:数据)',
    `code`        varchar(100) DEFAULT NULL COMMENT '资源编码',
    `name`        varchar(255)  NOT NULL COMMENT '资源名称',
    `alias`       varchar(255) DEFAULT NULL COMMENT '资源别名',
    `value`       varchar(1024) NOT NULL COMMENT '资源值（菜单是url，应用是应用id）',
    `sequence`    int(5)       DEFAULT '0' COMMENT '排序',
    `features`    longtext COMMENT '扩展字段，k-v形式存在',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `idx_value` (`value`(191))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 授权表（t_tro_authority）
CREATE TABLE IF NOT EXISTS `t_tro_authority`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'id主键',
    `resource_id` varchar(255) NOT NULL COMMENT '资源id',
    `action`      varchar(255) DEFAULT NULL COMMENT '操作类型(0:all,1:query,2:create,3:update,4:delete,5:start,6:stop,7:export,8:enable,9:disable,10:auth)',
    `object_type` tinyint(1)   NOT NULL COMMENT '对象类型(0:角色 1:用户)',
    `object_id`   varchar(255) NOT NULL COMMENT '对象id:角色,用户',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 用户角色关系表（t_tro_role_user_relation）
CREATE TABLE IF NOT EXISTS `t_tro_role_user_relation`
(
    `id`          bigint(20)   NOT NULL AUTO_INCREMENT COMMENT 'id主键',
    `role_id`     varchar(255) NOT NULL COMMENT '角色id',
    `user_id`     varchar(255) NOT NULL COMMENT '用户id',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`(191)),
    KEY `idx_role_id` (`role_id`(191))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

BEGIN;
-- 基础角色初始化数据
INSERT IGNORE INTO `t_tro_base_role`(`id`, `name`, `alias`, `code`, `description`, `status`, `action`)
VALUES (1, '应用管理员', '', 'APP_ADMIN', '应用管理员对应用具有基础的「编辑」、「删除」、「查看」权限，并可以对「应用组长」「应用组员」进行账号管理', 0,
        '[\"3\",\"4\",\"1\",\"10\"]');
INSERT IGNORE INTO `t_tro_base_role`(`id`, `name`, `alias`, `code`, `description`, `status`, `action`)
VALUES (2, '应用组长', '', 'APP_MANAGER', '应用组长对应用具有基础的「编辑」、「删除」、「查看」权限，并可以对「应用组员」进行账号管理', 0, '[\"3\",\"4\",\"1\",\"10\"]');
INSERT IGNORE INTO `t_tro_base_role`(`id`, `name`, `alias`, `code`, `description`, `status`, `action`)
VALUES (3, '应用组员', '', 'APP_USER', '应用组员对应用具有基础的「查看」权限', 0, '[\"1\"]');

-- 菜单资源初始化数据
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (1, NULL, 0, 'dashboard', '系统概览', '',
        '[\"/api/scenemanage/list\",\"/api/report/listReport\",\"/api/application/center/app/switch\",\"/api/settle/accountbook\",\"/api/user/work/bench/access\",\"/api/user/work/bench\"]',
        1000, NULL, NULL, '2020-09-01 17:10:02', '2020-09-08 20:05:31', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (2, NULL, 0, 'linkTease', '链路梳理', '', '', 2000, NULL, NULL, '2020-09-01 17:16:56', '2020-09-04 14:09:50', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (3, 2, 0, 'systemFlow', '系统流程', '',
        '[\"/api/link/linkmanage/middleware\",\"/api/link/midlleWare/cascade\",\"/api/link/tech/linkManage\"]', 2100,
        NULL, NULL, '2020-09-01 17:20:17', '2020-09-08 20:05:38', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (4, 2, 0, 'businessActivity', '业务活动', '',
        '[\"/api/link/business/manage\",\"/api/link/linkmanage/middleware\",\"/api/link/midlleWare/cascade\"]', 2200,
        NULL, NULL, '2020-09-01 17:26:09', '2020-09-08 20:06:41', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (5, 2, 0, 'businessFlow', '业务流程', '',
        '[\"/api/link/scene/manage\",\"/api/link/linkmanage/middleware\",\"/api/link/midlleWare/cascade\"]', 2300, NULL,
        NULL, '2020-09-01 17:26:54', '2020-09-08 20:06:39', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (6, NULL, 0, 'appManage', '应用管理', NULL,
        '[\"/api/application/center/app/switch\",\"/api/application/center/list\",\"/api/application/center/app/info\",\"/api/link/ds/manage\",\"/api/link/ds/enable\",\"/api/link/ds/enable\",\"/api/link/ds/manage/detail\",\"/api/link/guard/guardmanage\",\"/api/link/guard/guardmanage/info\",\"/api/shadow/job/query\",\"/api/shadow/job/query/detail\",\"/api/application/whitelist\",\"/api/global/switch/whitelist\"]',
        3000, NULL, NULL, '2020-09-01 17:31:32', '2020-09-10 14:26:15', 0);

INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (7, NULL, 0, 'appTrialManage', '应用管理（体验）', NULL, '[\"/api/application/center/list\"]', 4000, NULL, NULL,
        '2020-09-01 17:33:21', '2020-09-08 20:06:31', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (8, NULL, 0, 'pressureTestManage', '压测管理', NULL, '', 5000, NULL, NULL, '2020-09-01 17:36:41',
        '2020-09-04 14:09:46', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (9, 8, 0, 'pressureTestManage_pressureTestScene', '压测场景', NULL,
        '[\"/api/application/center/app/switch\",\"/api/scenemanage/list\",\"/api/settle/accountbook\"]', 5100, NULL,
        NULL, '2020-09-01 17:38:28', '2020-09-08 20:06:28', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (10, 8, 0, 'pressureTestManage_pressureTestReport', '压测报告', NULL, '[\"/api/report/listReport\"]', 5200, NULL,
        NULL, '2020-09-01 17:43:10', '2020-09-08 21:00:35', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (11, NULL, 0, 'configCenter', '配置中心', NULL, '', 6000, NULL, NULL, '2020-09-01 17:44:26', '2020-09-04 14:09:44',
        0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (12, 11, 0, 'configCenter_pressureMeasureSwitch', '压测开关设置', NULL, '[\"/api/application/center/app/switch\"]',
        6100, NULL, NULL, '2020-09-01 17:46:04', '2020-09-08 20:06:22', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (13, 11, 0, 'configCenter_whitelistSwitch', '白名单开关设置', NULL, '[\"/api/global/switch/whitelist\"]', 6200, NULL,
        NULL, '2020-09-01 17:47:15', '2020-09-08 20:06:20', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (14, 11, 0, 'configCenter_blacklist', '黑名单', NULL, '[\"/api/confcenter/wbmnt/query/blist\"]', 6300, NULL, NULL,
        '2020-09-01 17:48:02', '2020-09-08 20:06:18', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (15, 11, 0, 'configCenter_entryRule', '入口规则', NULL, '[\"/api/api/get\"]', 6400, NULL, NULL,
        '2020-09-01 17:49:15', '2020-09-08 20:06:16', 0);
INSERT IGNORE INTO `t_tro_resource` (`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`,
                                     `features`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (16, NULL, 0, 'flowAccount', '流量账户', NULL, '[\"/api/settle/accountbook\",\"/api/settle/balance/list\"]', 7000,
        NULL, NULL, '2020-09-01 17:51:25', '2020-09-09 21:16:46', 0);

INSERT IGNORE INTO `t_tro_user` (`id`,`customer_id`,`name`,`nick`,`key`,`salt`,`password`,`status`,`model`,`role`,`is_delete`,`gmt_create`,`gmt_update`,`user_type`) VALUES (1,1,'admin','admin','5b06060a-17cb-4588-bb71-edd7f65035aa','$2a$10$HucwJ/6sX5c4i8COpazjrO','$2a$10$HucwJ/6sX5c4i8COpazjrOU3S9i4yLNVR9Re22xM./9uWmDdDvqaW',0,0,0,0,'2020-03-25 10:49:35','2021-06-25 17:05:51',0);
COMMIT;