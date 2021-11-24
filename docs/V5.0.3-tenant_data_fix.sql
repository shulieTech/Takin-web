BEGIN;
-- 无涯 基础表
UPDATE t_pradar_zk_config set TENANT_ID = -1 and env_code = 'system'
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

-- todo 申通
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

-- caijy
BEGIN;
-- 记录userid和envCode
CREATE TEMPORARY TABLE IF NOT EXISTS `DATA_FIX_TABLE` AS
SELECT id as user_id,tenant_id,
       CASE
           WHEN customer_id <> tenant_id THEN
               'test'
           ELSE
               'prod'
           END AS env_code
FROM t_tro_user;
ALTER TABLE `DATA_FIX_TABLE` ADD PRIMARY KEY (`user_id`);

-- saas
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=1;
UPDATE `DATA_FIX_TABLE` SET `env_code`='prod' WHERE `user_id`=3;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=4;
UPDATE `DATA_FIX_TABLE` SET `env_code`='prod' WHERE `user_id`=5;
UPDATE `DATA_FIX_TABLE` SET `env_code`='prod' WHERE `user_id`=12;
UPDATE `DATA_FIX_TABLE` SET `env_code`='prod' WHERE `user_id`=13;
UPDATE `DATA_FIX_TABLE` SET `env_code`='prod' WHERE `user_id`=7;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=8;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=17;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=19;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=20;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=21;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=22;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=23;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=25;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=26;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=27;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=28;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=29;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=30;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=31;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=32;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=33;
UPDATE `DATA_FIX_TABLE` SET `env_code`='test' WHERE `user_id`=9;
UPDATE `DATA_FIX_TABLE` SET `env_code`='prod' WHERE `user_id`=10;
UPDATE `DATA_FIX_TABLE` SET `env_code`='prod' WHERE `user_id`=24;

-- demo环境


-- env_code
-- 应用表 t_application_mnt
UPDATE t_application_mnt t1 SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t1.user_id),'test');

-- t_business_link_manage_table
UPDATE t_business_link_manage_table t1 SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t1.user_id),'test');

-- t_script_manage
UPDATE t_script_manage t1 SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t1.user_id),'test');

-- t_leakcheck_config
UPDATE t_leakcheck_config t1 SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t1.user_id),'test');

-- t_leakcheck_config_detail
UPDATE t_leakcheck_config_detail t1 SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t1.user_id),'test');

-- t_operation_log
UPDATE t_operation_log t1 SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t1.user_id),'test');

-- t_ops_script_manage
UPDATE t_ops_script_manage t1 SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t1.user_id),'test');

-- t_tro_dbresource
UPDATE t_tro_dbresource t1 SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t1.user_id),'test');

-- t_login_record
UPDATE t_login_record t1
    LEFT JOIN t_tro_user t2 ON t1.user_name=t2.name
    SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t2.id),'test');

-- t_leakverify_result
UPDATE t_leakverify_result t1 SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t1.user_id),'test');

-- t_application_ds_manage
UPDATE t_application_ds_manage t1
    LEFT JOIN t_application_mnt t2 ON t1.APPLICATION_ID = t2.APPLICATION_ID
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_application_node_probe
UPDATE t_application_node_probe t1
    LEFT JOIN t_application_mnt t2 ON t1.application_name = t2.APPLICATION_NAME AND t1.customer_id = t2.customer_id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_application_plugins_config
UPDATE t_application_plugins_config t1
    LEFT JOIN t_application_mnt t2 ON t1.application_id = t2.APPLICATION_ID
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
    LEFT JOIN t_application_mnt t2 ON t1.application_Name = t2.APPLICATION_NAME AND t1.customer_id=t2.customer_id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_fast_debug_result t_business_link_manage_table
UPDATE t_fast_debug_result t1
    LEFT JOIN t_business_link_manage_table t2 ON t1.business_link_name = t2.LINK_NAME AND t1.customer_id=t2.customer_id
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
    LEFT JOIN t_application_mnt t2 ON t1.APPLICATION_NAME = t2.APPLICATION_NAME AND t1.CUSTOMER_ID=t2.customer_id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_application_focus 按理说用应用名称查会可能查出来多条 但是现在都是一条，所以目前这样迁移
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

-- t_leakverify_detail
UPDATE t_leakverify_detail t1
    LEFT JOIN t_leakverify_result t2 ON t1.result_id = t2.id
    SET t1.env_code = IFNULL(t2.env_code,'test');

-- t_link_detection
UPDATE t_link_detection t1
    LEFT JOIN t_application_mnt t2 ON t1.APPLICATION_ID = t2.APPLICATION_ID
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

-- liuchuan部分

-- t_agent_config
UPDATE t_agent_config m
    LEFT JOIN t_tro_user u ON u.`name` = m.operator
    LEFT JOIN DATA_FIX_TABLE fix ON fix.user_id=u.id
SET m.env_code = IFNULL(fix.env_code,'test');



-- liuchuan end

-- tenant_id
update t_application_ds_manage ds set ds.tenant_id=IFNULL((select tenant_id from t_tro_user where id= ds.user_id),1);
update t_application_mnt app set app.tenant_id=IFNULL((select tenant_id from t_tro_user where id= app.user_id),1);
update t_application_node_probe probe set probe.tenant_id=IFNULL((select tenant_id from t_application_mnt where APPLICATION_NAME = probe.application_name and customer_id=probe.customer_id),1);
update t_black_list set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_business_link_manage_table set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_fast_debug_config_info set tenant_id=IFNULL((select tenant_id from t_business_link_manage_table where LINK_ID= business_link_id),1);
update t_fast_debug_result set tenant_id=IFNULL((select tenant_id from t_fast_debug_config_info where id= config_id),1);
update t_fast_debug_exception set tenant_id=IFNULL((select tenant_id from t_fast_debug_result where id= result_id),1);
update t_script_manage set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_file_manage t1
    LEFT JOIN t_script_file_ref t2 ON t1.id = t2.file_id
    LEFT JOIN t_script_manage_deploy t3 ON t3.id = t2.script_deploy_id
    LEFT JOIN t_script_manage t4 ON t4.id = t3.script_id
    SET t1.tenant_id = IFNULL(t4.tenant_id,1);
update t_leakcheck_config set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_leakcheck_config_detail set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_leakverify_result set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_operation_log set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_ops_script_manage set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_link_guard set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_link_manage_table set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_tro_dbresource set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_application_plugins_config set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_application_focus set tenant_id=IFNULL((select tenant_id from t_application_mnt where APPLICATION_NAME = app_name),1);
update t_application_middleware t set tenant_id=IFNULL((select tenant_id from t_application_mnt where APPLICATION_ID = t.application_id),1);
update t_data_build t set t.tenant_id=(select tenant_id from t_application_mnt where APPLICATION_ID = t.APPLICATION_ID);
update t_datasource_tag_ref t set t.tenant_id=IFNULL((select tenant_id from t_tro_dbresource where id = t.datasource_id),1);
update t_leakverify_detail t set t.tenant_id=IFNULL((SELECT tenant_id FROM t_leakverify_result WHERE id=t.result_id and is_deleted=0),1);
update t_link_detection t set t.tenant_id=IFNULL((select tenant_id from t_application_mnt where APPLICATION_ID = t.APPLICATION_ID),1);
update t_login_record t set t.tenant_id=IFNULL((select tenant_id from t_tro_user WHERE name=t.user_name and is_delete=0),1);
update t_ops_script_batch_no t set t.tenant_id=IFNULL((select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_deleted=0),1);
update t_ops_script_execute_result t set t.tenant_id=IFNULL((select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_deleted=0),1);
update t_ops_script_file t set t.tenant_id=IFNULL((select tenant_id from t_ops_script_manage WHERE id=t.ops_script_id and is_deleted=0),1);
update t_performance_base_data t set t.tenant_id= IFNULL((select tenant_id from t_application_mnt where APPLICATION_NAME = t.app_name),1);
update t_performance_criteria_config t set t.tenant_id= IFNULL((select tenant_id from t_application_mnt where APPLICATION_ID = t.app_id),1);

COMMIT ;
-- caijy end

-- liuchuan
BEGIN;
-- t_activity_node_service_state 根据业务活动获得 租户id
UPDATE t_activity_node_service_state a
    LEFT JOIN (SELECT IFNULL(u.tenant_id, 1) tid, b.LINK_ID bid, b.env_code
    FROM t_business_link_manage_table b LEFT JOIN t_tro_user u ON u.id = b.USER_ID) t
ON t.bid = a.activity_id
    SET a.tenant_id = IFNULL(t.tid,1), a.env_code = IFNULL(t.env_code,'test');

-- t_agent_config 根据 key, 获得用户id, 然后获得 租户id
UPDATE t_agent_config m LEFT JOIN t_tro_user u ON u.`name` = m.operator SET m.tenant_id = IFNULL(u.tenant_id,1);
UPDATE t_agent_config set tenant_id=-1,env_code='system' WHERE tenant_id=1 AND env_code='test';
-- t_app_business_table_info 根据用户id查到租户id, 然后赋值
UPDATE t_app_business_table_info m LEFT JOIN t_tro_user u ON u.id = m.user_id SET m.tenant_id = IFNULL(u.tenant_id,1);
UPDATE t_app_business_table_info m LEFT JOIN t_application_mnt u ON u.APPLICATION_ID = m.APPLICATION_ID SET m.env_code = IFNULL(u.env_code,'test');

-- t_app_middleware_info 根据用户id查到租户id, 然后赋值
UPDATE t_app_middleware_info m LEFT JOIN t_tro_user u ON u.id = m.user_id SET m.tenant_id = IFNULL(u.tenant_id,1);
UPDATE t_app_middleware_info m LEFT JOIN t_application_mnt u ON u.APPLICATION_ID = m.APPLICATION_ID SET m.env_code = IFNULL(u.env_code,'test');

UPDATE t_app_remote_call m LEFT JOIN t_tro_user u ON u.id = m.user_id SET m.tenant_id = IFNULL(u.tenant_id,1);
UPDATE t_app_remote_call m LEFT JOIN t_application_mnt u ON u.APPLICATION_ID = m.APPLICATION_ID SET m.env_code = IFNULL(u.env_code,'test');

UPDATE t_app_agent_config_report m LEFT JOIN t_tro_user u ON u.id = m.user_id SET m.tenant_id = u.tenant_id;
UPDATE t_app_agent_config_report m LEFT JOIN t_application_mnt u ON u.APPLICATION_ID = m.application_id SET m.env_code = IFNULL(u.env_code,'test');

COMMIT;
-- liuchuan end

-- 兮曦 --
-- 场景数据必须优先订正
update e_patrol_scene set tenant_id=7,env_code='prod',user_id=7 where customer_id=7;
update e_patrol_scene set tenant_id=7,env_code='test',user_id=8 where customer_id=8;
update e_patrol_scene set tenant_id=1,env_code='test',user_id=1 where customer_id=1;
update e_patrol_scene a,t_tenant_info b set a.tenant_app_key = b.`key` where a.tenant_id=b.id and a.tenant_id is not null;

update e_patrol_activity_assert set env_code='test',tenant_id=1;

update e_patrol_board set tenant_id=7,env_code='prod',user_id=7 where customer_id=7;
update e_patrol_board set tenant_id=7,env_code='test',user_id=8 where customer_id=8;
update e_patrol_board set tenant_id=1,env_code='test',user_id=1 where customer_id=1;

update e_patrol_board_scene set env_code='test',tenant_id=1;

update e_patrol_exception set env_code='test',tenant_id=1;
update e_patrol_exception a,e_patrol_scene b set a.user_id= b.user_id where a.scene_id=b.id and a.scene_id <> '-1';

-- 必须先订正数据才能加索引，否则会有冲突
update e_patrol_exception_config set tenant_id=7,env_code='test' where customer_id=7;
update e_patrol_exception_config set tenant_id=7,env_code='prod' where customer_id=8;
update e_patrol_exception_config set tenant_id=1,env_code='test' where customer_id=1;
delete from e_patrol_exception_config where customer_id is null;
ALTER TABLE `e_patrol_exception_config` ADD UNIQUE INDEX `idx_config` ( `tenant_id`, `env_code`, `type_value`,`level_value` ) USING BTREE;

INSERT INTO `e_patrol_exception_config`(`order_number`, `type_value`, `level_value`, `threshold_value`, `contrast_factor`, `remarks`,`tenant_id`, `env_code`) VALUES (1, 1, 1, 100.00, 1, '一般卡慢', -1, 'system');
INSERT INTO `e_patrol_exception_config`(`order_number`, `type_value`, `level_value`, `threshold_value`, `contrast_factor`, `remarks`,`tenant_id`, `env_code`) VALUES (0, 1, 2, 230.00, 1, '严重卡慢', -1, 'system');
INSERT INTO `e_patrol_exception_config`(`order_number`, `type_value`, `level_value`, `threshold_value`, `contrast_factor`, `remarks`,`tenant_id`, `env_code`) VALUES (1, 2, 1, 90.00, -1, '一般瓶颈', -1, 'system');
INSERT INTO `e_patrol_exception_config`(`order_number`, `type_value`, `level_value`, `threshold_value`, `contrast_factor`, `remarks`,`tenant_id`, `env_code`) VALUES (0, 2, 2, 80.00, -1, '严重瓶颈', -1, 'system');
INSERT INTO `e_patrol_exception_config`(`order_number`, `type_value`, `level_value`, `threshold_value`, `contrast_factor`, `remarks`,`tenant_id`, `env_code`) VALUES (1, 4, 1, 30.00, 1, '一般慢SQL', -1, 'system');
INSERT INTO `e_patrol_exception_config`(`order_number`, `type_value`, `level_value`, `threshold_value`, `contrast_factor`, `remarks`,`tenant_id`, `env_code`) VALUES (0, 4, 2, 60.00, 1, '严重慢SQL', -1, 'system');

update e_patrol_exception_notice_config set env_code='test',tenant_id=1;

update e_patrol_scene_chain set env_code='test',tenant_id=1;
update e_patrol_scene_check set env_code='test',tenant_id=1;

-- 兮曦 --

-- 额外 租户期间增加的表
update t_mq_config_template set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
-- update t_application_ds_cache_manage set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
-- update t_application_ds_db_manage set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);
update t_application_ds_db_table set tenant_id=IFNULL((select tenant_id from t_tro_user where id= user_id),1);

-- 大表最后数据迁移
-- t_fast_debug_stack_info
UPDATE t_fast_debug_stack_info t1
    LEFT JOIN t_fast_debug_result t2 ON t1.trace_id = t2.trace_id
    SET t1.env_code = IFNULL(t2.env_code,'test');
UPDATE t_fast_debug_stack_info f SET f.tenant_id=IFNULL((SELECT t.tenant_id from t_fast_debug_result t WHERE  t.trace_id = f.trace_id AND t.is_deleted=0),1);
-- t_fast_debug_machine_performance
UPDATE t_fast_debug_machine_performance t1
    LEFT JOIN t_fast_debug_result t2 ON t1.trace_id = t2.trace_id
    SET t1.env_code = IFNULL(t2.env_code,'test');
UPDATE t_fast_debug_machine_performance f SET f.tenant_id=IFNULL((SELECT t.tenant_id from t_fast_debug_result t WHERE  t.trace_id = f.trace_id AND t.is_deleted=0),1);

-- 最后加索引
ALTER TABLE t_activity_node_service_state ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_agent_config ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_app_agent_config_report ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_app_business_table_info ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_app_middleware_info ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_app_remote_call ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_application_api_manage ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_application_api_manage ADD UNIQUE KEY `idx_app_api_method_tenant_env` ( `APPLICATION_NAME`,`api`,`method`,`tenant_id`,`env_code` );
ALTER TABLE t_application_api_manage DROP KEY `APPLICATION_NAME`;
ALTER TABLE `t_application_ds_manage` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_application_mnt ADD INDEX `idx_application_id` ( `application_id` );
ALTER TABLE t_application_mnt
DROP KEY `index_identifier_application_name`,
ADD UNIQUE KEY `idx_application_name_tenant_env` ( `APPLICATION_NAME`,`tenant_id`,`env_code`);
ALTER TABLE `t_application_focus` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_application_middleware` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_application_node_probe` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_application_plugins_config` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_black_list` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_business_link_manage_table` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_data_build` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_datasource_tag_ref` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_datasource_tag_ref ADD UNIQUE KEY `idx_datasource_tag_tenant_env` ( `datasource_id`,`tag_id`,`tenant_id`,`env_code` );
ALTER TABLE t_datasource_tag_ref DROP KEY `index_datasourceId_tagId`;
ALTER TABLE `t_dictionary_data`
DROP PRIMARY KEY,
ADD PRIMARY KEY(`ID`,`tenant_id`,`env_code`) USING BTREE;
ALTER TABLE `t_dictionary_type` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
-- t_exception_info 不需要订正 ALTER TABLE `t_exception_info` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_fast_debug_config_info` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_fast_debug_config_info ADD UNIQUE KEY `idx_name_tenant_env` ( `name`,`tenant_id`,`env_code` );
ALTER TABLE t_fast_debug_config_info DROP KEY `name`;
ALTER TABLE `t_fast_debug_exception` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_fast_debug_machine_performance` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_fast_debug_result` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_file_manage` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_leakcheck_config` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_leakcheck_config_detail` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_leakverify_detail` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_leakverify_result` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_detection` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_guard` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_manage_table` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_mnt` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_service_mnt` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_link_topology_info` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_login_record` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_middleware_info` ADD UNIQUE INDEX `idx_name_version_tenant_env` ( `MIDDLEWARE_NAME`, `MIDDLEWARE_VERSION`, `tenant_id`, `env_code` );
ALTER TABLE `t_middleware_link_relate` ADD UNIQUE INDEX `idx_middleware_tech_link_tenant_env` ( `MIDDLEWARE_ID`,`TECH_LINK_ID`,`tenant_id`,`env_code` );
ALTER TABLE `t_operation_log` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_ops_script_batch_no` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_ops_script_execute_result` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_ops_script_file` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_ops_script_manage` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_performance_base_data` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_performance_criteria_config` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_performance_thread_data` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_performance_thread_stack_data` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE `t_pessure_test_task_activity_config` ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_pradar_zk_config ADD UNIQUE INDEX `idx_path_tenant_env` ( `zk_path`,`tenant_id`,`env_code` );
DROP INDEX `idx_zk_path` on t_pradar_zk_config;
ALTER TABLE t_pressure_machine ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_pressure_machine_statistics ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_pressure_time_record ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_probe ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_quick_access ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_report_application_summary ADD UNIQUE INDEX `unique_idx_report_appliacation_tenant_env` ( `report_id`,`application_name`,`tenant_id`,`env_code` );
DROP index `unique_idx_report_appliacation` on t_report_application_summary ;
ALTER TABLE t_report_bottleneck_interface ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_report_machine ADD UNIQUE INDEX `unique_idx_report_appliacation_machine_tenant_env` ( `report_id`,`application_name`, `machine_ip`,`tenant_id`,`env_code` );
DROP index `unique_report_application_machine` ON t_report_machine ;
ALTER TABLE t_report_machine ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_report_summary ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_scene ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_scene_link_relate ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_scene_manage ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_scene_scheduler_task ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_scene_tag_ref ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_script_debug ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_script_execute_result ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_script_file_ref ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_script_manage ADD UNIQUE INDEX `unique_name_tenant_env` ( `name`,`tenant_id`,`env_code` );
DROP index `name` ON t_script_manage ;
ALTER TABLE t_script_manage_deploy ADD UNIQUE INDEX `unique_name_version_tenant_env` ( `name`,`script_version`,`tenant_id`,`env_code` );
DROP index `name_version` ON t_script_manage_deploy ;
ALTER TABLE t_script_tag_ref ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_shadow_job_config ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_shadow_mq_consumer ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_shadow_table_datasource ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_tag_manage ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_trace_manage ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_trace_manage_deploy ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_trace_node_info ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_tro_authority ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_tro_dbresource ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_tro_dept ADD INDEX `idx_tenant` ( `tenant_id` );
ALTER TABLE t_tro_role ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_tro_role_user_relation ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_tro_trace_entry ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_tro_user
    ADD INDEX `idx_tenant` ( `tenant_id`),
    ADD UNIQUE KEY `idx_name_tenant_id`(`tenant_id`,`name` );
ALTER TABLE t_tro_user_dept_relation ADD INDEX `idx_tenant` ( `tenant_id`);
ALTER TABLE t_upload_interface_data ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_white_list ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_whitelist_effective_app ADD INDEX `idx_tenant_env` ( `tenant_id`,`env_code` );
ALTER TABLE t_fast_debug_stack_info ADD INDEX `idx_tenant_env` (`tenant_id`,`env_code` );
-- 调试工具结果
ALTER TABLE t_fast_debug_result ADD INDEX `idx_trace_id` ( `trace_id`);
ALTER TABLE t_fast_debug_result ADD INDEX `idx_config_Id` (`config_Id`);
-- 异常信息
ALTER TABLE `t_exception_info` ADD INDEX `idx_code` ( `code`);
ALTER TABLE `t_exception_info` ADD INDEX `idx_agent_code` ( `agent_code`);

