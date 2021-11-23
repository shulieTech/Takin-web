-- DDL
CREATE TABLE IF NOT EXISTS `t_tenant_info`
(
    `id`             bigint(20)     NOT NULL AUTO_INCREMENT,
    `key`            varchar(512)  NOT NULL COMMENT '租户key 唯一，同时也是userappkey',
    `name`           varchar(512)   NOT NULL COMMENT '租户名称',
    `nick`           varchar(512)   NOT NULL COMMENT '租户中文名称',
    `code`           varchar(512)   NOT NULL COMMENT '租户代码',
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
    `tenant_id`      bigint(20)     NOT NULL COMMENT '租户id',
    `env_code`       varchar(512)   NOT NULL COMMENT '环境代码，测试环境：test,生产环境：prod',
    `env_name`       varchar(1024)  NOT NULL COMMENT '环境名称',
    `desc`           varchar(1024)  DEFAULT NULL COMMENT '描述',
    `is_default`     tinyint(1)     NOT NULL DEFAULT '0' COMMENT '是否默认',
    `is_deleted`     tinyint(1)     NOT NULL DEFAULT '0' COMMENT '0: 正常 1： 删除',
    `gmt_create`     datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`     datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_tenant_code` (`tenant_id`,`env_code`) USING BTREE
    ) ENGINE = InnoDB;

-- 配置表
CREATE TABLE IF NOT EXISTS `t_tenant_config`
(
    `id`             bigint(20)     NOT NULL AUTO_INCREMENT,
    `tenant_id`    	 bigint(20)     NOT NULL COMMENT '租户id',
    `env_code`       varchar(512)   NOT NULL COMMENT '环境代码，测试环境：test,生产环境：prod',
    `desc`           varchar(1024)  DEFAULT ""   COMMENT '配置描述',
    `key`           varchar(128)   NOT NULL COMMENT '配置名',
    `value`      		 LONGTEXT       NOT NULL COMMENT '配置值',
    `status`      	 tinyint(4)     NOT NULL DEFAULT '1' COMMENT '状态：0：停用；1：启用；',
    `is_deleted`     tinyint(1)     NOT NULL DEFAULT '0' COMMENT '0: 正常 1： 删除',
    `gmt_create`     datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`     datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_key_tenant_env` (`tenant_id`,`env_code`,`key`) USING BTREE
    ) ENGINE = InnoDB;


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

-- 流川
ALTER TABLE t_activity_node_service_state
ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1',
ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识';

ALTER TABLE t_agent_config
ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1',
ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识';

ALTER TABLE t_app_agent_config_report
ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1',
ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识';

ALTER TABLE t_app_business_table_info
ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1',
ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识';

ALTER TABLE t_app_middleware_info
ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1',
ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识';

ALTER TABLE t_app_remote_call
ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1',
ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识';

ALTER TABLE t_application_api_manage
ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1',
ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识';
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

ALTER TABLE `t_file_manage`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`,
    MODIFY COLUMN `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id ,废弃';

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

ALTER TABLE `t_middleware_link_relate`
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

ALTER TABLE `t_application_ds_manage` MODIFY COLUMN `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id ,废弃';
ALTER TABLE `t_application_mnt` MODIFY COLUMN `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id ,废弃';

CREATE VIEW APPLICATION_VIEW AS
SELECT APPLICATION_ID,APPLICATION_NAME,tenant_id AS TENANT_ID,env_code as ENV_CODE,info.key AS TENANTAPPKEY
FROM t_application_mnt app
INNER JOIN t_tenant_info info ON info.id=app.tenant_id;

-- 剑英

-- 无涯
ALTER TABLE t_pessure_test_task_process_config comment '表已废弃，压测任务业务流程配置 ';
ALTER TABLE t_prada_http_data comment '表已废弃，prada获取http接口表';
ALTER TABLE t_pradar_zk_config
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT -1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'system'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_pressure_machine
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_pressure_machine_statistics
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_pressure_test_engine_config comment '表已废弃，压测引擎配置';
ALTER TABLE t_pressure_time_record
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_probe
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_quick_access
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_report_application_summary comment '报告应用统计数据';
ALTER TABLE t_report_application_summary
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_report_bottleneck_interface comment '瓶颈接口';
ALTER TABLE t_report_bottleneck_interface
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_report_machine comment '报告机器数据';
ALTER TABLE t_report_machine
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_report_summary comment '报告数据汇总';
ALTER TABLE t_report_summary
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_scene
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_scene_link_relate
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_scene_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_scene_scheduler_task
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_scene_tag_ref comment '场景标签关联';
ALTER TABLE t_scene_tag_ref
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_script_debug
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE `t_script_debug`
    MODIFY COLUMN `customer_id` bigint(20) UNSIGNED NULL COMMENT '租户id' AFTER `cloud_report_id`;
ALTER TABLE t_script_execute_result comment '脚本执行结果';
ALTER TABLE t_script_execute_result
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_script_file_ref comment '脚本文件关联表';
ALTER TABLE t_script_file_ref
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_script_manage comment '脚本表';
ALTER TABLE t_script_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_script_manage_deploy comment '脚本文件关联表';
ALTER TABLE t_script_manage_deploy
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_script_tag_ref comment '脚本标签关联表';
ALTER TABLE t_script_tag_ref
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_shadow_job_config
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_shadow_mq_consumer
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_shadow_table_datasource
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_tag_manage comment '标签库表';
ALTER TABLE t_tag_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_trace_manage comment '方法追踪表';
ALTER TABLE t_trace_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_trace_manage_deploy comment '方法追踪实例表';
ALTER TABLE t_trace_manage_deploy
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_trace_node_info comment '调用栈节点表';
ALTER TABLE t_trace_node_info
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_tro_authority comment '菜单权限表';
ALTER TABLE t_tro_authority
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_tro_dbresource comment '数据源';
ALTER TABLE t_tro_dbresource
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_tro_dept comment '部门表';
ALTER TABLE t_tro_dept
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id';
ALTER TABLE t_tro_resource comment '菜单资源库表';
ALTER TABLE t_tro_role comment '角色表';
ALTER TABLE t_tro_role
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_tro_role_user_relation comment '用户角色关联表';
ALTER TABLE t_tro_role_user_relation
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_tro_trace_entry
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_tro_user
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id';
ALTER TABLE t_tro_user_dept_relation
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id';
ALTER TABLE t_upload_interface_data
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_white_list
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_whitelist_effective_app
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE t_mq_config_template
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE;

ALTER TABLE t_application_ds_cache_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;

ALTER TABLE t_application_ds_db_table
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;

ALTER TABLE t_application_ds_db_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`;

ALTER TABLE t_http_client_config_template
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE;

ALTER TABLE t_rpc_config_template
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE;

ALTER TABLE t_connectpool_config_template
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE;

ALTER TABLE t_cache_config_template
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE;

ALTER TABLE t_app_remote_call_template_mapping
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'test'  COMMENT '环境变量' AFTER `tenant_id`,
    ADD UNIQUE KEY `idx_interface_type_env_tenant` (`interfaceType`,`tenant_id`,`env_code`) USING BTREE;


ALTER TABLE t_application_ds_db_manage MODIFY COLUMN CUSTOMER_ID bigint(20) NULL DEFAULT '0' COMMENT '租户id 废弃';
ALTER TABLE t_application_ds_db_table MODIFY COLUMN CUSTOMER_ID bigint(20) NULL DEFAULT '0' COMMENT '租户id 废弃';
ALTER TABLE t_app_remote_call_template_mapping MODIFY COLUMN CUSTOMER_ID bigint(20) NULL DEFAULT '0' COMMENT '租户id 废弃';
ALTER TABLE t_cache_config_template MODIFY COLUMN CUSTOMER_ID bigint(20) NULL DEFAULT '0' COMMENT '租户id 废弃';
ALTER TABLE t_connectpool_config_template MODIFY COLUMN CUSTOMER_ID bigint(20) NULL DEFAULT '0' COMMENT '租户id 废弃';
ALTER TABLE t_application_ds_cache_manage MODIFY COLUMN CUSTOMER_ID bigint(20) NULL DEFAULT '0' COMMENT '租户id 废弃';
ALTER TABLE t_http_client_config_template MODIFY COLUMN CUSTOMER_ID bigint(20) NULL DEFAULT '0' COMMENT '租户id 废弃';
ALTER TABLE t_rpc_config_template MODIFY COLUMN CUSTOMER_ID bigint(20) NULL DEFAULT '0' COMMENT '租户id 废弃';
ALTER TABLE t_mq_config_template MODIFY COLUMN CUSTOMER_ID bigint(20) NULL DEFAULT '0' COMMENT '租户id 废弃';


-- 兮曦 --
ALTER TABLE e_patrol_activity_assert ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_activity_assert ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
alter table e_patrol_activity_assert ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

ALTER TABLE e_patrol_board ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_board ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
alter table e_patrol_board add column `user_id` bigint(20) DEFAULT NULL COMMENT '用户id';
alter table e_patrol_board ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_board modify column `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id(已废弃)';

ALTER TABLE e_patrol_board_scene ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_board_scene ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
alter table e_patrol_board_scene ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

ALTER TABLE e_patrol_exception ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_exception ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
alter table e_patrol_exception add column `user_id` bigint(20) DEFAULT NULL COMMENT '用户id';
alter table e_patrol_exception ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

ALTER TABLE e_patrol_exception_config ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_exception_config ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
alter table e_patrol_exception_config ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table e_patrol_exception_config modify column `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id(已废弃)';
ALTER TABLE `e_patrol_exception_config` ADD UNIQUE INDEX `idx_config` ( `tenant_id`, `env_code`, `type_value`,`level_value` ) USING BTREE;

ALTER TABLE e_patrol_exception_notice_config ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_exception_notice_config ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
alter table e_patrol_exception_notice_config add column `user_id` bigint(20) DEFAULT NULL COMMENT '用户id';
alter table e_patrol_exception_notice_config ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

ALTER TABLE e_patrol_exception_status_change_log ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_exception_status_change_log ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
alter table e_patrol_exception_status_change_log ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

ALTER TABLE e_patrol_scene ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_scene ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
alter table e_patrol_scene add column `user_id` bigint(20) DEFAULT NULL COMMENT '用户id';
alter table e_patrol_scene ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE e_patrol_scene ADD COLUMN tenant_app_key varchar(512) NOT NULL COMMENT '租户key 唯一，同时也是userappkey';

ALTER TABLE e_patrol_scene_chain ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_scene_chain ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
alter table e_patrol_scene_chain ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

ALTER TABLE e_patrol_scene_check ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1';
ALTER TABLE e_patrol_scene_check ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
alter table e_patrol_scene_check ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 兮曦 --
-- DML开始
BEGIN;
INSERT IGNORE INTO  `t_tenant_env_ref`(`tenant_id`, `env_code`, `env_name`,`is_default`) VALUES (1, 'test', '测试环境',1);
INSERT IGNORE INTO  `t_tenant_env_ref`(`tenant_id`, `env_code`, `env_name`,`desc`,`is_default`) VALUES (1, 'prod', '生产环境','当前环境为生产环境，请谨慎操作',0);
INSERT IGNORE INTO  `t_tenant_info`(`key`, `name`, `nick`, `code`) VALUES ('5b06060a-17cb-4588-bb71-edd7f65035af', 'default', 'default', 'default');

-- t_base_config
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('ALL_BUTTON', '[\n    \"appManage_2_create\",\n    \"appManage_3_update\",\n    \"appManage_4_delete\",\n    \"appManage_6_enable_disable\",\n    \"bottleneckConfig_3_update\",\n    \"businessActivity_2_create\",\n    \"businessActivity_3_update\",\n    \"businessActivity_4_delete\",\n    \"businessFlow_2_create\",\n    \"businessFlow_3_update\",\n    \"businessFlow_4_delete\",\n    \"configCenter_authorityConfig_2_create\",\n    \"configCenter_authorityConfig_3_update\",\n    \"configCenter_authorityConfig_4_delete\",\n    \"configCenter_bigDataConfig_3_update\",\n    \"configCenter_blacklist_2_create\",\n    \"configCenter_blacklist_3_update\",\n    \"configCenter_blacklist_4_delete\",\n    \"configCenter_blacklist_6_enable_disable\",\n    \"configCenter_dataSourceConfig_2_create\",\n    \"configCenter_dataSourceConfig_3_update\",\n    \"configCenter_dataSourceConfig_4_delete\",\n    \"configCenter_entryRule_2_create\",\n    \"configCenter_entryRule_3_update\",\n    \"configCenter_entryRule_4_delete\",\n    \"configCenter_middlewareManage_3_update\",\n    \"configCenter_pressureMeasureSwitch_6_enable_disable\",\n    \"configCenter_whitelistSwitch_6_enable_disable\",\n    \"debugTool_linkDebug_2_create\",\n    \"debugTool_linkDebug_3_update\",\n    \"debugTool_linkDebug_4_delete\",\n    \"debugTool_linkDebug_5_start_stop\",\n    \"exceptionNoticeManage_2_create\",\n    \"exceptionNoticeManage_3_update\",\n    \"exceptionNoticeManage_4_delete\",\n    \"patrolBoard_2_create\",\n    \"patrolBoard_3_update\",\n    \"patrolBoard_4_delete\",\n    \"patrolManage_2_create\",\n    \"patrolManage_3_update\",\n    \"patrolManage_4_delete\",\n    \"patrolManage_5_start_stop\",\n    \"pressureTestManage_pressureTestScene_2_create\",\n    \"pressureTestManage_pressureTestScene_3_update\",\n    \"pressureTestManage_pressureTestScene_4_delete\",\n    \"pressureTestManage_pressureTestScene_5_start_stop\",\n    \"scriptManage_2_create\",\n    \"scriptManage_3_update\",\n    \"scriptManage_4_delete\",\n    \"scriptManage_7_download\"\n]', '全部按钮名称', 0, '2021-10-20 18:53:10', '2021-10-20 18:53:10');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('ALL_MENU', '[\n    {\n        \"title\":\"系统概览\",\n        \"key\":\"dashboard\",\n        \"icon\":\"dashboard\",\n        \"path\":\"/dashboard\",\n        \"type\":\"Item\"\n    },\n    {\n        \"icon\":\"shop\",\n        \"title\":\"仿真平台\",\n        \"key\":\"appManage\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"title\":\"应用配置\",\n                \"key\":\"appManage\",\n                \"path\":\"/appConfig\",\n                \"type\":\"SubMenu\",\n                \"children\":[\n                    {\n                        \"title\":\"新应用接入\",\n                        \"path\":\"/appAccess\",\n                        \"type\":\"Item\",\n                        \"key\":\"appManage_appAccess\"\n                    },\n                    {\n                        \"title\":\"探针列表\",\n                        \"type\":\"Item\",\n                        \"path\":\"/agentManage\",\n                        \"key\":\"appManage_agentManage\"\n                    },\n                    {\n                        \"title\":\"应用管理\",\n                        \"key\":\"appManage\",\n                        \"path\":\"/appManage\",\n                        \"type\":\"Item\",\n                        \"children\":[\n                            {\n                                \"title\":\"应用详情\",\n                                \"key\":\"appManage\",\n                                \"type\":\"NoMenu\",\n                                \"path\":\"/appManages/details\"\n                            }\n                        ]\n                    },\n                    {\n                        \"title\":\"白名单列表\",\n                        \"key\":\"appWhiteList\",\n                        \"type\":\"Item\",\n                        \"path\":\"/pro/appWhiteList\"\n                    },\n                    {\n                        \"title\":\"异常日志\",\n                        \"path\":\"/errorLog\",\n                        \"type\":\"Item\",\n                        \"key\":\"appManage_errorLog\"\n                    }\n                ]\n            },\n            {\n                \"title\":\"链路管理\",\n                \"key\":\"linkTease\",\n                \"path\":\"/linkManage\",\n                \"type\":\"SubMenu\",\n                \"children\":[\n                    {\n                        \"title\":\"入口规则\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_entryRule\",\n                        \"path\":\"/configCenter/entryRule\"\n                    },\n                    {\n                        \"title\":\"业务活动\",\n                        \"type\":\"Item\",\n                        \"key\":\"businessActivity\",\n                        \"path\":\"/businessActivity\",\n                        \"children\":[\n                            {\n                                \"title\":\"新增业务活动\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"businessActivity\",\n                                \"path\":\"/businessActivity/addEdit\"\n                            }\n                        ]\n                    },\n                    {\n                        \"title\":\"业务流程\",\n                        \"key\":\"businessFlow\",\n                        \"type\":\"Item\",\n                        \"path\":\"/businessFlow\",\n                        \"children\":[\n                            {\n                                \"title\":\"新增业务流程\",\n                                \"key\":\"businessFlow\",\n                                \"type\":\"NoMenu\",\n                                \"path\":\"/businessFlow/addBusinessFlow\"\n                            }\n                        ]\n                    }\n                ]\n            },\n            {\n                \"title\":\"脚本管理\",\n                \"key\":\"scriptManages\",\n                \"type\":\"SubMenu\",\n                \"path\":\"/scriptManages\",\n                \"children\":[\n                    {\n                        \"title\":\"测试脚本\",\n                        \"key\":\"scriptManage\",\n                        \"type\":\"Item\",\n                        \"path\":\"/scriptManage\",\n                        \"children\":[\n                            {\n                                \"title\":\"脚本配置\",\n                                \"key\":\"scriptManage\",\n                                \"type\":\"NoMenu\",\n                                \"path\":\"/scriptManage/scriptConfig\"\n                            },\n                            {\n                                \"key\":\"scriptManage\",\n                                \"title\":\"脚本调试详情\",\n                                \"type\":\"NoMenu\",\n                                \"path\":\"/scriptManage/scriptDebugDetail\"\n                            }\n                        ]\n                    },\n                    {\n                        \"title\":\"运维脚本\",\n                        \"type\":\"Item\",\n                        \"key\":\"scriptOperation\",\n                        \"path\":\"/scriptOperation\"\n                    }\n                ]\n            },\n            {\n                \"title\":\"调试工具\",\n                \"key\":\"debugTool\",\n                \"path\":\"/debugTool\",\n                \"type\":\"SubMenu\",\n                \"children\":[\n                    {\n                        \"title\":\"链路调试\",\n                        \"type\":\"Item\",\n                        \"key\":\"debugTool_linkDebug\",\n                        \"path\":\"/pro/debugTool/linkDebug\",\n                        \"children\":[\n                            {\n                                \"title\":\"链路调试详情\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"debugTool_linkDebug\",\n                                \"path\":\"/pro/debugTool/linkDebug/detail\"\n                            }\n                        ]\n                    }\n                ]\n            },\n            {\n                \"title\":\"数据源管理\",\n                \"type\":\"SubMenu\",\n                \"path\":\"/dataSourceManage\",\n                \"children\":[\n                    {\n                        \"title\":\"数据源配置\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_dataSourceConfig\",\n                        \"path\":\"/configCenter/dataSourceConfig\"\n                    }\n                ]\n            }\n        ]\n    },\n    {\n        \"title\":\"压测平台\",\n        \"icon\":\"hourglass\",\n        \"path\":\"/hourglass\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"title\":\"压测管理\",\n                \"type\":\"SubMenu\",\n                \"path\":\"/pressureTestManage\",\n                \"children\":[\n                    {\n                        \"title\":\"压测场景\",\n                        \"type\":\"Item\",\n                        \"key\":\"pressureTestManage_pressureTestScene\",\n                        \"path\":\"/pressureTestManage/pressureTestScene\",\n                        \"children\":[\n                            {\n                                \"title\":\"压测场景配置\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"pressureTestManage_pressureTestScene\",\n                                \"path\":\"/pressureTestManage/pressureTestScene/pressureTestSceneConfig\"\n                            }\n                        ]\n                    },\n                    {\n                        \"title\":\"压测报告\",\n                        \"type\":\"Item\",\n                        \"key\":\"pressureTestManage_pressureTestReport\",\n                        \"path\":\"/pressureTestManage/pressureTestReport\",\n                        \"children\":[\n                            {\n                                \"title\":\"压测实况\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"pressureTestManage_pressureTestReport\",\n                                \"path\":\"/pressureTestManage/pressureTestReport/pressureTestLive\"\n                            },\n                            {\n                                \"title\":\"压测报告详请\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"pressureTestManage_pressureTestReport\",\n                                \"path\":\"/pressureTestManage/pressureTestReport/details\"\n                            }\n                        ]\n                    }\n                ]\n            }\n        ]\n    },\n    {\n        \"title\":\"巡检平台\",\n        \"key\":\"patrolScreen\",\n        \"path\":\"/accountBook\",\n        \"icon\":\"account-book\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"title\":\"巡检大盘\",\n                \"type\":\"Item\",\n                \"key\":\"patrolScreen\",\n                \"icon\":\"account-book\",\n                \"path\":\"/pro/e2eBigScreen\"\n            },\n            {\n                \"title\":\"巡检管理\",\n                \"path\":\"/pro/missionManage\",\n                \"type\":\"Item\",\n                \"icon\":\"account-book\",\n                \"key\":\"patrolBoard\",\n                \"children\":[\n                    {\n                        \"key\":\"patrolManage\",\n                        \"title\":\"巡检场景详情\",\n                        \"type\":\"NoMenu\",\n                        \"path\":\"/pro/missionManage/sceneDetails\"\n                    }\n                ]\n            },\n            {\n                \"title\":\"瓶颈列表\",\n                \"type\":\"Item\",\n                \"key\":\"bottleneckTable\",\n                \"icon\":\"unordered-list\",\n                \"path\":\"/pro/bottleneckTable\",\n                \"children\":[\n                    {\n                        \"title\":\"巡检场景详情\",\n                        \"key\":\"patrolManage\",\n                        \"type\":\"NoMenu\",\n                        \"path\":\"/pro/bottleneckTable/bottleneckDetails\"\n                    }\n                ]\n            },\n            {\n                \"title\":\"瓶颈通知\",\n                \"icon\":\"menu-unfold\",\n                \"type\":\"Item\",\n                \"key\":\"exceptionNoticeManage\",\n                \"path\":\"/pro/faultNotification\"\n            },\n            {\n                \"title\":\"配置中心\",\n                \"icon\":\"setting\",\n                \"type\":\"Item\",\n                \"path\":\"/pro/setFocus\",\n                \"key\":\"bottleneckConfig\"\n            }\n        ]\n    },\n    {\n        \"icon\":\"form\",\n        \"key\":\"tracks\",\n        \"path\":\"/tracks\",\n        \"title\":\"性能监控\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"key\":\"traceQuery\",\n                \"title\":\"链路查询\",\n                \"path\":\"/pro/track\",\n                \"type\":\"Item\"\n            }\n        ]\n    },\n    {\n        \"icon\":\"form\",\n        \"key\":\"safetyCenter\",\n        \"path\":\"/safetyCenter\",\n        \"title\":\"安全中心\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"key\":\"traceLogData\",\n                \"title\":\"trace数据审计\",\n                \"path\":\"/pro/traceLogData\",\n                \"type\":\"Item\"\n            }\n        ]\n    },\n    {\n        \"icon\":\"setting\",\n        \"title\":\"设置中心\",\n        \"path\":\"/setting\",\n        \"key\":\"configCenter\",\n        \"type\":\"SubMenu\",\n        \"children\":[\n            {\n                \"title\":\"系统管理\",\n                \"key\":\"configCenter\",\n                \"path\":\"/configCenter\",\n                \"type\":\"SubMenu\",\n                \"children\":[\n                    {\n                        \"title\":\"系统信息\",\n                        \"key\":\"system_info\",\n                        \"type\":\"Item\",\n                        \"path\":\"/configCenter/systemInfo\"\n                    },\n                    {\n                        \"title\":\"全局配置\",\n                        \"type\":\"Item\",\n                        \"key\":\"center_config\",\n                        \"path\":\"/configCenter/globalConfig\",\n                        \"children\":[\n                            {\n                                \"path\":\"\",\n                                \"title\":\"压测开关\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"configCenter_pressureMeasureSwitch\"\n                            },\n                            {\n                                \"path\":\"\",\n                                \"title\":\"白名单开关\",\n                                \"type\":\"NoMenu\",\n                                \"key\":\"configCenter_whitelistSwitch\"\n                            }\n                        ]\n                    },\n                    {\n                        \"title\":\"中间件库管理\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_middlewareManage\",\n                        \"path\":\"/configCenter/middlewareManage\"\n                    },\n                    {\n                        \"title\":\"开关配置\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_bigDataConfig\",\n                        \"path\":\"/configCenter/bigDataConfig\"\n                    },\n                    {\n                        \"path\":\"/admin\",\n                        \"title\":\"探针版本管理\",\n                        \"type\":\"Item\",\n                        \"key\":\"admins_admin\"\n                    },\n                    {\n                        \"type\":\"Item\",\n                        \"title\":\"仿真系统配置\",\n                        \"path\":\"/simulationConfig\",\n                        \"key\":\"admins_simulationConfig\"\n                    },\n                    {\n                        \"title\":\"流量账户\",\n                        \"key\":\"flowAccount\",\n                        \"type\":\"Item\",\n                        \"path\":\"/pro/flowAccount\"\n                    }\n                ]\n            },\n            {\n                \"title\":\"权限管理\",\n                \"type\":\"SubMenu\",\n                \"path\":\"/authorityManage\",\n                \"key\":\"configCenter_authorityConfig\",\n                \"children\":[\n                    {\n                        \"title\":\"权限配置中心\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_authorityConfig\",\n                        \"path\":\"/pro/configCenter/authorityConfig\"\n                    },\n                    {\n                        \"title\":\"操作日志\",\n                        \"type\":\"Item\",\n                        \"key\":\"configCenter_operationLog\",\n                        \"path\":\"/pro/configCenter/operationLog\"\n                    }\n                ]\n            }\n        ]\n    }\n]', '全部的菜单地址', 0, '2021-10-20 18:53:10', '2021-10-29 19:56:44');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('DINGDING_HOOK_URL', 'https://developers.dingtalk.com/document/app/custom-robot-access/title-72m-8ag-pqw', '钉钉机器人地址', 0, '2021-04-16 16:06:14', '2021-04-16 16:06:17');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('E2E_RT', '3000', '响应时间（单位：毫秒）', 0, NULL, NULL);
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('E2E_SUCCESS_RATE', '99.99', '成功率', 0, '2021-04-16 15:30:59', '2021-04-16 15:31:24');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('ES_SERVER', '{\"businessNodes\": \"192.168.1.210:9200,192.168.1.193:9200\",\"performanceTestNodes\": \"192.168.1.210:9200,192.168.1.193:9200\"}', '影子ES配置模板', 0, '2021-04-13 21:21:08', '2021-04-13 21:21:11');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('Hbase_SERVER', '{\"dataSourceBusiness\":{\"quorum\":\"192-168-1-171\",\"port\":\"2181\",\"znode\":\"/hbase\",\"params\":{}},\"dataSourcePerformanceTest\":{\"quorum\":\"192-168-1-137\",\"port\":\"2181\",\"znode\":\"/hbase\",\"params\":{}}}', '影子Hbase配置模板', 0, '2021-04-13 21:23:02', '2021-08-19 15:56:58');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('KAFKA_CLUSTER', '{\"key\": \"PT_业务主题\",\"topic\": \"PT_业务主题\",\"topicTokens\": \"PT_业务主题:影子主题token\",\"group\": \"\",\"systemIdToken\": \"\"}', '影子kafka集群配置模板', 0, '2021-04-25 20:57:12', '2021-04-25 20:57:12');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('NOTIFY_TYPE', 'DINGDING_HOOK_URL', '通知类型', 0, '2021-04-19 10:50:20', '2021-04-19 17:04:18');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('PRADAR_GUARD_TEMPLATE', 'import  com.example.demo.entity.User ;\nUser user = new User();\nuser.setName(\"挡板\");\nreturn user ;', '挡板模版', 1, NULL, '2020-06-09 10:58:15');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('REMOTE_CALL_ABLE_CONFIG', '{}', '远程调用配置类型可用性配置', 0, '2021-06-03 16:06:14', '2021-09-03 14:43:11');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_DB', '<configurations>\n              <!--数据源调停者-->\n              <datasourceMediator id=\"dbMediatorDataSource\">\n                  <property name=\"dataSourceBusiness\" ref=\"dataSourceBusiness\"/><!--业务数据源-->\n                  <property name=\"dataSourcePerformanceTest\" ref=\"dataSourcePerformanceTest\"/><!--压测数据源-->\n              </datasourceMediator>\n          \n              <!--数据源集合-->\n              <datasources>\n                  <datasource id=\"dataSourceBusiness\"><!--业务数据源--> <!--业务数据源只需要URL及用户名即可进行唯一性确认等验证-->\n                      <property name=\"url\" value=\"jdbc:mysql://114.55.42.181:3306/taco_app\"/><!--数据库连接URL-->\n                      <property name=\"username\" value=\"admin2017\"/><!--数据库连接用户名-->\n                  </datasource>\n                  <datasource id=\"dataSourcePerformanceTest\"><!--压测数据源-->\n                      <property name=\"driverClassName\" value=\"com.mysql.cj.jdbc.Driver\"/><!--数据库驱动-->\n                      <property name=\"url\" value=\"jdbc:mysql://114.55.42.181:3306/pt_taco_app\"/><!--数据库连接URL-->\n                      <property name=\"username\" value=\"admin2017\"/><!--数据库连接用户名-->\n                      <property name=\"password\" value=\"admin2017\"/><!--数据库连接密码-->\n                      <property name=\"initialSize\" value=\"5\"/>\n                      <property name=\"minIdle\" value=\"5\"/>\n                      <property name=\"maxActive\" value=\"20\"/>\n                      <property name=\"maxWait\" value=\"60000\"/>\n                      <property name=\"timeBetweenEvictionRunsMillis\" value=\"60000\"/>\n                      <property name=\"minEvictableIdleTimeMillis\" value=\"300000\"/>\n                      <property name=\"validationQuery\" value=\"SELECT 1 FROM DUAL\"/>\n                      <property name=\"testWhileIdle\" value=\"true\"/>\n                      <property name=\"testOnBorrow\" value=\"false\"/>\n                      <property name=\"testOnReturn\" value=\"false\"/>\n                      <property name=\"poolPreparedStatements\" value=\"true\"/>\n                      <property name=\"maxPoolPreparedStatementPerConnectionSize\" value=\"20\"/>\n                      <property name=\"connectionProperties\" value=\"druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500\"/>\n                  </datasource>\n              </datasources>\n          </configurations>', '影子库配置模板', 0, '2020-11-30 16:47:30', '2020-11-30 16:48:11');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_REDIS_cluster', '{\n       \"nodes\":\"192.168.1.241:6379\",\n       \"password\":\"123456\",\n       \"database\":0\n}', '影子redis集群模式配置模版', 0, NULL, '2021-10-13 15:47:50');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_REDIS_masterSlave', '{\n      \"master\":\"192.168.2.240\",\n       \"nodes\":\"192.168.1.241:6379,192.168.1.241:6380\",\n       \"password\":\"123456\",\n       \"database\":0\n}', '影子redis主从模式配置模版', 0, NULL, '2021-10-13 15:48:49');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_REDIS_replicated', '{\n       \"nodes\":\"192.168.1.241:6379\",\n       \"password\":\"123456\",\n       \"database\":0\n}\n', '影子redis云托管模式配置模版', 0, NULL, '2021-10-13 15:49:03');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_REDIS_sentinel', '{\n       \"master\": \"mymaster\",\n       \"nodes\":\"192.168.1.241:6379\",\n       \"password\":\"123456\",\n       \"database\":0\n}', '影子redis哨兵模式配置模版', 0, NULL, '2021-10-14 10:02:58');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_REDIS_single', '{\n       \"nodes\":\"192.168.1.241:6379\",\n       \"password\":\"123456\",\n       \"database\":0\n}', '影子redis单机模式配置模版', 0, NULL, '2021-10-13 15:49:16');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHADOW_SERVER', '{\n    \"dataSourceBusiness\":{\n        \"master\":\"192.168.2.240\",\n        \"nodes\":\"192.168.2.241:6379,192.168.2.241:6380\"\n\n    },\n    \"dataSourceBusinessPerformanceTest\":{\n      \"master\":\"192.168.2.240\",\n        \"nodes\":\"192.168.1.241:6379,192.168.1.241:6380\",\n        \"password\":\"123456\",\n\"database\":0\n    }\n}', '影子server配置模板', 0, '2020-11-30 16:48:35', '2020-12-02 14:03:21');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SHELL_SCRIPT_DOWNLOAD_SAMPLE', '[\n{\n    \"name\":\"影子库结构脚本\",\n        \"url\":\"/opt/tro/script/shell/sample/ddl.sh\"\n    },\n    {\n    \"name\":\"数据库清理脚本\",\n        \"url\":\"/opt/tro/script/shell/sample/clean.sh\"\n    },\n    {\n    \"name\":\"基础数据准备脚本\",\n        \"url\":\"/opt/tro/script/shell/sample/ready.sh\"\n    },\n    {\n    \"name\":\"铺底数据脚本\",\n        \"url\":\"/opt/tro/script/shell/sample/basic.sh\"\n    },\n    {\n    \"name\":\"缓存预热脚本\",\n        \"url\":\"/opt/tro/script/shell/sample/cache.sh\"\n    }\n\n]', 'shell脚本样例下载配置', 1, '2020-12-23 20:36:05', '2020-12-23 20:36:05');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('SQL_CHECK', '0', '全应用SQL检查开关 1开启 0关闭', 1, '2019-03-28 22:11:18', '2019-11-04 10:49:00');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('WECHAT_HOOK_URL', 'https://work.weixin.qq.com/help?person_id=1&doc_id=1337', '微信机器人地址', 0, '2021-04-16 15:55:33', '2021-04-16 15:57:19');
INSERT IGNORE INTO  `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('WHITE_LIST_SWITCH', '1', '白名单开关：0-关闭 1-开启', 0, NULL, '2021-08-23 16:59:30');

-- 系统信息的权限问题
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,`features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (510, NULL, 0, 'systemInfo', '系统信息', NULL, '', 9000, '[]', NULL, NULL, NULL, '2021-01-14 11:19:50',
        '2021-01-14 11:19:50', 0);

COMMIT;

-- 大表操作 删除agent异常上报的无用数据(只有调试的数据才是有用数据) t_fast_debug_stack_info
-- 1. 创建临时表
-- CREATE TABLE IF NOT EXISTS `tmp_stack_info` (
--     `id` bigint(20) NOT NULL AUTO_INCREMENT,
--     `app_name` varchar(255) DEFAULT NULL,
--     `agent_id` varchar(255) DEFAULT NULL,
--     `trace_id` varchar(512) DEFAULT NULL COMMENT 'traceId',
--     `rpc_id` varchar(512) NOT NULL COMMENT 'rpcid',
--     `level` varchar(64) DEFAULT NULL COMMENT '日志级别',
--     `type` tinyint(4) DEFAULT NULL COMMENT '服务端，客户端',
--     `content` longtext COMMENT 'stack信息',
--     `is_stack` tinyint(1) DEFAULT NULL COMMENT '是否调用栈日志',
--     `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
--     `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--     `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
--     `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
--     `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
--     PRIMARY KEY (`id`) USING BTREE,
--     KEY `index_traceId` (`trace_id`) USING BTREE
--     ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
-- 2. 查询有用的数据 复制为insert语句 BEGIN;---COMMIT;
-- SELECT t.*
-- FROM t_fast_debug_stack_info t
-- JOIN t_fast_debug_result t2 ON t2.trace_id=t.trace_id;
--
-- 3.全局替换 t_fast_debug_stack_info -> tmp_stack_info
-- 4.执行后比较 查询SQL和临时表的记录数是否一致
-- 5.删除原表t_fast_debug_machine_performance 临时表 tmp_stack_info 重命名为 t_fast_debug_stack_info
-- DROP TABLE IF EXISTS t_fast_debug_stack_info;
-- RENAME TABLE tmp_stack_info TO t_fast_debug_stack_info;

-- 大表操作 删除agent异常上报的无用数据(只有调试的数据才是有用数据) t_fast_debug_machine_performance
-- 1. 创建临时表
-- CREATE TABLE IF NOT EXISTS `tmp_machine` (
--     `id` bigint(20) NOT NULL AUTO_INCREMENT,
--     `trace_id` varchar(512) DEFAULT NULL COMMENT 'traceId',
--     `rpc_id` varchar(512) DEFAULT NULL COMMENT 'rpcid',
--     `log_type` tinyint(4) DEFAULT NULL COMMENT '服务端、客户端',
--     `index` varchar(128) DEFAULT NULL COMMENT '性能类型，beforeFirst/beforeLast/afterFirst/afterLast/exceptionFirst/exceptionLast',
--     `cpu_usage` decimal(10,4) DEFAULT '0.0000' COMMENT 'cpu利用率',
--     `cpu_load` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT 'cpu load',
--     `memory_usage` decimal(10,4) DEFAULT '0.0000' COMMENT '没存利用率',
--     `memory_total` decimal(20,2) DEFAULT '0.00' COMMENT '堆内存总和',
--     `io_wait` decimal(10,4) DEFAULT '0.0000' COMMENT 'io 等待率',
--     `young_gc_count` bigint(20) DEFAULT NULL,
--     `young_gc_time` bigint(20) DEFAULT NULL,
--     `old_gc_count` bigint(20) DEFAULT NULL,
--     `old_gc_time` bigint(20) DEFAULT NULL,
--     `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
--     `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
--     `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
--     `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
--     `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
--     PRIMARY KEY (`id`) USING BTREE
-- ) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC;
-- 2. 查询有用的数据 复制为insert语句 BEGIN;---COMMIT;
-- SELECT t.*
-- FROM t_fast_debug_machine_performance t
-- JOIN t_fast_debug_result t2 ON t2.trace_id=t.trace_id;
--
-- 3.全局替换 t_fast_debug_machine_performance -> tmp_machine
-- 4.执行后比较 查询SQL和临时表的记录数是否一致
-- 5.删除原表t_fast_debug_machine_performance 临时表 tmp_machine 重命名为 t_fast_debug_machine_performance
-- DROP TABLE IF EXISTS t_fast_debug_machine_performance;
-- RENAME TABLE tmp_machine TO t_fast_debug_machine_performance;


