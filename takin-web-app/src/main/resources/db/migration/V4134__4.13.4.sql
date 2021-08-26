-- 登录记录表
CREATE TABLE IF NOT EXISTS `t_login_record` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                  `user_name` varchar(512) DEFAULT NULL COMMENT '登录用户',
                                  `ip` varchar(128) DEFAULT NULL COMMENT '登录ip',
                                  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
                                  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
                                  PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;