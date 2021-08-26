-- agent_id字段
-- 字段添加
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count1 INT;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_report_machine' AND COLUMN_NAME = 'agent_id');

IF count1 = 0 THEN

ALTER TABLE `t_report_machine`
    ADD COLUMN `agent_id` varchar(128) DEFAULT NULL COMMENT 'agent' after `machine_ip`;

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;

-- 操作日志表
CREATE TABLE IF NOT EXISTS `t_operation_log`
(
    `id`         bigint(20)                   NOT NULL AUTO_INCREMENT COMMENT 'id',
    `module`     varchar(50) COLLATE utf8_bin NOT NULL COMMENT '操作模块-主模块',
    `sub_module` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '操作模块-字模块',
    `type`       varchar(50) COLLATE utf8_bin NOT NULL COMMENT '操作类型',
    `status`     varchar(50) COLLATE utf8_bin NOT NULL COMMENT '操作状态',
    `content`    text COLLATE utf8_bin        NOT NULL COMMENT '操作内容描述',
    `user_id`    bigint(20)                   NOT NULL COMMENT '操作人id',
    `user_name`  varchar(50) COLLATE utf8_bin NOT NULL COMMENT '操作人名称',
    `start_time` datetime                     NOT NULL COMMENT '开始时间',
    `end_time`   datetime                     NOT NULL COMMENT '结束时间',
    `is_delete`  tinyint(1)                   NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
    `gmt_create` datetime                     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update` datetime                     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- 插入开始
DROP PROCEDURE IF EXISTS insert_data;

DELIMITER $$

CREATE PROCEDURE insert_data()

BEGIN

DECLARE count1 INT;

SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'configCenter_operationLog');

IF count1 = 0 THEN

-- 资源表中增加操作日志菜单
INSERT INTO `t_tro_resource` (`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`)
VALUES (11, 0, 'configCenter_operationLog', '操作日志', NULL, '[\"/operation/log/list\"]', 6500);


END IF;

END $$

DELIMITER ;

CALL insert_data();

DROP PROCEDURE IF EXISTS insert_data;
-- 插入结束

-- 插件信息表
CREATE TABLE IF NOT EXISTS `t_agent_plugin`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `plugin_type` varchar(50)                                     DEFAULT NULL COMMENT '中间件类型：HTTP_CLIENT、JDBC、ORM、DB、JOB、MESSAGE、CACHE、POOL、JNDI、NO_SQL、RPC、SEARCH、MQ、SERIALIZE、OTHER',
    `plugin_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '中间件名称：Redis、Mysql、Es',
    `is_delete`   tinyint(1) NOT NULL                             DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
    `gmt_create`  datetime                                        DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`  datetime                                        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 初始化数据
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (1, 'HTTP_CLIENT', 'http-client', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (2, 'JDBC', 'jdbc', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (3, 'ORM', 'mybatis', 0, '2020-10-13 10:20:04', '2020-10-23 09:49:11');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (6, 'MESSAGE', 'pulsar', 0, '2020-10-13 10:20:04', '2020-10-23 10:36:43');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (7, 'CACHE', 'cache', 0, '2020-10-13 10:20:04', '2020-10-23 10:59:43');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (9, 'JNDI', 'jndi', 0, '2020-10-13 10:20:04', '2020-10-23 11:06:33');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (10, 'NO_SQL', 'aerospike', 0, '2020-10-13 10:20:04', '2020-10-23 11:07:23');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (11, 'RPC', 'dubbo', 0, '2020-10-13 10:20:04', '2020-10-23 11:07:59');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (12, 'SEARCH', 'es', 0, '2020-10-13 10:20:04', '2020-10-23 11:12:00');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (13, 'MQ', 'rocketmq', 0, '2020-10-13 10:20:04', '2020-10-23 11:13:37');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (14, 'SERIALIZE', 'hessian', 0, '2020-10-13 10:20:04', '2020-10-23 11:15:47');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (15, 'MQ', 'ons', 0, '2020-10-13 10:20:04', '2020-10-21 10:09:41');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (16, 'MQ', 'kafka', 0, '2020-10-13 10:20:04', '2020-10-23 11:20:13');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (17, 'MQ', 'rabbit', 0, '2020-10-13 10:20:04', '2020-10-23 11:22:05');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (18, 'MQ', 'jms', 0, '2020-10-13 10:20:04', '2020-10-21 10:06:34');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (19, 'HTTP_CLIENT', 'google-http', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (20, 'HTTP_CLIENT', 'okhttp', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (21, 'HTTP_CLIENT', 'grizzly', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (22, 'JDBC', 'ojdbc', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (23, 'DB', 'alibase', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (24, 'DB', 'oss', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (25, 'DB', 'mongodb', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (26, 'DB', 'neo4j', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (27, 'JOB', 'saturn', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (28, 'JOB', 'xxl-job', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (29, 'CACHE', 'jetcache', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (30, 'SERVLET_CONTAINER', 'jetty', 0, '2020-10-23 10:53:18', '2020-10-23 10:53:18');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (31, 'CACHE', 'redis', 0, '2020-10-13 10:20:04', '2020-10-20 10:10:57');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (32, 'CACHE', 'jedis', 0, '2020-10-23 10:56:26', '2020-10-23 10:56:30');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (33, 'CACHE', 'lettuce', 0, '2020-10-23 10:56:54', '2020-10-23 10:57:04');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (34, 'CACHE', 'redisson', 0, '2020-10-23 10:58:35', '2020-10-23 10:58:38');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (35, 'POOL', 'druid连接池', 0, '2020-10-23 11:00:55', '2020-10-23 11:00:55');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (36, 'POOL', 'tomcat连接池', 0, '2020-10-23 11:02:27', '2020-10-23 11:02:29');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (37, 'POOL', 'transactions连接池', 0, '2020-10-23 11:03:08', '2020-10-23 11:03:11');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (38, 'POOL', 'c3p0连接池', 0, '2020-10-23 11:03:59', '2020-10-23 11:04:02');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (39, 'POOL', 'dbcp连接池', 0, '2020-10-23 11:04:29', '2020-10-23 11:04:32');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (40, 'POOL', 'dbcp2连接池', 0, '2020-10-23 11:04:47', '2020-10-23 11:04:53');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (41, 'POOL', 'HikariCP连接池', 0, '2020-10-23 11:05:42', '2020-10-23 11:05:42');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (42, 'RPC', 'grpc', 0, '2020-10-23 11:08:50', '2020-10-23 11:08:50');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (43, 'RPC', 'motan', 0, '2020-10-23 11:09:39', '2020-10-23 11:09:39');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (44, 'MQ', 'spring-kafka', 0, '2020-10-23 11:20:07', '2020-10-23 11:20:07');
INSERT IGNORE INTO `t_agent_plugin`(`id`, `plugin_type`, `plugin_name`, `is_delete`, `gmt_create`, `gmt_update`)
VALUES (45, 'OTHER', '-', 0, '2020-10-23 17:31:54', '2020-10-23 17:31:54');

-- 插件客户端包支持表
CREATE TABLE IF NOT EXISTS `t_agent_plugin_lib_support`
(
    `id`                 bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `plugin_id`          bigint(20) NOT NULL COMMENT '插件id',
    `lib_name`           varchar(64) CHARACTER SET utf8 COLLATE utf8_bin   DEFAULT NULL COMMENT 'jar包名称',
    `lib_version_regexp` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'agent支持的中间件版本的正则表达式',
    `is_ignore`          tinyint(1) NOT NULL                               DEFAULT '0' COMMENT '状态 0: 不忽略 1： 忽略',
    `is_delete`          tinyint(1) NOT NULL                               DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
    `gmt_create`         datetime                                          DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`         datetime                                          DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uni_libname_index` (`lib_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- 初始化数据
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (1, 19, 'google-http-client', '[\"1.20.0\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 09:42:16');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (2, 1, 'httpclient', '[\"4.4.1\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 10:59:01');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (4, 20, 'okhttp', '[\"2.0.0\",\"3.8.1\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 09:42:24');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (5, 22, 'ojdbc14', '[\"10.2.0.3.0\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 09:47:55');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (6, 3, 'mybatis', '[\"3.4.2\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:01:39');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (7, 23, 'alihbase-client', '[\"2.0.3\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 09:52:18');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (10, 24, 'aliyun-sdk-oss', '[\"3.5.0\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:17:56');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (11, 27, 'saturn-core', '[\"3.5.0\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:31:10');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (12, 28, 'xxl-job-core', '[\"2.1.2\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:31:12');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (13, 6, 'pulsar-client', '[\"2.4.0\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:03:21');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (14, 29, 'jetcache-anno', '[\"2.6.0.M2\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:40:15');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (15, 30, 'jetty-server', '[\"9.2.11.v20150529\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:53:29');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (16, 30, 'jetty-servlet', '[\"9.2.11.v20150529\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:53:31');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (17, 32, 'jedis', '[\"3.1.0\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:56:35');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (18, 33, 'lettuce-core', '[\"5.1.4.RELEASE\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:57:20');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (19, 31, 'spring-boot-starter-data-redis', '[\"2.1.7.RELEASE\"]', 0, 0, '2020-10-13 10:21:25',
        '2020-10-23 10:58:20');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (20, 34, 'redisson', '[\"3.13.2\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:59:24');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (21, 7, 'spring-boot-starter-cache', '[\"2.1.1.RELEASE\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:05:41');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (22, 35, 'druid', '[\"1.1.14\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 17:50:19');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (23, 36, 'tomcat-jdbc', '[\"8.5.20\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:02:36');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (24, 37, 'transactions-jdbc', '[\"4.0.6\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:03:15');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (25, 38, 'c3p0', '[\"c3p0-0.9.5.2.jar\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:04:07');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (26, 39, 'dbcp', '[\"dbcp\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:04:40');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (27, 40, 'commons-dbcp2', '[\"2.7.0\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:05:01');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (28, 41, 'HikariCP', '[\"3.1.0\",\"2.7.9\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:05:47');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (29, 9, 'simple-jndi', '[\"0.11.4.1\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:04:07');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (30, 10, 'aerospike-client', '[\"3.2.2\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:07:26');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (31, 11, 'dubbo', '[\"2.8.4\",\"2.7.*\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:07:47');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (32, 42, 'grpc-core', '[\"1.14.0\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:08:59');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (33, 43, 'motan-core', '[\"0.3.1\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:09:43');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (34, 12, 'elasticsearch', '[\"6.8.6\",\"2.4.1\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:10:05');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (35, 13, 'rocketmq-client', '[\"3.2.6\",\"4.*.*\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 10:49:58');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (36, 15, 'ons-client', '[\"1.8.0.Final\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:14:33');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (37, 16, 'kafka-clients', '[\"0.8.0.0\",\"2.2.0\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:08:49');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (38, 44, 'spring-kafka', '[\"2.3.1.RELEASE\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:21:04');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (39, 17, 'spring-rabbit', '[\"1.7.0.RELEASE\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:09:08');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (40, 18, 'spring-jms', '[\"4.1.9.RELEASE\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:09:22');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (41, 14, 'hessian', '[\"4.0.63\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 11:09:31');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (42, 1, 'commons-httpclient', '[\"3.1\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-21 10:58:07');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (43, 29, 'jetcache-redis', '[\"2.6.0.M2\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:40:33');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (44, 43, 'motan-springsupport', '[\"0.3.1\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 11:09:44');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (45, 21, 'grizzly-framework', '[\"2.3.21\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 09:44:27');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (46, 21, 'grizzly-http-server', '[\"2.3.21\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 09:44:29');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (47, 21, 'grizzly-http', '[\"2.3.21\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 09:44:29');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (48, 21, 'grizzly-websockets', '[\"2.3.21\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 09:44:30');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (49, 25, 'mongodb-driver', '[\"3.11.2\",\"3.8.2\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:21:25');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (50, 25, 'spring-boot-starter-data-mongodb', '[\"1.5.10.RELEASE\"]', 0, 0, '2020-10-13 10:21:25',
        '2020-10-23 10:20:39');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (51, 26, 'neo4j-ogm-compiler', '[\"2.0.5\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:26:24');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (52, 26, 'nneo4j-ogm-embedded-driver', '[\"2.0.5\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:26:25');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (53, 26, 'neo4j-ogm-bolt-driver', '[\"2.0.5\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:26:26');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (54, 26, 'neo4j-ogm-http-driver', '[\"2.0.5\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:26:27');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (55, 26, 'neo4j-ogm-core', '[\"2.0.5\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:26:28');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (56, 25, 'mongo-java-driver', '[\"3.2.2\"]', 0, 0, '2020-10-13 10:21:25', '2020-10-23 10:21:25');
INSERT IGNORE INTO `t_agent_plugin_lib_support`(`id`, `plugin_id`, `lib_name`, `lib_version_regexp`, `is_ignore`,
                                                 `is_delete`, `gmt_create`, `gmt_update`)
VALUES (57, 45, 'lombok', '[\"1.18.6\"]', 1, 0, '2020-10-23 17:32:51', '2020-10-23 17:32:51');
