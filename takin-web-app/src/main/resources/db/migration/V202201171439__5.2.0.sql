alter table t_shadow_job_config  modify column name varchar(256);


UPDATE t_tro_resource SET  action = '[7]' WHERE code = 'pressureTestManage_pressureTestReport';