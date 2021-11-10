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

-- 	t_leakcheck_config
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
    LEFT JOIN t_application_mnt t2 ON t1.APPLICATION_ID = t2.id
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
    LEFT JOIN t_application_mnt t2 ON t1.APPLICATION_ID = t2.id
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
    SET t1.env_code = t4.env_code;

-- t_link_guard
UPDATE t_link_guard t1
    LEFT JOIN t_application_mnt t2 ON t1.application_id = t2.id
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
    LEFT JOIN t_application_mnt t2 ON t1.application_id = t2.id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_data_build
UPDATE t_data_build t1
    LEFT JOIN t_application_mnt t2 ON t1.application_id = t2.id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_datasource_tag_ref
UPDATE t_datasource_tag_ref t1
    LEFT JOIN t_tro_dbresource t2 ON t1.datasource_id = t2.id
    SET t1.env_code = IFNULL(t2.env_code,'test');


-- tenant_id
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
update t_login_record t set t.tenant_id=(select tenant_id from t_tro_user WHERE name=t.user_name and is_delete=0);
update t_ops_script_batch_no t set t.tenant_id=(select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_delete=0);
update t_ops_script_execute_result t set t.tenant_id=(select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_delete=0);
update t_ops_script_file t set t.tenant_id=(select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_delete=0);
update t_performance_base_data t set t.tenant_id= (select tenant_id from t_application_mnt where APPLICATION_NAME = t.app_name);
update t_performance_criteria_config t set t.tenant_id= (select tenant_id from t_application_mnt where APPLICATION_ID = t.app_id);
update t_performance_thread_data t set t.tenant_id= (select tenant_id from t_application_mnt where APPLICATION_NAME = t.app_name);

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
    (5, '5b06060a-17cb-4588-bb71-edd7f65035aa', 'lbx-pressure', '老百姓', 'lbx-pressure', 1),
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
update  t_tro_user set tenant_id = customer_id
-- 默认租户
INSERT INTO t_tro_user
( `name`, `nick`, `salt`, `password`, `status`, `user_type`, `model`, `role`, `tenant_id` )
VALUES ( 'admin@default', 'default', '$2a$10$MqkhiWRgANNXPlhM6uJdBe', '$2a$10$MqkhiWRgANNXPlhM6uJdBe0pT.SUmd9JdK9eIFp9CbcZSlOiAWL7S', 0, 2, 0, 0,1);
-- 测试租户
INSERT INTO `trodb`.`t_tro_user`
( `name`, `nick`, `salt`, `password`, `status`, `user_type`, `model`, `role`, `tenant_id` )
VALUES ( 'admin@test', '测试', '$2a$10$MqkhiWRgANNXPlhM6uJdBe', '$2a$10$MqkhiWRgANNXPlhM6uJdBe0pT.SUmd9JdK9eIFp9CbcZSlOiAWL7S', 0, 2, 0, 0,2);
-- T3出行出现
update  t_tro_user  set tenant_id = 3 WHERE name like 'T3Trip%'
update  t_tro_user  set user_type = 2  WHERE id = 3
-- 老百姓
update  t_tro_user  set tenant_id = 5 WHERE name like 'lbx%'
update  t_tro_user  set user_type = 2  WHERE id = 5
-- 申通账号
update  t_tro_user  set tenant_id = 7 WHERE name like 'sto%'
update  t_tro_user  set user_type = 2  WHERE id = 7
-- 联动云
update  t_tro_user  set tenant_id = 1O WHERE name like 'liandongyun%'
update  t_tro_user  set user_type = 2  WHERE id = 10
-- 爱库存
update  t_tro_user  set user_type = 2  WHERE id = 24

-- 修正部门表

-- 部门不区分环境
update t_tro_user_dept_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id
update t_tro_dept t1 left join (SELECT DISTINCT  dept_id,tenant_id FROM t_tro_user_dept_relation ) t2 on t1.id = t2.dept_id set t1.tenant_id =  t2.tenant_id

-- 角色 区分 环境  t_tro_authority
select GROUP_CONCAT(id)  from t_tenant_info
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code = 'prod'
WHERE t1.user_id in(2,3,5,7,10,24)

-- 默认租户
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code = 'test'
WHERE t1.tenant_id = 1

-- T3出行
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code = 'test'
WHERE t1.user_id = 4

-- 老板姓
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code = 'prod'
WHERE t1.user_id in(12,13)

-- 联动云
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code = 'test'
WHERE t1.user_id = 9


update t_tro_role t1 left join (SELECT DISTINCT  role_id,tenant_id FROM t_tro_role_user_relation ) t2 on t1.id = t2.role_id set t1.tenant_id =  t2.tenant_id
and t1.env_code =  t2.env_code

update t_tro_authority t1 left join t_tro_role  t2 on t1.role_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code =  t2.env_code



--

----------------------------更新修正报告相关
-- 更新 t_report_application_summary |t_report_machine |t_report_summary --
-- 默认租户 --
-- 找出默认租户用户id
SELECT GROUP_CONCAT(id) from t_tro_user WHERE tenant_id = 1
-- 找出默认租户的报告id trodb_cloud找
SELECT GROUP_CONCAT(id) from t_report WHERE user_id in()
-- 更新报告 t_report_application_summary
update t_report_application_summary set tenant_id = 1 and env_code = 'test' WHERE report_id in ()
update t_report_machine set tenant_id = 1 and env_code = 'test' WHERE report_id in ()
update t_report_summary set tenant_id = 1 and env_code = 'test' WHERE report_id in ()
update t_trace_manage set tenant_id = 1 and env_code = 'test' WHERE report_id in ()

-- T3出行
-- 测试环境 cloud查找
SELECT GROUP_CONCAT(id) from t_report WHERE user_id = 4
update t_report_application_summary set tenant_id = 3 and env_code = 'test' WHERE report_id in ()
update t_report_machine set tenant_id = 3 and env_code = 'test' WHERE report_id in ()
update t_report_summary set tenant_id = 3 and env_code = 'test' WHERE report_id in ()
update t_trace_manage set tenant_id = 3 and env_code = 'test' WHERE report_id in ()

-- 生产环境 cloud查找
SELECT GROUP_CONCAT(id) from t_report WHERE user_id = 3
update t_report_application_summary set tenant_id = 3 and env_code = 'prod' WHERE report_id in ()
update t_report_machine set tenant_id = 3 and env_code = 'prod' WHERE report_id in ()
update t_report_summary set tenant_id = 3 and env_code = 'prod' WHERE report_id in ()
update t_trace_manage set tenant_id = 3 and env_code = 'prod' WHERE report_id in ()




-- 老百姓
-- 生产环境 cloud查找
SELECT GROUP_CONCAT(id) from t_report WHERE user_id in(5,12,13)
update t_report_application_summary set tenant_id = 5 and env_code = 'prod' WHERE report_id in ()
update t_report_machine set tenant_id = 5 and env_code = 'prod' WHERE report_id in ()
update t_report_summary set tenant_id = 5 and env_code = 'prod' WHERE report_id in ()
update t_trace_manage set tenant_id = 5 and env_code = 'prod' WHERE report_id in ()



-- 申通快递
-- 测试环境 cloud查找
SELECT GROUP_CONCAT(id) from t_tro_user WHERE name like 'sto%' and id <> 7
SELECT GROUP_CONCAT(id) from t_report WHERE user_id in()
update t_report_application_summary set tenant_id = 7 and env_code = 'test' WHERE report_id in ()
update t_report_machine set tenant_id = 7 and env_code = 'test' WHERE report_id in ()
update t_report_summary set tenant_id = 7 and env_code = 'test' WHERE report_id in ()
update t_trace_manage set tenant_id = 7 and env_code = 'test' WHERE report_id in ()

-- 生产环境 cloud查找
update t_report_application_summary set tenant_id = 7 and env_code = 'prod' WHERE report_id in ()
update t_report_machine set tenant_id = 7 and env_code = 'prod' WHERE report_id in ()
update t_report_summary set tenant_id = 7 and env_code = 'prod' WHERE report_id in ()
update t_trace_manage set tenant_id = 7 and env_code = 'prod' WHERE report_id in ()

-- 联动云
-- 测试环境 cloud查找
SELECT GROUP_CONCAT(id) from t_report WHERE user_id = 9
update t_report_application_summary set tenant_id = 10 and env_code = 'test' WHERE report_id in ()
update t_report_machine set tenant_id = 10 and env_code = 'test' WHERE report_id in ()
update t_report_summary set tenant_id = 10 and env_code = 'test' WHERE report_id in ()
update t_trace_manage set tenant_id = 10 and env_code = 'test' WHERE report_id in ()


-- 生产环境 cloud查找
SELECT GROUP_CONCAT(id) from t_report WHERE user_id = 10
update t_report_application_summary set tenant_id = 10 and env_code = 'prod' WHERE report_id in ()
update t_report_machine set tenant_id = 10 and env_code = 'prod' WHERE report_id in ()
update t_report_summary set tenant_id = 10 and env_code = 'prod' WHERE report_id in ()
update t_trace_manage set tenant_id = 10 and env_code = 'prod' WHERE report_id in ()

-- 爱库存
-- 生产环境 cloud查找
SELECT GROUP_CONCAT(id) from t_report WHERE user_id = 24
update t_report_application_summary set tenant_id = 24 and env_code = 'prod' WHERE report_id in ()
update t_report_machine set tenant_id = 24 and env_code = 'prod' WHERE report_id in ()
update t_report_summary set tenant_id = 24 and env_code = 'prod' WHERE report_id in ()
update t_trace_manage set tenant_id = 24 and env_code = 'prod' WHERE report_id in ()


UPDATE t_trace_manage_deploy t1
    LEFT JOIN t_trace_manage t2 ON t1.trace_manage_id = t2.id
    SET t1.env_code = t2.env_code ,t1.tenant_id = t2.TENANT_ID;

---------------
-- 场景表 t_scene | t_shadow_job_config | t_shadow_mq_consumer
-- 查找租户
select GROUP_CONCAT(id)  from t_tenant_info
update t_scene set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
update t_shadow_job_config set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
update t_shadow_mq_consumer set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
update t_trace_node_info set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
update t_tro_dbresource set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
update t_white_list set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
update t_whitelist_effective_app set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
update t_application_mnt set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
update t_application_ds_cache_manage set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
update t_application_ds_db_manage set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)

-- 默认
update t_scene set tenant_id = customer_id and env_code = 'test' where customer_id = 1
update t_shadow_job_config set tenant_id = customer_id and env_code = 'test' where customer_id = 1
update t_shadow_mq_consumer set tenant_id = customer_id and env_code = 'test' where customer_id = 1
update t_trace_node_info set tenant_id = customer_id and env_code = 'test' where customer_id = 1
update t_tro_dbresource set tenant_id = customer_id and env_code = 'test' where customer_id = 1
update t_white_list set tenant_id = customer_id and env_code = 'test' where customer_id = 1
update t_whitelist_effective_app set tenant_id = customer_id and env_code = 'test' where customer_id = 1
update t_application_mnt set tenant_id = customer_id and env_code = 'test' where customer_id = 1
update t_application_ds_cache_manage set tenant_id = customer_id and env_code = 'test' where customer_id = 1
update t_application_ds_db_manage set tenant_id = customer_id and env_code = 'test' where customer_id = 1
-- T3出行
update t_scene set tenant_id = 3 and env_code = 'test' where  customer_id = 4
update t_shadow_job_config set tenant_id = 3 and env_code = 'test' where  customer_id = 4
update t_shadow_mq_consumer set tenant_id = 3 and env_code = 'test' where  customer_id = 4
update t_trace_node_info set tenant_id = 3 and env_code = 'test' where  customer_id = 4
update t_tro_dbresource set tenant_id = 3 and env_code = 'test' where  customer_id = 4
update t_white_list set tenant_id = 3 and env_code = 'test' where  customer_id = 4
update t_whitelist_effective_app set tenant_id = 3 and env_code = 'test' where  customer_id = 4
update t_application_mnt set tenant_id = 3 and env_code = 'test' where  customer_id = 4
update t_application_ds_cache_manage set tenant_id = 3 and env_code = 'test' where  customer_id = 4
update t_application_ds_db_manage set tenant_id = 3 and env_code = 'test' where  customer_id = 4
-- 老板姓
update t_scene set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
update t_shadow_job_config set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
update t_shadow_mq_consumer set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
update t_trace_node_info set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
update t_tro_dbresource set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
update t_white_list set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
update t_whitelist_effective_app set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
update t_application_mnt set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
update t_application_ds_cache_manage set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
update t_application_ds_db_manage set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)

-- 联动云
update t_scene set tenant_id = 10 and env_code = 'test' where  customer_id = 9
update t_shadow_job_config set tenant_id = 10 and env_code = 'test' where  customer_id = 9
update t_shadow_mq_consumer set tenant_id = 10 and env_code = 'test' where  customer_id = 9
update t_trace_node_info set tenant_id = 10 and env_code = 'test' where  customer_id = 9
update t_tro_dbresource set tenant_id = 10 and env_code = 'test' where  customer_id = 9
update t_white_list set tenant_id = 10 and env_code = 'test' where  customer_id = 9
update t_whitelist_effective_app set tenant_id = 10 and env_code = 'test' where  customer_id = 9
update t_application_mnt set tenant_id = 10 and env_code = 'test' where  customer_id = 9
update t_application_ds_cache_manage set tenant_id = 10 and env_code = 'test' where  customer_id = 9
update t_application_ds_db_manage set tenant_id = 10 and env_code = 'test' where  customer_id = 9
-- 更新 t_scene_link_relate
UPDATE t_scene_link_relate t1
    LEFT JOIN t_scene t2 ON t1.SCENE_ID = t2.id
    SET t1.env_code = t2.env_code ,t1.tenant_id = t2.TENANT_ID;

------脚本相关------
-- t_script_debug
select GROUP_CONCAT(id)  from t_tenant_info
update t_script_debug set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
update t_script_manage set tenant_id = customer_id and env_code = 'prod' where customer_id in(2,3,5,7,10,24)
-- 默认
update t_script_debug set tenant_id = customer_id and env_code = 'test' where customer_id = 1
update t_script_manage set tenant_id = customer_id and env_code = 'test' where customer_id = 1
-- T3出行
update t_script_debug set tenant_id = 3 and env_code = 'test' where  customer_id = 4
update t_script_manage set tenant_id = 3 and env_code = 'test' where  customer_id = 4
-- 老板姓
update t_script_debug set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
update t_script_manage set tenant_id = 5 and env_code = 'prod' where  customer_id in(12,13)
-- 联动云
update t_script_debug set tenant_id = 10 and env_code = 'test' where  customer_id = 9
update t_script_manage set tenant_id = 10 and env_code = 'test' where  customer_id = 9

UPDATE t_script_manage_deploy t1
    LEFT JOIN t_script_manage t2 ON t1.script_id = t2.id
    SET t1.env_code = t2.env_code ,t1.tenant_id = t2.TENANT_ID;

-- t_shadow_job_config



---无涯

