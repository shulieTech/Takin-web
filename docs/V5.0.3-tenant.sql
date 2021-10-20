CREATE TABLE IF NOT EXISTS `t_tenant_info`
(
    `id`             bigint(20)     NOT NULL AUTO_INCREMENT,
    `key`            varchar(512)  NOT NULL COMMENT '租户key 唯一，同时也是userappkey',
    `name`           varchar(512)   NOT NULL COMMENT '租户名称',
    `nick`           varchar(512)   NOT NULL COMMENT '租户中文名称',
    `code`           varchar(512)   NOT NULL COMMENT '租户代码',
    `config`       	 varchar(1024)  DEFAULT "" COMMENT '租户配置',
    `status`         tinyint(1)     NOT NULL DEFAULT '1' COMMENT '状态 0: 停用 1:正常 2：欠费 3：试用',
    `is_deleted`     tinyint(1)     NOT NULL DEFAULT '0' COMMENT '0: 正常 1： 删除',
    `gmt_create`     datetime       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`     datetime       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_key` (`key`) USING BTREE,
    UNIQUE KEY `unique_code` (`code`) USING BTREE
    ) ENGINE = InnoDB



-- 用户表
alter table t_tro_user
    add column `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id';
alter table t_tro_user
    add index `tenant_id` ( `tenant_id` );
-- 部门表
alter table t_tro_dept
    add column `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id';
alter table t_tro_dept
    add index `tenant_id` ( `tenant_id` );


alter table t_tro_user_dept_relation
    add column `tenant_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '租户id';

alter table t_tro_user_dept_relation
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