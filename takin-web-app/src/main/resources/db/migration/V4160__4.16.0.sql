CREATE TABLE IF NOT EXISTS `t_script_debug`  (
                                           `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
                                           `script_deploy_id` bigint(20) UNSIGNED NOT NULL,
                                           `status` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '调试记录状态, 0 未启动(默认), 1 启动中, 2 请求中, 3 请求结束, 4 调试成功, 5 调试失败',
                                           `failed_type` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '失败类型, 10 启动通知超时失败, 20 漏数失败, 30 非200检查失败, 后面会扩展',
                                           `leak_status` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '检查漏数状态, 0:正常;1:漏数;2:未检测;3:检测失败',
                                           `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '备注, 当调试失败时, 有失败信息',
                                           `request_num` int(10) UNSIGNED NULL DEFAULT 1 COMMENT '请求条数, 1-10000',
                                           `cloud_scene_id` bigint(20) UNSIGNED NULL DEFAULT 0 COMMENT '对应的 cloud 场景id',
                                           `cloud_report_id` bigint(20) UNSIGNED NULL DEFAULT 0 COMMENT '对应的 cloud 报告id',
                                           `customer_id` bigint(20) UNSIGNED NOT NULL COMMENT '租户id',
                                           `user_id` bigint(20) NOT NULL COMMENT '租户下的用户id',
                                           `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                           `updated_at` datetime(0) NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                           `is_deleted` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
                                           PRIMARY KEY (`id`) USING BTREE,
                                           INDEX `idx_si`(`script_deploy_id`) USING BTREE COMMENT '租户id, 用户id, 脚本id 联合索引'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '脚本调试表' ROW_FORMAT = Dynamic;