-- 额外 租户期间增加的表
update t_mq_config_template set tenant_id=customer_id;
update t_application_ds_cache_manage set tenant_id=customer_id;
update t_application_ds_db_manage set tenant_id=customer_id;
update t_application_ds_db_table set tenant_id=customer_id;

-- caijy
-- env_code
-- 应用表 t_application_mnt
UPDATE t_application_mnt t1
    LEFT JOIN (
    SELECT
    `name`,
    id,
    CASE
    WHEN LOCATE( 'test', `name` ) > 0 THEN
    'test' ELSE 'prod'
    END env_code
    FROM
    t_tro_user
    ) t2 ON t2.id = t1.USER_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_business_link_manage_table
UPDATE t_business_link_manage_table t1
    LEFT JOIN (
    SELECT
    `name`,
    id,
    CASE
    WHEN LOCATE( 'test', `name` ) > 0 THEN
    'test' ELSE 'prod'
    END env_code
    FROM
    t_tro_user
    ) t2 ON t2.id = t1.USER_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_script_manage
UPDATE t_script_manage t1
    LEFT JOIN (
    SELECT
    `name`,
    id,
    CASE
    WHEN LOCATE( 'test', `name` ) > 0 THEN
    'test' ELSE 'prod'
    END env_code
    FROM
    t_tro_user
    ) t2 ON t2.id = t1.user_id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_leakcheck_config
UPDATE t_leakcheck_config t1
    LEFT JOIN (
    SELECT
    `name`,
    id,
    CASE
    WHEN LOCATE( 'test', `name` ) > 0 THEN
    'test' ELSE 'prod'
    END env_code
    FROM
    t_tro_user
    ) t2 ON t2.id = t1.user_id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_leakcheck_config_detail
UPDATE t_leakcheck_config_detail t1
    LEFT JOIN (
    SELECT
    `name`,
    id,
    CASE
    WHEN LOCATE( 'test', `name` ) > 0 THEN
    'test' ELSE 'prod'
    END env_code
    FROM
    t_tro_user
    ) t2 ON t2.id = t1.user_id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_operation_log
UPDATE t_operation_log t1
    LEFT JOIN (
    SELECT
    `name`,
    id,
    CASE
    WHEN LOCATE( 'test', `name` ) > 0 THEN
    'test' ELSE 'prod'
    END env_code
    FROM
    t_tro_user
    ) t2 ON t2.id = t1.user_id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_ops_script_manage
UPDATE t_ops_script_manage t1
    LEFT JOIN (
    SELECT
    `name`,
    id,
    CASE
    WHEN LOCATE( 'test', `name` ) > 0 THEN
    'test' ELSE 'prod'
    END env_code
    FROM
    t_tro_user
    ) t2 ON t2.id = t1.user_id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_tro_dbresource
UPDATE t_tro_dbresource t1
    LEFT JOIN (
    SELECT
    `name`,
    id,
    CASE
    WHEN LOCATE( 'test', `name` ) > 0 THEN
    'test' ELSE 'prod'
    END env_code
    FROM
    t_tro_user
    ) t2 ON t2.id = t1.user_id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_application_ds_manage
UPDATE t_application_ds_manage t1
    LEFT JOIN t_application_mnt t2 ON t1.APPLICATION_ID = t2.APPLICATION_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_application_node_probe
UPDATE t_application_node_probe t1
    LEFT JOIN t_application_mnt t2 ON t1.application_name = t2.APPLICATION_NAME
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_application_plugins_config
UPDATE t_application_plugins_config t1
    LEFT JOIN t_application_mnt t2 ON t1.application_name = t2.APPLICATION_NAME
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_black_list
UPDATE t_black_list t1
    LEFT JOIN t_application_mnt t2 ON t1.APPLICATION_ID = t2.APPLICATION_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_exception_info 无需订正 就取默认的test

-- t_fast_debug_config_info t_business_link_manage_table
UPDATE t_fast_debug_config_info t1
    LEFT JOIN t_business_link_manage_table t2 ON t1.business_link_id = t2.LINK_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_fast_debug_exception
UPDATE t_fast_debug_exception t1
    LEFT JOIN t_application_mnt t2 ON t1.application_Name = t2.APPLICATION_NAME
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_fast_debug_result t_business_link_manage_table
UPDATE t_fast_debug_result t1
    LEFT JOIN t_business_link_manage_table t2 ON t1.business_link_name = t2.LINK_NAME
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_file_manage
UPDATE t_file_manage t1
    LEFT JOIN t_script_file_ref t2 ON t1.id = t2.file_id
    LEFT JOIN t_script_manage_deploy t3 ON t3.id = t2.script_deploy_id
    LEFT JOIN t_script_manage t4 ON t4.id = t3.script_id
    SET t1.env_code = IFNULL(t4.env_code,'test');

-- t_link_guard
UPDATE t_link_guard t1
    LEFT JOIN t_application_mnt t2 ON t1.application_id = t2.APPLICATION_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_link_manage_table
UPDATE t_link_manage_table t1
    LEFT JOIN t_application_mnt t2 ON t1.APPLICATION_NAME = t2.APPLICATION_NAME
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_application_focus
UPDATE t_application_focus t1
    LEFT JOIN t_application_mnt t2 ON t1.app_name = t2.APPLICATION_NAME
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_application_middleware
UPDATE t_application_middleware t1
    LEFT JOIN t_application_mnt t2 ON t1.application_id = t2.APPLICATION_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_data_build
UPDATE t_data_build t1
    LEFT JOIN t_application_mnt t2 ON t1.application_id = t2.APPLICATION_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_datasource_tag_ref
UPDATE t_datasource_tag_ref t1
    LEFT JOIN t_tro_dbresource t2 ON t1.datasource_id = t2.id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_fast_debug_stack_info
UPDATE t_fast_debug_stack_info t1
    LEFT JOIN t_application_mnt t2 ON t1.app_name = t2.APPLICATION_NAME
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_leakverify_result
UPDATE t_leakverify_result t1
    LEFT JOIN (
    SELECT
    `name`,
    id,
    CASE
    WHEN LOCATE( 'test', `name` ) > 0 THEN
    'test' ELSE 'prod'
    END env_code
    FROM
    t_tro_user
    ) t2 ON t2.id = t1.user_id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_leakverify_detail
UPDATE t_leakverify_detail t1
    LEFT JOIN t_leakverify_result t2 ON t1.result_id = t2.id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_link_detection
UPDATE t_link_detection t1
    LEFT JOIN t_application_mnt t2 ON t1.APPLICATION_ID = t2.APPLICATION_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_login_record
UPDATE t_login_record t1
    LEFT JOIN (
    SELECT
    `user_name`,
    CASE
    WHEN LOCATE( 'test', `user_name` ) > 0 THEN
    'test' ELSE 'prod'
    END env_code
    FROM
    t_login_record
    ) t2 ON t2.user_name = t1.user_name
    SET t1.env_code = IFNULL(t2.env_code,'test');
-- t_ops_script_batch_no
UPDATE t_ops_script_batch_no t1
    LEFT JOIN t_ops_script_manage t2 ON t1.ops_script_id = t2.id
    SET t1.env_code = IFNULL(t2.env_code,'test');
-- t_ops_script_execute_result
UPDATE t_ops_script_execute_result t1
    LEFT JOIN t_ops_script_manage t2 ON t1.ops_script_id = t2.id
    SET t1.env_code = IFNULL(t2.env_code,'test');
-- t_ops_script_file
UPDATE t_ops_script_file t1
    LEFT JOIN t_ops_script_manage t2 ON t1.ops_script_id = t2.id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_performance_base_data
UPDATE t_performance_base_data t1
    LEFT JOIN t_application_mnt t2 ON t1.app_name = t2.APPLICATION_NAME
    SET t1.env_code = IFNULL(t2.env_code,'test');
-- t_performance_criteria_config
UPDATE t_performance_criteria_config t1
    LEFT JOIN t_application_mnt t2 ON t1.app_id = t2.APPLICATION_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');
-- t_performance_thread_data
-- UPDATE t_performance_criteria_config t1
--     LEFT JOIN t_application_mnt t2 ON t1.app_name = t2.APPLICATION_NAME
--     SET t1.env_code = IFNULL(t2.env_code,'test');


-- tenant_id
update t_application_ds_manage set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_application_mnt set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_application_plugins_config set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_black_list set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_business_link_manage_table set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_leakcheck_config set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_script_manage set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_leakcheck_config_detail set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_leakverify_result set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);;
update t_link_guard set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_link_manage_table set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_operation_log set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_ops_script_manage set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_tro_dbresource set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_application_node_probe set tenant_id=IFNULL((select tenant_id from t_application_mnt where APPLICATION_NAME= application_name),1);
update t_fast_debug_config_info set tenant_id=IFNULL((select tenant_id from t_business_link_manage_table where LINK_ID= business_link_id),1);
update t_fast_debug_exception set tenant_id=IFNULL((select tenant_id from t_application_mnt where APPLICATION_NAME= application_Name),1);
update t_fast_debug_result set tenant_id=IFNULL((select tenant_id from t_business_link_manage_table where LINK_NAME= business_link_name),1);
update t_file_manage t1
    LEFT JOIN t_script_file_ref t2 ON t1.id = t2.file_id
    LEFT JOIN t_script_manage_deploy t3 ON t3.id = t2.script_deploy_id
    LEFT JOIN t_script_manage t4 ON t4.id = t3.script_id
    SET t1.tenant_id = IFNULL(t4.tenant_id,1);

update t_application_focus set tenant_id=IFNULL((select tenant_id from t_application_mnt where APPLICATION_NAME = app_name),1);
update t_application_middleware t set tenant_id=IFNULL((select tenant_id from t_application_mnt where APPLICATION_ID = t.application_id),1);
update t_data_build t set t.tenant_id=(select tenant_id from t_application_mnt where APPLICATION_ID = t.APPLICATION_ID);
update t_datasource_tag_ref t set t.tenant_id=IFNULL((select tenant_id from t_tro_dbresource where id = t.datasource_id),1);
update t_fast_debug_machine_performance f set f.tenant_id=IFNULL((SELECT t.tenant_id from t_fast_debug_result t  WHERE  t.trace_id = f.trace_id AND t.is_deleted=0),1);
update t_fast_debug_stack_info f set f.tenant_id=IFNULL((SELECT t.tenant_id from t_fast_debug_result t WHERE  t.trace_id = f.trace_id AND t.is_deleted=0),1);
update t_leakverify_detail t set t.tenant_id=IFNULL((SELECT tenant_id FROM t_leakverify_result WHERE id=t.result_id and is_deleted=0),1);
update t_link_detection t set t.tenant_id=IFNULL((select tenant_id from t_application_mnt where APPLICATION_ID = t.APPLICATION_ID),1);
update t_login_record t set t.tenant_id=IFNULL((select tenant_id from t_tro_user WHERE name=t.user_name and is_delete=0),1);
update t_ops_script_batch_no t set t.tenant_id=IFNULL((select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_deleted=0),1);
update t_ops_script_execute_result t set t.tenant_id=IFNULL((select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_deleted=0),1);
update t_ops_script_file t set t.tenant_id=IFNULL((select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_deleted=0),1);
update t_performance_base_data t set t.tenant_id= IFNULL((select tenant_id from t_application_mnt where APPLICATION_NAME = t.app_name),1);
update t_performance_criteria_config t set t.tenant_id= IFNULL((select tenant_id from t_application_mnt where APPLICATION_ID = t.app_id),1);
update t_performance_thread_data t set t.tenant_id= IFNULL((select tenant_id from t_application_mnt where APPLICATION_NAME = t.app_name),1);

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