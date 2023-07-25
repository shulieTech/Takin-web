ALTER TABLE `trodb`.`t_application_mnt`
    ADD COLUMN `conf_check_status` tinyint(2) NULL DEFAULT 1 COMMENT '配置校验状态，0下发中，1已生效' AFTER `cluster_name`,
ADD COLUMN `conf_check_version` int NULL DEFAULT 0 COMMENT '配置版本，标识配置更新次数' AFTER `conf_check_status`;

