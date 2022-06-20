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

 Date: 20/06/2022 16:13:59
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_interface_type_main
-- ----------------------------
DROP TABLE IF EXISTS `t_interface_type_main`;
CREATE TABLE `t_interface_type_main` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `value_order` int(11) DEFAULT NULL,
  `name` varchar(100) NOT NULL DEFAULT '' COMMENT '中间件中文描述',
  `eng_name` varchar(100) NOT NULL DEFAULT '' COMMENT '中间件英文名称',
  `rpc_type` tinyint(1) NOT NULL COMMENT '对应大数据rpcType',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `is_system` tinyint(1) NOT NULL DEFAULT '0' COMMENT '系统预设：1-预设，0-非预设(系统预设的不允许修改)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_eng_name` (`eng_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='远程调用中间件主类型表';

-- ----------------------------
-- Records of t_interface_type_main
-- ----------------------------
BEGIN;
INSERT INTO `t_interface_type_main` VALUES (1, 0, 'HTTP', 'HTTP', 0, 0, 1);
INSERT INTO `t_interface_type_main` VALUES (2, 1, 'DUBBO', 'DUBBO', 1, 0, 1);
INSERT INTO `t_interface_type_main` VALUES (3, 2, 'FEIGN', 'FEIGN', 1, 0, 1);
INSERT INTO `t_interface_type_main` VALUES (4, 3, 'GRPC', 'GRPC', 1, 0, 1);
INSERT INTO `t_interface_type_main` VALUES (11, 5, 'HTTP3', 'HTTP3', 1, 0, 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
