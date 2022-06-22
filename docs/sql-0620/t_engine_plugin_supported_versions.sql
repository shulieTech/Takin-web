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

 Date: 20/06/2022 16:11:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_engine_plugin_supported_versions
-- ----------------------------
DROP TABLE IF EXISTS `t_engine_plugin_supported_versions`;
CREATE TABLE `t_engine_plugin_supported_versions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_id` bigint(20) DEFAULT NULL COMMENT '外键关联-插件',
  `supported_version` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '版本',
  `file_ref_id` bigint(20) NOT NULL COMMENT '外键关联-插件文件',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压力引擎插件信息-版本';

-- ----------------------------
-- Records of t_engine_plugin_supported_versions
-- ----------------------------
BEGIN;
INSERT INTO `t_engine_plugin_supported_versions` VALUES (1, 1, 'all', 1);
INSERT INTO `t_engine_plugin_supported_versions` VALUES (2, 2, '0.8', 2);
INSERT INTO `t_engine_plugin_supported_versions` VALUES (3, 2, 'v2.5', 3);
INSERT INTO `t_engine_plugin_supported_versions` VALUES (4, 3, 'v3.4.4', 4);
INSERT INTO `t_engine_plugin_supported_versions` VALUES (5, 4, '0.8.2.2', 5);
INSERT INTO `t_engine_plugin_supported_versions` VALUES (6, 4, '2.8.0', 6);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
