ALTER TABLE `t_tenant_info`
    MODIFY COLUMN `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态 0: 停用 1:正常 2：试用 3：欠费 ';

INSERT IGNORE INTO `t_config_server` (`key`, `value`, `tenant_id`, `env_code`, `tenant_app_key`, `is_tenant`, `is_global`, `edition`, `gmt_create`, `gmt_update`, `is_deleted`) VALUES ('takin.asset.balance.default.try', '10000', -99, 'test', 'default', 1, 1, 2, '2021-12-03 18:22:46', NULL, 0);
INSERT IGNORE INTO `t_config_server` (`key`, `value`, `tenant_id`, `env_code`, `tenant_app_key`, `is_tenant`, `is_global`, `edition`, `gmt_create`, `gmt_update`, `is_deleted`) VALUES ('takin.asset.balance.default.formal', '100000', -99, 'test', 'default', 1, 1, 2, '2021-12-03 18:22:46', NULL, 0);
