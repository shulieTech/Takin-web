CREATE TABLE IF NOT EXISTS `t_dictionary_type` (
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
                                     PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据字典分类表';

CREATE TABLE IF NOT EXISTS `t_dictionary_data` (
                                     `ID` varchar(50) NOT NULL COMMENT 'ID',
                                     `DICT_TYPE` varchar(50) DEFAULT NULL COMMENT '数据字典分类',
                                     `VALUE_ORDER` int DEFAULT NULL COMMENT '序号',
                                     `VALUE_NAME` varchar(50) DEFAULT NULL COMMENT '值名称',
                                     `VALUE_CODE` varchar(50) DEFAULT NULL COMMENT '值代码',
                                     `LANGUAGE` varchar(10) DEFAULT NULL COMMENT '语言',
                                     `ACTIVE` char(1) DEFAULT NULL COMMENT '是否启用',
                                     `CREATE_TIME` date DEFAULT NULL COMMENT '创建时间',
                                     `MODIFY_TIME` date DEFAULT NULL COMMENT '更新时间',
                                     `CREATE_USER_CODE` varchar(50) DEFAULT NULL COMMENT '创建人',
                                     `MODIFY_USER_CODE` varchar(50) DEFAULT NULL COMMENT '更新人',
                                     `NOTE_INFO` varchar(250) DEFAULT NULL COMMENT '备注信息',
                                     `VERSION_NO` bigint DEFAULT '0' COMMENT '版本号',
                                     PRIMARY KEY (`ID`) USING BTREE,
                                     KEY `IDX_MUL_QUERY` (`ACTIVE`,`DICT_TYPE`,`VALUE_CODE`) USING BTREE,
                                     KEY `idx_VERSION_NO` (`VERSION_NO`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='数据字典基础数据表';

-- 线程栈数据存储
CREATE TABLE IF NOT EXISTS `t_performance_thread_stack_data` (
  `thread_stack` longtext COMMENT '线程堆栈',
  `thread_stack_link` bigint(20) NOT NULL unique COMMENT 'influxDB关联',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (`thread_stack_link`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 线程数据存储
CREATE TABLE IF NOT EXISTS `t_performance_thread_data` (
  `agent_id` varchar(256) COMMENT 'agent_id',
  `app_name` varchar(256) COMMENT 'app_name',
  `timestamp` varchar(256) COMMENT 'timestamp',
  `app_ip` varchar(256) COMMENT 'app_ip',
  `thread_data` longtext COMMENT '线程栈数据',
  `base_id` bigint(20) NOT NULL unique COMMENT 'base_thread_关联关系',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  PRIMARY KEY (`base_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_tc_sequence
-- ----------------------------
CREATE TABLE IF NOT EXISTS `t_tc_sequence` (
  `id` bigint(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `value` bigint(20) DEFAULT NULL,
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_NAME` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `t_pressure_machine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '压力机名称',
  `ip` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '压力机IP',
  `flag` varchar(125) COLLATE utf8_bin DEFAULT NULL COMMENT '标签',
  `cpu` int(2) NOT NULL COMMENT 'cpu核数',
  `memory` bigint(20) NOT NULL COMMENT '内存，单位字节',
  `machine_usage` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '机器水位 ',
  `disk` bigint(20) NOT NULL COMMENT '磁盘，单位字节',
  `cpu_usage` decimal(10,4) NOT NULL COMMENT 'cpu利用率',
  `cpu_load` decimal(10,4) NOT NULL COMMENT 'cpu load',
  `memory_used` decimal(10,4) NOT NULL COMMENT '内存利用率',
  `disk_io_wait` decimal(10,4) DEFAULT NULL COMMENT '磁盘 IO 等待率',
  `transmitted_total` bigint(20) NOT NULL DEFAULT '0' COMMENT '机器网络带宽总大小',
  `transmitted_in` bigint(20) NOT NULL DEFAULT '0' COMMENT '网络带宽入大小',
  `transmitted_in_usage` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '网络带宽入利用率',
  `transmitted_out` bigint(20) NOT NULL DEFAULT '0' COMMENT '网络带宽出大小',
  `transmitted_out_usage` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '网络带宽出利用率',
  `transmitted_usage` decimal(10,4) NOT NULL COMMENT '网络带宽利用率',
  `scene_names` varchar(125) COLLATE utf8_bin DEFAULT NULL COMMENT '压测场景id',
  `status` int(2) NOT NULL DEFAULT '0' COMMENT '状态 0：空闲 ；1：压测中  -1:离线',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_ip` (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `t_trace_manage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_object` varchar(128) NOT NULL COMMENT '追踪对象',
  `report_id` bigint(20) DEFAULT NULL COMMENT '报告id',
  `agent_id` varchar(128) DEFAULT NULL,
  `server_ip` varchar(16) DEFAULT NULL COMMENT '服务器ip',
  `process_id` int(11) DEFAULT NULL COMMENT '进程id',
  `create_time` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `feature` text COMMENT '拓展字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `t_trace_manage_deploy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_manage_id` bigint(20) NOT NULL COMMENT '追踪对象id',
  `trace_deploy_object` varchar(128) NOT NULL COMMENT '追踪对象实例名称',
  `sample_id` varchar(128) DEFAULT NULL COMMENT '追踪凭证',
  `level` int(11) DEFAULT NULL COMMENT '级别',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父id',
  `has_children` int(11) DEFAULT '2' COMMENT '0:没有;1:有;2未知',
  `line_num` int(11) DEFAULT NULL COMMENT '行号',
  `avg_cost` decimal(20,6) DEFAULT NULL COMMENT '平均耗时',
  `p50` decimal(20,6) DEFAULT NULL COMMENT 'p50',
  `p90` decimal(20,6) DEFAULT NULL COMMENT 'p90',
  `p95` decimal(20,6) DEFAULT NULL COMMENT 'p95',
  `p99` decimal(20,6) DEFAULT NULL COMMENT 'p99',
  `min` decimal(20,6) DEFAULT NULL COMMENT 'min',
  `max` decimal(20,6) DEFAULT NULL COMMENT 'max',
  `status` int(11) NOT NULL COMMENT '状态0:待采集;1:采集中;2:采集结束',
  `create_time` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `feature` text COMMENT '拓展字段',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

BEGIN;
INSERT IGNORE INTO `t_dictionary_type`(`id`,`TYPE_NAME`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `PARENT_CODE`, `TYPE_ALIAS`, `IS_LEAF`) VALUES ('6ba75716d726493783bfd64cce058110', 'PRESSURE_MACHINE_STATUS', 'Y', '2020-11-17', '2020-11-17', '000000', '000000', NULL, 'PRESSURE_MACHINE_STATUS', NULL);

INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('0723f0bef309485c84ae0d645f26das32', '6ba75716d726493783bfd64cce058110', -1, '离线', '-1', 'ZH_CN', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, NULL);

INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('0723f0bef309485c84ae0d645f26das33', '6ba75716d726493783bfd64cce058110', 0, '空闲', '0', 'ZH_CN', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, NULL);

INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('0723f0bef309485c84ae0d645f26das34', '6ba75716d726493783bfd64cce058110', 1, '压测中', '1', 'ZH_CN', 'Y', '2019-06-17', '2019-06-17', '000000', '000000', NULL, NULL);


INSERT IGNORE INTO `t_tc_sequence`(`NAME`, `value`,`gmt_modified`) VALUES ('BASE_ORDER_LINE', 0, NOW());

INSERT IGNORE INTO `t_tc_sequence`(`name`, `value`,`gmt_modified`) VALUES ('THREAD_ORDER_LINE', 0, NOW());

INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('fc77d5f788dc45528b039d5b1b4fda90', 'f644eb266aba4a2186341b708f33eegg', 5, 'CPU利用率', '4', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('fc77d5f788dc45528b039d5b1b4fda91', 'f644eb266aba4a2186341b708f33eegg', 6, '内存占用', '5', 'ZH_CN', 'Y', '2020-03-17', '2020-03-17', '000000', '000000', NULL, NULL);
COMMIT;