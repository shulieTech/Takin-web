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

 Date: 20/06/2022 16:13:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_interface_type_config
-- ----------------------------
DROP TABLE IF EXISTS `t_interface_type_config`;
CREATE TABLE `t_interface_type_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `interface_type_id` bigint(20) NOT NULL COMMENT '中间件子类型id',
  `config_id` int(11) NOT NULL COMMENT '支持配置',
  `config_text` varchar(250) DEFAULT '' COMMENT '支持配置文本',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_eng_name_env_tenant` (`interface_type_id`,`config_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COMMENT='远程调用接口类型配置表';

-- ----------------------------
-- Records of t_interface_type_config
-- ----------------------------
BEGIN;
INSERT INTO `t_interface_type_config` VALUES (1, 1, 1, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (2, 1, 2, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (3, 1, 3, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (4, 1, 4, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (5, 1, 5, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (6, 2, 1, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (7, 2, 2, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (8, 2, 3, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (9, 2, 4, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (10, 2, 5, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (11, 3, 1, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (12, 3, 2, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (13, 3, 3, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (14, 3, 4, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (15, 3, 5, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (16, 4, 1, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (17, 4, 2, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (18, 4, 3, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (19, 4, 4, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (20, 4, 5, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (21, 5, 1, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (22, 5, 2, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (23, 5, 3, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (24, 5, 4, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (25, 5, 5, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (26, 6, 1, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (27, 6, 2, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (28, 6, 3, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (29, 6, 4, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (30, 6, 5, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (31, 7, 1, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (32, 7, 2, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (33, 7, 3, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (34, 7, 4, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (35, 7, 5, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (36, 8, 1, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (37, 8, 2, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (38, 8, 3, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (39, 8, 4, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (40, 8, 5, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (41, 9, 1, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (42, 9, 2, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (43, 9, 3, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (44, 9, 4, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (45, 9, 5, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (46, 10, 1, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (47, 10, 2, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (48, 10, 3, '', '', 1);
INSERT INTO `t_interface_type_config` VALUES (49, 10, 4, '', '', 0);
INSERT INTO `t_interface_type_config` VALUES (50, 10, 5, '', '', 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
