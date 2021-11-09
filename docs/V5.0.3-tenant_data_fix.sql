-- 额外 租户期间增加的表
update t_mq_config_template set tenant_id=customer_id;
update t_application_ds_cache_manage set tenant_id=customer_id;
update t_application_ds_db_manage set tenant_id=customer_id;
update t_application_ds_db_table set tenant_id=customer_id;

-- caijy
update t_application_ds_manage set tenant_id=customer_id;
update t_application_mnt set tenant_id=customer_id;
update t_application_node_probe set tenant_id=customer_id;
update t_application_plugins_config set tenant_id=customer_id;
update t_black_list set tenant_id=customer_id;
update t_business_link_manage_table set tenant_id=customer_id;
update t_exception_info set tenant_id=customer_id;
update t_fast_debug_config_info set tenant_id=customer_id;
update t_fast_debug_exception set tenant_id=customer_id;
update t_fast_debug_result set tenant_id=customer_id;
update t_file_manage set tenant_id=customer_id;
update t_leakcheck_config set tenant_id=customer_id;
update t_leakcheck_config_detail set tenant_id=customer_id;
update t_leakverify_result set tenant_id=customer_id;
update t_link_guard set tenant_id=customer_id;
update t_link_manage_table set tenant_id=customer_id;
update t_operation_log set tenant_id=customer_id;
update t_ops_script_manage set tenant_id=customer_id;
update t_tro_dbresource set tenant_id=customer_id;


update t_application_focus set tenant_id=(select tenant_id from t_application_mnt where APPLICATION_NAME = app_name);
update t_application_middleware set tenant_id=(select tenant_id from t_application_mnt where APPLICATION_ID = application_id);
update t_data_build t set t.tenant_id=(select tenant_id from t_application_mnt where APPLICATION_ID = t.APPLICATION_ID);
update t_datasource_tag_ref t set t.tenant_id=(select tenant_id from t_tro_dbresource where id = t.datasource_id)
update t_fast_debug_result f set f.tenant_id=(SELECT customer_id from t_fast_debug_config_info WHERE id = f.config_id AND is_deleted=0);
update t_fast_debug_machine_performance f set f.tenant_id=(SELECT t.tenant_id from t_fast_debug_result t  WHERE  t.trace_id = f.trace_id AND t.is_deleted=0);
update t_fast_debug_stack_info f set f.tenant_id=(SELECT t.tenant_id from t_fast_debug_result t WHERE  t.trace_id = f.trace_id AND t.is_deleted=0);
update t_leakverify_detail t set t.tenant_id=(SELECT tenant_id FROM t_leakverify_result WHERE id=t.result_id and is_deleted=0;
update t_link_detection t set t.tenant_id=(select tenant_id from t_application_mnt where APPLICATION_ID = t.APPLICATION_ID);
update t_link_mnt set tenant_id=
update t_link_service_mnt set tenant_id=
update t_link_topology_info set tenant_id=
update t_login_record t set t.tenant_id=(select tenant_id from t_tro_user WHERE name=t.user_name and is_delete=0);
update t_middleware_info set tenant_id=
update t_middleware_jar set tenant_id=
update t_middleware_link_relate set tenant_id=
update t_middleware_summary set tenant_id=
update t_ops_script_batch_no t set t.tenant_id=(select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_delete=0);
update t_ops_script_execute_result t set t.tenant_id=(select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_delete=0);
update t_ops_script_file t set t.tenant_id=(select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_delete=0);
update t_performance_base_data t set t.tenant_id= (select tenant_id from t_application_mnt where APPLICATION_NAME = t.app_name);
update t_performance_criteria_config t set t.tenant_id= (select tenant_id from t_application_mnt where APPLICATION_ID = t.app_id);
update t_performance_thread_data t set t.tenant_id= (select tenant_id from t_application_mnt where APPLICATION_NAME = t.app_name);
-- t_performance_thread_stack_data 不用迁移
update t_pessure_test_task_activity_config set tenant_id=

-- caijy end


---无涯
UPDATE t_pradar_zk_config set TENANT_ID = -1 and env_code = 'system'

-- 查出租户
SELECT * from t_tro_user WHERE id = customer_id
-- 插入租户
INSERT INTO `t_tenant_info`
(`id`, `key`, `name`, `nick`, `code`, `status`)
VALUES (1, 'ed45ef6b-bf94-48fa-b0c0-15e0285365d2', 'default', 'default', 'default', 1),
    (2, 'ed45ef6b-bf94-48fa-b0c0-15e0285365d3', 'test', '测试', 'test', 1),
    (3, '4d2a89a0-204d-4eab-a031-d9d55eaafc3c', 'T3Trip', 'T3出行', 'T3Trip', 1),
    (5, '5b06060a-17cb-4588-bb71-edd7f65035aa', 'lbx-pressure', 'lbx-pressure', 'lbx-pressure', 1),
    (7, '292e98d4-9efe-4e24-a0c1-e906fa568009', 'sto', '申通快递', 'sto', 1),
    (10, '4beee59d-e8f5-497e-a487-dbbb75194dde', 'liandongyun', '联动云', 'liandongyun', 1),
    (24, '89e425fc-feaa-4983-91c2-8a6c877afb03', 'akc', '爱库存', 'akc', 1);

-- 插入环境
INSERT INTO `t_tenant_env_ref`
(`tenant_id`, `env_code`, `env_name`, `desc`)
VALUES
    (1, 'test', '测试环境', ''),
    (1, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作'),
    (2, 'test', '测试环境', ''),
    (2, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作'),
    (3, 'test', '测试环境', ''),
    (3, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作'),
    (5, 'test', '测试环境', ''),
    (5, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作'),
    (7, 'test', '测试环境', ''),
    (7, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作'),
    (10, 'test', '测试环境', ''),
    (10, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作'),
    (24, 'test', '测试环境', ''),
    (24, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作');

-- 更正t_tro_user



---无涯