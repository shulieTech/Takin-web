DROP TABLE IF EXISTS `t_application_tag_ref`;
CREATE TABLE `t_application_tag_ref`
(
    `id`               bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `application_id`   bigint(20) NOT NULL COMMENT '应用id',
    `application_name` varchar(20)  DEFAULT '' COMMENT '应用名',
    `tag_id`           bigint(20) NOT NULL COMMENT '标签id',
    `tag_name`         varchar(64) NOT NULL COMMENT '标签名',
    `gmt_create`       datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_update`       datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `env_code`         varchar(100) DEFAULT 'test' COMMENT '环境标识',
    `tenant_id`        bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
    `IS_DELETED`       tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用标签表';

DROP TABLE IF EXISTS `t_plugin_library`;
CREATE TABLE `t_plugin_library`
(
    `id`                 bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `plugin_name`        varchar(100) NOT NULL COMMENT '插件名称',
    `plugin_type`        tinyint(3) DEFAULT 0 COMMENT '插件类型 0:插件 1:主版本 2:agent版本',
    `version`            varchar(20)  NOT NULL COMMENT '插件版本',
    `version_num`        bigint COMMENT '版本对应数值',
    `is_custom_mode`     tinyint(3) DEFAULT 0 COMMENT '是否为定制插件，0：否，1：是',
    `update_description` varchar(1024)                    DEFAULT '' COMMENT '更新说明',
    `download_path`      varchar(255)                    DEFAULT '' COMMENT '下载地址',
    `remark`             varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
    `ext`                varchar(200) NOT NULL           DEFAULT '' COMMENT 'jar包配置文件原始数据,当前是主版本时填',
    `gmt_create`         datetime                        DEFAULT CURRENT_TIMESTAMP,
    `gmt_update`         datetime                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `IS_DELETED`         tinyint(2) NOT NULL DEFAULT 0 COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `pluginName_version` (`plugin_name`,`version`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='插件版本库';

DROP TABLE IF EXISTS `t_plugin_dependent`;
CREATE TABLE `t_plugin_dependent`
(
    `id`                           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `plugin_name`                  varchar(100)                    DEFAULT '' COMMENT '插件名称',
    `plugin_version`               varchar(20)                     DEFAULT '' COMMENT '插件版本',
    `plugin_version_num`           bigint COMMENT '版本对应数值',
    `dependent_plugin_name`        varchar(100)                    DEFAULT '' COMMENT '插件名称',
    `dependent_plugin_version`     varchar(20)                     DEFAULT '' COMMENT '插件版本',
    `dependent_plugin_version_num` bigint COMMENT '版本对应数值',
    `requisite`                    tinyint(2) DEFAULT 0 COMMENT '是否必须 0:必须 1:非必须',
    `remark`                       varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
    `gmt_create`                   datetime                        DEFAULT CURRENT_TIMESTAMP,
    `gmt_update`                   datetime                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `IS_DELETED`                   tinyint(2) NOT NULL DEFAULT 0 COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX                          `idx_pluginName_pluginVersion`(`plugin_name`, `plugin_version`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='插件依赖库';

DROP TABLE IF EXISTS `t_application_plugin_upgrade`;
CREATE TABLE `t_application_plugin_upgrade`
(
   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
   `application_id` bigint(20) DEFAULT '0' COMMENT '应用id',
   `application_name` varchar(20) DEFAULT '' COMMENT '应用名',
   `upgrade_batch` varchar(64) NOT NULL COMMENT '升级批次 根据升级内容生成MD5',
   `upgrade_context` varchar(10240) DEFAULT '' COMMENT '升级内容 格式 {pluginId,pluginId}',
   `upgrade_agent_id` varchar(40) DEFAULT NULL COMMENT '处理升级对应的agentId',
   `download_path` varchar(255) DEFAULT '' COMMENT '下载地址',
   `plugin_upgrade_status` tinyint(2) DEFAULT '0' COMMENT '升级状态 0 未升级 1升级成功 2升级失败 3已回滚',
   `error_info` varchar(2048) DEFAULT NULL COMMENT '升级失败信息',
   `type` tinyint(2) DEFAULT NULL COMMENT '升级单类型 0 agent上报，1 主动升级',
   `remark` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
   `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
   `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
   `IS_DELETED` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE KEY `uni_applicationId_upgradeBatch_env` (`application_id`,`upgrade_batch`,`env_code`,`tenant_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用升级单';

DROP TABLE IF EXISTS `t_application_plugin_upgrade_ref`;
CREATE TABLE `t_application_plugin_upgrade_ref`
(
    `id`                 bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `upgrade_batch`      varchar(64) NOT NULL COMMENT '升级批次 根据升级内容生成MD5',
    `plugin_name`        varchar(100)                    DEFAULT '' COMMENT '插件名称',
    `plugin_version`     varchar(20)                     DEFAULT '' COMMENT '插件版本',
    `plugin_version_num` bigint COMMENT '版本对应数值',
    `remark`             varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
    `gmt_create`         datetime                        DEFAULT CURRENT_TIMESTAMP,
    `gmt_update`         datetime                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `env_code`           varchar(100)                    DEFAULT 'test' COMMENT '环境标识',
    `tenant_id`          bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
    `IS_DELETED`         tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uni_upgradeBatch_plugin_env` (`upgrade_batch`,`plugin_name`,`plugin_version`,`env_code`,`tenant_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='应用升级批次明细';

DROP TABLE IF EXISTS `t_agent_report`;
CREATE TABLE `t_agent_report`
(
    `id`                   bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `application_id`       bigint(20) DEFAULT 0 COMMENT '应用id',
    `application_name`     varchar(20)  DEFAULT '' COMMENT '应用名',
    `agent_id`             varchar(40) NOT NULL COMMENT 'agentId',
    `ip_address`           varchar(20)  DEFAULT '' COMMENT '节点ip',
    `progress_id`          varchar(20)  DEFAULT '' COMMENT '进程号',
    `agent_version`        varchar(20)  DEFAULT '' COMMENT 'agent版本号',
    `cur_upgrade_batch`    varchar(64)  DEFAULT 0 COMMENT '升级批次 根据升级内容生成MD5',
    `status`               tinyint(2) DEFAULT 0 COMMENT '节点状态 0:未知,1:启动中,2:升级待重启,3:运行中,4:异常,5:休眠,6:卸载',
    `agent_error_info`     varchar(1024) COMMENT 'agent的错误信息',
    `simulator_error_info` varchar(1024) COMMENT 'simulator错误信息',
    `gmt_create`           datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_update`           datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `env_code`             varchar(100) DEFAULT 'test' COMMENT '环境标识',
    `tenant_id`            bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
    `IS_DELETED`           tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uni_applicationId_agentId_envCode_tenantId` (`application_id`,`agent_id`,`env_code`,`tenant_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='探针心跳数据';

DROP TABLE IF EXISTS `t_application_plugin_download_path`;
CREATE TABLE `t_application_plugin_download_path`
(
    `id`               bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `application_id`   bigint(20) DEFAULT 0 NOT NULL COMMENT '应用id',
    `application_name` varchar(20)  DEFAULT '' COMMENT '应用名',
    `path_type`        tinyint(4) NOT NULL DEFAULT 0 COMMENT '类型 0:oss;1:ftp;2:nginx',
    `context`          varchar(255) NOT NULL COMMENT '配置内容',
    `salt`             varchar(60)  NOT NULL COMMENT '密码加密的密钥',
    `gmt_create`       datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_update`       datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `env_code`         varchar(100) DEFAULT 'test' COMMENT '环境标识',
    `tenant_id`        bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
    `valid_status`     tinyint(4) DEFAULT 0 COMMENT '检测状态 0:未检测,1:检测失败,2:检测成功',
    `valid_error_info`     varchar(10240) DEFAULT '' COMMENT '检测异常信息',
    `IS_DELETED`       tinyint(4) NOT NULL DEFAULT 0 COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='探针根目录';

DROP TABLE IF EXISTS `t_plugin_tenant_ref`;
CREATE TABLE `t_plugin_tenant_ref`
(
    `id`                 bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `plugin_name`        varchar(100) DEFAULT '' COMMENT '插件名称',
    `plugin_version`     varchar(20)  DEFAULT '' COMMENT '插件版本',
    `owning_tenant_id`   bigint(20) NOT NUll COMMENT '所属租户id',
    `gmt_create`         datetime     DEFAULT CURRENT_TIMESTAMP,
    `gmt_update`         datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `IS_DELETED`         tinyint(2) NOT NULL DEFAULT 0 COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `pluginName_pluginVersion_owningTenantId` (`plugin_name`,`plugin_version`,`owning_tenant_id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='插件租户关联表';

UPDATE t_tro_resource SET IS_SUPER = 0 WHERE code = "admins_admin";