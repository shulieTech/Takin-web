BEGIN;
-- 额外 租户期间增加的表
update t_mq_config_template set tenant_id=customer_id;
update t_application_ds_cache_manage set tenant_id=customer_id;
update t_application_ds_db_manage set tenant_id=customer_id;
update t_application_ds_db_table set tenant_id=customer_id;

-- 记录userid和envCode
CREATE TEMPORARY TABLE IF NOT EXISTS `DATA_FIX_TABLE`
(
    `id`             bigint(20)     NOT NULL AUTO_INCREMENT,
    `user_Id`    bigint(20)     		NOT NULL COMMENT '用户ID',
    `env_code`       varchar(512)   NOT NULL COMMENT '环境代码，测试环境：test,生产环境：prod',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB;
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(1,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(3,'prod');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(4,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(5 ,'prod');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(12,'prod');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(13,'prod');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(7 ,'prod');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(8 ,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(17,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(19,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(20,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(21,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(22,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(23,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(25,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(26,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(27,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(28,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(29,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(30,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(31,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(32,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(33,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(9 ,'test');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(10,'prod');
INSERT INTO DATA_FIX_TABLE(`user_id`,`env_code`)  VALUES(24,'prod');

-- caijy
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
UPDATE t_login_record t1 SET t1.env_code = IFNULL((select env_code from DATA_FIX_TABLE where user_id=t1.user_id),'test');

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

-- t_fast_debug_stack_info
UPDATE t_fast_debug_stack_info t1
    LEFT JOIN t_application_mnt t2 ON t1.app_name = t2.APPLICATION_NAME
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
-- update t_performance_thread_data t set t.tenant_id= IFNULL((select tenant_id from t_application_mnt where APPLICATION_NAME = t.app_name),1);

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

-- 流川
-- t_activity_node_service_state 根据业务活动获得 租户id
UPDATE t_activity_node_service_state a
    LEFT JOIN (SELECT IFNULL(u.tenant_id, 1) tid, b.LINK_ID bid, b.env_code
    FROM t_business_link_manage_table b LEFT JOIN t_tro_user u ON u.id = b.USER_ID) t
ON t.bid = a.activity_id
    SET a.tenant_id = t.tid, a.env_code = t.env_code;

-- t_agent_config 根据 key, 获得用户id, 然后获得 租户id
-- 需要单独给 env_code
UPDATE t_agent_config m LEFT JOIN t_tro_user u ON u.`key` = m.user_app_key SET m.tenant_id = u.tenant_id;

-- t_app_business_table_info 根据用户id查到租户id, 然后赋值
UPDATE t_app_business_table_info m LEFT JOIN t_tro_user u ON u.id = m.user_id SET m.tenant_id = u.customer_id;
UPDATE t_app_business_table_info m LEFT JOIN t_application_mnt u ON u.APPLICATION_ID = m.APPLICATION_ID SET m.env_code = u.env_code;

-- t_app_middleware_info 根据用户id查到租户id, 然后赋值
UPDATE t_app_middleware_info m LEFT JOIN t_tro_user u ON u.id = m.user_id SET m.tenant_id = u.customer_id;
UPDATE t_app_middleware_info m LEFT JOIN t_application_mnt u ON u.APPLICATION_ID = m.APPLICATION_ID SET m.env_code = u.env_code;

UPDATE t_app_remote_call m LEFT JOIN t_tro_user u ON u.id = m.user_id SET m.tenant_id = u.customer_id;
UPDATE t_app_remote_call m LEFT JOIN t_application_mnt u ON u.APPLICATION_ID = m.APPLICATION_ID SET m.env_code = u.env_code;

UPDATE t_app_agent_config_report m LEFT JOIN t_tro_user u ON u.id = m.user_id SET m.tenant_id = u.customer_id;
UPDATE t_app_agent_config_report m LEFT JOIN t_application_mnt u ON u.APPLICATION_ID = m.application_id SET m.env_code = u.env_code;

COMMIT;

