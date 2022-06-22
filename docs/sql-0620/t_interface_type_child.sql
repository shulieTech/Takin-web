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

 Date: 20/06/2022 16:13:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_interface_type_child
-- ----------------------------
DROP TABLE IF EXISTS `t_interface_type_child`;
CREATE TABLE `t_interface_type_child` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '' COMMENT '中间件中文描述',
  `main_id` varchar(40) NOT NULL DEFAULT '' COMMENT '中间件主类型',
  `eng_name` varchar(100) NOT NULL DEFAULT '' COMMENT '中间件英文名称',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_eng_name` (`eng_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='远程调用中间件子类型表';

-- ----------------------------
-- Records of t_interface_type_child
-- ----------------------------
BEGIN;
INSERT INTO `t_interface_type_child` VALUES (1, 'httpclient3', '1', 'httpclient3', 0);
INSERT INTO `t_interface_type_child` VALUES (2, 'httpclient4', '1', 'httpclient4', 0);
INSERT INTO `t_interface_type_child` VALUES (3, 'jdk-http', '1', 'jdk-http', 0);
INSERT INTO `t_interface_type_child` VALUES (4, 'async-httpclient', '1', 'async-httpclient', 0);
INSERT INTO `t_interface_type_child` VALUES (5, 'google-httpclient', '1', 'google-httpclient', 0);
INSERT INTO `t_interface_type_child` VALUES (6, 'HTTP', '1', 'HTTP', 0);
INSERT INTO `t_interface_type_child` VALUES (7, 'okhttp', '1', 'okhttp', 0);
INSERT INTO `t_interface_type_child` VALUES (8, 'dubbo', '2', 'dubbo', 0);
INSERT INTO `t_interface_type_child` VALUES (9, 'feign', '3', 'feign', 0);
INSERT INTO `t_interface_type_child` VALUES (10, 'grpc', '4', 'grpc', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
