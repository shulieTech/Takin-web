/*
 Navicat Premium Data Transfer

 Source Server         : 【22】-192.168.1.129
 Source Server Type    : MySQL
 Source Server Version : 50733
 Source Host           : 192.168.1.129:3306
 Source Schema         : trodb_combine_225

 Target Server Type    : MySQL
 Target Server Version : 50733
 File Encoding         : 65001

 Date: 20/06/2022 16:15:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_middleware_type
-- ----------------------------
DROP TABLE IF EXISTS `t_middleware_type`;
CREATE TABLE `t_middleware_type` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件中文描述',
  `eng_name` varchar(100) NOT NULL DEFAULT '' COMMENT '中间件英文名称',
  `type` varchar(20) NOT NULL COMMENT '分类',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `value_order` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_eng_name` (`type`,`eng_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COMMENT='调用类型表';

-- ----------------------------
-- Records of t_middleware_type
-- ----------------------------
BEGIN;
INSERT INTO `t_middleware_type` VALUES (1, 'HTTP', 'HTTP', 'HTTP', 0, 1);
INSERT INTO `t_middleware_type` VALUES (2, 'undertow', 'undertow', '容器', 0, 2);
INSERT INTO `t_middleware_type` VALUES (3, 'tomcat', 'tomcat', '容器', 0, 3);
INSERT INTO `t_middleware_type` VALUES (4, 'weblogic', 'weblogic', '容器', 0, 4);
INSERT INTO `t_middleware_type` VALUES (5, 'jetty', 'jetty', '容器', 0, 5);
INSERT INTO `t_middleware_type` VALUES (6, 'Dubbo', 'Dubbo', 'RPC', 0, 6);
INSERT INTO `t_middleware_type` VALUES (7, 'Feign', 'Feign', 'RPC', 0, 7);
INSERT INTO `t_middleware_type` VALUES (8, 'GRPC', 'GRPC', 'RPC', 0, 8);
INSERT INTO `t_middleware_type` VALUES (9, 'HESSIAN', 'HESSIAN', 'RPC', 0, 9);
INSERT INTO `t_middleware_type` VALUES (10, 'MYSQL', 'MYSQL', '数据库', 0, 10);
INSERT INTO `t_middleware_type` VALUES (11, 'POSTGRESQL', 'POSTGRESQL', '数据库', 0, 11);
INSERT INTO `t_middleware_type` VALUES (12, 'ORACLE', 'ORACLE', '数据库', 0, 12);
INSERT INTO `t_middleware_type` VALUES (13, 'SQLSERVER', 'SQLSERVER', '数据库', 0, 13);
INSERT INTO `t_middleware_type` VALUES (14, ' MONGODB', 'MONGODB', 'nosql', 0, 14);
INSERT INTO `t_middleware_type` VALUES (15, 'HBASE', 'HBASE', 'nosql', 0, 15);
INSERT INTO `t_middleware_type` VALUES (16, 'CACHE', 'CACHE', '缓存', 0, 16);
INSERT INTO `t_middleware_type` VALUES (17, 'REDIS', 'REDIS', '缓存', 0, 17);
INSERT INTO `t_middleware_type` VALUES (18, 'MEMCACHE', 'MEMCACHE', '缓存', 0, 18);
INSERT INTO `t_middleware_type` VALUES (19, 'ROCKETMQ', 'ROCKETMQ', '消息', 0, 19);
INSERT INTO `t_middleware_type` VALUES (20, 'RABBITMQ', 'RABBITMQ', '消息', 0, 20);
INSERT INTO `t_middleware_type` VALUES (21, 'KAFKA', 'KAFKA', '消息', 0, 21);
INSERT INTO `t_middleware_type` VALUES (22, 'ACTIVEMQ', 'ACTIVEMQ', '消息', 0, 22);
INSERT INTO `t_middleware_type` VALUES (23, 'IBMMQ', 'IBMMQ', '消息', 0, 23);
INSERT INTO `t_middleware_type` VALUES (24, 'ES', 'ES', '索引', 0, 24);
INSERT INTO `t_middleware_type` VALUES (25, 'OSS', 'OSS', '文件存储', 0, 25);
INSERT INTO `t_middleware_type` VALUES (26, '其他', '其他', '其他', 0, 26);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
