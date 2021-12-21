/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
	IF NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_tro_user' AND column_name='is_super')
	THEN
ALTER TABLE `t_tro_user`
    ADD COLUMN `is_super` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否有超管权限，1=是，0=否，默认0';
END IF;

    IF NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_tenant_info' AND column_name='create_by')
    THEN
ALTER TABLE `t_tenant_info`
    ADD COLUMN `create_by` BIGINT(20) NOT NULL COMMENT '创建者，用户ID';
END IF;
END $$
DELIMITER ;
CALL add_column;

ALTER TABLE `t_tenant_info`
    MODIFY COLUMN `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态 0: 停用 1:正常 2：试用 3：欠费 ';
ALTER TABLE `t_config_server` MODIFY COLUMN `value` VARCHAR(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '配置的值';

BEGIN ;
INSERT IGNORE INTO `t_config_server` (`key`, `value`, `tenant_id`, `env_code`, `tenant_app_key`, `is_tenant`, `is_global`, `edition`, `gmt_create`, `gmt_update`, `is_deleted`) VALUES ('takin.asset.balance.default.try', '10000', -99, 'test', 'default', 0, 1, 2, '2021-12-03 18:22:46', NULL, 0);
INSERT IGNORE INTO `t_config_server` (`key`, `value`, `tenant_id`, `env_code`, `tenant_app_key`, `is_tenant`, `is_global`, `edition`, `gmt_create`, `gmt_update`, `is_deleted`) VALUES ('takin.asset.balance.default.formal', '100000', -99, 'test', 'default', 0, 1, 2, '2021-12-03 18:22:46', NULL, 0);
INSERT IGNORE INTO `t_config_server` (`key`, `value`, `tenant_id`, `env_code`, `tenant_app_key`, `is_tenant`, `is_global`, `edition`, `gmt_create`, `gmt_update`, `is_deleted`) VALUES ('takin.tenant.default.password', 'shulie@2021', -99, 'test', 'default', 0, 1, 2, '2021-12-03 18:22:46', NULL, 0);

UPDATE `t_tro_user` SET  `is_super`= 1 WHERE name='superadmin';

UPDATE t_tro_resource SET is_super=1 WHERE code='flowAccount';
UPDATE t_tro_resource SET is_super=1 WHERE code='configCenter_bigDataConfig';
UPDATE t_tro_resource SET is_super=1 WHERE code='admins_admin';
UPDATE t_tro_resource SET is_super=1 WHERE code='configCenter_middlewareManage';

UPDATE `t_config_server` SET `value` = 'true' WHERE `key` = 'takin.login.dingding.push.enable';
UPDATE `t_config_server` SET `value` = 'https://oapi.dingtalk.com/robot/send?access_token=7ff71ca59b08340b5b07a146b053527eebb4f834856380d2c33f5a1d6fcd3bb8' WHERE `key` = 'takin.login.dingding.push.url';
UPDATE `t_config_server` SET is_tenant=0 WHERE `key` = 'takin.login.dingding.push.enable';
COMMIT ;

