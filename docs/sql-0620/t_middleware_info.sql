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

 Date: 20/06/2022 16:14:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_middleware_info
-- ----------------------------
DROP TABLE IF EXISTS `t_middleware_info`;
CREATE TABLE `t_middleware_info` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `MIDDLEWARE_TYPE` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '中间件类型',
  `MIDDLEWARE_NAME` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '中间件名称',
  `MIDDLEWARE_VERSION` varchar(100) DEFAULT NULL COMMENT '中间件版本号',
  `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `t_middleware_info` (`MIDDLEWARE_TYPE`) USING BTREE,
  KEY `MIDDLEWARE_NAME` (`MIDDLEWARE_NAME`(255)) USING BTREE,
  KEY `MIDDLEWARE_VERSION` (`MIDDLEWARE_VERSION`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='中间件信息表';

-- ----------------------------
-- Records of t_middleware_info
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
