ALTER TABLE `trodb`.`t_config_server`
    ADD COLUMN `user_id` bigint(20) NULL COMMENT '用户ID' AFTER `value`;