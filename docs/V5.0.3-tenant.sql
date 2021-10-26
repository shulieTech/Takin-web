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
    ) ENGINE = InnoDB


----- 流川 -----
-- tenant_id
ALTER TABLE e_patrol_activity_assert ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE e_patrol_board ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE e_patrol_board_scene ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE e_patrol_exception ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE e_patrol_exception_config ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE e_patrol_exception_notice_config ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE e_patrol_exception_status_change_log ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE e_patrol_scene ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE e_patrol_scene_chain ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE e_patrol_scene_check ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE job_execution_log ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE job_status_trace_log ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pradar_app_agent ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pradar_app_bamp ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pradar_app_group ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pradar_app_info ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pradar_app_point ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pradar_app_warn ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pradar_biz_key_config ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pradar_user_login ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_t_ac_account ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_t_ac_account_book ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_t_app_middleware_info ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_t_application_mnt ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_t_report ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_t_return_data ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_t_scene_business_activity_ref ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_t_scene_manage ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_t_scene_script_ref ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT  '租户 id, 默认 0';
ALTER TABLE pt_t_scene_sla_ref ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_t_tro_user ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE pt_user ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_abstract_data ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_ac_account ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_ac_account_balance ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_ac_account_book ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_activity_node_service_state ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_agent_config ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_agent_plugin ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_agent_plugin_lib_support ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_agent_version ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_alarm_list ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_app_agent_config_report ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_app_business_table_info ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_app_middleware_info ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_app_remote_call ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';
ALTER TABLE t_application_api_manage ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 0 COMMENT '租户 id, 默认 0';

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
ALTER TABLE pt_t_ac_account ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_t_ac_account_book ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_t_app_middleware_info ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_t_application_mnt ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_t_report ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_t_return_data ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_t_scene_business_activity_ref ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_t_scene_manage ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_t_scene_script_ref ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_t_scene_sla_ref ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_t_tro_user ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
ALTER TABLE pt_user ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识' ;
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
alter table pt_t_ac_account ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_t_ac_account_book ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_t_app_middleware_info ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_t_application_mnt ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_t_report ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_t_return_data ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_t_scene_business_activity_ref ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_t_scene_manage ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_t_scene_script_ref ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_t_scene_sla_ref ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_t_tro_user ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table pt_user ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_abstract_data ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_ac_account ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_ac_account_balance ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_ac_account_book ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_activity_node_service_state ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_agent_config ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_agent_plugin ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_agent_plugin_lib_support ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_agent_version ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_alarm_list ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_app_agent_config_report ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_app_business_table_info ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_app_middleware_info ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_app_remote_call ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
alter table t_application_api_manage ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
----- 流川 -----

----- 剑英 -----
ALTER TABLE `t_application_ds_manage`
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code',
ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_application_focus`
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code',
ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE `t_application_middleware`
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE `t_application_mnt`
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
ALTER TABLE 't_application_node_probe'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_application_plugins_config'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_base_config'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_black_list'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_business_link_manage_table'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_data_build'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_datasource_tag_ref'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_dictionary_data'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_dictionary_type'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_exception_info'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_fast_debug_config_info'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_fast_debug_exception'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_fast_debug_machine_performance'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_fast_debug_result'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_fast_debug_stack_info'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_file_manage'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_leakcheck_config'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_leakcheck_config_detail'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_leakverify_detail'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_leakverify_result'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_link_detection'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_link_guard'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_link_manage_table'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_link_mnt'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_link_service_mnt'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_link_topology_info'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_login_record'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_middleware_info'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_middleware_jar'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_middleware_jar_copy1'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_middleware_link_relate'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_middleware_summary'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_middleware_summary_copy1'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_migration_history'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_operation_log'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_ops_script_batch_no'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_ops_script_execute_result'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_ops_script_file'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_ops_script_manage'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_performance_base_data'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_performance_criteria_config'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_performance_thread_data'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_performance_thread_stack_data'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;
ALTER TABLE 't_pessure_test_task_activity_config'
    ADD COLUMN `env_code` bigint(20) NULL DEFAULT NULL COMMENT '环境code' ,
    ADD COLUMN `tenant_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id' AFTER `env_code`;


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
ALTER TABLE `t_base_config`
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
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
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
ALTER TABLE `t_middleware_info`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_middleware_jar`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_middleware_jar_copy1`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_middleware_link_relate`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_middleware_summary`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_middleware_summary_copy1`
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_migration_history`
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
ALTER TABLE `t_application_middleware`
    MODIFY COLUMN `customer_id` bigint(20) NULL DEFAULT NULL COMMENT '租户id ,废弃';
----- 剑英 -----

----- 无涯 -----
-- t_pessure_test_task_process_config  压测任务业务流程配置  已废弃 -----
ALTER TABLE t_pessure_test_task_process_config comment '表已废弃，压测任务业务流程配置 ';

-- t_prada_http_data prada获取http接口表 已废弃
ALTER TABLE t_prada_http_data comment '表已废弃，prada获取http接口表';



-- t_pradar_zk_config zk配置信息表 增加zk配置
alter table t_pradar_zk_config
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	  ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_pradar_zk_config
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有索引 idx_zk_path


-- t_pressure_machine 压测引擎机器 先不加
ALTER TABLE t_pressure_machine comment '压测引擎机器';
alter table t_pressure_machine
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	  ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_pressure_machine
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_pressure_machine_log 压测引擎机器日志 先不加
alter table t_pressure_machine
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_pressure_machine
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_pressure_machine_statistics 压测引擎机器统计 先不加
alter table t_pressure_machine_statistics
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_pressure_machine_statistics
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_pressure_test_engine_config 压测引擎配置 已废弃
ALTER TABLE t_pressure_test_engine_config comment '表已废弃，压测引擎配置';

---- t_pressure_time_record 先不加
alter table t_pressure_time_record
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_pressure_time_record
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_probe 探针包表
alter table t_probe
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_probe
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_quick_access 快速接入
alter table t_quick_access
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_quick_access
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_report_application_summary
ALTER TABLE t_report_application_summary comment '报告应用统计数据';
alter table t_report_application_summary
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_report_application_summary
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有唯一索引 ：unique_idx_report_appliacation 报告id 应用名

-- t_report_bottleneck_interface 瓶颈接口
ALTER TABLE t_report_bottleneck_interface comment '瓶颈接口';
alter table t_report_bottleneck_interface
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_report_bottleneck_interface
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有报告索引 idx_report_id

-- t_report_machine 报告机器数据
ALTER TABLE t_report_machine comment '报告机器数据';
alter table t_report_machine
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_report_machine
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 唯一索引 unique_report_application_machine 报告id,应用名，机器ip

-- t_report_summary 报告数据汇总
ALTER TABLE t_report_summary comment '报告数据汇总';
alter table t_report_summary
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_report_summary
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 索引 idx_report_id 报告id

-- t_scene 场景表
alter table t_scene
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_scene
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有两个索引：T_LINK_MNT_INDEX1 场景名 T_LINK_MNT_INDEX3 创建时间


-- t_scene_link_relate 链路场景关联表
alter table t_scene_link_relate
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_scene_link_relate
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有一个索引：T_LINK_MNT_INDEX2 链路入口

-- t_scene_manage
alter table t_scene_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_scene_manage
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_scene_scheduler_task
alter table t_scene_scheduler_task
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_scene_scheduler_task
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_scene_tag_ref  --标签
ALTER TABLE t_scene_tag_ref comment '场景标签关联';
alter table t_scene_tag_ref
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_scene_tag_ref
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有唯一索引 index_sceneId_tagId


-- t_script_debug
alter table t_script_debug
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_debug
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有索引 idx_si 快照id

-- t_script_execute_result  --脚本执行结果
ALTER TABLE t_script_execute_result comment '脚本执行结果';
alter table t_script_execute_result
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_execute_result
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 索引 idx_script_deploy_id 快照id


-- t_script_file_ref  --脚本文件关联表
ALTER TABLE t_script_file_ref comment '脚本文件关联表';
alter table t_script_file_ref
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_file_ref
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_script_manage  --脚本表
ALTER TABLE t_script_manage comment '脚本表';
alter table t_script_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_manage
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 唯一索引 name



-- t_script_manage_deploy  --脚本文件关联表
ALTER TABLE t_script_manage_deploy comment '脚本文件关联表';
alter table t_script_manage_deploy
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_manage_deploy
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 唯一索引 name_version name和脚本版本


-- t_script_tag_ref  --脚本标签关联表
ALTER TABLE t_script_tag_ref comment '脚本标签关联表';
alter table t_script_tag_ref
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_script_tag_ref
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_shadow_job_config  --影子job任务配置
alter table t_shadow_job_config
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_shadow_job_config
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有索引 idx_app_id




-- t_shadow_mq_consumer  --影子job任务配置
alter table t_shadow_mq_consumer
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_shadow_mq_consumer
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_shadow_table_datasource  --影子表数据源配置
alter table t_shadow_table_datasource
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_shadow_table_datasource
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有SHADOW_DATASOURCE_INDEX2 `APPLICATION_ID`, `DATABASE_IPPORT`, `DATABASE_NAME`



-- t_tag_manage
ALTER TABLE t_tag_manage comment '标签库表';
alter table t_tag_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tag_manage
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );



-- t_tc_sequence
ALTER TABLE t_tc_sequence comment '特斯拉表，用于性能分析';
alter table t_tc_sequence
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tc_sequence
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_trace_manage
ALTER TABLE t_trace_manage comment '方法追踪表';
alter table t_trace_manage
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_trace_manage
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_trace_manage_deploy
ALTER TABLE t_trace_manage_deploy comment '方法追踪实例表';
alter table t_trace_manage_deploy
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_trace_manage_deploy
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );



-- t_trace_node_info
ALTER TABLE t_trace_node_info comment '调用栈节点表';
alter table t_trace_node_info
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_trace_node_info
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );



-- t_tro_authority
ALTER TABLE t_tro_authority comment '菜单权限表';
alter table t_tro_authority
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_authority
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_tro_dbresource
ALTER TABLE t_tro_dbresource comment '数据源';
alter table t_tro_dbresource
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_dbresource
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_tro_dept
ALTER TABLE t_tro_dept comment '部门表';
alter table t_tro_dept
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id';
alter table t_tro_dept
    ADD INDEX `idx_tenant` ( `tenant_id` );


-- t_tro_resource
ALTER TABLE t_tro_resource comment '菜单资源库表';
alter table t_tro_resource
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_resource
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有索引 idx_value value



-- t_tro_role
ALTER TABLE t_tro_role comment '角色表';
alter table t_tro_role
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_role
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- 已有索引 idx_application_id 应用id


-- t_tro_role_user_relation
ALTER TABLE t_tro_role_user_relation comment '用户角色关联表';
alter table t_tro_role_user_relation
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_role_user_relation
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
--  已有索引 user_id role_id

-- t_tro_trace_entry
alter table t_tro_trace_entry
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_tro_trace_entry
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_tro_user
alter table t_tro_user
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id';
alter table t_tro_user
    ADD INDEX `idx_tenant` ( `tenant_id`);
-- idx_name 唯一索引

-- t_tro_user_dept_relation
alter table t_tro_user_dept_relation
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id';
alter table t_tro_user_dept_relation
    ADD INDEX `idx_tenant` ( `tenant_id`);
-- 已有索引 idx_user_id idx_dept_id


-- t_upload_interface_data
alter table t_upload_interface_data
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_upload_interface_data
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );


-- t_white_list
alter table t_white_list
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_white_list
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

-- t_whitelist_effective_app
alter table t_whitelist_effective_app
    ADD COLUMN `tenant_id` bigint(20)  NOT NULL DEFAULT 0 COMMENT '租户id',
	ADD COLUMN `env_code`  varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`;
alter table t_whitelist_effective_app
    ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );

----- 无涯 -----
-- 用户表
alter table t_tro_user
    add column `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id';
alter table t_tro_user
    add index `tenant_id` ( `tenant_id` );
-- 部门表
alter table t_tro_dept
    add column `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id';
alter table t_tro_dept
    add index `tenant_id` ( `tenant_id` );


alter table t_tro_user_dept_relation
    add column `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id';

alter table t_tro_user_dept_relation
    add index `tenant_id` ( `tenant_id` );

ALTER TABLE `t_dictionary_data`
    ADD COLUMN `tenant_id` bigint(20) NOT NULL COMMENT '租户ID' AFTER `VERSION_NO`,
ADD COLUMN `env_code` varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`,
DROP
PRIMARY KEY,
ADD PRIMARY KEY (`ID`, `tenant_id`, `env_code`) USING BTREE;


ALTER TABLE `t_base_config`
    ADD COLUMN `tenant_id` bigint(20) NOT NULL COMMENT '租户ID' AFTER `UPDATE_TIME`,
ADD COLUMN `env_code` varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`,
DROP
PRIMARY KEY,
ADD PRIMARY KEY (`CONFIG_CODE`, `tenant_id`, `env_code`) USING BTREE;

ALTER TABLE `t_base_config`
    ADD INDEX `unique_idx_env_code_tenant_id`(`env_code`, `tenant_id`) USING BTREE;

ALTER TABLE `t_dictionary_data`
    ADD INDEX `unique_idx_env_code_tenant_id`(`env_code`, `tenant_id`) USING BTREE;