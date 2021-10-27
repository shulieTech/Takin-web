CREATE TABLE IF NOT EXISTS `t_cache_config_template` (
                                           `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                           `name` varchar(100) DEFAULT '' COMMENT '中间件中文描述',
                                           `type` varchar(100) DEFAULT '' COMMENT '中间件所属类型',
                                           `eng_name` varchar(100) DEFAULT '' COMMENT '中间件英文名称',
                                           `cobm_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持自动梳理;0:不支持;1:支持',
                                           `shadowtdb_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持影子key;0:不支持;1:支持',
                                           `shadowttable_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持影子集群;0:不支持;1:支持',
                                           `config` varchar(500) DEFAULT NULL COMMENT '配置文本',
                                           `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '1.可用，2不可用',
                                           `remark` varchar(500) DEFAULT NULL COMMENT '标记',
                                           `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
                                           `CUSTOMER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
                                           `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
                                           `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                           `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           UNIQUE KEY `eng_name` (`eng_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='缓存配置模版表';

CREATE TABLE IF NOT EXISTS `t_connectpool_config_template` (
                                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                                 `name` varchar(100) DEFAULT '' COMMENT '中间件中文描述',
                                                 `type` varchar(100) DEFAULT '' COMMENT '中间件所属类型',
                                                 `eng_name` varchar(100) DEFAULT '' COMMENT '中间件英文名称',
                                                 `cobm_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持自动梳理;0:不支持;1:支持',
                                                 `shadowtable_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持影子表方案;0:不支持;1:支持',
                                                 `shadowdb_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持影子库方案;0:不支持;1:支持',
                                                 `shadowdbwithshadowtable_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持影子库和影子库方案;0:不支持;1:支持',
                                                 `shadowdb_attribute` text COMMENT '影子库方案配置属性',
                                                 `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '1.可用，2不可用',
                                                 `remark` varchar(500) DEFAULT NULL COMMENT '标记',
                                                 `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
                                                 `CUSTOMER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
                                                 `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
                                                 `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                                 `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
                                                 PRIMARY KEY (`id`) USING BTREE,
                                                 UNIQUE KEY `eng_name` (`eng_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='连接池配置模版表';


CREATE TABLE IF NOT EXISTS `t_http_client_config_template` (
                                                 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                                 `name` varchar(100) DEFAULT '' COMMENT '中间件中文描述',
                                                 `type` varchar(100) DEFAULT '' COMMENT '中间件所属类型',
                                                 `eng_name` varchar(100) DEFAULT '' COMMENT '中间件英文名称',
                                                 `cobm_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持自动梳理;0:不支持;1:支持',
                                                 `whitelist_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持白名单;0:不支持;1:支持',
                                                 `return_mock_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持返回值mock;0:不支持;1:支持',
                                                 `return_mock` text CHARACTER SET utf8 COMMENT '返回值mock文本',
                                                 `forward_mock_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持转发mock;0:不支持;1:支持',
                                                 `forward_mock` text CHARACTER SET utf8 COMMENT '转发mock文本',
                                                 `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '1.可用，2不可用',
                                                 `remark` varchar(500) DEFAULT NULL COMMENT '标记',
                                                 `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
                                                 `CUSTOMER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
                                                 `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
                                                 `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
                                                 `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                 `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                 PRIMARY KEY (`id`) USING BTREE,
                                                 UNIQUE KEY `eng_name` (`eng_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='http-client配置模版表';

CREATE TABLE IF NOT EXISTS `t_app_remote_call_template_mapping` (
                                                      `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                                      `interfaceType` varchar(50) DEFAULT '' COMMENT '接口类型',
                                                      `template` varchar(50) DEFAULT '' COMMENT '对应的模板',
                                                      `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '0.可用，1不可用',
                                                      `remark` varchar(500) DEFAULT NULL COMMENT '标记',
                                                      `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
                                                      `CUSTOMER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
                                                      `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
                                                      `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                      `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                                      `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
                                                      PRIMARY KEY (`id`) USING BTREE,
                                                      UNIQUE KEY `eng_name` (`interfaceType`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='远程调用接口类型与模板映射';


CREATE TABLE IF NOT EXISTS `t_mq_config_template` (
                                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                        `name` varchar(100) DEFAULT '' COMMENT '中间件中文描述',
                                        `type` varchar(100) DEFAULT '' COMMENT '中间件所属类型',
                                        `eng_name` varchar(100) DEFAULT '' COMMENT '中间件英文名称',
                                        `cobm_enable` tinyint(3) DEFAULT '1' COMMENT '是否支持自动梳理;0:不支持;1:支持',
                                        `shadowconsumer_enable` tinyint(3) DEFAULT '1' COMMENT '是否支持影子消费;0:不支持;1:支持',
                                        `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '0.可用，1不可用',
                                        `remark` varchar(500) DEFAULT NULL COMMENT '标记',
                                        `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
                                        `CUSTOMER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
                                        `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
                                        `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                        `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
                                        `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE KEY `eng_name` (`eng_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='MQ配置模版表';


CREATE TABLE IF NOT EXISTS `t_rpc_config_template` (
                                         `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                         `name` varchar(100) DEFAULT '' COMMENT '中间件中文描述',
                                         `type` varchar(100) DEFAULT '' COMMENT '中间件所属类型',
                                         `eng_name` varchar(100) DEFAULT '' COMMENT '中间件英文名称',
                                         `cobm_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持自动梳理;0:不支持;1:支持',
                                         `whitelist_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持白名单;0:不支持;1:支持',
                                         `return_mock_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持返回值mock;0:不支持;1:支持',
                                         `return_mock` text CHARACTER SET utf8 COMMENT '返回值mock文本',
                                         `forward_mock_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持转发mock;0:不支持;1:支持',
                                         `forward_mock` text CHARACTER SET utf8 COMMENT '转发mock文本',
                                         `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '1.可用，2不可用',
                                         `remark` varchar(500) DEFAULT NULL COMMENT '标记',
                                         `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
                                         `CUSTOMER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
                                         `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
                                         `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                         `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                         `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
                                         `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         PRIMARY KEY (`id`) USING BTREE,
                                         UNIQUE KEY `eng_name` (`eng_name`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='rpc框架配置模版';



--相关配置存储表

CREATE TABLE IF NOT EXISTS `t_application_ds_cache_manage` (
                                                 `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                                 `APPLICATION_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '应用主键',
                                                 `APPLICATION_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '应用名称',
                                                 `CACHE_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '中间件名称',
                                                 `AGENT_SOURCE_TYPE` varchar(50) NOT NULL DEFAULT '' COMMENT 'agent上报的模板类型key',
                                                 `COLONY` varchar(250) NOT NULL DEFAULT '' COMMENT '集群地址',
                                                 `USER_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '源用户名',
                                                 `PWD` varchar(50) NOT NULL DEFAULT '' COMMENT '源密码',
                                                 `TYPE` varchar(50) NOT NULL COMMENT '缓存模式 ',
                                                 `DS_TYPE` tinyint(2) DEFAULT '6' COMMENT '影子方案类型 ',
                                                 `FILE_EXTEDN` text COMMENT '额外配置,存json',
                                                 `CONFIG_JSON` text COMMENT '配置全部参数json化',
                                                 `SOURCE` tinyint(2) NOT NULL DEFAULT '0' COMMENT '方案类型 0:amdb 1:手动录入',
                                                 `STATUS` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0:启用；1:禁用',
                                                 `CUSTOMER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
                                                 `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
                                                 `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                                 `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
                                                 `SHA_DOW_FILE_EXTEDN` text COMMENT '影子连接池额外配置,存json',
                                                 PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='缓存影子库表配置表';

CREATE TABLE IF NOT EXISTS `t_application_ds_db_manage` (
                                              `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                              `APPLICATION_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '应用主键',
                                              `APPLICATION_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '应用名称',
                                              `CONN_POOL_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '连接池名称 druid, hikari,c3p0等',
                                              `AGENT_SOURCE_TYPE` varchar(50) NOT NULL DEFAULT '' COMMENT 'agent上报的模板类型key',
                                              `DB_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源名称',
                                              `URL` varchar(250) NOT NULL DEFAULT '' COMMENT '业务数据源url',
                                              `USER_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源用户名',
                                              `PWD` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源密码',
                                              `DS_TYPE` tinyint(2) DEFAULT '100' COMMENT '方案类型 0:影子库 1:影子表 2:影子server',
                                              `SHA_DOW_URL` varchar(250) NOT NULL DEFAULT '' COMMENT '影子数据源url',
                                              `SHA_DOW_USER_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '影子数据源用户名',
                                              `SHA_DOW_PWD` varchar(50) NOT NULL DEFAULT '' COMMENT '影子数据源密码',
                                              `FILE_EXTEDN` text COMMENT '连接池额外配置,存json',
                                              `SHA_DOW_FILE_EXTEDN` text COMMENT '影子连接池额外配置,存json',
                                              `CONFIG_JSON` text COMMENT '配置全部参数json化',
                                              `SOURCE` tinyint(2) NOT NULL DEFAULT '0' COMMENT '方案类型 0:amdb 1:手动录入',
                                              `STATUS` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0:启用；1:禁用',
                                              `CUSTOMER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
                                              `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
                                              `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                              `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                              `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
                                              PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8 COMMENT='db连接池影子库表配置表';

CREATE TABLE IF NOT EXISTS `t_application_ds_db_table` (
                                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                             `app_id` bigint(20) NOT NULL COMMENT '应用id',
                                             `url` varchar(250) DEFAULT '' COMMENT '业务数据源url',
                                             `user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源用户名',
                                             `biz_data_base` varchar(128) DEFAULT '' COMMENT '业务库',
                                             `biz_table` varchar(128) DEFAULT '' COMMENT '业务表',
                                             `shadow_table` varchar(128) DEFAULT NULL COMMENT '影子表',
                                             `is_check` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否选中 0=未选中，1=选中',
                                             `manual_tag` tinyint(2) DEFAULT '0' COMMENT '是否手动录入 0:否;1:是',
                                             `CUSTOMER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
                                             `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
                                             `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
                                             PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4 COMMENT='业务数据库表';


--表结构变更
ALTER TABLE `t_app_remote_call`
    ADD  `manual_tag`  tinyint(2)     default 0   not null comment '是否手动录入 0:否;1:是',
  ADD  `interface_child_type` varchar(50)      default '' not null comment '接口子类型',
  ADD  `remark` varchar(50)      default ''  not null comment '备注';


--- 影子消费者表新增手动录入标识字段
ALTER TABLE `t_shadow_mq_consumer`
    ADD  `manual_tag`  tinyint(2)     default 0   not null comment '是否手动录入 0:否;1:是';



-- 数据初始化脚本
-- ----------------------------
-- Table structure for t_app_remote_call_template_mapping
-- ----------------------------

INSERT INTO `t_app_remote_call_template_mapping` VALUES (1, 'httpclient3', 'http', 0, NULL, NULL, 0, 0, '2021-09-09 14:47:26', '2021-09-09 14:47:26', 0);
INSERT INTO `t_app_remote_call_template_mapping` VALUES (2, 'httpclient4', 'http', 0, NULL, NULL, 0, 0, '2021-09-09 14:47:39', '2021-09-09 14:47:39', 0);
INSERT INTO `t_app_remote_call_template_mapping` VALUES (3, 'jdk-http', 'http', 0, NULL, NULL, 0, 0, '2021-09-09 14:47:48', '2021-09-09 14:47:48', 0);
INSERT INTO `t_app_remote_call_template_mapping` VALUES (4, 'async-httpclient', 'http', 0, NULL, NULL, 0, 0, '2021-09-09 14:47:57', '2021-09-09 14:47:57', 0);
INSERT INTO `t_app_remote_call_template_mapping` VALUES (5, 'google-httpclient', 'http', 0, NULL, NULL, 0, 0, '2021-09-09 14:48:05', '2021-09-09 14:48:05', 0);
INSERT INTO `t_app_remote_call_template_mapping` VALUES (6, 'dubbo', 'rpc', 0, NULL, NULL, 0, 0, '2021-09-09 14:48:13', '2021-09-09 14:48:13', 0);
INSERT INTO `t_app_remote_call_template_mapping` VALUES (7, 'grpc', 'rpc', 0, NULL, NULL, 0, 0, '2021-09-09 14:48:40', '2021-09-09 14:48:40', 0);
INSERT INTO `t_app_remote_call_template_mapping` VALUES (8, 'feign', 'rpc', 0, NULL, NULL, 0, 0, '2021-09-09 14:48:55', '2021-09-09 14:48:55', 0);
INSERT INTO `t_app_remote_call_template_mapping` VALUES (9, 'HTTP', 'http', 0, NULL, NULL, 0, 0, '2021-09-10 18:01:11', '2021-09-10 18:01:11', 0);



INSERT INTO `t_cache_config_template`  VALUES (1, 'jedis', '缓存', 'jedis', 1, 1, 1, NULL, 0, NULL, NULL, 0, 0, '2021-09-16 16:56:45', '2021-09-16 16:56:45', 0);
INSERT INTO `t_cache_config_template`  VALUES (2, 'lettuce', '缓存', 'lettuce', 1, 1, 1, NULL, 0, NULL, NULL, 0, 0, '2021-09-22 15:26:47', '2021-09-22 15:26:47', 0);
INSERT INTO `t_cache_config_template`  VALUES (3, 'redisson', '缓存', 'redisson', 1, 1, 1, NULL, 0, NULL, NULL, 0, 0, '2021-09-22 15:27:16', '2021-09-22 15:27:16', 0);


INSERT INTO `t_connectpool_config_template` VALUES (1, 'druid', '连接池', 'druid', 1, 1, 1, 1, 'driverClassName,maxActive,initialSize', 0, NULL, NULL, 0, 0, '2021-09-16 16:54:51', '2021-09-16 16:54:51', 0);
INSERT INTO `t_connectpool_config_template` VALUES (2, 'hikari', '连接池', 'hikari', 1, 1, 1, 1, 'driverClassName,maximumPoolSize\n', 0, NULL, NULL, 0, 0, '2021-10-08 11:24:10', '2021-10-08 11:24:10', 0);
INSERT INTO `t_connectpool_config_template` VALUES (3, 'c3p0', '连接池', 'c3p0', 1, 1, 1, 1, NULL, 0, NULL, NULL, 0, 0, '2021-10-08 15:46:04', '2021-10-08 15:46:04', 0);
INSERT INTO `t_connectpool_config_template` VALUES (4, 'proxool', '连接池', 'proxool', 1, 1, 1, 1, 'driverClassName,alias\n', 0, NULL, NULL, 0, 0, '2021-10-10 21:52:14', '2021-10-10 21:52:14', 0);
INSERT INTO `t_connectpool_config_template` VALUES (5, 'dbcp', '连接池', 'dbcp', 1, 1, 1, 1, 'driverClassName', 0, NULL, NULL, 0, 0, '2021-10-10 21:52:43', '2021-10-10 21:52:43', 0);
INSERT INTO `t_connectpool_config_template` VALUES (6, 'dbcp2', '连接池', 'dbcp2', 1, 1, 1, 1, 'driverClassName', 0, NULL, NULL, 0, 0, '2021-10-10 21:53:13', '2021-10-10 21:53:13', 0);
INSERT INTO `t_connectpool_config_template` VALUES (7, 'other', '连接池', 'other', 1, 1, 1, 1, NULL, 0, NULL, NULL, 0, 0, '2021-10-20 15:30:59', '2021-10-20 15:30:59', 0);


INSERT INTO `t_http_client_config_template`  VALUES (1, 'httpclient3', 'http', 'httpclient3', 1, 1, 1, '返回mock文本', 1, '转发mock文本', 0, NULL, NULL, 0, 0, 0, '2021-09-09 15:34:12', '2021-09-09 15:34:12');
INSERT INTO `t_http_client_config_template`  VALUES (2, 'httpclient4', 'http', 'httpclient4', 1, 1, 1, '返回mock文本', 1, '转发mock文本', 0, NULL, NULL, 0, 0, 0, '2021-09-09 15:34:12', '2021-09-09 15:34:12');
INSERT INTO `t_http_client_config_template`  VALUES (3, 'jdk-http', 'http', 'jdk-http', 1, 1, 1, '返回mock文本', 1, '转发', 0, NULL, NULL, 0, 0, 0, '2021-09-09 15:34:12', '2021-09-09 15:34:12');
INSERT INTO `t_http_client_config_template`  VALUES (4, 'async-httpclient', 'http', 'async-httpclient', 1, 1, 1, '返货mock', 1, '转发', 0, NULL, NULL, 0, 0, 0, '2021-09-09 15:34:12', '2021-09-09 15:34:12');
INSERT INTO `t_http_client_config_template`  VALUES (5, 'google-httpclient\n', 'http', 'google-httpclient', 1, 1, 1, '返回mock', 1, '转发', 0, NULL, NULL, 0, 0, 0, '2021-09-09 15:34:12', '2021-09-09 15:34:12');
INSERT INTO `t_http_client_config_template`  VALUES (6, 'HTTP', 'http', 'HTTP', 1, 1, 1, NULL, 1, NULL, 0, NULL, NULL, 0, 0, 0, '2021-09-10 17:59:08', '2021-09-10 17:59:08');


INSERT INTO `t_mq_config_template`  VALUES (1, 'RabbitMQ', 'MQ', 'RABBITMQ', 1, 1, 0, NULL, NULL, 0, 0, '2021-09-09 14:15:02', '2021-09-09 14:15:02', 0, '2021-09-09 14:26:00', '2021-09-09 14:26:00');
INSERT INTO `t_mq_config_template`  VALUES (2, 'RocketMQ', 'MQ', 'ROCKETMQ', 1, 1, 0, NULL, NULL, 0, 0, '2021-09-09 14:15:29', '2021-09-09 14:15:29', 0, '2021-09-09 14:26:00', '2021-09-09 14:26:00');
INSERT INTO `t_mq_config_template`  VALUES (3, 'KafKa', 'MQ', 'KAFKA', 1, 1, 0, NULL, NULL, 0, 0, '2021-09-09 14:15:47', '2021-09-09 14:15:47', 0, '2021-09-09 14:26:00', '2021-09-09 14:26:00');


INSERT INTO `t_rpc_config_template` VALUES (1, 'dubbo', 'rpc', 'dubbo', 1, 1, 1, 'mock', 0, 'mock', 0, NULL, NULL, 0, 0, '2021-09-09 14:57:51', '2021-09-09 14:57:51', 0, '2021-09-09 15:25:35', '2021-09-09 15:25:35');
INSERT INTO `t_rpc_config_template` VALUES (2, 'grpc', 'rpc', 'grpc', 1, 1, 0, 'mock', 0, 'mock', 0, NULL, NULL, 0, 0, '2021-09-09 14:58:30', '2021-09-09 14:58:30', 0, '2021-09-09 15:25:35', '2021-09-09 15:25:35');
INSERT INTO `t_rpc_config_template` VALUES (3, 'feign', 'rpc', 'feign', 1, 1, 1, 'mock', 0, 'mock', 0, NULL, NULL, 0, 0, '2021-09-09 14:58:44', '2021-09-09 14:58:44', 0, '2021-09-09 15:25:35', '2021-09-09 15:25:35');


