CREATE TABLE IF NOT EXISTS `t_tenant_info`
(
    `id`             bigint(20)     NOT NULL AUTO_INCREMENT,
    `key`            varchar(512)  NOT NULL COMMENT '租户key 唯一，同时也是userappkey',
    `name`           varchar(512)   NOT NULL COMMENT '租户名称',
    `nick`           varchar(512)   NOT NULL COMMENT '租户中文名称',
    `code`           varchar(512)   NOT NULL COMMENT '租户代码',
    `config`       	 varchar(1024)  DEFAULT "" COMMENT '租户配置',
    `status`         tinyint(1)     NOT NULL DEFAULT '1' COMMENT '状态 0: 停用 1:正常 2：欠费 3：试用',
    `is_deleted`     tinyint(1)     NOT NULL DEFAULT '0' COMMENT '0: 正常 1： 删除',
    `gmt_create`     datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`     datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_key` (`key`) USING BTREE,
    UNIQUE KEY `unique_code` (`code`) USING BTREE
    ) ENGINE = InnoDB;
CREATE TABLE IF NOT EXISTS `t_tenant_env_ref`
(
    `id`             bigint(20)     NOT NULL AUTO_INCREMENT,
    `tenant_id`    bigint(20)     NOT NULL COMMENT '租户id',
    `env_code`       varchar(512)   NOT NULL COMMENT '环境代码，测试环境：test,生产环境：prod',
    `env_name`       varchar(1024)  NOT NULL COMMENT '环境名称',
    `desc`           varchar(1024)  DEFAULT NULL COMMENT '描述',
    `is_default`     tinyint(1)     NOT NULL DEFAULT '0' COMMENT '是否默认',
    `is_deleted`     tinyint(1)     NOT NULL DEFAULT '0' COMMENT '0: 正常 1： 删除',
    `gmt_create`     datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`     datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_code` (`env_code`) USING BTREE
    ) ENGINE = InnoDB;

-- 配置表
CREATE TABLE IF NOT EXISTS `t_tenant_config`
(
    `id`             bigint(20)     NOT NULL AUTO_INCREMENT,
    `tenant_id`    	 bigint(20)     NOT NULL COMMENT '租户id',
    `env_code`       varchar(512)   NOT NULL COMMENT '环境代码，测试环境：test,生产环境：prod',
    `type`           tinyint(10)  	NOT NULL COMMENT '配置类型":0:存储配置;',
    `key`           varchar(128)   NOT NULL COMMENT '配置名',
    `value`      		 LONGTEXT       NOT NULL COMMENT '配置值',
    `status`      	 tinyint(4)     NOT NULL DEFAULT '1' COMMENT '状态：0：停用；1：启用；',
    `is_deleted`     tinyint(1)     NOT NULL DEFAULT '0' COMMENT '0: 正常 1： 删除',
    `gmt_create`     datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`     datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_key_tenant_env` (`tenant_id`,`env_code`,`key`) USING BTREE
    ) ENGINE = InnoDB;
INSERT INTO `t_tenant_env_ref`(`tenant_id`, `env_code`, `env_name`,`is_default`) VALUES (1, 'test', '测试环境',1);
INSERT INTO `t_tenant_env_ref`(`tenant_id`, `env_code`, `env_name`,`desc`,`is_default`) VALUES (1, 'prod', '生产环境','当前环境为生产环境，请谨慎操作',0);

INSERT INTO `t_tenant_info`(`key`, `name`, `nick`, `code`, `config`) VALUES ('5b06060a-17cb-4588-bb71-edd7f65035af', 'default', 'default', 'default', '');

---t_base_config
DROP TABLE t_base_config;
CREATE TABLE `t_base_config` (
     `CONFIG_CODE` varchar(64) NOT NULL COMMENT '配置编码',
     `CONFIG_VALUE` longtext NOT NULL COMMENT '配置值',
     `CONFIG_DESC` varchar(128) NOT NULL COMMENT '配置说明',
     `USE_YN` int(11) DEFAULT '0' COMMENT '是否可用(0表示未启用,1表示启用)',
     `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
     `UPDATE_TIME` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     `env_code` VARCHAR ( 20 ) NOT NULL DEFAULT 'system' COMMENT '环境code',
     `tenant_id` BIGINT ( 20 ) NOT NULL DEFAULT -1 COMMENT '租户id',
      PRIMARY KEY (`CONFIG_CODE`, `env_code`, `tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='takin基础配置表';

INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('ALL_BUTTON', '[\n    \"appManage_2_create\",\n    \"appManage_3_update\",\n    \"appManage_4_delete\",\n    \"appManage_6_enable_disable\",\n    \"bottleneckConfig_3_update\",\n    \"businessActivity_2_create\",\n    \"businessActivity_3_update\",\n    \"businessActivity_4_delete\",\n    \"businessFlow_2_create\",\n    \"businessFlow_3_update\",\n    \"businessFlow_4_delete\",\n    \"configCenter_authorityConfig_2_create\",\n    \"configCenter_authorityConfig_3_update\",\n    \"configCenter_authorityConfig_4_delete\",\n    \"configCenter_bigDataConfig_3_update\",\n    \"configCenter_blacklist_2_create\",\n    \"configCenter_blacklist_3_update\",\n    \"configCenter_blacklist_4_delete\",\n    \"configCenter_blacklist_6_enable_disable\",\n    \"configCenter_dataSourceConfig_2_create\",\n    \"configCenter_dataSourceConfig_3_update\",\n    \"configCenter_dataSourceConfig_4_delete\",\n    \"configCenter_entryRule_2_create\",\n    \"configCenter_entryRule_3_update\",\n    \"configCenter_entryRule_4_delete\",\n    \"configCenter_middlewareManage_3_update\",\n    \"configCenter_pressureMeasureSwitch_6_enable_disable\",\n    \"configCenter_whitelistSwitch_6_enable_disable\",\n    \"debugTool_linkDebug_2_create\",\n    \"debugTool_linkDebug_3_update\",\n    \"debugTool_linkDebug_4_delete\",\n    \"debugTool_linkDebug_5_start_stop\",\n    \"exceptionNoticeManage_2_create\",\n    \"exceptionNoticeManage_3_update\",\n    \"exceptionNoticeManage_4_delete\",\n    \"patrolBoard_2_create\",\n    \"patrolBoard_3_update\",\n    \"patrolBoard_4_delete\",\n    \"patrolManage_2_create\",\n    \"patrolManage_3_update\",\n    \"patrolManage_4_delete\",\n    \"patrolManage_5_start_stop\",\n    \"pressureTestManage_pressureTestScene_2_create\",\n    \"pressureTestManage_pressureTestScene_3_update\",\n    \"pressureTestManage_pressureTestScene_4_delete\",\n    \"pressureTestManage_pressureTestScene_5_start_stop\",\n    \"scriptManage_2_create\",\n    \"scriptManage_3_update\",\n    \"scriptManage_4_delete\",\n    \"scriptManage_7_download\"\n]', '全部按钮名称', 0, '2021-10-20 18:53:10', '2021-10-20 18:53:10');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('ALL_MENU', '[\n    {\n        \"title\":\"系统概览\",\n        \"key\":\"dashboard\",\n        \"icon\":\"dashboard\",\n        \"path\":\"/dashboard\",\n        \"type\":\"Item\"\n    },\n    {\n        \"icon\":\"shop\",\n        \"title\":\"仿真平台\",\n        \"key\":\"appManage\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"title\":\"应用配置\",\n                \"key\":\"appManage\",\n                \"path\":\"/appConfig\",\n                \"type\":\"SubMenu\",\n                \"children\":[\n                    {\n                        \"title\":\"新应用接入\",\n                        \"path\":\"/appAccess\",\n                        \"type\":\"Item\",\n                        \"key\":\"appManage_appAccess\"\n                    },\n                    {\n                        \"title\":\"探针列表\",\n                        \"type\":\"Item\",\n                        \"path\":\"/agentManage\",\n                        \"key\":\"appManage_agentManage\"\n                    },\n                    {\n                        \"title\":\"应用管理\",\n                        \"key\":\"appManage\",\n                        \"path\":\"/appManage\",\n                        \"type\":\"Item\",\n                        \"children\":[\n                            {\n                                \"title\":\"应用详情\",\n                                \"key\":\"appManage\",\n                                \"type\":\"NoMenu\",\n                                \"path\":\"/appManages/details\"\n                            }\n                        ]\n                    },\n                    {\n                        \"title\":\"白名单列表\",\n                        \"key\":\"appWhiteList\",\n                        \"type\":\"Item\",\n                        \"path\":\"/pro/appWhiteList\"\n                    },\n                    {\n                        \"title\":\"异常日志\",\n                        \"path\":\"/errorLog\",\n                        \"type\":\"Item\",\n                        \"key\":\"appManage_errorLog\"\n                    }\n                ]\n            },\n            {\n                \"title\":\"链路管理\",\n                \"key\":\"linkTease\",\n                \"path\":\"/linkManage\",\n                \"type\":\"SubMenu\",\n                \"children\":[\n                    {\n                        \"title\":\"入口规则\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_entryRule\",\n                        \"path\":\"/configCenter/entryRule\"\n                    },\n                    {\n                        \"title\":\"业务活动\",\n                        \"type\":\"Item\",\n                        \"key\":\"businessActivity\",\n                        \"path\":\"/businessActivity\",\n                        \"children\":[\n                            {\n                                \"title\":\"新增业务活动\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"businessActivity\",\n                                \"path\":\"/businessActivity/addEdit\"\n                            }\n                        ]\n                    },\n                    {\n                        \"title\":\"业务流程\",\n                        \"key\":\"businessFlow\",\n                        \"type\":\"Item\",\n                        \"path\":\"/businessFlow\",\n                        \"children\":[\n                            {\n                                \"title\":\"新增业务流程\",\n                                \"key\":\"businessFlow\",\n                                \"type\":\"NoMenu\",\n                                \"path\":\"/businessFlow/addBusinessFlow\"\n                            }\n                        ]\n                    }\n                ]\n            },\n            {\n                \"title\":\"脚本管理\",\n                \"key\":\"scriptManages\",\n                \"type\":\"SubMenu\",\n                \"path\":\"/scriptManages\",\n                \"children\":[\n                    {\n                        \"title\":\"测试脚本\",\n                        \"key\":\"scriptManage\",\n                        \"type\":\"Item\",\n                        \"path\":\"/scriptManage\",\n                        \"children\":[\n                            {\n                                \"title\":\"脚本配置\",\n                                \"key\":\"scriptManage\",\n                                \"type\":\"NoMenu\",\n                                \"path\":\"/scriptManage/scriptConfig\"\n                            },\n                            {\n                                \"key\":\"scriptManage\",\n                                \"title\":\"脚本调试详情\",\n                                \"type\":\"NoMenu\",\n                                \"path\":\"/scriptManage/scriptDebugDetail\"\n                            }\n                        ]\n                    },\n                    {\n                        \"title\":\"运维脚本\",\n                        \"type\":\"Item\",\n                        \"key\":\"scriptOperation\",\n                        \"path\":\"/scriptOperation\"\n                    }\n                ]\n            },\n            {\n                \"title\":\"调试工具\",\n                \"key\":\"debugTool\",\n                \"path\":\"/debugTool\",\n                \"type\":\"SubMenu\",\n                \"children\":[\n                    {\n                        \"title\":\"链路调试\",\n                        \"type\":\"Item\",\n                        \"key\":\"debugTool_linkDebug\",\n                        \"path\":\"/pro/debugTool/linkDebug\",\n                        \"children\":[\n                            {\n                                \"title\":\"链路调试详情\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"debugTool_linkDebug\",\n                                \"path\":\"/pro/debugTool/linkDebug/detail\"\n                            }\n                        ]\n                    }\n                ]\n            },\n            {\n                \"title\":\"数据源管理\",\n                \"type\":\"SubMenu\",\n                \"path\":\"/dataSourceManage\",\n                \"children\":[\n                    {\n                        \"title\":\"数据源配置\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_dataSourceConfig\",\n                        \"path\":\"/configCenter/dataSourceConfig\"\n                    }\n                ]\n            }\n        ]\n    },\n    {\n        \"title\":\"压测平台\",\n        \"icon\":\"hourglass\",\n        \"path\":\"/hourglass\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"title\":\"压测管理\",\n                \"type\":\"SubMenu\",\n                \"path\":\"/pressureTestManage\",\n                \"children\":[\n                    {\n                        \"title\":\"压测场景\",\n                        \"type\":\"Item\",\n                        \"key\":\"pressureTestManage_pressureTestScene\",\n                        \"path\":\"/pressureTestManage/pressureTestScene\",\n                        \"children\":[\n                            {\n                                \"title\":\"压测场景配置\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"pressureTestManage_pressureTestScene\",\n                                \"path\":\"/pressureTestManage/pressureTestScene/pressureTestSceneConfig\"\n                            }\n                        ]\n                    },\n                    {\n                        \"title\":\"压测报告\",\n                        \"type\":\"Item\",\n                        \"key\":\"pressureTestManage_pressureTestReport\",\n                        \"path\":\"/pressureTestManage/pressureTestReport\",\n                        \"children\":[\n                            {\n                                \"title\":\"压测实况\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"pressureTestManage_pressureTestReport\",\n                                \"path\":\"/pressureTestManage/pressureTestReport/pressureTestLive\"\n                            },\n                            {\n                                \"title\":\"压测报告详请\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"pressureTestManage_pressureTestReport\",\n                                \"path\":\"/pressureTestManage/pressureTestReport/details\"\n                            }\n                        ]\n                    }\n                ]\n            }\n        ]\n    },\n    {\n        \"title\":\"巡检平台\",\n        \"key\":\"patrolScreen\",\n        \"path\":\"/accountBook\",\n        \"icon\":\"account-book\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"title\":\"巡检大盘\",\n                \"type\":\"Item\",\n                \"key\":\"patrolScreen\",\n                \"icon\":\"account-book\",\n                \"path\":\"/pro/e2eBigScreen\"\n            },\n            {\n                \"title\":\"巡检管理\",\n                \"path\":\"/pro/missionManage\",\n                \"type\":\"Item\",\n                \"icon\":\"account-book\",\n                \"key\":\"patrolBoard\",\n                \"children\":[\n                    {\n                        \"key\":\"patrolManage\",\n                        \"title\":\"巡检场景详情\",\n                        \"type\":\"NoMenu\",\n                        \"path\":\"/pro/missionManage/sceneDetails\"\n                    }\n                ]\n            },\n            {\n                \"title\":\"瓶颈列表\",\n                \"type\":\"Item\",\n                \"key\":\"bottleneckTable\",\n                \"icon\":\"unordered-list\",\n                \"path\":\"/pro/bottleneckTable\",\n                \"children\":[\n                    {\n                        \"title\":\"巡检场景详情\",\n                        \"key\":\"patrolManage\",\n                        \"type\":\"NoMenu\",\n                        \"path\":\"/pro/bottleneckTable/bottleneckDetails\"\n                    }\n                ]\n            },\n            {\n                \"title\":\"瓶颈通知\",\n                \"icon\":\"menu-unfold\",\n                \"type\":\"Item\",\n                \"key\":\"exceptionNoticeManage\",\n                \"path\":\"/pro/faultNotification\"\n            },\n            {\n                \"title\":\"配置中心\",\n                \"icon\":\"setting\",\n                \"type\":\"Item\",\n                \"path\":\"/pro/setFocus\",\n                \"key\":\"bottleneckConfig\"\n            }\n        ]\n    },\n    {\n        \"icon\":\"form\",\n        \"key\":\"tracks\",\n        \"path\":\"/tracks\",\n        \"title\":\"性能监控\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"key\":\"traceQuery\",\n                \"title\":\"链路查询\",\n                \"path\":\"/pro/track\",\n                \"type\":\"Item\"\n            }\n        ]\n    },\n    {\n        \"icon\":\"form\",\n        \"key\":\"safetyCenter\",\n        \"path\":\"/safetyCenter\",\n        \"title\":\"安全中心\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"key\":\"traceLogData\",\n                \"title\":\"trace数据审计\",\n                \"path\":\"/pro/traceLogData\",\n                \"type\":\"Item\"\n            }\n        ]\n    },\n    {\n        \"icon\":\"setting\",\n        \"title\":\"设置中心\",\n        \"path\":\"/setting\",\n        \"key\":\"configCenter\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"title\":\"系统管理\",\n                \"key\":\"configCenter\",\n                \"path\":\"/configCenter\",\n                \"type\":\"SubMenu\",\n                \"children\":[\n                    {\n                        \"title\":\"系统信息\",\n                        \"key\":\"system_info\",\n                        \"type\":\"Item\",\n                        \"path\":\"/configCenter/systemInfo\"\n                    },\n                    {\n                        \"title\":\"全局配置\",\n                        \"type\":\"Item\",\n                        \"key\":\"center_config\",\n                        \"path\":\"/configCenter/globalConfig\",\n                        \"children\":[\n                            {\n                                \"path\":\"\",\n                                \"title\":\"压测开关\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"configCenter_pressureMeasureSwitch\"\n                            },\n                            {\n                                \"path\":\"\",\n                                \"title\":\"白名单开关\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"configCenter_whitelistSwitch\"\n                            }\n                        ]\n                    },\n                    {\n                        \"title\":\"中间件库管理\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_middlewareManage\",\n                        \"path\":\"/configCenter/middlewareManage\"\n                    },\n                    {\n                        \"title\":\"开关配置\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_bigDataConfig\",\n                        \"path\":\"/configCenter/bigDataConfig\"\n                    },\n                    {\n                        \"path\":\"/admin\",\n                        \"title\":\"探针版本管理\",\n                        \"type\":\"Item\",\n                        \"key\":\"admins_admin\"\n                    },\n                    {\n                        \"type\":\"Item\",\n                        \"title\":\"仿真系统配置\",\n                        \"path\":\"/simulationConfig\",\n                        \"key\":\"admins_simulationConfig\"\n                    },\n                    {\n                        \"title\":\"流量账户\",\n                        \"key\":\"flowAccount\",\n                        \"type\":\"Item\",\n                        \"path\":\"/pro/flowAccount\"\n                    }\n                ]\n            },\n            {\n                \"title\":\"权限管理\",\n                \"type\":\"SubMenu\",\n                \"path\":\"/authorityManage\",\n                \"key\":\"configCenter_authorityConfig\",\n                \"children\":[\n                    {\n                        \"title\":\"权限配置中心\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_authorityConfig\",\n                        \"path\":\"/pro/configCenter/authorityConfig\"\n                    },\n                    {\n                        \"title\":\"操作日志\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_operationLog\",\n                        \"path\":\"/pro/configCenter/operationLog\"\n                    }\n                ]\n            }\n        ]\n    }\n]', '全部的菜单地址', 0, '2021-10-20 18:53:10', '2021-10-29 19:56:44');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('DINGDING_HOOK_URL', 'https://developers.dingtalk.com/document/app/custom-robot-access/title-72m-8ag-pqw', '钉钉机器人地址', 0, '2021-04-16 16:06:14', '2021-04-16 16:06:17');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('E2E_RT', '3000', '响应时间（单位：毫秒）', 0, NULL, NULL);
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('E2E_SUCCESS_RATE', '99.99', '成功率', 0, '2021-04-16 15:30:59', '2021-04-16 15:31:24');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('ES_SERVER', '{\"businessNodes\": \"192.168.1.210:9200,192.168.1.193:9200\",\"performanceTestNodes\": \"192.168.1.210:9200,192.168.1.193:9200\"}', '影子ES配置模板', 0, '2021-04-13 21:21:08', '2021-04-13 21:21:11');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('Hbase_SERVER', '{\"dataSourceBusiness\":{\"quorum\":\"192-168-1-171\",\"port\":\"2181\",\"znode\":\"/hbase\",\"params\":{}},\"dataSourcePerformanceTest\":{\"quorum\":\"192-168-1-137\",\"port\":\"2181\",\"znode\":\"/hbase\",\"params\":{}}}', '影子Hbase配置模板', 0, '2021-04-13 21:23:02', '2021-08-19 15:56:58');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('KAFKA_CLUSTER', '{\"key\": \"PT_业务主题\",\"topic\": \"PT_业务主题\",\"topicTokens\": \"PT_业务主题:影子主题token\",\"group\": \"\",\"systemIdToken\": \"\"}', '影子kafka集群配置模板', 0, '2021-04-25 20:57:12', '2021-04-25 20:57:12');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('NOTIFY_TYPE', 'DINGDING_HOOK_URL', '通知类型', 0, '2021-04-19 10:50:20', '2021-04-19 17:04:18');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('PRADAR_GUARD_TEMPLATE', 'import  com.example.demo.entity.User ;\nUser user = new User();\nuser.setName(\"挡板\");\nreturn user ;', '挡板模版', 1, NULL, '2020-06-09 10:58:15');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('REMOTE_CALL_ABLE_CONFIG', '{}', '远程调用配置类型可用性配置', 0, '2021-06-03 16:06:14', '2021-09-03 14:43:11');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_DB', '<configurations>\n              <!--数据源调停者-->\n              <datasourceMediator id=\"dbMediatorDataSource\">\n                  <property name=\"dataSourceBusiness\" ref=\"dataSourceBusiness\"/><!--业务数据源-->\n                  <property name=\"dataSourcePerformanceTest\" ref=\"dataSourcePerformanceTest\"/><!--压测数据源-->\n              </datasourceMediator>\n          \n              <!--数据源集合-->\n              <datasources>\n                  <datasource id=\"dataSourceBusiness\"><!--业务数据源--> <!--业务数据源只需要URL及用户名即可进行唯一性确认等验证-->\n                      <property name=\"url\" value=\"jdbc:mysql://114.55.42.181:3306/taco_app\"/><!--数据库连接URL-->\n                      <property name=\"username\" value=\"admin2017\"/><!--数据库连接用户名-->\n                  </datasource>\n                  <datasource id=\"dataSourcePerformanceTest\"><!--压测数据源-->\n                      <property name=\"driverClassName\" value=\"com.mysql.cj.jdbc.Driver\"/><!--数据库驱动-->\n                      <property name=\"url\" value=\"jdbc:mysql://114.55.42.181:3306/pt_taco_app\"/><!--数据库连接URL-->\n                      <property name=\"username\" value=\"admin2017\"/><!--数据库连接用户名-->\n                      <property name=\"password\" value=\"admin2017\"/><!--数据库连接密码-->\n                      <property name=\"initialSize\" value=\"5\"/>\n                      <property name=\"minIdle\" value=\"5\"/>\n                      <property name=\"maxActive\" value=\"20\"/>\n                      <property name=\"maxWait\" value=\"60000\"/>\n                      <property name=\"timeBetweenEvictionRunsMillis\" value=\"60000\"/>\n                      <property name=\"minEvictableIdleTimeMillis\" value=\"300000\"/>\n                      <property name=\"validationQuery\" value=\"SELECT 1 FROM DUAL\"/>\n                      <property name=\"testWhileIdle\" value=\"true\"/>\n                      <property name=\"testOnBorrow\" value=\"false\"/>\n                      <property name=\"testOnReturn\" value=\"false\"/>\n                      <property name=\"poolPreparedStatements\" value=\"true\"/>\n                      <property name=\"maxPoolPreparedStatementPerConnectionSize\" value=\"20\"/>\n                      <property name=\"connectionProperties\" value=\"druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\"/>\n                  </datasource>\n              </datasources>\n          </configurations>', '影子库配置模板', 0, '2020-11-30 16:47:30', '2020-11-30 16:48:11');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_REDIS_cluster', '{\n       \"nodes\":\"192.168.1.241:6379\",\n       \"password\":\"123456\",\n       \"database\":0\n}', '影子redis集群模式配置模版', 0, NULL, '2021-10-13 15:47:50');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_REDIS_masterSlave', '{\n      \"master\":\"192.168.2.240\",\n       \"nodes\":\"192.168.1.241:6379,192.168.1.241:6380\",\n       \"password\":\"123456\",\n       \"database\":0\n}', '影子redis主从模式配置模版', 0, NULL, '2021-10-13 15:48:49');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_REDIS_replicated', '{\n       \"nodes\":\"192.168.1.241:6379\",\n       \"password\":\"123456\",\n       \"database\":0\n}\n', '影子redis云托管模式配置模版', 0, NULL, '2021-10-13 15:49:03');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_REDIS_sentinel', '{\n       \"master\": \"mymaster\",\n       \"nodes\":\"192.168.1.241:6379\",\n       \"password\":\"123456\",\n       \"database\":0\n}', '影子redis哨兵模式配置模版', 0, NULL, '2021-10-14 10:02:58');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_REDIS_single', '{\n       \"nodes\":\"192.168.1.241:6379\",\n       \"password\":\"123456\",\n       \"database\":0\n}', '影子redis单机模式配置模版', 0, NULL, '2021-10-13 15:49:16');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_SERVER', '{\n    \"dataSourceBusiness\":{\n        \"master\":\"192.168.2.240\",\n        \"nodes\":\"192.168.2.241:6379,192.168.2.241:6380\"\n\n    },\n    \"dataSourceBusinessPerformanceTest\":{\n      \"master\":\"192.168.2.240\",\n        \"nodes\":\"192.168.1.241:6379,192.168.1.241:6380\",\n        \"password\":\"123456\",\n\"database\":0\n    }\n}', '影子server配置模板', 0, '2020-11-30 16:48:35', '2020-12-02 14:03:21');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHELL_SCRIPT_DOWNLOAD_SAMPLE', '[\n{\n    \"name\":\"影子库结构脚本\",\n        \"url\":\"/opt/tro/script/shell/sample/ddl.sh\"\n    },\n    {\n    \"name\":\"数据库清理脚本\",\n        \"url\":\"/opt/tro/script/shell/sample/clean.sh\"\n    },\n    {\n    \"name\":\"基础数据准备脚本\",\n        \"url\":\"/opt/tro/script/shell/sample/ready.sh\"\n    },\n    {\n    \"name\":\"铺底数据脚本\",\n        \"url\":\"/opt/tro/script/shell/sample/basic.sh\"\n    },\n    {\n    \"name\":\"缓存预热脚本\",\n        \"url\":\"/opt/tro/script/shell/sample/cache.sh\"\n    }\n\n]', 'shell脚本样例下载配置', 1, '2020-12-23 20:36:05', '2020-12-23 20:36:05');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SQL_CHECK', '0', '全应用SQL检查开关 1开启 0关闭', 1, '2019-03-28 22:11:18', '2019-11-04 10:49:00');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('WECHAT_HOOK_URL', 'https://work.weixin.qq.com/help?person_id=1&doc_id=1337', '微信机器人地址', 0, '2021-04-16 15:55:33', '2021-04-16 15:57:19');
INSERT INTO `trodb`.`t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('WHITE_LIST_SWITCH', '1', '白名单开关：0-关闭 1-开启', 0, NULL, '2021-08-23 16:59:30');

-- 流川
-- tenant_id
ALTER TABLE e_patrol_activity_assert ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_board ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_board_scene ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_exception ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_exception_config ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_exception_notice_config ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_exception_status_change_log ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_scene ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_scene_chain ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_scene_check ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE job_execution_log ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE job_status_trace_log ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE pradar_app_agent ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE pradar_app_bamp ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE pradar_app_group ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE pradar_app_info ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE pradar_app_point ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE pradar_app_warn ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE pradar_biz_key_config ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE pradar_user_login ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';

ALTER TABLE t_abstract_data ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_ac_account ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_ac_account_balance ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_ac_account_book ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_activity_node_service_state ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_agent_config ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT NULL COMMENT '租户 id, 默认 1';
ALTER TABLE t_agent_plugin ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_agent_plugin_lib_support ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_agent_version ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_alarm_list ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_app_agent_config_report ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_app_business_table_info ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_app_middleware_info ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_app_remote_call ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE t_application_api_manage ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';

-- env_code
ALTER TABLE e_patrol_activity_assert ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE e_patrol_board ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE e_patrol_board_scene ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE e_patrol_exception ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE e_patrol_exception_config ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE e_patrol_exception_notice_config ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE e_patrol_exception_status_change_log ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE e_patrol_scene ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE e_patrol_scene_chain ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE e_patrol_scene_check ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE job_execution_log ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE job_status_trace_log ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pradar_app_agent ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pradar_app_bamp ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pradar_app_group ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pradar_app_info ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pradar_app_point ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pradar_app_warn ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pradar_biz_key_config ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pradar_user_login ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_abstract_data ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_ac_account ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_ac_account_balance ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_ac_account_book ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_activity_node_service_state ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_agent_config ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_agent_plugin ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_agent_plugin_lib_support ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_agent_version ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_alarm_list ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_app_agent_config_report ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_app_business_table_info ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_app_middleware_info ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_app_remote_call ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE t_application_api_manage ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;

-- index
alter table e_patrol_activity_assert ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_board ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_board_scene ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_exception ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_exception_config ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_exception_notice_config ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_exception_status_change_log ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_scene ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_scene_chain ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_scene_check ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table job_execution_log ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table job_status_trace_log ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pradar_app_agent ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pradar_app_bamp ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pradar_app_group ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pradar_app_info ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pradar_app_point ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pradar_app_warn ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pradar_biz_key_config ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pradar_user_login ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

alter table t_abstract_data ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_ac_account ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_ac_account_balance ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_ac_account_book ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_activity_node_service_state ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_agent_config ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_agent_plugin ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_agent_plugin_lib_support ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

ALTER TABLE t_agent_version
-- DROP KEY `version`,
ADD UNIQUE KEY `idx_version_tenant_id_env_code`( `version`,`tenant_id`,`env_code` );

alter table t_alarm_list ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_app_agent_config_report ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_app_business_table_info ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_app_middleware_info ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_app_remote_call ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_application_api_manage ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 流川

-- 剑英
ALTER TABLE `t_application_ds_manage`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code',
ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;

ALTER TABLE `t_application_focus`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;

ALTER TABLE `t_application_middleware`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_application_mnt`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_application_node_probe`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_application_plugins_config`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_black_list`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_business_link_manage_table`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_data_build`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_datasource_tag_ref`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_dictionary_data`
    ADD COLUMN `env_code` varchar(20) NOT NULL DEFAULT 'system'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NOT NULL DEFAULT -1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_dictionary_type`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_exception_info`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_fast_debug_config_info`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_fast_debug_exception`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_fast_debug_machine_performance`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_fast_debug_result`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_fast_debug_stack_info`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_file_manage`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_leakcheck_config`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_leakcheck_config_detail`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_leakverify_detail`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_leakverify_result`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_link_detection`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_link_guard`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_link_manage_table`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_link_mnt`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_link_service_mnt`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_link_topology_info`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_login_record`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_middleware_info`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_middleware_jar`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_middleware_link_relate`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_middleware_summary`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_operation_log`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_ops_script_batch_no`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_ops_script_execute_result`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_ops_script_file`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_ops_script_manage`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_performance_base_data`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_performance_criteria_config`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_performance_thread_data`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_performance_thread_stack_data`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_pessure_test_task_activity_config`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;

ALTER TABLE `t_application_ds_manage`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_application_mnt`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_application_focus`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_application_middleware`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_application_node_probe`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_application_plugins_config`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_black_list`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_business_link_manage_table`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_data_build`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_datasource_tag_ref`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_dictionary_data`
    DROP PRIMARY KEY,
    ADD PRIMARY KEY(`ID`,`tenant_id`,`env_code`) USING BTREE;
ALTER TABLE `t_dictionary_type`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_exception_info`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_fast_debug_config_info`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_fast_debug_exception`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_fast_debug_machine_performance`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_fast_debug_result`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_fast_debug_stack_info`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_file_manage`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_leakcheck_config`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_leakcheck_config_detail`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_leakverify_detail`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_leakverify_result`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_detection`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_guard`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_manage_table`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_mnt`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_service_mnt`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_topology_info`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_login_record`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_middleware_info` ADD UNIQUE INDEX `idx_name_version_tenant_env` ( `MIDDLEWARE_NAME`, `MIDDLEWARE_VERSION`, `tenant_id`, `env_code` );
ALTER TABLE `t_middleware_jar`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_middleware_link_relate`
    ADD UNIQUE INDEX `idx_middleware_tech_link_tenant_env` ( `MIDDLEWARE_ID`,`TECH_LINK_ID`,`tenant_id`,`env_code` );
ALTER TABLE `t_middleware_summary`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_operation_log`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_ops_script_batch_no`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_ops_script_execute_result`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_ops_script_file`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_ops_script_manage`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_performance_base_data`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_performance_criteria_config`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_performance_thread_data`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_performance_thread_stack_data`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_pessure_test_task_activity_config`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

ALTER TABLE `t_application_ds_manage`
    MODIFY COLUMN `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id ,废弃';
ALTER TABLE `t_application_mnt`
    MODIFY COLUMN `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id ,废弃';
-- ALTER TABLE `t_application_middleware`
--     MODIFY COLUMN `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id ,废弃';

CREATE VIEW APPLICATION_VIEW AS
SELECT APPLICATION_ID,APPLICATION_NAME,tenant_id AS TENANT_ID,env_code as ENV_CODE FROM t_application_mnt;

-- 系统信息的权限问题
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (510, NULL, 0, 'systemInfo', '系统信息', NULL, '', 9000, '[]', NULL, NULL, NULL, '2021-01-14 11:19:50',
        '2021-01-14 11:19:50', 0);
-- 剑英

-- 无涯
-- t_pessure_test_task_process_config  压测任务业务流程配置  已废弃
ALTER TABLE t_pessure_test_task_process_config comment '表已废弃，压测任务业务流程配置 ';

-- t_prada_http_data prada获取http接口表 已废弃
ALTER TABLE t_prada_http_data comment '表已废弃，prada获取http接口表';



-- t_pradar_zk_config zk配置信息表 增加zk配置
alter table t_pradar_zk_config
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT -1 COMMENT '租户id',
	  ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'system'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_pradar_zk_config
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有索引 idx_zk_path


-- t_pressure_machine_log 压测引擎机器日志 先不加
alter table t_pressure_machine
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_pressure_machine
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_pressure_machine_statistics 压测引擎机器统计 先不加
alter table t_pressure_machine_statistics
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_pressure_machine_statistics
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_pressure_test_engine_config 压测引擎配置 已废弃
ALTER TABLE t_pressure_test_engine_config comment '表已废弃，压测引擎配置';

-- t_pressure_time_record 先不加
alter table t_pressure_time_record
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_pressure_time_record
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_probe 探针包表
alter table t_probe
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_probe
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_quick_access 快速接入
alter table t_quick_access
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_quick_access
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_report_application_summary
ALTER TABLE t_report_application_summary comment '报告应用统计数据';
alter table t_report_application_summary
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_report_application_summary
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有唯一索引 ：unique_idx_report_appliacation 报告id 应用名

-- t_report_bottleneck_interface 瓶颈接口
ALTER TABLE t_report_bottleneck_interface comment '瓶颈接口';
alter table t_report_bottleneck_interface
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_report_bottleneck_interface
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有报告索引 idx_report_id

-- t_report_machine 报告机器数据
ALTER TABLE t_report_machine comment '报告机器数据';
alter table t_report_machine
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_report_machine
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 唯一索引 unique_report_application_machine 报告id,应用名，机器ip

-- t_report_summary 报告数据汇总
ALTER TABLE t_report_summary comment '报告数据汇总';
alter table t_report_summary
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_report_summary
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 索引 idx_report_id 报告id

-- t_scene 场景表
alter table t_scene
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_scene
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有两个索引：T_LINK_MNT_INDEX1 场景名 T_LINK_MNT_INDEX3 创建时间


-- t_scene_link_relate 链路场景关联表
alter table t_scene_link_relate
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_scene_link_relate
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有一个索引：T_LINK_MNT_INDEX2 链路入口

-- t_scene_manage
alter table t_scene_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_scene_manage
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_scene_scheduler_task
alter table t_scene_scheduler_task
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_scene_scheduler_task
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_scene_tag_ref  标签
ALTER TABLE t_scene_tag_ref comment '场景标签关联';
alter table t_scene_tag_ref
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_scene_tag_ref
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有唯一索引 index_sceneId_tagId


-- t_script_debug
alter table t_script_debug
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_debug
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有索引 idx_si 快照id

-- t_script_execute_result  脚本执行结果
ALTER TABLE t_script_execute_result comment '脚本执行结果';
alter table t_script_execute_result
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_execute_result
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 索引 idx_script_deploy_id 快照id


-- t_script_file_ref -脚本文件关联表
ALTER TABLE t_script_file_ref comment '脚本文件关联表';
alter table t_script_file_ref
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_file_ref
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_script_manage  脚本表
ALTER TABLE t_script_manage comment '脚本表';
alter table t_script_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_manage
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 唯一索引 name



-- t_script_manage_deploy  脚本文件关联表
ALTER TABLE t_script_manage_deploy comment '脚本文件关联表';
alter table t_script_manage_deploy
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_manage_deploy
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 唯一索引 name_version name和脚本版本


-- t_script_tag_ref  脚本标签关联表
ALTER TABLE t_script_tag_ref comment '脚本标签关联表';
alter table t_script_tag_ref
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_tag_ref
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_shadow_job_config  影子job任务配置
alter table t_shadow_job_config
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_shadow_job_config
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有索引 idx_app_id




-- t_shadow_mq_consumer  影子job任务配置
alter table t_shadow_mq_consumer
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_shadow_mq_consumer
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_shadow_table_datasource  影子表数据源配置
alter table t_shadow_table_datasource
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_shadow_table_datasource
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有SHADOW_DATASOURCE_INDEX2 `APPLICATION_ID`, `DATABASE_IPPORT`, `DATABASE_NAME`



-- t_tag_manage
ALTER TABLE t_tag_manage comment '标签库表';
alter table t_tag_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tag_manage
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );



-- t_tc_sequence
ALTER TABLE t_tc_sequence comment '特斯拉表，用于性能分析';
alter table t_tc_sequence
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tc_sequence
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_trace_manage
ALTER TABLE t_trace_manage comment '方法追踪表';
alter table t_trace_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_trace_manage
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_trace_manage_deploy
ALTER TABLE t_trace_manage_deploy comment '方法追踪实例表';
alter table t_trace_manage_deploy
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_trace_manage_deploy
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );



-- t_trace_node_info
ALTER TABLE t_trace_node_info comment '调用栈节点表';
alter table t_trace_node_info
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_trace_node_info
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );



-- t_tro_authority
ALTER TABLE t_tro_authority comment '菜单权限表';
alter table t_tro_authority
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_authority
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_tro_dbresource
ALTER TABLE t_tro_dbresource comment '数据源';
alter table t_tro_dbresource
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_dbresource
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_tro_dept
ALTER TABLE t_tro_dept comment '部门表';
alter table t_tro_dept
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id';
alter table t_tro_dept
    ADD INDEX `idx_tenant` ( `tenant_id` );
--  已有索引 idx_value value



-- t_tro_role
ALTER TABLE t_tro_role comment '角色表';
alter table t_tro_role
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_role
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有索引 idx_application_id 应用id


-- t_tro_role_user_relation
ALTER TABLE t_tro_role_user_relation comment '用户角色关联表';
alter table t_tro_role_user_relation
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_role_user_relation
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有索引 user_id role_id

-- t_tro_trace_entry
alter table t_tro_trace_entry
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_trace_entry
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_tro_user
alter table t_tro_user
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id';
alter table t_tro_user
    ADD INDEX `idx_tenant` ( `tenant_id`);
-- idx_name 唯一索引

-- t_tro_user_dept_relation
alter table t_tro_user_dept_relation
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id';
alter table t_tro_user_dept_relation
    ADD INDEX `idx_tenant` ( `tenant_id`);
-- 已有索引 idx_user_id idx_dept_id


-- t_upload_interface_data
alter table t_upload_interface_data
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_upload_interface_data
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_white_list
alter table t_white_list
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_white_list
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_whitelist_effective_app
alter table t_whitelist_effective_app
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
alter table t_whitelist_effective_app
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- e_patrol_activity_assert 断言配置表
update e_patrol_activity_assert set env_code='test',tenant_id=1;

-- e_patrol_board 看板表
alter table e_patrol_board modify column `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id(已废弃)';
update e_patrol_board set tenant_id=customer_id,env_code='test';

-- e_patrol_board_scene 看板场景关系表
update e_patrol_board_scene set env_code='test',tenant_id=1;

-- e_patrol_exception 瓶颈异常记录表
update e_patrol_exception set env_code='test',tenant_id=1;

-- e_patrol_exception_config 瓶颈配置表
alter table e_patrol_exception_config modify column `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id(已废弃)';
update e_patrol_exception_config set tenant_id=customer_id;
update e_patrol_exception_config set env_code='test' where tenant_id is not null;
ALTER TABLE `e_patrol_exception_config` ADD UNIQUE INDEX `idx_config` ( `env_code`, `tenant_id`, `type_value`,`level_value` ) USING BTREE;
-- 注意顺序，先添加唯一索引，再insert新的默认配置
INSERT INTO `trodb`.`e_patrol_exception_config`(`order_number`, `type_value`, `level_value`, `threshold_value`, `contrast_factor`, `remarks`) VALUES (1, 4, 1, 30, 1, '一般慢SQL');
INSERT INTO `trodb`.`e_patrol_exception_config`(`order_number`, `type_value`, `level_value`, `threshold_value`, `contrast_factor`, `remarks`) VALUES (0, 4, 2, 60, 1, '严重慢SQL');

-- e_patrol_exception_notice_config 瓶颈通知配置表
update e_patrol_exception_notice_config set env_code='test',tenant_id=1;

-- e_patrol_scene 巡检场景表
update e_patrol_scene set env_code='test',tenant_id=customer_id;

-- e_patrol_scene_chain 链路配置表
update e_patrol_scene_chain set env_code='test',tenant_id=1;

-- e_patrol_scene_check 巡检任务启动异常表
update e_patrol_scene_check set env_code='test',tenant_id=1;


alter table t_application_mnt
    ADD INDEX `idx_application_id` ( `application_id` );

-- 额外补充 租户期间增加的表
alter table t_mq_config_template
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
--     DROP KEY `eng_name`,
    ADD UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE;

alter table t_application_ds_cache_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;


alter table t_application_ds_db_table
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;

alter table t_application_ds_db_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;

alter table t_http_client_config_template
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE;

alter table t_rpc_config_template
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE;

alter table t_connectpool_config_template
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE;

alter table t_cache_config_template
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE;

alter table t_app_remote_call_template_mapping
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_interface_type_env_tenant` (`interfaceType`,`tenant_id`,`env_code`) USING BTREE;



