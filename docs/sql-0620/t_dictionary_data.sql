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

 Date: 20/06/2022 16:09:52
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_dictionary_data
-- ----------------------------
DROP TABLE IF EXISTS `t_dictionary_data`;
CREATE TABLE `t_dictionary_data` (
  `ID` varchar(50) NOT NULL COMMENT 'ID',
  `DICT_TYPE` varchar(50) DEFAULT NULL COMMENT '数据字典分类',
  `VALUE_ORDER` int(11) DEFAULT NULL COMMENT '序号',
  `VALUE_NAME` varchar(50) DEFAULT NULL COMMENT '值名称',
  `VALUE_CODE` varchar(50) DEFAULT NULL COMMENT '值代码',
  `LANGUAGE` varchar(10) DEFAULT NULL COMMENT '语言',
  `ACTIVE` char(1) DEFAULT NULL COMMENT '是否启用',
  `CREATE_TIME` date DEFAULT NULL COMMENT '创建时间',
  `MODIFY_TIME` date DEFAULT NULL COMMENT '更新时间',
  `CREATE_USER_CODE` varchar(50) DEFAULT NULL COMMENT '创建人',
  `MODIFY_USER_CODE` varchar(50) DEFAULT NULL COMMENT '更新人',
  `NOTE_INFO` varchar(250) DEFAULT NULL COMMENT '备注信息',
  `VERSION_NO` bigint(20) DEFAULT '0' COMMENT '版本号',
  `env_code` varchar(20) NOT NULL DEFAULT 'system' COMMENT '环境code',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户id',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `IDX_MUL_QUERY` (`ACTIVE`,`DICT_TYPE`,`VALUE_CODE`) USING BTREE,
  KEY `idx_VERSION_NO` (`VERSION_NO`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据字典基础数据表';

-- ----------------------------
-- Records of t_dictionary_data
-- ----------------------------
BEGIN;
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309485c84ae0d645f26das32', '6ba75716d726493783bfd64cce058110', -1, '离线', '-1', 'ZH_CN', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309485c84ae0d645f26das33', '6ba75716d726493783bfd64cce058110', 0, '空闲', '0', 'ZH_CN', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309485c84ae0d645f26das34', '6ba75716d726493783bfd64cce058110', 1, '压测中', '1', 'ZH_CN', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309485c84ae0d645f26de5a', 'f48c856d2aec493a8902da7485720404', 3, '《 15分钟', 'less15', 'ZH_CN', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309485c84aeo9s45f26de5a', 'f644eb266aba4a2186341b708f33ios0', 1, '开通任务', '1', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de10', 'f644eb266aba4a2186341b708f33mnk1', 1, '开通中', '1', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de12', 'f644eb266aba4a2186341b708f33mnk1', 2, '开通成功', '2', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de13', 'f644eb266aba4a2186341b708f33mnk1', 3, '启动中', '3', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de14', 'f644eb266aba4a2186341b708f33mnk1', 4, '启动失败', '4', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de15', 'f644eb266aba4a2186341b708f33mnk1', 5, '初始化中', '5', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de16', 'f644eb266aba4a2186341b708f33mnk1', 6, '初始化失败', '6', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de17', 'f644eb266aba4a2186341b708f33mnk1', 7, '运行中', '7', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de18', 'f644eb266aba4a2186341b708f33mnk1', 8, '销毁中', '8', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de19', 'f644eb266aba4a2186341b708f33mnk1', 9, '已过期', '9', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de20', 'f644eb266aba4a2186341b708f33mnk1', 10, '已锁定', '10', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de21', 'f644eb266aba4a2186341b708f33mnk1', 11, '销毁失败', '11', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26de22', 'f644eb266aba4a2186341b708f33mnk1', 12, '已销毁', '12', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26rty1', 'f644eb266aba4a2186341b708f33aai1', 1, '2核8G500G磁盘', '1', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26rty2', 'f644eb266aba4a2186341b708f33aai1', 2, '8核32G500G磁盘', '2', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309llop8sdso9s45f26rty3', 'f644eb266aba4a2186341b708f33aai1', 3, '16核64G500G磁盘', '3', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309ppp984aeo9s45f26de5a', 'f644eb266aba4a2186341b708f33ios0', 2, '销毁任务', '2', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309ppp98sdso9s45f26de12', 'f644eb266aba4a2186341b708f33ios7', 2, '开通失败', '2', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309ppp98sdso9s45f26de13', 'f644eb266aba4a2186341b708f33ios7', 3, '开通成功', '3', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309ppp98sdso9s45f26de14', 'f644eb266aba4a2186341b708f33ios7', 4, '销毁中', '4', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309ppp98sdso9s45f26de15', 'f644eb266aba4a2186341b708f33ios7', 5, '销毁失败', '5', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309ppp98sdso9s45f26de16', 'f644eb266aba4a2186341b708f33ios7', 6, '销毁成功', '6', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309ppp98sdso9s45f26de5a', 'f644eb266aba4a2186341b708f33ios7', 1, '开通中', '1', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309tyu98sdso9s45f26de12', 'f644eb266aba4a2186341b708f33wer1', 1, '长期开通', '1', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('0723f0bef309tyu98sdso9s45f26de13', 'f644eb266aba4a2186341b708f33wer1', 2, '短期抢占', '2', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('07461d8c848e4644b563f9eef56bf1a7', '41a309a6d1e04105acfd5fb08200f0c5', 1, '页面', 'PAGE', 'ZH_CN', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('092e39d5b9614092b333f849588ee288', 'b8f4ce1a989b4f19a842e08975ee3eb1', 1, '业务链路', 'BUSINESS_LINK', 'zh_CN', 'Y', '2018-12-24', '2018-12-24', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('12e7f7fc6f5240ea906a8d13344f86ba', 'f48c856d2aec493a8902da7485720404', 2, '《 5分钟', 'less5', 'ZH_CN', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('12f606f88ebc4b04806f9c094e94d3f3', '470557d08abe4eb6a794764209c7763d', 2, '简单查询页面/复杂界面', '5s', 'ZH_CN', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('16f6a9e9f20947cfa4e4cd4f5f5430fa', 'f644eb266aba4a2186341b708f33eece', 6, '签收', 'SIGNING', 'ZH_CN', 'N', '2019-04-04', '2019-04-04', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('17101eefd9ff4624a581d2e6c29cd7b3', '6ba75716d726493783bfd64cce058780', 3, '近1周', '168', 'ZH_CN', 'Y', '2019-07-10', '2019-07-11', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('1bfe98ddb7a84401b04b108f402e4a16', '8c3c52c6889c435a8e055d988016e02a', 2, '一般操作/查询', '100ms', 'ZH_CN', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104231502560e2e06000000000001', '202104281025590e2e00000000000006', 0, '系统误判(规则不完善)', '1', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104231502560e2e06000000000002', '202104281025590e2e00000000000006', 1, '不可抗因素(机房断电等)', '2', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104231502560e2e06000000000003', '202104281025590e2e00000000000006', 2, '其它', '3', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e01000000000001', '202104281025590e2e00000000000001', 0, '业务', '0', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e01000000000002', '202104281025590e2e00000000000001', 0, '技术', '1', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e02000000000001', '202104281025590e2e00000000000002', 1, '卡慢', '1', 'ZH_CN', 'Y', '2021-04-23', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e02000000000002', '202104281025590e2e00000000000002', 2, '接口异常', '2', 'ZH_CN', 'Y', '2021-04-23', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e02000000000003', '202104281025590e2e00000000000002', 0, '全部', '0', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e02000000000004', '202104281025590e2e00000000000002', 3, '巡检异常', '3', 'ZH_CN', 'Y', '2021-10-14', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e02000000000005', '202104281025590e2e00000000000002', 4, '慢SQL', '4', 'ZH_CN', 'Y', '2021-10-14', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e02000000000006', '202104281025590e2e00000000000002', 5, '流量异常', '5', 'ZH_CN', 'Y', '2021-10-14', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e03000000000001', '202104281025590e2e00000000000003', 0, '一般', '1', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e03000000000002', '202104281025590e2e00000000000003', 1, '严重', '2', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e04000000000001', '202104281025590e2e00000000000005', 0, '钉钉', '0', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e04000000000002', '202104281025590e2e00000000000005', 1, '企业微信', '1', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e04000000000003', '202104281025590e2e00000000000005', 2, '自定义', '2', 'ZH_CN', 'Y', '2021-04-23', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e05000000000001', '202104281025590e2e00000000000004', 0, '待处理', '1', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e05000000000002', '202104281025590e2e00000000000004', 1, '已恢复', '2', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('202104281025590e2e05000000000003', '202104281025590e2e00000000000004', 2, '判定为误报', '3', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('2021060319173opsscripttype1001', '20210616opsscript0001', 1, '影子库表创建脚本', '1', 'ZH_CN', 'Y', '2021-06-16', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('2021060319173opsscripttype1002', '20210616opsscript0001', 2, '基础数据准备脚本', '2', 'ZH_CN', 'Y', '2021-06-16', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('2021060319173opsscripttype1003', '20210616opsscript0001', 3, '铺底数据脚本', '3', 'ZH_CN', 'Y', '2021-06-16', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('2021060319173opsscripttype1004', '20210616opsscript0001', 4, '影子库表清理脚本', '4', 'ZH_CN', 'Y', '2021-06-16', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('2021060319173opsscripttype1005', '20210616opsscript0001', 5, '缓存预热脚本', '5', 'ZH_CN', 'Y', '2021-06-16', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('2021060319173remotecallnode1001', '2021060319173remotecall0001', 0, 'HTTP', '0', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('2021060319173remotecallnode1002', '2021060319173remotecall0001', 0, 'DUBBO', '1', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('2021060319173remotecallnode1003', '2021060319173remotecall0001', 0, 'FEIGN', '2', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('226588d83e814de09e703f10d4582a85', '944da50e5a334e128b34df906971b113', 1, '堆栈异常', '1', 'ZH_CN', 'Y', '2019-03-29', '2019-03-29', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('287b5429a5634e72838f53023673fdf9', '6ba75716d726493783bfd64cce058780', 2, '近1天', '24', 'ZH_CN', 'Y', '2019-07-10', '2019-07-11', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('29e1ac2d7b614b0686cb5e00ee476862', 'f48c856d2aec493a8902da7485720404', 4, '《 60分钟', 'less60', 'ZH_CN', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('352f90a0e56c41908e543aa421359a31', 'ca888ed801664c81815d8c4f5b8dff0c', 5, 'MQ', 'mq', 'ZH_CN', 'Y', '2019-04-03', '2019-04-03', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('425fffeddc0b42bca9908a167abd98af', 'f644eb266aba4a2186341b708f33eece', 7, 'CUBC结算', 'SETTLEMENT', 'ZH_CN', 'N', '2019-04-04', '2019-04-04', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('445521c501cc4ea3bf19732ea81d29b2', 'ca888ed801664c81815d8c4f5b8dff0c', 1, 'HTTP', 'http', 'zh_CN', 'Y', '2018-07-05', '2019-04-03', '', '', '', 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('45ddc5768689413386ac14bc3442d539', '944da50e5a334e128b34df906971b113', 2, 'SQL解析异常', '2', 'ZH_CN', 'Y', '2019-03-29', '2019-03-29', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('4642aca4bcac47288b279a9e044cea54', '2e81620c1b74421cbe071224822e5725', 2, 'PRADAR', 'pradar', 'ZH_CN', 'Y', '2019-07-10', '2019-07-10', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('49a8c79896ea41a784b6b61aa083f542', 'f644eb266aba4a2186341b708f33eece', 8, '轨迹', 'TRAIL', 'ZH_CN', 'N', '2019-04-17', '2019-04-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('4e27eecaeb3a4d1a865c5bd874ad127b', 'ca888ed801664c81815d8c4f5b8dff0c', 4, 'JOB', 'job', 'ZH_CN', 'Y', '2019-03-04', '2019-04-03', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('5434f289c4d5494e8002b842893153b3', '6ba75716d726493783bfd64cce058780', 5, '自定义时间段', '0', 'ZH_CN', 'Y', '2019-07-10', '2019-07-11', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('576ab6777e7f410289b153635324120d', 'ada9370f26ac4c79acbac0ad7acb0992', 1, 'MYSQL', 'MYSQL', 'zh_CN', 'Y', '2018-08-30', '2018-08-30', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('5cea119bd6f0473db30cd227109285bc', 'f644eb266aba4a2186341b708f33eece', 4, '移动端-教师', 'TRANSFER', 'ZH_CN', 'Y', '2019-04-04', '2019-04-04', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('5d59803f47cc41e0a8b4cf1867248954', '2e81620c1b74421cbe071224822e5725', 1, 'SPT', 'spt', 'ZH_CN', 'Y', '2019-07-10', '2019-07-10', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('6438e36d424c4f54ab4f0773d338f9a1', '51a309asdsdfads8779fd5fb08200f03u', 0, '订单域', '0', 'ZH_CN', 'Y', '2021-01-15', '2021-01-15', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('6438e36d424c4f54ab4f0773d338f9a2', '51a309asdsdfads8779fd5fb08200f03u', 1, '运单域', '1', 'ZH_CN', 'Y', '2021-01-15', '2021-01-15', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('6438e36d424c4f54ab4f0773d338f9a3', '51a309asdsdfads8779fd5fb08200f03u', 2, '结算域', '2', 'ZH_CN', 'Y', '2021-01-15', '2021-01-15', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('702e091427f44807ae4baed6e1871078', '3ea3284fbca841bcbcda50afa0d8a24b', 1, '计算订单量', 'orderVolume', 'ZH_CN', 'Y', '2019-04-04', '2019-04-09', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('782ccd949d9f453a857577a20b06392a', 'ec18bced105c41018cfbcaf6b3327b9a', 4, 'DPBOOT_ROCKETMQ', 'DPBOOT_ROCKETMQ', 'zh_CN', 'Y', '2018-10-10', '2018-10-10', '', '', '', 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('7ac9c0823e3645a1afd3d2f521d468c0', 'f48c856d2aec493a8902da7485720404', 1, '《 1分钟', 'less1', 'ZH_CN', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('7d16b5e08c66499da224adb240da6426', 'f644eb266aba4a2186341b708f33eece', 3, '移动端-学生', 'BILLING', 'ZH_CN', 'Y', '2019-04-04', '2019-04-04', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('81a43f092f9a4df6a1dfd23b45213eef', 'ada9370f26ac4c79acbac0ad7acb0992', 2, 'ORACLE', 'ORACLE', 'zh_CN', 'Y', '2018-08-30', '2018-08-30', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('853152241ac7404c8910f2c723a50863', '8c3c52c6889c435a8e055d988016e02a', 3, '复杂操作', '300ms', 'ZH_CN', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('880554742fa343d8adc9faff1a632999', 'd69be19362c4461fb7e84dfbc21f1747', 2, 'p1', 'p1', 'zh_CN', 'Y', '2018-07-05', '2018-07-05', '', '', '', 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('8a57a87e1ee14effaa5a99d65b7f9780', '8c3c52c6889c435a8e055d988016e02a', 5, '调用外网操作', 'other', 'ZH_CN', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('8a5a846bed734a76914aa133ad7c8d45', '470557d08abe4eb6a794764209c7763d', 1, '普通页面加载', '3s', 'ZH_CN', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('8c0b5b11fe324b22a8ef010fa8799b23', '3ea3284fbca841bcbcda50afa0d8a24b', 2, '计算运单量', 'billingVolume', 'ZH_CN', 'Y', '2019-04-04', '2019-04-09', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('9009f0bef309485c84aeo9s45f26de01', '2e81620c1disodsobe071224822e5725', 1, 'PUT', '1', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('9009f0bef309485c84aeo9s45f26de02', '2e81620c1disodsobe071224822e5725', 2, 'GET', '2', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('9009f0bef309485c84aeo9s45f26de03', '2e81620c1disodsobe071224822e5725', 3, 'POST', '3', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('9009f0bef309485c84aeo9s45f26de04', '2e81620c1disodsobe071224822e5725', 4, 'OPTIONS', '4', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('9009f0bef309485c84aeo9s45f26de05', '2e81620c1disodsobe071224822e5725', 5, 'DELETE', '5', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('9009f0bef309485c84aeo9s45f26de06', '2e81620c1disodsobe071224822e5725', 6, 'HEAD', '6', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('9009f0bef309485c84aeo9s45f26de07', '2e81620c1disodsobe071224822e5725', 7, 'TRACE', '7', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('9009f0bef309485c84aeo9s45f26de08', '2e81620c1disodsobe071224822e5725', 8, 'CONNECT', '8', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('9009f0bef309485c84aeo9s45f26de09', '2e81620c1disodsobe071224822e5725', 9, 'DEFAULT', '0', 'ZH_CN', 'Y', '2020-05-14', '2020-05-14', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('901fb9678a254b80b30e527b7a980947', 'f644eb266aba4a2186341b708f33eece', 10, '非主流程链路', 'NON_CORE_LINKE', 'ZH_CN', 'N', '2019-04-04', '2019-04-04', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('91e2170859034632bfa6ab4d4c49fed8', 'd69be19362c4461fb7e84dfbc21f1747', 4, 'p3', 'p3', 'zh_CN', 'Y', '2018-07-05', '2018-07-05', '', '', '', 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('a54e6e51ee9c49cf8968bc3976f1ef19', '3ea3284fbca841bcbcda50afa0d8a24b', 3, '不计算单量', 'noVolume', 'ZH_CN', 'Y', '2019-04-09', '2019-04-09', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('af5d138bd14d4d4bbfe6eafb411bc029', 'ca888ed801664c81815d8c4f5b8dff0c', 3, '禁止压测', 'block', 'zh_CN', 'Y', '2018-05-14', '2018-05-14', '', '', '', 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('b4924562ebf745a1a631762c5b89b12f', 'ca888ed801664c81815d8c4f5b8dff0c', 2, 'DUBBO', 'dubbo', 'zh_CN', 'Y', '2018-07-05', '2019-04-03', '', '', '', 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('bcb4bd52b1984336930c255056403b31', 'd69be19362c4461fb7e84dfbc21f1747', 1, 'p0', 'p0', 'zh_CN', 'Y', '2018-07-05', '2018-07-05', '', '', '', 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('c2a41e51ced0457b9cfeb09e31f137a9', 'f644eb266aba4a2186341b708f33eece', 1, 'PC学生', 'CREATE_ORDER', 'ZH_CN', 'Y', '2019-04-04', '2019-04-04', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('cfccbe57756f4716accb165e1a568ef8', '6ba75716d726493783bfd64cce058780', 4, '近1月', '720', 'ZH_CN', 'Y', '2019-07-10', '2019-07-11', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('d5acad1497414134a3547a13ce661054', '41a309a6d1e04105acfd5fb08200f0c5', 2, '接口', 'INTERFACE', 'ZH_CN', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('d6cb7549a94c4e4687318bff334a8601', 'f644eb266aba4a2186341b708f33eece', 5, '派送', 'DELIVERY', 'ZH_CN', 'N', '2019-04-04', '2019-04-04', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('dde28d43648b4428b80e06469ec679b0', 'ec18bced105c41018cfbcaf6b3327b9a', 1, 'ESB', 'ESB', 'zh_CN', 'Y', '2018-07-30', '2018-07-30', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('dee1a075947641d4846304e568f2052e', 'ec18bced105c41018cfbcaf6b3327b9a', 3, 'ROCKETMQ', 'ROCKETMQ', 'zh_CN', 'Y', '2018-08-16', '2018-08-16', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('e1f9137866a4436188513255ca2567ac', 'b8f4ce1a989b4f19a842e08975ee3eb1', 2, '技术链路', 'TECHNOLOGY_LINK', 'zh_CN', 'Y', '2018-12-24', '2018-12-24', '', '', '', 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('e4217237551a430abd346ee09bc38740', '6ba75716d726493783bfd64cce058780', 1, '近1小时', '1', 'ZH_CN', 'Y', '2019-07-10', '2019-07-10', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('eb81a4b38b404ce5b6a966cb96640897', 'f644eb266aba4a2186341b708f33eece', 2, 'PC教师', 'ORDER', 'ZH_CN', 'Y', '2019-04-04', '2019-04-04', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('ec18bced105c41018cfbcaf6b3327b9a', 'ec18bced105c41018cfbcaf6b3327b9a', 2, 'IBM', 'IBM', 'zh_CN', 'Y', '2018-07-30', '2018-07-30', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f22c57900cfc4069a965bf29c17e8d86', '8c3c52c6889c435a8e055d988016e02a', 1, '简单操作/查询', '50ms', 'ZH_CN', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f26c27c432644ca985b95ca46979c422', '8c3c52c6889c435a8e055d988016e02a', 4, '涉及级联嵌套调用多服务操作', '500ms', 'ZH_CN', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b708f33wex4', 'f644eb266aba4a2186341b708f33wex3', 0, '未知', '4', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b708f33wex5', 'f644eb266aba4a2186341b708f33wex3', 2, '未支持', '2', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b708f33wex6', 'f644eb266aba4a2186341b708f33wex3', 1, '已支持', '1', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b708f33wex7', 'f644eb266aba4a2186341b708f33wex3', 3, '无需支持', '3', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we01', 'f644eb266aba4a2186341b708f33wex6', 1, '连接池', '连接池', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we02', 'f644eb266aba4a2186341b708f33wex6', 2, '存储', '存储', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we03', 'f644eb266aba4a2186341b708f33wex6', 3, '消息队列', '消息队列', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we04', 'f644eb266aba4a2186341b708f33wex6', 4, '配置', '配置', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we05', 'f644eb266aba4a2186341b708f33wex6', 5, 'http-client', 'http-client', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we06', 'f644eb266aba4a2186341b708f33wex6', 6, '缓存', '缓存', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we07', 'f644eb266aba4a2186341b708f33wex6', 7, '数据源', '数据源', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we08', 'f644eb266aba4a2186341b708f33wex6', 8, 'RPC框架', 'RPC框架', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we09', 'f644eb266aba4a2186341b708f33wex6', 9, '定时任务', '定时任务', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we10', 'f644eb266aba4a2186341b708f33wex6', 10, '容器', '容器', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we11', 'f644eb266aba4a2186341b708f33wex6', 11, '网关', '网关', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we12', 'f644eb266aba4a2186341b708f33wex6', 12, '其他', '其他', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('f644eb266aba4a2186341b709f33we13', 'f644eb266aba4a2186341b708f33wex6', 13, '日志', '日志', 'ZH_CN', 'Y', '2021-06-09', NULL, NULL, NULL, NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('faf2c31d2f4c47c0be7426a36dc02bfc', 'd69be19362c4461fb7e84dfbc21f1747', 3, 'p2', 'p2', 'zh_CN', 'Y', '2018-05-21', '2018-05-21', '', '', '', 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fda90', 'f644eb266aba4a2186341b708f33eegg', 5, 'CPU利用率', '4', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fda91', 'f644eb266aba4a2186341b708f33eegg', 6, '内存占用', '5', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb12f', 'f644eb266aba4a2186341b708f33eegb', 3, '云服务器', '华为云', 'ZH_CN', 'Y', '2020-03-12', '2020-03-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb4a', 'f644eb266aba4a2186341b708f33eegb', 1, '云服务器', '阿里云ECS', 'ZH_CN', 'Y', '2020-03-12', '2020-03-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb60', 'f644eb266aba4a2186341b708f33eegc', 0, '待启动', '0', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb61', 'f644eb266aba4a2186341b708f33eegc', 1, '启动中', '1', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb62', 'f644eb266aba4a2186341b708f33eegc', 2, '压测中', '2', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb6f', '470557d08abe4eb6a794764209c7763d', 3, '复杂查询页面', '8s', 'ZH_CN', 'Y', '2019-06-12', '2019-06-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb87', 'f644eb266aba4a2186341b708f33eegb', 2, '云服务器', '腾讯云', 'ZH_CN', 'Y', '2020-03-12', '2020-03-12', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb90', 'f644eb266aba4a2186341b708f33eegd', 1, '否', '0', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb91', 'f644eb266aba4a2186341b708f33eegd', 2, '是', '1', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb92', 'f644eb266aba4a2186341b708f33eege', 1, 'JMeter', '0', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb93', 'f644eb266aba4a2186341b708f33eege', 2, 'Gatling', '1', 'ZH_CN', 'N', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb94', 'f644eb266aba4a2186341b708f33eege', 3, 'LoadRunner', '2', 'ZH_CN', 'N', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb95', 'f644eb266aba4a2186341b708f33eegf', 1, '脚本文件', '0', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb96', 'f644eb266aba4a2186341b708f33eegf', 2, '数据文件', '1', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb97', 'f644eb266aba4a2186341b708f33eegg', 1, 'RT', '0', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb98', 'f644eb266aba4a2186341b708f33eegg', 2, 'TPS', '1', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdb99', 'f644eb266aba4a2186341b708f33eegg', 3, '成功率', '2', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdc01', 'f644eb266aba4a2186341b708f33eegg', 4, 'SA', '3', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdc02', 'f644eb266aba4a2186341b708f33eegh', 1, '>=', '0', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdc03', 'f644eb266aba4a2186341b708f33eegh', 2, '>', '1', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdc04', 'f644eb266aba4a2186341b708f33eegh', 3, '=', '2', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdc05', 'f644eb266aba4a2186341b708f33eegh', 4, '<=', '3', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdc06', 'f644eb266aba4a2186341b708f33eegh', 5, '<', '4', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdc07', 'f644eb266aba4a2186341b708f33eegk', 1, '启用', '0', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('fc77d5f788dc45528b039d5b1b4fdc08', 'f644eb266aba4a2186341b708f33eegk', 2, '禁用', '1', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('iodld5f788dc45dsfds098d5b1b4fcv01', '91a309asdsdfads2323fd5fb08200ikld', 1, '<slp>巡检面板</slp>', 'BOARD_NAME', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('iodld5f788dc45dsfds098d5b1b4fcv02', '91a309asdsdfads2323fd5fb08200ikld', 2, '<slp>巡检场景</slp>', 'PATROL_SCENE_NAME', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('iodld5f788dc45dsfds098d5b1b4fcv03', '91a309asdsdfads2323fd5fb08200ikld', 3, '<slp>巡检任务</slp>', 'PATROL_TASK', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('iodld5f788dc45dsfds098d5b1b4fcv04', '91a309asdsdfads2323fd5fb08200ikld', 4, '<slp>瓶颈结果</slp>', 'EXCEPTION_TYPE', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('iodld5f788dc45dsfds098d5b1b4fcv05', '91a309asdsdfads2323fd5fb08200ikld', 5, '<slp>瓶颈程度</slp>', 'EXCEPTION_LEVEL', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('iodld5f788dc45dsfds098d5b1b4fcv06', '91a309asdsdfads2323fd5fb08200ikld', 6, '<slp>瓶颈ID</slp>', 'EXCEPTION_ID', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('iodld5f788dc45dsfds098d5b1b4fcv07', '91a309asdsdfads2323fd5fb08200ikld', 7, '<slp>持续时间</slp>', 'LAST_TIME', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('iodld5f788dc45dsfds098d5b1b4fcv08', '91a309asdsdfads2323fd5fb08200ikld', 8, '<slp>瓶颈详情</slp>', 'DETAIL_URL', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('iodld5f788dc45dsfds098d5b1b4fcv09', '91a309asdsdfads2323fd5fb08200ikld', 9, '<slp>发现时间</slp>', 'START_TIME', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('tc77d5f788dc45dsfds039d5b1b4fdc01', 'f644eb266aba4a2186341b708f33kklll', 0, 'MYSQL', '0', 'ZH_CN', 'Y', '2021-01-06', '2021-01-06', NULL, NULL, NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('tc77d5f788dc45dsfds039d5b1b4fdc10', 'f644eb266aba4a2186341b708f33wer2', 0, 'ROCKETMQ', 'ROCKETMQ', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('tc77d5f788dc45dsfds039d5b1b4fdc11', 'f644eb266aba4a2186341b708f33wer2', 1, 'KAFKA', 'KAFKA', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0, 'system', -1);
INSERT INTO `t_dictionary_data` VALUES ('tc77d5f788dc45dsfds039d5b1b4fdc12', 'f644eb266aba4a2186341b708f33wer2', 2, 'RABBITMQ', 'RABBITMQ', 'ZH_CN', 'Y', '2021-09-07', '2021-09-07', '000000', '000000', NULL, 0, 'system', -1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
