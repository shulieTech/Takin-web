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
    ) ENGINE = InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

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
    ) ENGINE = InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

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
    ) ENGINE = InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

-- t_base_config
ALTER TABLE t_base_config
ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT -1 COMMENT '租户 id, 默认 -1',
ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'system' COMMENT '环境标识';

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

-- 清理无用数据
DELETE FROM t_app_remote_call
WHERE type = 0 OR id NOT IN (
    SELECT t.id FROM(
        SELECT MIN( id ) AS id FROM t_app_remote_call
        GROUP BY
            APP_NAME,
            interface_name,
            interface_type,
            APPLICATION_ID,
            interface_type,
            mock_return_value,
            type
    ) t
);
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

-- ALTER TABLE `t_exception_info`
--     ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
--     ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;

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

-- 性能分析数据全部删除就行 无需订正
DELETE FROM t_performance_thread_stack_data;
ALTER TABLE `t_performance_thread_stack_data`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;

ALTER TABLE `t_pessure_test_task_activity_config`
    ADD COLUMN `env_code` varchar(20) NULL DEFAULT 'test'  COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT 1 COMMENT '租户id' AFTER `env_code`;

ALTER TABLE `t_application_ds_manage` MODIFY COLUMN `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id ,废弃';
ALTER TABLE `t_application_mnt` MODIFY COLUMN `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id ,废弃';

DROP VIEW IF EXISTS APPLICATION_VIEW;
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
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT -1 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL DEFAULT 'system'  COMMENT '环境变量' AFTER `tenant_id`;
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
-- 系统信息的权限问题
INSERT IGNORE INTO `t_tro_resource` ( `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (NULL, 0, 'system_info', '系统信息', NULL, '[\"api/sys\"]', 9000, '[]', NULL, NULL, NULL, '2021-01-14 11:19:50', '2021-12-01 11:28:01', 0);

COMMIT;

-- 大表操作 删除agent异常上报的无用数据(只有调试的数据才是有用数据) t_fast_debug_stack_info
-- 方案1：
-- DELETE FROM t_fast_debug_stack_info WHERE id NOT IN (
--     SELECT * FROM (
--       SELECT t.id
--       FROM t_fast_debug_stack_info t
--                JOIN t_fast_debug_result t2 ON t2.trace_id=t.trace_id
--   )tmp
-- )
-- 方案2
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
-- 方案1：
-- DELETE FROM t_fast_debug_machine_performance WHERE id NOT IN (
--     SELECT * FROM (
--           SELECT t.id
--           FROM t_fast_debug_machine_performance t
--           JOIN t_fast_debug_result t2 ON t2.trace_id=t.trace_id
--       )tmp
-- )
-- 方案2：
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


