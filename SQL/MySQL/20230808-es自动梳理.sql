ALTER TABLE `trodb`.`t_application_ds_manage`
    ADD COLUMN `is_manual` tinyint(4) NULL DEFAULT 1 COMMENT '是否为手动添加，0否，1是' AFTER `IS_DELETED`;
