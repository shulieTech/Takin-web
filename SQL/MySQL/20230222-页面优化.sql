ALTER TABLE `trodb`.`t_shadow_mq_consumer`
    ADD COLUMN `customize_topic_group` varchar(1000) NULL COMMENT '自定义topic和group' AFTER `topic_group`;

ALTER TABLE `trodb`.`t_application_ds_manage`
    ADD COLUMN `config_type` tinyint(4) NULL COMMENT '配置类型，0字段配置，1json配置' AFTER `IS_DELETED`;

ALTER TABLE `trodb`.`t_fast_debug_config_info`
    ADD COLUMN `request_address` varchar(255) NULL COMMENT '请求地址' AFTER `request_url`,
ADD COLUMN `request_path` varchar(255) NULL COMMENT '请求path' AFTER `request_address`;

CREATE TABLE `t_application_ds_warn` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                         `app_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '应用名称',
                                         `agent_id` varchar(64) COLLATE utf8_bin NOT NULL COMMENT 'agentId',
                                         `check_interval` int(11) NOT NULL COMMENT '校验时间间隔',
                                         `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态',
                                         `check_time` bigint(20) NOT NULL COMMENT '校验时间',
                                         `check_url` varchar(225) COLLATE utf8_bin NOT NULL COMMENT '校验URL',
                                         `check_user` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '校验用户',
                                         `error_msg` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '校验错误信息',
                                         `host_ip` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '校验错误信息',
                                         `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-否 1-是',
                                         `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                         `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后修改时间',
                                         `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
                                         `env_code` varchar(100) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境标识',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         UNIQUE KEY `unique` (`app_name`,`agent_id`,`check_url`,`check_user`,`tenant_id`,`env_code`) USING BTREE COMMENT '唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=2230 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;