CREATE TABLE IF NOT EXISTS `t_probe` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `version` varchar(30) COLLATE utf8mb4_bin NOT NULL COMMENT '版本号',
    `path` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '上传地址',
    `customer_id` bigint(20) unsigned NOT NULL DEFAULT '1' COMMENT '租户id',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
    PRIMARY KEY (`id`),
    KEY `idx_ci_v` (`customer_id`,`version`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='探针包表'