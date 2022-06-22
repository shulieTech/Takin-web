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

 Date: 20/06/2022 16:10:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_engine_plugin_files_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_engine_plugin_files_ref`;
CREATE TABLE `t_engine_plugin_files_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_id` bigint(20) NOT NULL COMMENT '外键关联-插件',
  `file_name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '文件名称',
  `file_path` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '文件路径',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压力引擎插件信息-文件';

-- ----------------------------
-- Records of t_engine_plugin_files_ref
-- ----------------------------
BEGIN;
INSERT INTO `t_engine_plugin_files_ref` VALUES (1, 1, 'jmeter-plugins-dubbo-all.jar', '/config/jars/jmeter-plugins-dubbo-all.jar', '2022-03-15 10:38:54');
INSERT INTO `t_engine_plugin_files_ref` VALUES (2, 2, 'kafkameter-0.8.jar', '/config/jars/kafkameter-0.8.jar', '2022-03-15 10:38:54');
INSERT INTO `t_engine_plugin_files_ref` VALUES (3, 2, 'kafkameter-2.5.jar', '/config/jars/kafkameter-2.5.jar', '2022-03-15 10:38:54');
INSERT INTO `t_engine_plugin_files_ref` VALUES (4, 3, 'rabbitmq-3.4.4.jar', '/config/jars/rabbitmq-3.4.4.jar', '2022-03-15 10:38:54');
INSERT INTO `t_engine_plugin_files_ref` VALUES (5, 4, 'kafka-data_set-0.8.2.2.jar', '/config/jars/kafka-data_set-0.8.2.2.jar', '2022-03-15 10:38:54');
INSERT INTO `t_engine_plugin_files_ref` VALUES (6, 4, 'kafka-data_set-2.8.0.jar', '/config/jars/kafka-data_set-2.8.0.jar', '2022-03-15 10:38:54');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
