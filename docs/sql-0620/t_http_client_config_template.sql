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

 Date: 20/06/2022 16:12:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_http_client_config_template
-- ----------------------------
DROP TABLE IF EXISTS `t_http_client_config_template`;
CREATE TABLE `t_http_client_config_template` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT '' COMMENT '中间件中文描述',
  `type` varchar(100) DEFAULT '' COMMENT '中间件所属类型',
  `eng_name` varchar(100) DEFAULT '' COMMENT '中间件英文名称',
  `cobm_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持自动梳理;0:不支持;1:支持',
  `whitelist_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持白名单;0:不支持;1:支持',
  `return_mock_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持返回值mock;0:不支持;1:支持',
  `return_mock` text CHARACTER SET utf8 COMMENT '返回值mock文本',
  `forward_mock_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持转发mock;0:不支持;1:支持',
  `forward_mock` text CHARACTER SET utf8 COMMENT '转发mock文本',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '1.可用，2不可用',
  `remark` varchar(500) DEFAULT NULL COMMENT '标记',
  `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `CUSTOMER_ID` bigint(20) DEFAULT '0' COMMENT '租户id 废弃',
  `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  `return_fix_mock_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持固定值返回mock;0:不支持;1:支持',
  `fix_return_mock` text CHARACTER SET utf8 COMMENT '固定值转发mock文本',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `eng_name` (`eng_name`) USING BTREE,
  UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='http-client配置模版表';

-- ----------------------------
-- Records of t_http_client_config_template
-- ----------------------------
BEGIN;
INSERT INTO `t_http_client_config_template` VALUES (1, 'httpclient3', 'http', 'httpclient3', 1, 1, 1, '返回mock文本', 1, '转发mock文本', 0, NULL, NULL, 0, 0, 0, '2021-09-09 07:34:12', '2021-09-09 07:34:12', 1, 'test', 0, NULL);
INSERT INTO `t_http_client_config_template` VALUES (2, 'httpclient4', 'http', 'httpclient4', 1, 1, 1, '返回mock文本', 1, '转发mock文本', 0, NULL, NULL, 0, 0, 0, '2021-09-09 07:34:12', '2021-09-09 07:34:12', 1, 'test', 0, NULL);
INSERT INTO `t_http_client_config_template` VALUES (3, 'jdk-http', 'http', 'jdk-http', 1, 1, 1, '返回mock文本', 1, '转发', 0, NULL, NULL, 0, 0, 0, '2021-09-09 07:34:12', '2021-09-09 07:34:12', 1, 'test', 0, NULL);
INSERT INTO `t_http_client_config_template` VALUES (4, 'async-httpclient', 'http', 'async-httpclient', 1, 1, 1, '返货mock', 1, '转发', 0, NULL, NULL, 0, 0, 0, '2021-09-09 07:34:12', '2021-09-09 07:34:12', 1, 'test', 0, NULL);
INSERT INTO `t_http_client_config_template` VALUES (5, 'google-httpclient', 'http', 'google-httpclient', 1, 1, 1, '返回mock', 1, '转发', 0, NULL, NULL, 0, 0, 0, '2021-09-09 07:34:12', '2021-09-09 07:34:12', 1, 'test', 0, NULL);
INSERT INTO `t_http_client_config_template` VALUES (6, 'HTTP', 'http', 'HTTP', 1, 1, 1, NULL, 1, NULL, 0, NULL, NULL, 0, 0, 0, '2021-09-10 09:59:08', '2021-09-10 09:59:08', 1, 'test', 0, NULL);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
