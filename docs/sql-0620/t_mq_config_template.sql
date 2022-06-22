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

 Date: 20/06/2022 16:15:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_mq_config_template
-- ----------------------------
DROP TABLE IF EXISTS `t_mq_config_template`;
CREATE TABLE `t_mq_config_template` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT '' COMMENT '中间件中文描述',
  `type` varchar(100) DEFAULT '' COMMENT '中间件所属类型',
  `eng_name` varchar(100) DEFAULT '' COMMENT '中间件英文名称',
  `cobm_enable` tinyint(3) DEFAULT '1' COMMENT '是否支持自动梳理;0:不支持;1:支持',
  `shadowconsumer_enable` tinyint(3) DEFAULT '1' COMMENT '是否支持影子消费;0:不支持;1:支持',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '0.可用，1不可用',
  `remark` varchar(500) DEFAULT NULL COMMENT '标记',
  `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `CUSTOMER_ID` bigint(20) DEFAULT '0' COMMENT '租户id 废弃',
  `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `eng_name` (`eng_name`) USING BTREE,
  UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='MQ配置模版表';

-- ----------------------------
-- Records of t_mq_config_template
-- ----------------------------
BEGIN;
INSERT INTO `t_mq_config_template` VALUES (1, 'RabbitMQ', 'MQ', 'RABBITMQ', 1, 1, 0, NULL, NULL, 0, 0, '2021-09-09 14:15:02', '2021-09-09 14:15:02', 0, '2021-09-09 14:26:00', '2021-09-09 14:26:00', 1, 'test');
INSERT INTO `t_mq_config_template` VALUES (2, 'RocketMQ', 'MQ', 'ROCKETMQ', 1, 1, 0, NULL, NULL, 0, 0, '2021-09-09 14:15:29', '2021-09-09 14:15:29', 0, '2021-09-09 14:26:00', '2021-09-09 14:26:00', 1, 'test');
INSERT INTO `t_mq_config_template` VALUES (3, 'KafKa', 'MQ', 'KAFKA', 1, 1, 0, NULL, NULL, 0, 0, '2021-09-09 14:15:47', '2021-09-09 14:15:47', 0, '2021-09-09 14:26:00', '2021-09-09 14:26:00', 1, 'test');
INSERT INTO `t_mq_config_template` VALUES (4, 'ACTIVEMQ', 'MQ', 'ACTIVEMQ', 1, 1, 0, NULL, NULL, 0, 0, '2022-03-04 18:42:29', '2022-03-04 18:42:29', 0, '2022-03-04 18:42:29', '2022-03-04 18:42:29', 1, 'test');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
