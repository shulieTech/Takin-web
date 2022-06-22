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

 Date: 20/06/2022 16:16:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_plugin_dependent
-- ----------------------------
DROP TABLE IF EXISTS `t_plugin_dependent`;
CREATE TABLE `t_plugin_dependent` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `plugin_name` varchar(100) DEFAULT '' COMMENT '插件名称',
  `plugin_version` varchar(20) DEFAULT '' COMMENT '插件版本',
  `plugin_version_num` bigint(20) DEFAULT NULL COMMENT '版本对应数值',
  `dependent_plugin_name` varchar(100) DEFAULT '' COMMENT '插件名称',
  `dependent_plugin_version` varchar(20) DEFAULT '' COMMENT '插件版本',
  `dependent_plugin_version_num` bigint(20) DEFAULT NULL COMMENT '版本对应数值',
  `requisite` tinyint(2) DEFAULT '0' COMMENT '是否必须 0:必须 1:非必须',
  `remark` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `IS_DELETED` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_pluginName_pluginVersion` (`plugin_name`,`plugin_version`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1323 DEFAULT CHARSET=utf8mb4 COMMENT='插件依赖库';

-- ----------------------------
-- Records of t_plugin_dependent
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
