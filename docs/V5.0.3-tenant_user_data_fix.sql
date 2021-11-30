-- 查出租户
SELECT *
from t_tro_user
WHERE id = customer_id;
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
    (`tenant_id`, `env_code`, `env_name`, `desc`, `is_default`)
VALUES (1, 'test', '测试环境', '', 1),
       (1, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作', 0),
       (2, 'test', '测试环境', '', 1),
       (2, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作', 0),
       (3, 'test', '测试环境', '', 1),
       (3, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作', 0),
       (5, 'test', '测试环境', '', 1),
       (5, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作', 0),
       (7, 'test', '测试环境', '', 1),
       (7, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作', 0),
       (10, 'test', '测试环境', '', 1),
       (10, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作', 0),
       (24, 'test', '测试环境', '', 1),
       (24, 'prod', '生产环境', '当前环境为生产环境，请谨慎操作', 0);
SELECT *
from t_tro_user;
-- 更正t_tro_user
update t_tro_user
set tenant_id = customer_id
where customer_id is not null;

SELECT *
from t_tenant_config;

-- 默认租户
INSERT INTO t_tro_user
    (`name`, `nick`, `salt`, `password`, `status`, `user_type`, `model`, `role`, `tenant_id`)
VALUES ('admin@default', 'default', '$2a$10$MqkhiWRgANNXPlhM6uJdBe', '$2a$10$MqkhiWRgANNXPlhM6uJdBe0pT.SUmd9JdK9eIFp9CbcZSlOiAWL7S', 0, 2, 0, 0, 1);
-- 测试租户
INSERT INTO t_tro_user
    (`name`, `nick`, `salt`, `password`, `status`, `user_type`, `model`, `role`, `tenant_id`)
VALUES ('admin@test', '测试', '$2a$10$MqkhiWRgANNXPlhM6uJdBe', '$2a$10$MqkhiWRgANNXPlhM6uJdBe0pT.SUmd9JdK9eIFp9CbcZSlOiAWL7S', 0, 2, 0, 0, 2);

-- T3出行出现
update t_tro_user
set tenant_id = 3
WHERE name like 'T3Trip%';
update t_tro_user
set user_type = 2
WHERE id = 3;
-- 老百姓
update t_tro_user
set tenant_id = 5
WHERE name like 'lbx%';
update t_tro_user
set user_type = 2
WHERE id = 5;
-- 申通账号
update t_tro_user
set tenant_id = 7
WHERE name like 'sto%';
update t_tro_user
set user_type = 2
WHERE id = 7;
-- 联动云
update t_tro_user
set tenant_id = 10
WHERE name like 'liandongyun%';
update t_tro_user
set user_type = 2
WHERE id = 10;
-- 爱库存
update t_tro_user
set user_type = 2
WHERE id = 24;

-- 修正部门表
select *
from t_tro_dept;
SELECT *
from t_tro_user_dept_relation;
-- 部门不区分环境
update t_tro_user_dept_relation t1 join t_tro_user t2 on t1.user_id = t2.id
set t1.tenant_id = t2.tenant_id;
update t_tro_dept t1 join (SELECT DISTINCT dept_id, tenant_id FROM t_tro_user_dept_relation) t2 on t1.id = t2.dept_id
set t1.tenant_id = t2.tenant_id;
-- 修正部分根用户
update t_tro_dept t1 join (SELECT t2.parent_id, t2.tenant_id FROM t_tro_dept t2 WHERE t2.parent_id is not null) a
    on t1.tenant_id = a.tenant_id
set t1.tenant_id = a.tenant_id
WHERE t1.parent_id is null;


-- 角色 区分 环境  t_tro_authority
select GROUP_CONCAT(id)
from t_tenant_info;
update t_tro_role_user_relation t1 join t_tro_user t2 on t1.user_id = t2.id
set t1.tenant_id = t2.tenant_id,
    t1.env_code  = 'prod'
WHERE t1.user_id in (2, 3, 5, 7, 10, 24);

SELECT *
from t_tro_role_user_relation
WHERE user_id in (10, 9);
-- 默认租户
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id
set t1.tenant_id = t2.tenant_id,
    t1.env_code  = 'test'
WHERE t1.tenant_id = 1;

-- T3出行
update t_tro_role_user_relation t1 join t_tro_user t2 on t1.user_id = t2.id
set t1.tenant_id = t2.tenant_id,
    t1.env_code  = 'test'
WHERE t1.user_id = 4;

-- 老板姓
update t_tro_role_user_relation t1 join t_tro_user t2 on t1.user_id = t2.id
set t1.tenant_id = t2.tenant_id,
    t1.env_code  = 'prod'
WHERE t1.user_id in (12, 13);

-- 联动云
update t_tro_role_user_relation t1 left join t_tro_user t2 on t1.user_id = t2.id
set t1.tenant_id = t2.tenant_id,
    t1.env_code  = 'test'
WHERE t1.user_id = 9;

-- 申通客户
SELECT GROUP_CONCAT(id)
FROM t_tro_user
WHERE name like 'sto%'
  and tenant_id != customer_id;
update t_tro_role_user_relation t1 join t_tro_user t2 on t1.user_id = t2.id
set t1.tenant_id = t2.tenant_id,
    t1.env_code  = 'test'
WHERE t1.user_id in (8, 17, 18, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33);

SELECT *
from t_tro_role_user_relation;

SELECT *
from t_tro_role_user_relation;
-- 把原有角色全部赋予 默认租户 test
update t_tro_role
set tenant_id =1;

SELECT *
from t_tro_role;
-- 找到相关角色
SELECT t1.id, t1.role_id, t1.user_id, t1.tenant_id, t1.env_code, t2.id, t2.name
from t_tro_role_user_relation t1
         left join t_tro_role t2 on t1.role_id = t2.id and t1.tenant_id = t2.tenant_id and t1.env_code = t2.env_code
order by tenant_Id;

-- 角色表增加索引
ALTER TABLE t_tro_role
    ADD UNIQUE INDEX `unique_idx_name_tenant_env` (`tenant_id`, `env_code`, `name`) USING BTREE;

-- 一个个更新
-- T3出行 生产 3 测试 4
SELECT *
from t_tro_role_user_relation
WHERE user_id in (3, 4); -- 2
SELECT max(id)
from t_tro_role;
-- 插入两个角色
INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, 'Takin Saas用户', NULL, NULL, 'Takin 使用，不包含 E2E', 0, NULL, NULL, NULL, '2021-08-16 14:37:36', '2021-11-24 13:39:53', 0, 3, 'prod');
INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, 'Takin Saas用户', NULL, NULL, 'Takin 使用，不包含 E2E', 0, NULL, NULL, NULL, '2021-08-16 14:37:36', '2021-11-24 13:39:53', 0, 3, 'test');

SELECT *
from t_tro_role_user_relation
WHERE user_id in (3, 4);
-- 订正语句
update t_tro_role_user_relation t1 join t_tro_role t2 on t1.env_code = t2.env_code
set t1.role_id = t2.id
WHERE t1.user_id in (3, 4)
  and t2.tenant_id = 3;
--
SELECT *
from t_tro_authority;
-- 查询
SELECT role_id, resource_id, action, status, scope, 3 as tenant_id, 'test' as env_code
from t_tro_authority
WHERE role_id = 2
UNION ALL
SELECT role_id, resource_id, action, status, scope, 3 as tenant_id, 'prod' as env_code
from t_tro_authority
WHERE role_id = 2;

INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '4', '[2, 3, 4]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '5', '[2, 3, 4]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '6', '[2, 3, 4, 6]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '368', '[2, 3, 4, 7]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '9', '[2, 3, 4, 5]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '10', '[]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '413', '[3]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '12', '[6]', 0, NULL, 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '13', '[6]', 0, NULL, 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '14', '[2, 3, 4, 6]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '15', '[2, 3, 4]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '403', '[2, 3, 4]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '404', '[3]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '402', '[]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '401', '[2, 3, 4, 5]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '400', '[]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '501', '[]', 0, '[]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '307', '[]', 0, NULL, 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '16', '[]', 0, NULL, 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '412', '[2, 3, 4]', 1, NULL, 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '405', '[2, 3, 5]', 1, NULL, 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '406', '[2]', 1, NULL, 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '407', '[2, 3, 5, 6]', 1, NULL, 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '408', '[2, 3, 5, 6]', 1, NULL, 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '409', '[3]', 0, NULL, 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '503', '[2, 7]', 0, '[]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '504', '[3, 4]', 0, '[]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '506', '[]', 0, '[]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '507', '[]', 0, '[]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '508', '[]', 0, '[]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '509', '[]', 0, '[]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '511', '[]', 0, '[3]', 3, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '4', '[2, 3, 4]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '5', '[2, 3, 4]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '6', '[2, 3, 4, 6]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '368', '[2, 3, 4, 7]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '9', '[2, 3, 4, 5]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '10', '[]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '413', '[3]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '12', '[6]', 0, NULL, 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '13', '[6]', 0, NULL, 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '14', '[2, 3, 4, 6]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '15', '[2, 3, 4]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '403', '[2, 3, 4]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '404', '[3]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '402', '[]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '401', '[2, 3, 4, 5]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '400', '[]', 0, '[3]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '501', '[]', 0, '[]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '307', '[]', 0, NULL, 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '16', '[]', 0, NULL, 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '412', '[2, 3, 4]', 1, NULL, 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '405', '[2, 3, 5]', 1, NULL, 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '406', '[2]', 1, NULL, 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '407', '[2, 3, 5, 6]', 1, NULL, 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '408', '[2, 3, 5, 6]', 1, NULL, 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '409', '[3]', 0, NULL, 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '503', '[2, 7]', 0, '[]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '504', '[3, 4]', 0, '[]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '506', '[]', 0, '[]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '507', '[]', 0, '[]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '508', '[]', 0, '[]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '509', '[]', 0, '[]', 3, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '511', '[]', 0, '[3]', 3, 'prod');

-- 订正语句
update t_tro_authority t1 join t_tro_role t2 on t1.env_code = t2.env_code
set t1.role_id = t2.id
WHERE t1.role_id = 2
  and t2.tenant_id = 3
  and t1.tenant_id = 3;


-- 老百姓 5 12 13 生产

SELECT *
from t_tro_role_user_relation
WHERE user_id in (5, 12, 13); -- 6,7
SELECT *
from t_tro_role
WHERE id in (6, 7);
-- 插入两个角色
INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, '老百姓主账号', NULL, NULL, '老百姓领导和测试专用', 0, NULL, NULL, NULL, '2021-09-18 16:30:05', '2021-11-24 13:39:53', 0, 5, 'prod');
INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, '老百姓运维账号', NULL, NULL, '老百姓运维专用', 0, NULL, NULL, NULL, '2021-09-18 16:30:25', '2021-11-24 13:39:53', 0, 5, 'prod');

SELECT *
from t_tro_role
WHERE tenant_id = 5;
SELECT *
from t_tro_role_user_relation
WHERE user_id in (5, 12, 13);
-- 订正语句
update t_tro_role_user_relation t1 join (SELECT t2.id as temp_Id, t3.id, t3.tenant_Id
                                         from t_tro_role t2,
                                              t_tro_role t3
                                         WHERE t2.name = t3.name) a
    on t1.role_Id = a.temp_Id
set t1.role_id = a.id
WHERE t1.user_id in (5, 12, 13)
  and a.tenant_id = 5;
--
SELECT *
from t_tro_role_user_relation
WHERE tenant_id = 5;
-- 查询

SELECT role_id, resource_id, action, status, scope, 5 as tenant_id, 'prod' as env_code
from t_tro_authority
WHERE role_id in (6, 7);

INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '4', '[2, 3, 4]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '5', '[2, 3, 4]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '501', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '6', '[2, 3, 4, 6]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '368', '[2, 3, 4, 7]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '9', '[2, 3, 4, 5]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '10', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '16', '[]', 0, NULL, 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '401', '[2, 3, 4, 5]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '402', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '411', '[2, 3, 4]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '503', '[2, 7]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '506', '[7]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '507', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('6', '508', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '4', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '5', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '501', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '6', '[2, 3, 4, 6]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '9', '[2, 3, 4, 5]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '10', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '16', '[]', 0, NULL, 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '401', '[2, 3, 4, 5]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '402', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '503', '[2, 7]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '506', '[7]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '507', '[]', 0, '[3]', 5, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('7', '508', '[]', 0, '[3]', 5, 'prod');

-- 订正语句
SELECT *
from t_tro_authority
WHERE tenant_id = 5;

update t_tro_authority t1 join (SELECT t2.id as temp_Id, t3.id, t3.tenant_Id
                                from t_tro_role t2,
                                     t_tro_role t3
                                WHERE t2.name = t3.name) a
    on t1.role_id = a.temp_Id
set t1.role_id = a.id
WHERE t1.role_id = 6
  and a.tenant_id = 5
  and a.tenant_id = 5;


-- 申通账号 生产 7 测试 8,17,18,19,20,21,22,23,25,26,27,28,29,30,31,32,33
SELECT *
FROM t_tro_user
WHERE name like 'sto%';
SELECT GROUP_CONCAT(id)
FROM t_tro_user
WHERE name like 'sto%'
  and customer_id != tenant_id;

SELECT *
from t_tro_role_user_relation
WHERE user_id in (7, 8, 17, 18, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33);
-- 3,9
-- 插入两个角色
SELECT *
from t_tro_role
WHERE id in (3, 9);

INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, 'Takin Saas 全功能用户', NULL, NULL, 'takin使用，包含E2E', 0, NULL, NULL, NULL, '2021-09-06 19:33:58', '2021-11-24 13:39:53', 0, 7, 'test');
INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, '申通部门角色', NULL, NULL, '申通单个部门角色', 0, NULL, NULL, NULL, '2021-10-11 16:06:31', '2021-11-24 13:39:53', 0, 7, 'test');

INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, 'Takin Saas 全功能用户', NULL, NULL, 'takin使用，包含E2E', 0, NULL, NULL, NULL, '2021-09-06 19:33:58', '2021-11-24 13:39:53', 0, 7, 'prod');
INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, '申通部门角色', NULL, NULL, '申通单个部门角色', 0, NULL, NULL, NULL, '2021-10-11 16:06:31', '2021-11-24 13:39:53', 0, 7, 'prod');

SELECT *
from t_tro_role_user_relation
WHERE user_id in (7, 8, 17, 18, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33);
-- 订正语句
update t_tro_role_user_relation t1 join (SELECT t2.id as temp_Id, t3.id, t3.tenant_Id, t3.env_code
                                         from t_tro_role t2,
                                              t_tro_role t3
                                         WHERE t2.name = t3.name) a
    on t1.env_code = a.env_code and t1.role_id = a.temp_Id
set t1.role_id = a.id
WHERE t1.user_id in (7, 8, 17, 18, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33)
  and a.tenant_id = 7;
--
SELECT *
from t_tro_authority;
-- 查询
SELECT role_id, resource_id, action, status, scope, 7 as tenant_id, 'test' as env_code
from t_tro_authority
WHERE role_id in (3, 9)
UNION ALL
SELECT role_id, resource_id, action, status, scope, 7 as tenant_id, 'prod' as env_code
from t_tro_authority
WHERE role_id in (3, 9);

INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '4', '[2, 3, 4]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '5', '[2, 3, 4]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '501', '[]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '6', '[2, 3, 4, 6]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '368', '[2, 3, 4, 7]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '9', '[2, 3, 4, 5]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '10', '[]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '413', '[]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '367', '[2, 3, 4]', 1, NULL, 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '12', '[6]', 0, NULL, 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '13', '[6]', 0, NULL, 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '14', '[2, 3, 4, 6]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '15', '[2, 3, 4]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '307', '[]', 0, NULL, 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '403', '[2, 3, 4]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '404', '[3]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '16', '[]', 0, NULL, 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '401', '[2, 3, 4, 5]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '402', '[]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '412', '[2, 3, 4]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '405', '[2, 3, 4, 5]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '406', '[2]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '407', '[2, 3, 5, 6]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '408', '[2, 3, 5, 6]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '409', '[3]', 0, NULL, 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '503', '[2, 7]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '504', '[3, 4]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '506', '[7]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '507', '[]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '508', '[]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '4', '[2, 3, 4]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '5', '[2, 3, 4]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '501', '[]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '412', '[2, 3, 4]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '405', '[2, 3, 4, 5]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '406', '[2]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '407', '[2, 3, 5, 6]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '408', '[2, 3, 5, 6]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '409', '[3]', 0, NULL, 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '506', '[2]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '6', '[2, 3, 4, 6]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '507', '[]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '508', '[]', 0, '[3]', 7, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '4', '[2, 3, 4]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '5', '[2, 3, 4]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '501', '[]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '6', '[2, 3, 4, 6]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '368', '[2, 3, 4, 7]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '9', '[2, 3, 4, 5]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '10', '[]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '413', '[]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '367', '[2, 3, 4]', 1, NULL, 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '12', '[6]', 0, NULL, 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '13', '[6]', 0, NULL, 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '14', '[2, 3, 4, 6]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '15', '[2, 3, 4]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '307', '[]', 0, NULL, 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '403', '[2, 3, 4]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '404', '[3]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '16', '[]', 0, NULL, 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '401', '[2, 3, 4, 5]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '402', '[]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '412', '[2, 3, 4]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '405', '[2, 3, 4, 5]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '406', '[2]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '407', '[2, 3, 5, 6]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '408', '[2, 3, 5, 6]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '409', '[3]', 0, NULL, 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '503', '[2, 7]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '504', '[3, 4]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '506', '[7]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '507', '[]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('3', '508', '[]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '4', '[2, 3, 4]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '5', '[2, 3, 4]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '501', '[]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '412', '[2, 3, 4]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '405', '[2, 3, 4, 5]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '406', '[2]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '407', '[2, 3, 5, 6]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '408', '[2, 3, 5, 6]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '409', '[3]', 0, NULL, 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '506', '[2]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '6', '[2, 3, 4, 6]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '507', '[]', 0, '[3]', 7, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('9', '508', '[]', 0, '[3]', 7, 'prod');
-- 订正语句

update t_tro_authority t1 left join (SELECT t2.id as temp_Id, t3.id, t3.tenant_Id, t3.env_code
                                     from t_tro_role t2,
                                          t_tro_role t3
                                     WHERE t2.name = t3.name) a
    on t1.env_code = a.env_code and t1.role_id = a.temp_id
set t1.role_id = a.id
WHERE t1.role_id in (3, 9)
  and a.tenant_id = 7
  and t1.tenant_id = 7;


-- 联动云
SELECT *
FROM t_tro_user
WHERE name like 'liandongyun%';
SELECT GROUP_CONCAT(id)
FROM t_tro_user
WHERE name like 'liandongyun%'
  and customer_id != tenant_id;

SELECT *
from t_tro_role_user_relation
WHERE user_id in (9, 10);
-- 2
-- 插入两个角色
SELECT *
from t_tro_role
WHERE id = 2;

INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, 'Takin Saas用户', NULL, NULL, 'Takin 使用，不包含 E2E', 0, NULL, NULL, NULL, '2021-08-16 14:37:36', '2021-11-24 13:39:53', 0, 10, 'prod');
INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, 'Takin Saas用户', NULL, NULL, 'Takin 使用，不包含 E2E', 0, NULL, NULL, NULL, '2021-08-16 14:37:36', '2021-11-24 13:39:53', 0, 10, 'test');


SELECT *
from t_tro_role_user_relation
WHERE user_id in (9, 10);
-- 订正语句
update t_tro_role_user_relation t1 join (SELECT t2.id as temp_Id, t3.id, t3.tenant_Id, t3.env_code
                                         from t_tro_role t2,
                                              t_tro_role t3
                                         WHERE t2.name = t3.name) a
    on t1.env_code = a.env_code and t1.role_id = a.temp_Id
set t1.role_id = a.id
WHERE t1.user_id in (9, 10)
  and a.tenant_id = 10;
--
SELECT *
from t_tro_authority;
-- 查询
SELECT role_id, resource_id, action, status, scope, 10 as tenant_id, 'test' as env_code
from t_tro_authority
WHERE role_id = 2
UNION ALL
SELECT role_id, resource_id, action, status, scope, 10 as tenant_id, 'prod' as env_code
from t_tro_authority
WHERE role_id = 2;

INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '4', '[2, 3, 4]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '5', '[2, 3, 4]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '6', '[2, 3, 4, 6]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '368', '[2, 3, 4, 7]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '9', '[2, 3, 4, 5]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '10', '[]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '413', '[3]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '12', '[6]', 0, NULL, 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '13', '[6]', 0, NULL, 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '14', '[2, 3, 4, 6]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '15', '[2, 3, 4]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '403', '[2, 3, 4]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '404', '[3]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '402', '[]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '401', '[2, 3, 4, 5]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '400', '[]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '501', '[]', 0, '[]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '307', '[]', 0, NULL, 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '16', '[]', 0, NULL, 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '412', '[2, 3, 4]', 1, NULL, 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '405', '[2, 3, 5]', 1, NULL, 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '406', '[2]', 1, NULL, 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '407', '[2, 3, 5, 6]', 1, NULL, 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '408', '[2, 3, 5, 6]', 1, NULL, 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '409', '[3]', 0, NULL, 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '503', '[2, 7]', 0, '[]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '504', '[3, 4]', 0, '[]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '506', '[]', 0, '[]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '507', '[]', 0, '[]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '508', '[]', 0, '[]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '509', '[]', 0, '[]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '511', '[]', 0, '[3]', 10, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '4', '[2, 3, 4]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '5', '[2, 3, 4]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '6', '[2, 3, 4, 6]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '368', '[2, 3, 4, 7]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '9', '[2, 3, 4, 5]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '10', '[]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '413', '[3]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '12', '[6]', 0, NULL, 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '13', '[6]', 0, NULL, 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '14', '[2, 3, 4, 6]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '15', '[2, 3, 4]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '403', '[2, 3, 4]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '404', '[3]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '402', '[]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '401', '[2, 3, 4, 5]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '400', '[]', 0, '[3]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '501', '[]', 0, '[]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '307', '[]', 0, NULL, 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '16', '[]', 0, NULL, 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '412', '[2, 3, 4]', 1, NULL, 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '405', '[2, 3, 5]', 1, NULL, 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '406', '[2]', 1, NULL, 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '407', '[2, 3, 5, 6]', 1, NULL, 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '408', '[2, 3, 5, 6]', 1, NULL, 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '409', '[3]', 0, NULL, 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '503', '[2, 7]', 0, '[]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '504', '[3, 4]', 0, '[]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '506', '[]', 0, '[]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '507', '[]', 0, '[]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '508', '[]', 0, '[]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '509', '[]', 0, '[]', 10, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '511', '[]', 0, '[3]', 10, 'prod');

-- 订正语句

update t_tro_authority t1 left join (SELECT t2.id as temp_Id, t3.id, t3.tenant_Id, t3.env_code
                                     from t_tro_role t2,
                                          t_tro_role t3
                                     WHERE t2.name = t3.name) a
    on t1.env_code = a.env_code and t1.role_id = a.temp_id
set t1.role_id = a.id
WHERE t1.role_id = 2
  and a.tenant_id = 10
  and t1.tenant_id = 10;


-- 爱库存 --
SELECT *
FROM t_tro_user
WHERE name like 'akc%';

SELECT *
from t_tro_role_user_relation
WHERE user_id in (24);
-- 2
-- 插入两个角色
SELECT *
from t_tro_role
WHERE id = 2;

INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, 'Takin Saas用户', NULL, NULL, 'Takin 使用，不包含 E2E', 0, NULL, NULL, NULL, '2021-08-16 14:37:36', '2021-11-24 13:39:53', 0, 24, 'prod');
INSERT INTO t_tro_role (`application_id`, `name`, `alias`, `code`, `description`, `status`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `tenant_id`, `env_code`)
VALUES (NULL, 'Takin Saas用户', NULL, NULL, 'Takin 使用，不包含 E2E', 0, NULL, NULL, NULL, '2021-08-16 14:37:36', '2021-11-24 13:39:53', 0, 24, 'test');


SELECT *
from t_tro_role_user_relation
WHERE user_id in (24);
-- 订正语句
update t_tro_role_user_relation t1 join (SELECT t2.id as temp_Id, t3.id, t3.tenant_Id, t3.env_code
                                         from t_tro_role t2,
                                              t_tro_role t3
                                         WHERE t2.name = t3.name) a
    on t1.env_code = a.env_code and t1.role_id = a.temp_Id
set t1.role_id = a.id
WHERE t1.user_id in (24)
  and a.tenant_id = 24;
--
SELECT *
from t_tro_authority;
-- 查询
SELECT role_id, resource_id, action, status, scope, 24 as tenant_id, 'test' as env_code
from t_tro_authority
WHERE role_id = 2
UNION ALL
SELECT role_id, resource_id, action, status, scope, 24 as tenant_id, 'prod' as env_code
from t_tro_authority
WHERE role_id = 2;

INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '4', '[2, 3, 4]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '5', '[2, 3, 4]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '6', '[2, 3, 4, 6]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '368', '[2, 3, 4, 7]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '9', '[2, 3, 4, 5]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '10', '[]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '413', '[3]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '12', '[6]', 0, NULL, 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '13', '[6]', 0, NULL, 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '14', '[2, 3, 4, 6]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '15', '[2, 3, 4]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '403', '[2, 3, 4]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '404', '[3]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '402', '[]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '401', '[2, 3, 4, 5]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '400', '[]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '501', '[]', 0, '[]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '307', '[]', 0, NULL, 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '16', '[]', 0, NULL, 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '412', '[2, 3, 4]', 1, NULL, 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '405', '[2, 3, 5]', 1, NULL, 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '406', '[2]', 1, NULL, 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '407', '[2, 3, 5, 6]', 1, NULL, 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '408', '[2, 3, 5, 6]', 1, NULL, 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '409', '[3]', 0, NULL, 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '503', '[2, 7]', 0, '[]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '504', '[3, 4]', 0, '[]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '506', '[]', 0, '[]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '507', '[]', 0, '[]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '508', '[]', 0, '[]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '509', '[]', 0, '[]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '511', '[]', 0, '[3]', 24, 'test');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '4', '[2, 3, 4]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '5', '[2, 3, 4]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '6', '[2, 3, 4, 6]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '368', '[2, 3, 4, 7]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '9', '[2, 3, 4, 5]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '10', '[]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '413', '[3]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '12', '[6]', 0, NULL, 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '13', '[6]', 0, NULL, 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '14', '[2, 3, 4, 6]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '15', '[2, 3, 4]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '403', '[2, 3, 4]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '404', '[3]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '402', '[]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '401', '[2, 3, 4, 5]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '400', '[]', 0, '[3]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '501', '[]', 0, '[]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '307', '[]', 0, NULL, 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '16', '[]', 0, NULL, 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '412', '[2, 3, 4]', 1, NULL, 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '405', '[2, 3, 5]', 1, NULL, 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '406', '[2]', 1, NULL, 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '407', '[2, 3, 5, 6]', 1, NULL, 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '408', '[2, 3, 5, 6]', 1, NULL, 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '409', '[3]', 0, NULL, 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '503', '[2, 7]', 0, '[]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '504', '[3, 4]', 0, '[]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '506', '[]', 0, '[]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '507', '[]', 0, '[]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '508', '[]', 0, '[]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '509', '[]', 0, '[]', 24, 'prod');
INSERT INTO t_tro_authority (`role_id`, `resource_id`, `action`, `status`, `scope`, `tenant_id`, `env_code`)
VALUES ('2', '511', '[]', 0, '[3]', 24, 'prod');

-- 订正语句

update t_tro_authority t1 left join (SELECT t2.id as temp_Id, t3.id, t3.tenant_Id, t3.env_code
                                     from t_tro_role t2,
                                          t_tro_role t3
                                     WHERE t2.name = t3.name) a
    on t1.env_code = a.env_code and t1.role_id = a.temp_id
set t1.role_id = a.id
WHERE t1.role_id = 2
  and a.tenant_id = 24
  and t1.tenant_id = 24

