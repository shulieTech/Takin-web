-- 需要查cloud数据 订正

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
