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

 Date: 20/06/2022 16:07:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_tro_resource
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_resource`;
CREATE TABLE `t_tro_resource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源id主键',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父资源id',
  `type` tinyint(1) NOT NULL COMMENT '资源类型(0:菜单1:按钮 2:数据)',
  `code` varchar(100) DEFAULT NULL COMMENT '资源编码',
  `name` varchar(255) DEFAULT NULL COMMENT '资源名称',
  `alias` varchar(255) DEFAULT NULL COMMENT '资源别名，存放后端资源编码',
  `value` varchar(1024) NOT NULL COMMENT '资源值（菜单是url，应用是应用id）',
  `sequence` int(11) DEFAULT '0' COMMENT '排序',
  `action` varchar(255) DEFAULT NULL COMMENT '操作权限',
  `features` longtext COMMENT '扩展字段，k-v形式存在',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `IS_SUPER` tinyint(1) DEFAULT '0' COMMENT '只有超级管理员才能看到',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_value` (`value`(191)) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=520 DEFAULT CHARSET=utf8mb4 COMMENT='菜单资源库表';

-- ----------------------------
-- Records of t_tro_resource
-- ----------------------------
BEGIN;
INSERT INTO `t_tro_resource` VALUES (1, NULL, 0, 'dashboard', '系统概览', '', '[\"/api/user/work/bench\"]', 1000, '[]', NULL, NULL, NULL, '2020-09-01 17:10:02', '2021-01-19 17:20:09', 0, 0);
INSERT INTO `t_tro_resource` VALUES (2, NULL, 0, 'linkTease', '链路梳理', '', '', 2400, '[]', NULL, NULL, NULL, '2020-09-01 17:16:56', '2020-11-12 20:12:24', 0, 0);
INSERT INTO `t_tro_resource` VALUES (4, 2, 0, 'businessActivity', '业务活动', '', '[\"/api/activities\"]', 2200, '[2,3,4]', NULL, NULL, NULL, '2020-09-01 17:26:09', '2021-01-19 15:31:25', 0, 0);
INSERT INTO `t_tro_resource` VALUES (5, 2, 0, 'businessFlow', '业务流程', '', '[\"/api/link/scene/manage\"]', 2300, '[2,3,4]', NULL, NULL, NULL, '2020-09-01 17:26:54', '2021-01-19 17:20:21', 0, 0);
INSERT INTO `t_tro_resource` VALUES (6, 505, 0, 'appManage', '应用管理', NULL, '[\"/api/application/center/list\",\"/api/application/center/app/switch\",\"/api/console/switch/whitelist\"]', 3000, '[2,3,4,6]', NULL, NULL, NULL, '2020-09-01 17:31:32', '2021-09-03 18:15:59', 0, 0);
INSERT INTO `t_tro_resource` VALUES (8, NULL, 0, 'pressureTestManage', '压测管理', NULL, '', 6300, '[]', NULL, NULL, NULL, '2020-09-01 17:36:41', '2020-12-17 11:17:50', 0, 0);
INSERT INTO `t_tro_resource` VALUES (9, 8, 0, 'pressureTestManage_pressureTestScene', '压测场景', NULL, '[\"/api/scenemanage/list\",\"/api/application/center/app/switch\",\"/api/console/switch/whitelist\"]', 6100, '[2,3,4,5]', NULL, NULL, NULL, '2020-09-01 17:38:28', '2021-01-19 17:21:09', 0, 0);
INSERT INTO `t_tro_resource` VALUES (10, 8, 0, 'pressureTestManage_pressureTestReport', '压测报告', NULL, '[\"/api/report/listReport\"]', 6200, '[7]', NULL, NULL, NULL, '2020-09-01 17:43:10', '2022-02-09 17:37:35', 0, 0);
INSERT INTO `t_tro_resource` VALUES (11, NULL, 0, 'configCenter', '配置中心', NULL, '', 7600, '[]', NULL, NULL, NULL, '2020-09-01 17:44:26', '2020-12-17 11:18:01', 0, 0);
INSERT INTO `t_tro_resource` VALUES (12, 11, 0, 'configCenter_pressureMeasureSwitch', '压测开关设置', NULL, '[\"/api/application/center/app/switch\"]', 7100, '[6]', NULL, NULL, NULL, '2020-09-01 17:46:04', '2020-12-17 11:18:04', 0, 0);
INSERT INTO `t_tro_resource` VALUES (13, 11, 0, 'configCenter_whitelistSwitch', '白名单开关设置', NULL, '[\"/api/console/switch/whitelist\"]', 7200, '[6]', NULL, NULL, NULL, '2020-09-01 17:47:15', '2021-01-19 17:34:22', 0, 0);
INSERT INTO `t_tro_resource` VALUES (14, 11, 0, 'configCenter_blacklist', '黑名单', NULL, '[\"/api/confcenter/query/blist\"]', 7300, '[2,3,4,6]', NULL, NULL, NULL, '2020-09-01 17:48:02', '2021-01-19 17:19:58', 0, 0);
INSERT INTO `t_tro_resource` VALUES (15, 11, 0, 'configCenter_entryRule', '入口规则', NULL, '[\"/api/api/get\"]', 7400, '[2,3,4]', NULL, NULL, NULL, '2020-09-01 17:49:15', '2020-12-17 11:18:15', 0, 0);
INSERT INTO `t_tro_resource` VALUES (16, NULL, 0, 'flowAccount', '流量账户', NULL, '[\"/api/settle/balance/list\"]', 8000, '[]', NULL, NULL, NULL, '2020-09-01 17:51:25', '2022-01-10 14:40:09', 0, 1);
INSERT INTO `t_tro_resource` VALUES (307, 11, 0, 'configCenter_operationLog', '操作日志', NULL, '[\"/api/operation/log/list\"]', 7500, '[]', NULL, NULL, NULL, '2020-09-28 15:27:38', '2021-01-19 17:28:09', 0, 0);
INSERT INTO `t_tro_resource` VALUES (340, 8, 0, 'pressureTestManage_pressureTestStatistic', '压测统计', NULL, '[\"/api/statistic\"]', 6000, '[]', NULL, NULL, NULL, '2020-12-30 19:12:13', '2020-12-30 19:38:59', 0, 0);
INSERT INTO `t_tro_resource` VALUES (367, 11, 0, 'configCenter_authorityConfig', '权限管理', NULL, '[\"/api/role/list\"]', 7000, '[2,3,4]', NULL, NULL, NULL, '2020-11-10 11:53:09', '2021-01-19 17:22:05', 0, 0);
INSERT INTO `t_tro_resource` VALUES (368, 410, 0, 'scriptManage', '测试脚本', NULL, '[\"/api/scriptManage\"]', 4000, '[2,3,4,7]', NULL, NULL, NULL, '2020-11-10 18:55:05', '2021-08-06 19:43:22', 0, 0);
INSERT INTO `t_tro_resource` VALUES (369, NULL, 0, 'shellManage', 'Shell管理', NULL, '[\"/api/shellManage\"]', 5000, '[]', NULL, NULL, NULL, '2020-12-17 11:16:27', '2020-12-17 11:18:39', 0, 0);
INSERT INTO `t_tro_resource` VALUES (400, NULL, 0, 'debugTool', '调试工具', NULL, '', 9001, '[]', NULL, NULL, NULL, '2021-01-14 11:19:50', '2021-03-09 15:43:10', 0, 0);
INSERT INTO `t_tro_resource` VALUES (401, 400, 0, 'debugTool_linkDebug', '链路调试', NULL, '[\"/debugTool/linkDebug\",\"/api/fastdebug/config/list\"]', 9000, '[2,3,4,5]', NULL, NULL, NULL, '2021-01-14 11:22:03', '2021-10-08 15:46:37', 0, 0);
INSERT INTO `t_tro_resource` VALUES (402, 12, 0, 'debugTool_linkDebug_detail', '链路调试详情', NULL, '[\"/debugTool/linkDebug/detail\"]', 9002, '[]', NULL, NULL, NULL, '2021-01-14 11:23:19', '2021-01-14 11:32:13', 0, 0);
INSERT INTO `t_tro_resource` VALUES (403, 11, 0, 'configCenter_dataSourceConfig', '数据源配置', NULL, '[\"/api/datasource/list\"]', 7600, ' [2,3,4]', NULL, NULL, NULL, '2021-01-06 15:17:40', '2021-01-06 15:19:12', 0, 0);
INSERT INTO `t_tro_resource` VALUES (404, 11, 0, 'configCenter_bigDataConfig', '开关配置', NULL, '[\"/api/pradar/switch/list\"]', 7700, '[2,3,4]', NULL, NULL, NULL, '2021-02-22 14:22:49', '2022-01-10 14:40:09', 0, 1);
INSERT INTO `t_tro_resource` VALUES (405, NULL, 0, 'patrolManage', '巡检管理', NULL, '[\"/api/patrol/manager\"]', 10000, '[2,3,4,5]', NULL, NULL, NULL, '2021-05-19 15:45:09', '2021-09-07 20:14:45', 0, 0);
INSERT INTO `t_tro_resource` VALUES (406, NULL, 0, 'patrolScreen', '巡检大屏', NULL, '[\"/api/patrol/screen\"]', 11000, '[2]', NULL, NULL, NULL, '2021-05-19 15:45:09', '2021-05-19 15:45:09', 0, 0);
INSERT INTO `t_tro_resource` VALUES (407, NULL, 0, 'bottleneckTable', '瓶颈列表', NULL, '[\"/api/patrol/manager/exception/query\"]', 12000, '[2,3,5,6]', NULL, NULL, NULL, '2021-05-19 15:45:09', '2021-05-19 15:45:09', 0, 0);
INSERT INTO `t_tro_resource` VALUES (408, NULL, 0, 'exceptionNoticeManage', '异常通知管理', NULL, '[\"/api/patrol/manager/exception_notice\"]', 13000, '[2,3,5,6]', NULL, NULL, NULL, '2021-05-19 15:45:09', '2021-05-19 15:45:09', 0, 0);
INSERT INTO `t_tro_resource` VALUES (409, NULL, 0, 'bottleneckConfig', '瓶颈配置', NULL, '[\"/api/patrol/manager/exception_config\"]', 14000, '[3]', NULL, NULL, NULL, '2021-05-19 15:45:09', '2021-09-24 15:05:22', 0, 0);
INSERT INTO `t_tro_resource` VALUES (410, NULL, 0, 'scriptManages', '脚本管理', NULL, '', 16000, '[]', NULL, NULL, NULL, '2021-07-05 14:13:16', '2021-08-06 19:42:09', 0, 0);
INSERT INTO `t_tro_resource` VALUES (411, 410, 0, 'scriptManage_ops', '运维脚本', NULL, '[\"/api/opsScriptManage\"]', 15000, '[2,3,4]', NULL, NULL, NULL, '2021-07-05 14:13:16', '2021-07-05 14:13:16', 0, 0);
INSERT INTO `t_tro_resource` VALUES (412, NULL, 0, 'patrolBoard', '巡检看板', NULL, '[\"/api/patrol/board\"]', 9999, '[2,3,4]', NULL, NULL, NULL, '2021-08-06 18:54:19', '2021-08-06 18:54:19', 0, 0);
INSERT INTO `t_tro_resource` VALUES (413, 11, 0, 'configCenter_middlewareManage', '中间件库管理', NULL, '[\"/api/application/middlewareSummary\"]', 6288, '[3]', NULL, NULL, NULL, '2021-08-06 18:54:19', '2022-01-10 14:40:09', 0, 1);
INSERT INTO `t_tro_resource` VALUES (501, NULL, 0, 'traceQuery', '链路查询', '', '[\"/api/apm/traceFlowManage/list\"]', 2500, '[]', NULL, NULL, NULL, '2021-08-25 02:16:56', '2021-08-25 02:16:56', 0, 0);
INSERT INTO `t_tro_resource` VALUES (502, NULL, 0, 'admins', '后台管理', NULL, '', 20000, '[]', NULL, NULL, NULL, '2021-09-03 18:15:59', '2021-09-03 18:15:59', 0, 0);
INSERT INTO `t_tro_resource` VALUES (503, 502, 0, 'admins_admin', '探针版本管理', NULL, '[\"/api/fast/agent/access\"]', 21000, '[2,7]', NULL, NULL, NULL, '2021-09-03 18:15:59', '2022-01-17 10:44:42', 0, 0);
INSERT INTO `t_tro_resource` VALUES (504, 502, 0, 'admins_simulationConfig', '仿真系统配置', NULL, '[\"/api/fast/agent/access/config\"]', 22000, '[3,4]', NULL, NULL, NULL, '2021-09-03 18:15:59', '2021-09-03 18:15:59', 0, 0);
INSERT INTO `t_tro_resource` VALUES (505, NULL, 0, 'appConfigs', '应用配置', NULL, '', 30000, '[]', NULL, NULL, NULL, '2021-09-03 18:15:59', '2021-09-03 18:15:59', 0, 0);
INSERT INTO `t_tro_resource` VALUES (506, 505, 0, 'appManage_appAccess', '新应用接入', NULL, '[\"/api/fast/agent/access\"]', 31000, '[2]', NULL, NULL, NULL, '2021-09-03 18:15:59', '2021-09-23 07:59:17', 0, 0);
INSERT INTO `t_tro_resource` VALUES (507, 505, 0, 'appManage_agentManage', '探针管理', NULL, '[\"/api/fast/agent/access/probe\"]', 32000, '[3]', NULL, NULL, NULL, '2021-09-03 18:15:59', '2022-02-09 16:57:27', 0, 0);
INSERT INTO `t_tro_resource` VALUES (508, 505, 0, 'appManage_errorLog', '异常日志', NULL, '[\"/api/fast/agent/access/errorLog\"]', 33000, '[]', NULL, NULL, NULL, '2021-09-03 18:15:59', '2021-09-03 18:15:59', 0, 0);
INSERT INTO `t_tro_resource` VALUES (509, NULL, 0, 'configCenter_globalConfig', '全局配置', NULL, '[\"/configCenter/globalConfig\"]', 33000, '[6]', NULL, NULL, NULL, '2021-10-12 21:06:35', '2021-12-05 21:48:05', 0, 0);
INSERT INTO `t_tro_resource` VALUES (511, NULL, 0, 'traceLogData', 'trace数据审计', NULL, '[\"/api/trace/log/list\"]', 33000, '[]', NULL, NULL, NULL, '2021-10-12 21:06:35', '2021-10-13 18:51:45', 0, 0);
INSERT INTO `t_tro_resource` VALUES (512, NULL, 0, 'system_info', '系统信息', NULL, '', 9000, '[]', NULL, NULL, NULL, '2021-01-14 11:19:50', '2021-12-08 11:28:17', 0, 0);
INSERT INTO `t_tro_resource` VALUES (513, NULL, 0, 'securityCenter', '安全中心', '', '[\"/securityCenter\"]', 40000, '[]', NULL, NULL, NULL, '2020-09-01 17:10:02', '2022-03-10 10:23:25', 0, 0);
INSERT INTO `t_tro_resource` VALUES (514, 513, 0, 'securityCenter_desensitizePolicysManage', '脱敏策略', NULL, '[\"/pro/securityCenter/desensitizePolicysManage\"]', 40001, '[2,3,4]', NULL, NULL, NULL, '2021-09-03 18:15:59', '2022-03-23 11:01:10', 0, 0);
INSERT INTO `t_tro_resource` VALUES (515, 513, 0, 'securityCenter_trafficConfig', '限流配置', NULL, '[\"/pro/securityCenter/trafficConfig\"]', 40002, '[3]', NULL, NULL, NULL, '2021-09-03 18:15:59', '2022-03-23 11:01:14', 0, 0);
INSERT INTO `t_tro_resource` VALUES (516, 513, 0, 'securityCenter_keysConfig', '秘钥对配置', NULL, '[\"/pro/securityCenter/keysConfig\"]', 40002, '[3]', NULL, NULL, NULL, '2021-09-03 18:15:59', '2022-03-23 16:58:39', 0, 0);
INSERT INTO `t_tro_resource` VALUES (517, 8, 0, 'pressureTestManage_recycle', '回收站', NULL, '[\"/api/scenemanage/list\"]', 3000, '[]', NULL, NULL, NULL, '2022-05-05 10:02:41', '2022-05-11 16:15:43', 0, 0);
INSERT INTO `t_tro_resource` VALUES (519, 8, 0, 'pressureTestManage_testByInterface', 'Takin压测', NULL, '[\"api/interfaceperformance/config/query\"]', 3001, '[2,3,4]', NULL, NULL, NULL, '2020-09-01 17:38:28', '2022-06-06 21:33:58', 0, 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
