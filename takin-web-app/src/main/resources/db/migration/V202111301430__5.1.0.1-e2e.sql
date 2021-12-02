alter table e_patrol_exception_config
    add column service varchar(255) COMMENT '接口名称';
ALTER TABLE `e_patrol_exception_config` DROP INDEX `idx_config`;
ALTER TABLE `e_patrol_exception_config` ADD UNIQUE INDEX `idx_config` ( `tenant_id`, `env_code`, `type_value`,`level_value`, `service`) USING BTREE;