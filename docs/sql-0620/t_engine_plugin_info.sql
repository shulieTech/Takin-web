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

 Date: 20/06/2022 16:11:05
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_engine_plugin_info
-- ----------------------------
DROP TABLE IF EXISTS `t_engine_plugin_info`;
CREATE TABLE `t_engine_plugin_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_type` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '插件类型',
  `plugin_name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '插件名称',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_update` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `t_engine_plugin_info_plugin_type` (`plugin_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压力引擎插件信息';

-- ----------------------------
-- Records of t_engine_plugin_info
-- ----------------------------
BEGIN;
INSERT INTO `t_engine_plugin_info` VALUES (1, 'dubbo', 'dubbo-all', 1, '2022-03-15 10:38:54', '2022-03-15 10:38:54');
INSERT INTO `t_engine_plugin_info` VALUES (2, 'kafka', 'kafka-all', 1, '2022-03-15 10:38:54', '2022-03-15 10:38:54');
INSERT INTO `t_engine_plugin_info` VALUES (3, 'rabbitmq', 'rabbitmq-3.4.4', 1, '2022-03-15 10:38:54', '2022-03-15 10:38:54');
INSERT INTO `t_engine_plugin_info` VALUES (4, 'kafka-data_set', 'kafka-数据集', 1, '2022-03-15 10:38:54', '2022-03-15 10:38:54');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
