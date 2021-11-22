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
( `name`, `nick`, `salt`, `password`, `status`, `user_type`, `model`, `role`, `tenant_id` );
VALUES ( 'admin@test', '测试', '$2a$10$MqkhiWRgANNXPlhM6uJdBe', '$2a$10$MqkhiWRgANNXPlhM6uJdBe0pT.SUmd9JdK9eIFp9CbcZSlOiAWL7S', 0, 2, 0, 0,2);
-- T3出行出现
update  t_tro_user  set tenant_id = 3 WHERE name like 'T3Trip%';
update  t_tro_user  set user_type = 2  WHERE id = 3;
-- 老百姓
update  t_tro_user  set tenant_id = 5 WHERE name like 'lbx%';
update  t_tro_user  set user_type = 2  WHERE id = 5;
-- 申通账号
update  t_tro_user  set tenant_id = 7 WHERE name like 'sto%';
update  t_tro_user  set user_type = 2  WHERE id = 7;
-- 联动云
update  t_tro_user  set tenant_id = 10 WHERE name like 'liandongyun%';
update  t_tro_user  set user_type = 2  WHERE id = 10;
-- 爱库存
update  t_tro_user  set user_type = 2  WHERE id = 24;

-- 修正部门表

-- 部门不区分环境
update t_tro_user_dept_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id;
update t_tro_dept t1 left join (SELECT DISTINCT  dept_id,tenant_id FROM t_tro_user_dept_relation ) t2 on t1.id = t2.dept_id set t1.tenant_id =  t2.tenant_id;

-- 角色 区分 环境  t_tro_authority
select GROUP_CONCAT(id)  from t_tenant_info;
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code = 'prod'
WHERE t1.user_id in(2,3,5,7,10,24);

-- 默认租户
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code = 'test'
WHERE t1.tenant_id = 1;

-- T3出行
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code = 'test'
WHERE t1.user_id = 4;

-- 老板姓
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code = 'prod';
WHERE t1.user_id in(12,13)

-- 联动云
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code = 'test';
WHERE t1.user_id = 9

SELECT * from t_tro_role_user_relation
SELECT * from t_tro_role

update t_tro_role t1  join (SELECT DISTINCT  role_id,tenant_id,env_code FROM t_tro_role_user_relation ) t2 on t1.id = t2.role_id set t1.tenant_id =  t2.tenant_id
    and t1.env_code =  t2.env_code;

update t_tro_authority t1 left join t_tro_role  t2 on t1.role_id = t2.id set t1.tenant_id =  t2.tenant_id ,t1.env_code =  t2.env_code;
