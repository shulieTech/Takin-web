alter table t_tro_user
    add column `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id';
alter table t_tro_user
    add index `tenant_id` ( `tenant_id` );

ALTER TABLE `t_dictionary_data`
    ADD COLUMN `tenant_id` bigint(20) NOT NULL COMMENT '租户ID' AFTER `VERSION_NO`,
ADD COLUMN `env_code` varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`,
DROP
PRIMARY KEY,
ADD PRIMARY KEY (`ID`, `tenant_id`, `env_code`) USING BTREE;


ALTER TABLE `t_base_config`
    ADD COLUMN `tenant_id` bigint(20) NOT NULL COMMENT '租户ID' AFTER `UPDATE_TIME`,
ADD COLUMN `env_code` varchar(20) NOT NULL COMMENT '环境变量' AFTER `tenant_id`,
DROP
PRIMARY KEY,
ADD PRIMARY KEY (`CONFIG_CODE`, `tenant_id`, `env_code`) USING BTREE;

ALTER TABLE `t_base_config`
    ADD INDEX `unique_idx_env_code_tenant_id`(`env_code`, `tenant_id`) USING BTREE;

ALTER TABLE `t_dictionary_data`
    ADD INDEX `unique_idx_env_code_tenant_id`(`env_code`, `tenant_id`) USING BTREE;