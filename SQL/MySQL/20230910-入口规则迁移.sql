ALTER TABLE `amdb`.`t_amdb_app`
    ADD COLUMN `api_rules` longtext NULL COMMENT '应用入口规则合集' AFTER `ext`;