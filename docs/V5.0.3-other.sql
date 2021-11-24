SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_config_server
-- ----------------------------
DROP TABLE IF EXISTS `t_config_server`;
CREATE TABLE `t_config_server` (
                                   `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                                   `key` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '配置的 key',
                                   `value` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '配置的值',
                                   `tenant_id` bigint(20) DEFAULT '-99' COMMENT '租户id, -99 表示无',
                                   `env_code` varchar(20) COLLATE utf8mb4_bin DEFAULT '' COMMENT '环境',
                                   `tenant_app_key` varchar(80) COLLATE utf8mb4_bin DEFAULT '' COMMENT '租户key',
                                   `is_tenant` tinyint(3) unsigned DEFAULT '1' COMMENT '是否是住户使用, 1 是, 0 否',
                                   `is_global` tinyint(3) unsigned DEFAULT '1' COMMENT '是否是全局的, 1 是, 0 否',
                                   `edition` tinyint(3) unsigned DEFAULT '6' COMMENT '归属版本, 1 开源版, 2 企业版, 6 开源版和企业版',
                                   `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                   `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                   `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
                                   PRIMARY KEY (`id`) USING BTREE,
                                   KEY `idx_k_uk_e` (`key`,`tenant_app_key`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='配置表-服务的配置';

-- ----------------------------
-- Records of t_config_server
-- ----------------------------
BEGIN;
INSERT INTO `t_config_server` VALUES (2, 'takin.fast.debug.call.stack.path', '/data/fastdebug/callstack', -99, 'test', 'default', 0, 1, 2, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (3, 'takin.fast.watch.time.second', '120', -99, 'test', 'default', 0, 1, 2, '2021-10-12 12:01:45', '2021-11-11 11:28:42', 0);
INSERT INTO `t_config_server` VALUES (4, 'takin.login.dingding.push.enable', 'false', -99, 'test', 'default', 1, 1, 2, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (5, 'takin.login.dingding.push.url', '', -99, 'test', 'default', 1, 1, 2, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (6, 'takin.remote-call.sync', 'true', -99, 'test', 'default', 1, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (7, 'takin.remote.call.auto.join.white', 'false', -99, 'test', 'default', 1, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (8, 'takin.file.ops_script.path', 'ops_nfs_dir/', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (9, 'takin.file.ops_script.deploy_user', 'appdeploy', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (10, 'takin.file.ops_script.deploy_user_enable', 'false', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (11, 'takin.white_list.number.limit', '5', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (12, 'takin.white_list.duplicate.name.check', 'false', -99, 'test', 'default', 1, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (13, 'takin.white_list.config.path', '/data/nfs_dir/opt/tro/conf/tro-web/api/confcenter/wbmnt/query/', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (14, 'takin.report.open.task', 'true', -99, 'test', 'default', 1, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:07:34', 0);
INSERT INTO `t_config_server` VALUES (15, 'takin.cloud.url', 'http://localhost:10010/takin-cloud', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (16, 'takin.config.zk.addr', '127.0.0.1:2181', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (17, 'takin.remote.client.download.uri', '/api/bigfile/download', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (18, 'takin.script.check', 'true', -99, 'test', 'default', 1, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (19, 'takin.link.flow.check.enable', 'false', -99, 'test', 'default', 1, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (20, 'takin.application.new-agent', '1', -99, 'test', 'default', 1, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:22:54', 0);
INSERT INTO `t_config_server` VALUES (21, 'agent.interactive.takin.web.url', 'http://localhost:10008/takin-web', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (22, 'agent.http.update.version', 'true', -99, 'test', 'default', 1, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (23, 'agent.registered.path', '/config/log/pradar/client/', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (24, 'performance.base.agent.frequency', '1', -99, 'test', 'default', 1, 1, 6, '2021-10-12 12:01:45', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (25, 'takin.query.async.critica.value', '20000', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (26, 'takin.performance.clear.second', '172800', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (27, 'takin.pressure.machine.upload.interval.time', '180000', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (28, 'spring.performance.influxdb.database', 'performance', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (29, 'spring.influxdb.url', '192.168.1.151:8086', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (30, 'spring.influxdb.database', 'base', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (31, 'spring.influxdb.user', 'pradar', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (32, 'spring.influxdb.password', 'pradar', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (33, 'takin.script-debug.rpcType', 'KAFKA', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (34, 'takin.start.task.check.application', 'true', -99, 'test', 'default', 1, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (35, 'takin.risk.collect.time', '300000', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (36, 'takin.pradar.switch.processing.wait.time', '61', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (37, 'takin.customer.id', '0', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (38, 'takin.risk.max.norm.scale', '80D', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (39, 'takin.risk.max.norm.maxLoad', '2', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (40, 'takin.blacklist.data.fix.enable', 'false', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (41, 'takin.link.fix.enable', 'false', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (42, 'takin.file.upload.url', 'http://localhost:10010/takin-cloud', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (43, 'takin.file.upload.user.data.dir', '/data/nfs_dir/user/', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:06', 0);
INSERT INTO `t_config_server` VALUES (44, 'takin.file.upload.tmp.path', '/data/nfs_dir/', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:07', 0);
INSERT INTO `t_config_server` VALUES (45, 'takin.file.upload.script.path', '/data/nfs_dir/takin/web/script/', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:07', 0);
INSERT INTO `t_config_server` VALUES (47, 'takin.file.upload.script.path', '/data/nfs_dir/takin/web/script/', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:07', 0);
INSERT INTO `t_config_server` VALUES (48, 'takin.data.path', '/data/nfs_dir/', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:07', 0);
INSERT INTO `t_config_server` VALUES (49, 'takin.basePath', '/data/nfs_dir/base', -99, 'test', 'default', 0, 1, 6, '2021-10-12 12:01:46', '2021-11-08 19:04:07', 0);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;