-- sql from by liuchuan
CREATE TABLE IF NOT EXISTS `t_scene_excluded_application` (
    `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
    `scene_id` bigint(20) NOT NULL COMMENT 'cloud场景id',
    `application_id` bigint(20) NOT NULL COMMENT '应用id',
    `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
    PRIMARY KEY (`id`),
    KEY `idx_scnene_id` (`scene_id`)
    ) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='探针包表';
