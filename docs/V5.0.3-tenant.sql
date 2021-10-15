alter table t_tro_user add column `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id';
alter table t_tro_user add index  `tenant_id` ( `tenant_id` )