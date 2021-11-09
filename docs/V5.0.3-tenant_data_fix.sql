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

-- 兮曦 --
update e_patrol_activity_assert set env_code='test',tenant_id=1;
update e_patrol_board set tenant_id=customer_id,env_code='test';
update e_patrol_board_scene set env_code='test',tenant_id=1;
update e_patrol_exception set env_code='test',tenant_id=1;
update e_patrol_exception_config set tenant_id=customer_id;
update e_patrol_exception_config set env_code='test' where tenant_id is not null;
INSERT INTO `trodb`.`e_patrol_exception_config`(`order_number`, `type_value`, `level_value`, `threshold_value`, `contrast_factor`, `remarks`) VALUES (1, 4, 1, 30, 1, '一般慢SQL');
INSERT INTO `trodb`.`e_patrol_exception_config`(`order_number`, `type_value`, `level_value`, `threshold_value`, `contrast_factor`, `remarks`) VALUES (0, 4, 2, 60, 1, '严重慢SQL');
update e_patrol_exception_config set env_code='system',tenant_id=-1 where env_code is null and tenant_id is null;
update e_patrol_exception_notice_config set env_code='test',tenant_id=1;
update e_patrol_scene set env_code='test',tenant_id=customer_id;
update e_patrol_scene a,t_tenant_info b set a.tenant_app_key = b.`key` where a.tenant_id=b.id and a.tenant_id is not null;
update e_patrol_scene_chain set env_code='test',tenant_id=1;
update e_patrol_scene_check set env_code='test',tenant_id=1;
-- 兮曦 --

