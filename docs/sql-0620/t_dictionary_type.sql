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

 Date: 20/06/2022 16:10:14
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_dictionary_type
-- ----------------------------
DROP TABLE IF EXISTS `t_dictionary_type`;
CREATE TABLE `t_dictionary_type` (
  `ID` varchar(50) NOT NULL COMMENT 'ID',
  `TYPE_NAME` varchar(50) DEFAULT NULL COMMENT '分类名称',
  `ACTIVE` char(1) DEFAULT NULL COMMENT '是否启用',
  `CREATE_TIME` date DEFAULT NULL COMMENT '创建时间',
  `MODIFY_TIME` date DEFAULT NULL COMMENT '更新时间',
  `CREATE_USER_CODE` varchar(50) DEFAULT NULL COMMENT '创建人',
  `MODIFY_USER_CODE` varchar(50) DEFAULT NULL COMMENT '更新人',
  `PARENT_CODE` varchar(50) DEFAULT NULL COMMENT '上级分类编码',
  `TYPE_ALIAS` varchar(50) DEFAULT NULL COMMENT '分类别名',
  `IS_LEAF` char(1) DEFAULT NULL COMMENT '是否为子分类',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据字典分类表';

-- ----------------------------
-- Records of t_dictionary_type
-- ----------------------------
BEGIN;
INSERT INTO `t_dictionary_type` VALUES ('202104281025590e2e00000000000001', '巡检类型', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, 'PATROL_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('202104281025590e2e00000000000002', '巡检异常类型', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, 'PATROL_EXCEPTION_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('202104281025590e2e00000000000003', '巡检异常程度', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, 'PATROL_EXCEPTION_LEVEL', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('202104281025590e2e00000000000004', '巡检异常状态', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, 'PATROL_EXCEPTION_STATUS', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('202104281025590e2e00000000000005', '巡检异常通知渠道', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, 'NOTIFY_CHANNEL', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('202104281025590e2e00000000000006', '巡检异常-状态[误判]更改原因', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, 'PATROL_EXCEPTION_STATUS_CHANGE_1', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('2021060319173remotecall0001', '远程调用接口类型', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, 'REMOTE_CALL_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('20210616opsscript0001', '运维脚本类型', 'Y', '2021-06-16', NULL, NULL, NULL, NULL, 'OPS_SCRIPT_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('2e81620c1b74421cbe071224822e5725', '链路探活取数来源', 'Y', '2019-07-10', '2019-07-10', '000000', '000000', NULL, 'LIVE_SOURCE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('2e81620c1disodsobe071224822e5725', 'HTTP请求类型', 'Y', '2020-07-07', '2020-07-07', '000000', '000000', NULL, 'HTTP_METHOD_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('3ea3284fbca841bcbcda50afa0d8a24b', '单量计算方式', 'Y', '2019-04-04', '2019-04-04', '000000', '000000', NULL, 'VOLUME_CALC_STATUS', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('41a309a6d1e04105acfd5fb08200f0c5', 'http类型', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, 'HTTP_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('470557d08abe4eb6a794764209c7763d', '页面分类', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, 'PAGE_LEVEL', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('51a309asdsdfads8779fd5fb08200f03u', '业务域类型', 'Y', '2021-01-15', '2021-01-15', '000000', '000000', NULL, 'domain', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('6ba75716d726493783bfd64cce058110', '压力机状态', 'Y', '2020-11-17', '2020-11-17', '000000', '000000', NULL, 'PRESSURE_MACHINE_STATUS', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('6ba75716d726493783bfd64cce058780', '链路探活时间', 'Y', '2019-07-10', '2019-07-10', '000000', '000000', NULL, 'LINK_LIVE_TIME', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('8c3c52c6889c435a8e055d988016e02a', '接口分类', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, 'INTERFACE_LEVEL', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('91a309asdsdfads2323fd5fb08200ikld', '推送内容模板参数', 'Y', '2021-05-14', '2021-05-14', '000000', '000000', NULL, 'PUSH_MESSAGE_PARAM', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('944da50e5a334e128b34df906971b113', '应用上传信息类型', 'Y', '2019-03-29', '2019-03-29', '000000', '000000', NULL, 'UPLOAD_INFO_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('ada9370f26ac4c79acbac0ad7acb0992', '数据库类型', 'Y', '2018-08-30', '2018-08-30', NULL, NULL, NULL, 'DBTYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('b8f4ce1a989b4f19a842e08975ee3eb1', '链路类型', 'Y', '2018-12-24', '2018-12-24', '578992', NULL, NULL, 'LINK_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('ca888ed801664c81815d8c4f5b8dff0c', '白名单', 'Y', '2018-07-05', '2018-07-05', '', '', '', 'WLIST', '', 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('d69be19362c4461fb7e84dfbc21f1747', '链路等级', 'Y', '2018-07-05', '2018-07-05', '', '', '', 'LINKRANK', '', 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('ec18bced105c41018cfbcaf6b3327b9a', 'MQ消息类型', 'Y', '2018-07-30', '2018-07-30', '557092', NULL, NULL, 'MQMSG', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f48c856d2aec493a8902da7485720404', 'JOB调度间隔', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, 'JOB_INTERVAL', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33aai1', '规格类型', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 'SPEC_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33eece', '链路所属模块', 'Y', '2019-04-04', '2019-04-04', '000000', '000000', NULL, 'LINK_MODULE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33eegb', '云服务器', 'Y', '2020-03-12', '2020-03-12', '000000', '000000', NULL, 'CLOUD_SERVER', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33eegc', '场景状态', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, 'SCENE_MANAGE_STATUS', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33eegd', '是否删除', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, 'IS_DELETED', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33eege', '脚本类型', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, 'SCRIPT_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33eegf', '文件类型', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, 'FILE_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33eegg', 'SLA指标类型', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, 'SLA_TARGER_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33eegh', '数值比较', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, 'COMPARE_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33eegk', '存活状态', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, 'LIVE_STATUS', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33ios0', '机器任务类型', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 'MACHINE_TASK_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33ios7', '机器任务状态', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 'MACHINE_TASK_STATUS', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33kklll', '可验证的数据源类型', 'Y', '2021-01-06', '2021-01-06', '000000', '000000', NULL, 'VERIFY_DATASOURCE_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33mnk1', '机器状态', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 'MACHINE_STATUS', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33wer1', '开通模式', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 'OPEN_TYPE', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33wer2', '影子消费者', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 'SHADOW_CONSUMER', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33wex3', '中间件状态', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, 'MIDDLEWARE_STATUS', NULL, 'test', 1);
INSERT INTO `t_dictionary_type` VALUES ('f644eb266aba4a2186341b708f33wex6', '中间件类型', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, 'MIDDLEWARE_TYPE', NULL, 'test', 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
