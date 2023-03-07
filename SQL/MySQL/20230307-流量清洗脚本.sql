CREATE TABLE `t_traffic_record_script` (
                                           `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键自增',
                                           `tenant_code` varchar(128) NOT NULL COMMENT '租户编码',
                                           `app_name` varchar(128) NOT NULL COMMENT '应用名',
                                           `service_name` varchar(128) NOT NULL COMMENT '服务名',
                                           `clear_script` text NOT NULL COMMENT '清洗脚本',
                                           `tenant_id` bigint(20) NOT NULL COMMENT '租户编号',
                                           `env_code` varchar(128) NOT NULL COMMENT '环境编码',
                                           `module_id` varchar(128) NOT NULL COMMENT '模块ID',
                                           `creator` varchar(128) NOT NULL COMMENT '创建者账号',
                                           `creator_name` varchar(128) NOT NULL COMMENT '创建者名称',
                                           `modifier` varchar(128) NOT NULL COMMENT '修改者账号',
                                           `modifier_name` varchar(128) NOT NULL COMMENT '修改者名称',
                                           `gmt_create` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
                                           `gmt_modify` datetime NOT NULL COMMENT '修改时间',
                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT  CHARSET=utf8mb4