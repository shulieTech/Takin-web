CREATE TABLE IF NOT EXISTS `t_application_middleware`  (
                                                     `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT,
                                                     `application_id` bigint(20) NOT NULL COMMENT '应用id',
                                                     `application_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '应用名称',
                                                     `artifact_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '项目名称',
                                                     `group_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '项目组织名称',
                                                     `version` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '版本号',
                                                     `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT '' COMMENT '类型, 字符串形式',
                                                     `status` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '状态, 3已支持, 2 未支持, 4 无需支持, 1 未知, 0 无',
                                                     `gmt_create` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
                                                     `gmt_update` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
                                                     `is_deleted` tinyint(3) UNSIGNED NULL DEFAULT 0 COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
                                                     PRIMARY KEY (`id`) USING BTREE,
                                                     INDEX `idx_aid`(`application_id`) USING BTREE,
                                                     INDEX `idx_a_name`(`application_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin COMMENT = '应用中间件' ROW_FORMAT = Dynamic;

CREATE TABLE IF NOT EXISTS `t_middleware_jar` (
                                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                        `name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件中文名称',
                                        `type` varchar(25) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件类型',
                                        `status` tinyint(3) unsigned NOT NULL DEFAULT 0 COMMENT '支持的包状态, 1 已支持, 2 待支持, 3 无需支持, 4 待验证',
                                        `artifact_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件名称',
                                        `group_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件组织名称',
                                        `version` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件版本',
                                        `agv` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT 'artifactId_groupId_version, 做唯一标识,',
                                        `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
                                        `gmt_update` datetime DEFAULT NULL ON UPDATE current_timestamp() COMMENT '更新时间',
                                        `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标记',
                                        `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE KEY `idx_agv` (`agv`) USING BTREE,
                                        KEY `idx_artifact_id` (`artifact_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='中间件包表';

CREATE TABLE IF NOT EXISTS `t_middleware_summary` (
                                        `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                        `name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件中文名称',
                                        `type` varchar(25) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件类型',
                                        `total_num` int(11) NOT NULL DEFAULT 0 COMMENT '中间件总数',
                                        `supported_num` int(11) NOT NULL DEFAULT 0 COMMENT '已支持数量',
                                        `unknown_num` int(11) NOT NULL DEFAULT 0 COMMENT '未知数量',
                                        `not_supported_num` int(11) NOT NULL DEFAULT 0 COMMENT '无需支持的数量',
                                        `artifact_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件名称',
                                        `group_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件组织名称',
                                        `ag` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT 'artifactId_groupId, 做唯一标识,',
                                        `gmt_create` datetime NOT NULL DEFAULT current_timestamp() COMMENT '创建时间',
                                        `gmt_update` datetime DEFAULT NULL ON UPDATE current_timestamp() COMMENT '更新时间',
                                        `commit` varchar(500) CHARACTER SET utf8 DEFAULT '' COMMENT '备注',
                                        `status` tinyint(4) NOT NULL COMMENT '状态',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE KEY `idx_ag` (`ag`) USING BTREE,
                                        KEY `idx_artifact_id` (`artifact_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='中间件信息表';

-- 插入开始
DROP PROCEDURE IF EXISTS insert_data;
DELIMITER $$
CREATE PROCEDURE insert_data()
BEGIN

DECLARE count1 INT;
SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'configCenter_middlewareManage');

-- 如果这条数据不存在
IF count1 = 0 THEN

-- 添加菜单
INSERT IGNORE INTO `t_tro_resource` (`id`,`parent_id`,`type`,`code`,`name`,`alias`,`value`,`sequence`,`action`,`features`,`customer_id`,`remark`,`create_time`,`update_time`,`is_deleted`) VALUES (NULL,11,0,'configCenter_middlewareManage','中间件库管理',NULL,'[\"/api/application/middlewareSummary\"]',6288,'[3]',NULL,NULL,NULL,NOW(),NOW(),0);

END IF;

END $$
DELIMITER ;
CALL insert_data();
DROP PROCEDURE IF EXISTS insert_data;
-- 插入结束


BEGIN;

INSERT IGNORE INTO `t_dictionary_type`(`ID`, `TYPE_NAME`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `PARENT_CODE`, `TYPE_ALIAS`, `IS_LEAF`) VALUES ('f644eb266aba4a2186341b708f33wex3', '中间件状态', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, 'MIDDLEWARE_STATUS', NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b708f33wex4', 'f644eb266aba4a2186341b708f33wex3', 0, '未知', '4', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b708f33wex5', 'f644eb266aba4a2186341b708f33wex3', 2, '未支持', '2', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b708f33wex6', 'f644eb266aba4a2186341b708f33wex3', 1, '已支持', '1', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b708f33wex7', 'f644eb266aba4a2186341b708f33wex3', 3, '无需支持', '3', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL);

INSERT IGNORE INTO `t_dictionary_type`(`ID`, `TYPE_NAME`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `PARENT_CODE`, `TYPE_ALIAS`, `IS_LEAF`) VALUES ('f644eb266aba4a2186341b708f33wex6', '中间件类型', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, 'MIDDLEWARE_TYPE', NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we01', 'f644eb266aba4a2186341b708f33wex6', 1, '连接池', '连接池', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we02', 'f644eb266aba4a2186341b708f33wex6', 2, '存储', '存储', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we03', 'f644eb266aba4a2186341b708f33wex6', 3, '消息队列', '消息队列', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we04', 'f644eb266aba4a2186341b708f33wex6', 4, '配置', '配置', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we05', 'f644eb266aba4a2186341b708f33wex6', 5, 'http-client', 'http-client', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we06', 'f644eb266aba4a2186341b708f33wex6', 6, '缓存', '缓存', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we07', 'f644eb266aba4a2186341b708f33wex6', 7, '数据源', '数据源', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we08', 'f644eb266aba4a2186341b708f33wex6', 8, 'RPC框架', 'RPC框架', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we09', 'f644eb266aba4a2186341b708f33wex6', 9, '定时任务', '定时任务', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we10', 'f644eb266aba4a2186341b708f33wex6', 10, '容器', '容器', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we11', 'f644eb266aba4a2186341b708f33wex6', 11, '网关', '网关', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we12', 'f644eb266aba4a2186341b708f33wex6', 12, '其他', '其他', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('f644eb266aba4a2186341b709f33we13', 'f644eb266aba4a2186341b708f33wex6', 13, '日志', '日志', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL);
COMMIT;