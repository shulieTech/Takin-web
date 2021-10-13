
alter table e_patrol_exception_config
    add column customer_id BIGINT(20) COMMENT '租户id';

CREATE TABLE t_activity_node_service_state  (
                                                `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'ID',
                                                `activity_id` bigint(20) NOT NULL COMMENT '业务ID',
                                                `service_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '服务名称',
                                                `owner_app` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '所属应用',
                                                `state` tinyint(1) NOT NULL COMMENT '状态',
                                                PRIMARY KEY (`id`) USING BTREE,
                                                UNIQUE INDEX `id`(`id`) USING BTREE,
                                                INDEX `activity_id`(`activity_id`) USING BTREE,
                                                INDEX `owner_app`(`owner_app`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '业务活动节点服务开关表' ROW_FORMAT = Dynamic;