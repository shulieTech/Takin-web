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

 Date: 20/06/2022 16:16:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_plugin_tenant_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_plugin_tenant_ref`;
CREATE TABLE `t_plugin_tenant_ref` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `plugin_name` varchar(100) DEFAULT '' COMMENT '插件名称',
  `plugin_version` varchar(20) DEFAULT '' COMMENT '插件版本',
  `owning_tenant_id` bigint(20) NOT NULL COMMENT '所属租户id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `IS_DELETED` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `pluginName_pluginVersion_owningTenantId` (`plugin_name`,`plugin_version`,`owning_tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COMMENT='插件租户关联表';

-- ----------------------------
-- Records of t_plugin_tenant_ref
-- ----------------------------
BEGIN;
INSERT INTO `t_plugin_tenant_ref` VALUES (2, 'test-druid-this-names', '5.2.0.1', 25, '2022-01-15 20:44:01', '2022-01-15 20:44:01', 0);
INSERT INTO `t_plugin_tenant_ref` VALUES (3, 'best-netty', '1.0.0.0', 2, '2022-01-15 20:47:25', '2022-01-15 20:47:25', 0);
INSERT INTO `t_plugin_tenant_ref` VALUES (4, 'best-netty', '1.0.0.0', 25, '2022-01-15 20:47:25', '2022-01-15 20:47:25', 0);
INSERT INTO `t_plugin_tenant_ref` VALUES (6, 'best-netty', '5.2.0.0', 25, '2022-01-15 20:47:40', '2022-01-15 20:47:40', 0);
INSERT INTO `t_plugin_tenant_ref` VALUES (7, 'best-netty', '5.2.0.0', 2, '2022-01-15 20:47:40', '2022-01-15 20:47:40', 0);
INSERT INTO `t_plugin_tenant_ref` VALUES (8, 'za24-datasource', '5.2.3.0', 25, '2022-01-18 20:59:37', '2022-01-18 20:59:37', 0);
INSERT INTO `t_plugin_tenant_ref` VALUES (9, 'za24-datasource', '5.2.1.0', 25, '2022-01-18 20:59:46', '2022-01-18 20:59:46', 0);
INSERT INTO `t_plugin_tenant_ref` VALUES (10, 'test-druid-this-names', '5.2.4.0', 25, '2022-01-21 11:34:04', '2022-01-21 11:34:04', 0);
INSERT INTO `t_plugin_tenant_ref` VALUES (11, 't3-cache', '5.2.4.1', 25, '2022-01-21 16:57:43', '2022-01-21 16:57:43', 0);
INSERT INTO `t_plugin_tenant_ref` VALUES (12, 't3-cache', '5.2.4.0', 25, '2022-01-21 16:57:54', '2022-01-21 16:57:54', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
