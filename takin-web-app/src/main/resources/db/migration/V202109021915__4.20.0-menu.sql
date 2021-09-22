-- agent版本管理表
CREATE TABLE IF NOT EXISTS `t_agent_version`
(
    `id`               bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`       datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`       datetime            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `operator`         varchar(32)         NOT NULL COMMENT '操作人',
    `first_version`    varchar(64)         NOT NULL COMMENT '大版本号',
    `version`          varchar(64)         NOT NULL COMMENT '版本号',
    `version_num`      bigint(20) unsigned NOT NULL COMMENT '版本号对应的数值',
    `file_path`        varchar(1024)       NOT NULL COMMENT '文件存储路径',
    `version_features` varchar(2048)       NOT NULL COMMENT '版本特性',
    `is_deleted`       tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除字段, 1 已删除, 0 未删除, 默认 0, 无符号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `version_unique` (`version`) USING BTREE COMMENT '版本号唯一索引'
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4 COMMENT ='agent版本管理';

CREATE TABLE IF NOT EXISTS `t_agent_config`
(
    `id`                     bigint unsigned  NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `gmt_create`             datetime         NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`             datetime         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`             tinyint unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除字段, 1 已删除, 0 未删除, 默认 0, 无符号',
    `operator`               varchar(32)      NOT NULL COMMENT '操作人',
    `type`                   tinyint unsigned NOT NULL DEFAULT '0' COMMENT '配置类型0：全局配置，1：应用配置',
    `zh_key`                 varchar(255)              DEFAULT NULL COMMENT '中文配置key',
    `en_key`                 varchar(255)     NOT NULL COMMENT '英文配置key',
    `default_value`          varchar(255)     NOT NULL COMMENT '配置默认值',
    `desc`                   varchar(1024)             DEFAULT NULL COMMENT '配置描述',
    `effect_type`            tinyint unsigned NOT NULL DEFAULT '0' COMMENT '配置作用范围 0：探针配置，1：agent配置',
    `effect_mechanism`       tinyint unsigned NOT NULL DEFAULT '0' COMMENT '生效机制0：重启生效，1：立即生效',
    `effect_min_version`     varchar(64)               DEFAULT NULL COMMENT '配置生效最低版本',
    `effect_min_version_num` bigint(20) unsigned       DEFAULT NULL COMMENT '生效最低版本对应的数值',
    `effect_max_version`     varchar(64)               DEFAULT NULL COMMENT '配置生效最大版本（废弃）',
    `effect_max_version_num` bigint(20) unsigned       DEFAULT NULL COMMENT '生效最高版本对应的数值（废弃）',
    `editable`               tinyint unsigned NOT NULL DEFAULT '0' COMMENT '是否可编辑0：可编辑，1：不可编辑',
    `value_type`             tinyint unsigned NOT NULL DEFAULT '0' COMMENT '值类型0：文本，1：单选',
    `value_option`           varchar(1024)             DEFAULT NULL COMMENT '值类型为单选时的可选项，多个可选项之间用,分隔',
    `project_name`           varchar(255)              DEFAULT NULL COMMENT '应用名称（应用配置时才生效）',
    `user_app_key`           varchar(255)              DEFAULT NULL COMMENT '用户信息（应用配置时才生效）',
    PRIMARY KEY (`id`)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4 COMMENT ='agent配置管理';

BEGIN;
INSERT
IGNORE INTO `t_tro_resource`(`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES ( NULL, 0, 'admins', '后台管理', NULL, '', 20000, '[]', NULL, NULL, NULL, NOW(), NOW(), 0);
set
@parentId = (SELECT id FROM `t_tro_resource` where `code`="admins");
INSERT
IGNORE INTO `t_tro_resource`(`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES (@parentId, 0, 'admins_admin', '探针版本管理', NULL, '[\"/api/fast/agent/access\"]', 21000, '[2,7]', NULL, NULL, NULL, NOW(), NOW(), 0);
INSERT
IGNORE INTO `t_tro_resource`(`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES (@parentId, 0, 'admins_simulationConfig', '仿真系统配置', NULL, '[\"/api/fast/agent/access/config\"]', 22000, '[3,4]', NULL, NULL, NULL, NOW(), NOW(), 0);

INSERT
IGNORE INTO `t_tro_resource`(`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES (NULL, 0, 'appConfigs', '应用配置', NULL, '', 30000, '[]', NULL, NULL, NULL, NOW(), NOW(), 0);

set
@appParentId = (SELECT id FROM `t_tro_resource` where `code`="appConfigs");
INSERT
IGNORE INTO `t_tro_resource`(`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES (@appParentId, 0, 'appManage_appAccess', '新应用接入', NULL, '[\"/api/fast/agent/access\"]', 31000, '[7]', NULL, NULL, NULL, NOW(), NOW(), 0);
INSERT
IGNORE INTO `t_tro_resource`(`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES (@appParentId, 0, 'appManage_agentManage', '探针管理', NULL, '[\"/api/fast/agent/access/probe\"]', 32000, '[]', NULL, NULL, NULL, NOW(), NOW(), 0);
INSERT
IGNORE INTO `t_tro_resource`(`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES (@appParentId, 0, 'appManage_errorLog', '异常日志', NULL, '[\"/api/fast/agent/access/errorLog\"]', 33000, '[]', NULL, NULL, NULL, NOW(), NOW(), 0);
UPDATE `t_tro_resource` SET `parent_id` = @appParentId WHERE `code` = "appManage" OR `code` = "appWhiteList";
COMMIT;