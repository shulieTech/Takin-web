CREATE TABLE IF NOT EXISTS `t_app_agent_config_report` (
                                             `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                             `agent_id` varchar(50) NOT NULL DEFAULT '' COMMENT 'AgentId',
                                             `application_id` bigint(50) NOT NULL DEFAULT '0' COMMENT '应用id',
                                             `application_name` varchar(50) NOT NULL DEFAULT '' COMMENT '应用名',
                                             `config_type` tinyint(3) NOT NULL DEFAULT '0' COMMENT '配置类型 0:开关',
                                             `config_key` varchar(50) NOT NULL DEFAULT '' COMMENT '配置KEY',
                                             `config_value` varchar(50) NOT NULL DEFAULT '' COMMENT '配置值',
                                             `commit` varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
                                             `customer_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
                                             `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
                                             `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
                                             PRIMARY KEY (`id`) USING BTREE,
                                             KEY `CUSTOMER_ID_TYPE_INDEX` (`customer_id`,`config_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='agent配置上报详情';