CREATE TABLE IF NOT EXISTS `t_application_focus` (
                                       `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
                                       `app_name` varchar(50) CHARACTER SET utf8 NOT NULL COMMENT '应用名称',
                                       `interface_name` varchar(256) COLLATE utf8mb4_bin NOT NULL COMMENT '服务名称',
                                       `focus` tinyint(1) NOT NULL COMMENT '是否关注',
                                       PRIMARY KEY (`id`),
                                       KEY `focus_index` (`app_name`,`interface_name`) USING BTREE COMMENT '关注索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;