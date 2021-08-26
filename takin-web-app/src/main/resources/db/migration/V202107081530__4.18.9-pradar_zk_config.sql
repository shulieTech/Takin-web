CREATE TABLE IF NOT EXISTS `t_pradar_zk_config`
(
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `zk_path` varchar(256) NOT NULL COMMENT 'zk路径',
    `type` varchar(64) NULL COMMENT '类型',
    `value` varchar(64) NULL COMMENT '数值',
    `remark` varchar(512) DEFAULT NULL COMMENT '配置说明',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
    PRIMARY KEY (`id`),
    KEY `idx_zk_path` (`zk_path`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='zk配置信息表';

BEGIN;
INSERT IGNORE  INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (1, '/pradar/config/rt/hdfsDisable', 'Boolean', 'false', 'hdfs开关配置，控制数据是否不写入hdfs', '2021-07-08 15:08:39', '2021-07-08 15:08:39', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (2, '/pradar/config/rt/hdfsSampling', 'Int', '1', '日志原文写入hdfs采样率配置，代表每多少条数据采样1条', '2021-07-08 15:37:27', '2021-07-08 15:37:27', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (3, '/pradar/config/rt/monitroDisable', 'Boolean', 'false', '基础信息处理开关，控制压测指标信息是否不处理', '2021-07-08 15:37:58', '2021-07-08 15:37:58', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (4, '/pradar/config/rt/clickhouseDisable', 'Boolean', 'false', '日志原文写入clickhouse开关，控制日志原文是否不写入clickhouse', '2021-07-08 15:38:17', '2021-07-08 15:38:17', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (5, '/pradar/config/rt/clickhouseSampling', 'Int', '1', '日志原文写入clickhouse采样率，代表每多少条数据采样1条', '2021-07-08 15:41:25', '2021-07-08 15:41:25', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (6, '/config/log/trace/simpling', 'Int', '1', '全局日志采样率', '2021-07-08 15:41:58', '2021-07-08 15:41:58', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (7, '/pradar/config/rt/e2eMetricsDisable', 'Boolean', 'false', 'E2E流量计算开关,控制流量是否不计算', '2021-07-08 15:42:26', '2021-07-08 15:42:26', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (8, '/pradar/config/ptl/fileEnable', 'Boolean', 'true', '是否输出ptl文件', '2021-07-08 15:42:53', '2021-07-08 15:42:53', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (9, '/pradar/config/ptl/errorOnly', 'Boolean', 'false', 'ptl文件是否只输出异常信息', '2021-07-08 15:43:22', '2021-07-08 15:43:22', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (10, '/pradar/config/ptl/timeoutOnly', 'Boolean', 'false', 'ptl是否只输出超过固定接口调用时长的日志', '2021-07-08 15:43:53', '2021-07-08 15:43:53', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (11, '/pradar/config/ptl/timeoutThreshold', 'Int', '300', '接口调用时长阈值', '2021-07-08 15:44:22', '2021-07-08 15:44:22', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (12, '/pradar/config/ptl/cutoff', 'Boolean', 'false', 'ptl报文日志是否截断', '2021-07-08 15:44:44', '2021-07-08 15:44:44', 0);
INSERT IGNORE INTO `t_pradar_zk_config` (`id`, `zk_path`, `type`, `value`, `remark`, `create_time`, `modify_time`, `is_deleted`) VALUES (13, '/config/engine/local/mount/sceneIds', 'String', '\"\"', '本地挂载的场景ID, 多个以逗号分隔', '2021-07-08 17:12:03', '2021-07-08 17:12:03', 0);
COMMIT;