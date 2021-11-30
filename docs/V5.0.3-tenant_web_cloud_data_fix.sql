-- 需要查cloud数据 订正

-- 场景表
ALTER TABLE trodb_cloud.t_scene_manage
    ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1',
    ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识';
-- 报告表
ALTER TABLE trodb_cloud.t_report
    ADD COLUMN `tenant_id` bigint(0) NULL DEFAULT 1 COMMENT '租户 id, 默认 1',
    ADD COLUMN `env_code` varchar(100) NULL DEFAULT 'test' COMMENT '环境标识';

-- T3出行
update trodb_cloud.t_scene_manage set tenant_id = 3           , env_code = 'test' where  customer_id = 4;
update trodb_cloud.t_report       set tenant_id = 3           , env_code = 'test' where  customer_id = 4;
-- 联动云
update trodb_cloud.t_scene_manage set tenant_id = 10          , env_code = 'test' where  customer_id = 9;
update trodb_cloud.t_report       set tenant_id = 10          , env_code = 'test' where  customer_id = 9;
-- 老板姓
update trodb_cloud.t_scene_manage set tenant_id = 5           , env_code = 'prod' where  customer_id in (12,13);
update trodb_cloud.t_report       set tenant_id = 5           , env_code = 'prod' where  customer_id in (12,13);
-- 默认
update trodb_cloud.t_scene_manage set tenant_id = customer_id , env_code = 'test' where customer_id = 1;
update trodb_cloud.t_report       set tenant_id = customer_id , env_code = 'test' where customer_id = 1;
-- 测试、T3出行、lbx-pressure、申通快递、联动云
update trodb_cloud.t_scene_manage set tenant_id = customer_id , env_code = 'prod' where customer_id in (2,3,5,7,10,24);
update trodb_cloud.t_report       set tenant_id = customer_id , env_code = 'prod' where customer_id in (2,3,5,7,10,24);
--

-- 更新修正报告相关
-- 更新 t_report_application_summary |t_report_machine |t_report_summary --
-- 默认租户 --
-- 找出默认租户用户id
-- 找出默认租户的报告id trodb_cloud找

-- 更新报告 t_report_application_summary
update t_report_application_summary set tenant_id = 1 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE tenant_id = 1));
update t_report_machine set tenant_id = 1 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE tenant_id = 1));
update t_report_summary set tenant_id = 1 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE tenant_id = 1));
update t_trace_manage set tenant_id = 1 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE tenant_id = 1));

-- T3出行
-- 测试环境 cloud查找
update t_report_application_summary t set t.tenant_id = 3, t.env_code = 'test' WHERE t.report_id in (SELECT id from trodb_cloud.t_report a WHERE a.user_id = 4);
update t_report_machine set tenant_id = 3 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report a WHERE a.user_id = 4);
update t_report_summary set tenant_id = 3 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report a WHERE a.user_id = 4);
update t_trace_manage set tenant_id = 3 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report a WHERE a.user_id = 4);

-- 生产环境 cloud查找
update t_report_application_summary set tenant_id = 3 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 3);
update t_report_machine set tenant_id = 3 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 3);
update t_report_summary set tenant_id = 3 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 3);
update t_trace_manage set tenant_id = 3 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 3);




-- 老百姓
-- 生产环境 cloud查找
update t_report_application_summary set tenant_id = 5 and env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(5,12,13));
update t_report_machine set tenant_id = 5 and env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(5,12,13));
update t_report_summary set tenant_id = 5 and env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(5,12,13));
update t_trace_manage set tenant_id = 5 and env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(5,12,13));



-- 申通快递
-- 测试环境 cloud查找
update t_report_application_summary set tenant_id = 7 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE name like 'sto%' and id <> 7));

update t_report_machine set tenant_id = 7 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE name like 'sto%' and id <> 7));

update t_report_summary set tenant_id = 7 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE name like 'sto%' and id <> 7));

update t_trace_manage set tenant_id = 7 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE name like 'sto%' and id <> 7));


-- 生产环境 cloud查找
update t_report_application_summary set tenant_id = 7 and env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE name like 'sto%' and id = 7));
update t_report_machine set tenant_id = 7 and env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE name like 'sto%' and id = 7));
update t_report_summary set tenant_id = 7 and env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE name like 'sto%' and id = 7));
update t_trace_manage set tenant_id = 7 and env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id in(SELECT id from t_tro_user WHERE name like 'sto%' and id = 7));

-- 联动云
-- 测试环境 cloud查找
update t_report_application_summary set tenant_id = 10 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 9);
update t_report_machine set tenant_id = 10 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 9);
update t_report_summary set tenant_id = 10 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 9);
update t_trace_manage set tenant_id = 10 , env_code = 'test' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 9);


-- 生产环境 cloud查找
update t_report_application_summary set tenant_id = 10 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 10);
update t_report_machine set tenant_id = 10 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 10);
update t_report_summary set tenant_id = 10 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 10);
update t_trace_manage set tenant_id = 10 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 10);

-- 爱库存
-- 生产环境 cloud查找
update t_report_application_summary set tenant_id = 24 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 24);
update t_report_machine set tenant_id = 24 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 24);
update t_report_summary set tenant_id = 24 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 24);
update t_trace_manage set tenant_id = 24 , env_code = 'prod' WHERE report_id in (SELECT id from trodb_cloud.t_report WHERE user_id = 24);
