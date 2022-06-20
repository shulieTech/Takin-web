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

 Date: 20/06/2022 16:16:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_plugin_library
-- ----------------------------
DROP TABLE IF EXISTS `t_plugin_library`;
CREATE TABLE `t_plugin_library` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `plugin_name` varchar(100) NOT NULL COMMENT '插件名称',
  `plugin_type` tinyint(3) DEFAULT '0' COMMENT '插件类型 0:插件 1:主版本 2:agent版本',
  `version` varchar(20) NOT NULL COMMENT '插件版本',
  `version_num` bigint(20) DEFAULT NULL COMMENT '版本对应数值',
  `is_custom_mode` tinyint(3) DEFAULT '0' COMMENT '是否为定制插件，0：否，1：是',
  `is_common_module` tinyint(3) DEFAULT '0' COMMENT '是否为公共模块，0：否，1：是',
  `update_description` varchar(1024) DEFAULT '' COMMENT '更新说明',
  `download_path` varchar(255) DEFAULT '' COMMENT '下载地址',
  `remark` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `ext` varchar(200) NOT NULL DEFAULT '' COMMENT 'jar包配置文件原始数据,当前是主版本时填',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `IS_DELETED` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `pluginName_version` (`plugin_name`,`version`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1075 DEFAULT CHARSET=utf8mb4 COMMENT='插件版本库';

-- ----------------------------
-- Records of t_plugin_library
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
