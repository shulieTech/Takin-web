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

 Date: 20/06/2022 16:02:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for e_patrol_activity_assert
-- ----------------------------
DROP TABLE IF EXISTS `e_patrol_activity_assert`;
CREATE TABLE `e_patrol_activity_assert` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `chain_id` bigint(20) DEFAULT NULL COMMENT '链路ID',
  `chain_type` int(11) DEFAULT NULL COMMENT '是否MQ：1-是；0-否',
  `assert_name` varchar(255) DEFAULT NULL COMMENT '断言名称',
  `param_type` int(11) DEFAULT NULL COMMENT '断言参数类型：1-出参；2-入参',
  `param_key` varchar(255) DEFAULT NULL COMMENT '参数名',
  `param_value` varchar(255) DEFAULT NULL COMMENT '标准值',
  `assert_condition` int(11) DEFAULT NULL COMMENT '断言方式：1-等于；2-不等于；3-包含；4-不包含',
  `mq_delay_time` bigint(20) DEFAULT NULL COMMENT 'mq延迟时间',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT NULL COMMENT '修改时间',
  `is_deleted` int(11) DEFAULT NULL COMMENT '是否删除：0-否；1-是',
  `assert_code` varchar(255) DEFAULT NULL COMMENT 'code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for e_patrol_board
-- ----------------------------
DROP TABLE IF EXISTS `e_patrol_board`;
CREATE TABLE `e_patrol_board` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id(已废弃)',
  `board_name` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '看板名称',
  `patrol_scene_num` int(11) DEFAULT NULL COMMENT '看板包含场景数量',
  `board_status` int(11) DEFAULT '0' COMMENT '看板状态（是否大屏展示）：0-否；1-是',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int(11) NOT NULL DEFAULT '0' COMMENT '是否已删除',
  `is_merge` tinyint(1) DEFAULT NULL COMMENT '是否合并',
  `is_horizontally` tinyint(1) DEFAULT NULL COMMENT '是否横向',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境标识',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='看板信息表';

-- ----------------------------
-- Table structure for e_patrol_board_scene
-- ----------------------------
DROP TABLE IF EXISTS `e_patrol_board_scene`;
CREATE TABLE `e_patrol_board_scene` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patrol_board_id` bigint(20) DEFAULT NULL COMMENT '看板ID',
  `patrol_board_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '看板名称',
  `patrol_scene_id` bigint(20) DEFAULT NULL COMMENT '场景id',
  `patrol_scene_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '场景名称',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int(11) NOT NULL DEFAULT '0' COMMENT '是否已删除',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=79 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='看板-场景关联表';

-- ----------------------------
-- Table structure for e_patrol_exception
-- ----------------------------
DROP TABLE IF EXISTS `e_patrol_exception`;
CREATE TABLE `e_patrol_exception` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `status` int(11) NOT NULL COMMENT '状态',
  `type` int(11) NOT NULL COMMENT '异常类型',
  `level` int(11) NOT NULL COMMENT '异常程度',
  `rt` decimal(10,2) DEFAULT NULL COMMENT '响应时间',
  `success_rate` decimal(10,2) DEFAULT NULL COMMENT '成功率',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `chain_id` bigint(20) NOT NULL COMMENT '任务标识',
  `activity_name` varchar(100) NOT NULL COMMENT '任务名称',
  `patrol_type` int(11) NOT NULL COMMENT '任务类型',
  `rpc_type` varchar(100) DEFAULT NULL COMMENT '业务入口',
  `scene_id` bigint(20) NOT NULL COMMENT '巡检场景',
  `scene_name` varchar(1024) DEFAULT NULL,
  `is_finish` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否处理完毕',
  `activity_id` bigint(20) DEFAULT NULL COMMENT '业务活动',
  `data_source` tinyint(1) NOT NULL DEFAULT '0' COMMENT '异常数据来源 0:巡检 1:其他',
  `edge_id` varchar(2048) DEFAULT NULL COMMENT '边ID',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=31548 DEFAULT CHARSET=utf8mb4 COMMENT='巡检异常信息';

-- ----------------------------
-- Table structure for e_patrol_exception_config
-- ----------------------------
DROP TABLE IF EXISTS `e_patrol_exception_config`;
CREATE TABLE `e_patrol_exception_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `order_number` int(11) DEFAULT NULL COMMENT '进行判断时的顺序',
  `type_value` int(11) NOT NULL COMMENT '巡检异常类型',
  `level_value` int(11) DEFAULT NULL COMMENT '巡检异常程度',
  `threshold_value` decimal(10,2) NOT NULL COMMENT '阈值',
  `contrast_factor` int(11) NOT NULL COMMENT '对比因子',
  `remarks` varchar(10) DEFAULT NULL COMMENT '备注',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否已删除',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id(已废弃)',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `service` varchar(255) DEFAULT NULL COMMENT '接口名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_config` (`tenant_id`,`env_code`,`type_value`,`level_value`,`service`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=155 DEFAULT CHARSET=utf8mb4 COMMENT='巡检异常程度配置';

-- ----------------------------
-- Table structure for e_patrol_exception_notice_config
-- ----------------------------
DROP TABLE IF EXISTS `e_patrol_exception_notice_config`;
CREATE TABLE `e_patrol_exception_notice_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patrol_exception_type_id` bigint(20) NOT NULL COMMENT '故障类型主键.0:全部;*:具体的关联主键;',
  `patrol_board_id` bigint(20) NOT NULL COMMENT '场景面板ID:0:全部;*:具体的关联主键;',
  `patrol_board_name` varchar(100) DEFAULT NULL COMMENT '看板名称',
  `patrol_scene_id` bigint(20) NOT NULL COMMENT '巡检场景ID:0:全部;*:具体的关联主键;',
  `patrol_scene_name` varchar(100) DEFAULT NULL COMMENT '场景名称',
  `patrol_scene_chain_id` bigint(20) NOT NULL COMMENT '巡检节点ID:0:全部;*:具体的关联主键;',
  `business_name` varchar(100) DEFAULT NULL COMMENT '根据巡检节点生成,用于前台模糊查询.如果是全部,则为null.',
  `channel` int(11) NOT NULL COMMENT '巡检异常通知渠道',
  `hook_url` varchar(500) NOT NULL COMMENT 'Hook地址',
  `interval_threshold_value` bigint(20) NOT NULL COMMENT '通知的间隔时间',
  `max_number_of_times` bigint(20) NOT NULL COMMENT '最大通知次数.-1,代表不限.',
  `content_type` varchar(50) DEFAULT NULL COMMENT '请求参数类型',
  `template` text COMMENT '推送模版',
  `create_user_id` bigint(20) NOT NULL COMMENT '创建者',
  `create_user_name` varchar(100) DEFAULT NULL COMMENT '创建者名称',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `customer_id` bigint(20) DEFAULT '0' COMMENT '租户id 废弃',
  `modify_user_id` bigint(20) NOT NULL COMMENT '修改者',
  `modify_user_name` varchar(100) DEFAULT NULL COMMENT '修改者名称',
  `modify_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='巡检异常通知配置';

-- ----------------------------
-- Table structure for e_patrol_exception_status_change_log
-- ----------------------------
DROP TABLE IF EXISTS `e_patrol_exception_status_change_log`;
CREATE TABLE `e_patrol_exception_status_change_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `exception_id` bigint(20) NOT NULL COMMENT '异常主键',
  `type` int(11) NOT NULL COMMENT '操作类型',
  `time` datetime NOT NULL COMMENT '操作时间',
  `category` int(11) NOT NULL COMMENT '操作原因',
  `detail` varchar(100) NOT NULL COMMENT '详细原因',
  `user_id` bigint(20) NOT NULL COMMENT '操作人',
  `user_name` varchar(100) NOT NULL COMMENT '操作人名称',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='巡检异常信息-状态变更日志';

-- ----------------------------
-- Table structure for e_patrol_scene
-- ----------------------------
DROP TABLE IF EXISTS `e_patrol_scene`;
CREATE TABLE `e_patrol_scene` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `patrol_scene_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '巡检场景名称',
  `ref_type` int(11) DEFAULT '1' COMMENT '巡检场景类型：1-业务活动；2-业务流程',
  `ref_num` int(11) DEFAULT NULL COMMENT '巡检场景包含业务活动数量',
  `patrol_status` int(11) DEFAULT '0' COMMENT '巡检任务状态:0-正常巡检,1-巡检配置异常',
  `task_status` int(11) DEFAULT '0' COMMENT '任务启用状态：0-任务关闭，1-任务开启',
  `ref_id` bigint(20) DEFAULT NULL COMMENT '业务活动或者业务流程ID',
  `script_id` bigint(20) DEFAULT NULL COMMENT '业务活动/流程对应的脚本ID',
  `patrol_period` bigint(20) DEFAULT '5' COMMENT '巡检周期：单位秒，默认5',
  `scene_id` bigint(20) DEFAULT NULL COMMENT '压测场景ID',
  `report_id` bigint(20) DEFAULT NULL COMMENT '压测报告ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int(11) NOT NULL DEFAULT '0' COMMENT '是否已删除',
  `features` text COLLATE utf8_bin COMMENT '拓展字段',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境标识',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `tenant_app_key` varchar(512) COLLATE utf8_bin NOT NULL COMMENT '租户key 唯一，同时也是userappkey',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=85 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='巡检场景信息表';

-- ----------------------------
-- Table structure for e_patrol_scene_chain
-- ----------------------------
DROP TABLE IF EXISTS `e_patrol_scene_chain`;
CREATE TABLE `e_patrol_scene_chain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `patrol_scene_id` bigint(20) DEFAULT NULL COMMENT '场景id',
  `activity_id` bigint(20) DEFAULT NULL COMMENT '业务活动id',
  `activity_name` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '业务活动名称',
  `patrol_type` int(11) DEFAULT '0' COMMENT '巡检类型:0-业务巡检,1-技术巡检',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '关联业务活动id,此字段只有在`patrol_type`=1 时，才会大于0',
  `is_mq` int(11) DEFAULT '0' COMMENT '是否MQ类型，0-否，1-是',
  `activity_order` int(11) DEFAULT NULL COMMENT '排序',
  `activity_status` int(11) DEFAULT NULL COMMENT '状态，0-可用，1-不可用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int(11) NOT NULL DEFAULT '0' COMMENT '是否已删除',
  `entrance_application_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '入口应用名称',
  `entrance_application_id` bigint(20) DEFAULT NULL COMMENT '入口应用ID',
  `entrance_type` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '入口类型',
  `entrance_rpc_type` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT 'rpcType',
  `entrance_service_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '入口服务名称',
  `entrance_method` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '入口方法',
  `entrance_name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '入口名称',
  `tech_node_id` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '技术节点ID',
  `chainSource` int(2) DEFAULT NULL COMMENT '场景链路来源：0.巡检 1.其他',
  `eagleId` varchar(2048) COLLATE utf8_bin DEFAULT NULL COMMENT '边ID',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=376 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='场景链路信息表';

-- ----------------------------
-- Table structure for e_patrol_scene_check
-- ----------------------------
DROP TABLE IF EXISTS `e_patrol_scene_check`;
CREATE TABLE `e_patrol_scene_check` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `patrol_scene_id` bigint(20) DEFAULT NULL COMMENT '巡检场景ID',
  `patrol_chain_id` bigint(20) DEFAULT NULL COMMENT '巡检链路ID',
  `app_name` varchar(255) DEFAULT NULL COMMENT '应用名称',
  `error_code` varchar(255) DEFAULT NULL COMMENT '错误码',
  `error_description` varchar(1024) DEFAULT NULL COMMENT '错误描述',
  `error_detail` varchar(2048) DEFAULT NULL COMMENT '错误详情',
  `modify_date` datetime DEFAULT NULL COMMENT '修改时间',
  `activity_name` varchar(255) DEFAULT NULL COMMENT '业务活动名称',
  `is_deleted` int(11) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9099 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for pradar_app_agent
-- ----------------------------
DROP TABLE IF EXISTS `pradar_app_agent`;
CREATE TABLE `pradar_app_agent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_group_id` bigint(20) DEFAULT NULL COMMENT '主机IP',
  `ip` varchar(64) DEFAULT NULL COMMENT '主机IP PORT',
  `machine_room` varchar(128) DEFAULT NULL COMMENT '机器名',
  `host_port` varchar(16) DEFAULT NULL COMMENT '主机IP',
  `hostname` varchar(256) DEFAULT NULL COMMENT '主机名称',
  `agent_status` tinyint(4) DEFAULT NULL COMMENT 'agent状态，1：已上线，2：暂停中，3：已下线',
  `agent_version` varchar(32) DEFAULT NULL COMMENT 'agent版本',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  `modify_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `deleted` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用状态表';

-- ----------------------------
-- Table structure for pradar_app_bamp
-- ----------------------------
DROP TABLE IF EXISTS `pradar_app_bamp`;
CREATE TABLE `pradar_app_bamp` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_point_id` bigint(20) DEFAULT NULL COMMENT '埋点ip',
  `bamp_interval` int(11) DEFAULT NULL COMMENT '间隔，单位min',
  `index_code` varchar(128) DEFAULT NULL COMMENT '指标编码',
  `rt_avg` int(11) DEFAULT NULL COMMENT '响应耗时',
  `deleted` tinyint(1) DEFAULT NULL,
  `gmt_created` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='出数系统';

-- ----------------------------
-- Table structure for pradar_app_group
-- ----------------------------
DROP TABLE IF EXISTS `pradar_app_group`;
CREATE TABLE `pradar_app_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '组ID',
  `app_info_id` bigint(20) DEFAULT NULL COMMENT '关联应用ID',
  `group_name` varchar(256) DEFAULT NULL COMMENT '组名称',
  `domain_name` varchar(256) DEFAULT NULL COMMENT '域名',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `deleted` tinyint(4) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pradar_app_info
-- ----------------------------
DROP TABLE IF EXISTS `pradar_app_info`;
CREATE TABLE `pradar_app_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(128) DEFAULT '' COMMENT '应用名',
  `manager_name` varchar(32) DEFAULT NULL COMMENT '负责人姓名',
  `product_line` varchar(256) NOT NULL COMMENT '产品线',
  `app_group` varchar(256) DEFAULT NULL COMMENT '分组',
  `host_ip` varchar(64) DEFAULT NULL COMMENT '主机IP',
  `modify_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  `deleted` tinyint(4) DEFAULT NULL COMMENT '0：未删除 1：删除',
  `PE` varchar(64) DEFAULT NULL COMMENT 'PE',
  `app_manager` varchar(64) DEFAULT NULL COMMENT '应用管理员',
  `SCM` varchar(64) DEFAULT NULL COMMENT 'SCM管理员',
  `DBA` varchar(64) DEFAULT NULL COMMENT 'DBA',
  `job_number` varchar(20) DEFAULT NULL COMMENT '工号',
  `sms_number` varchar(20) DEFAULT NULL COMMENT '电话号码',
  `reverser_registration` varchar(2) DEFAULT NULL COMMENT '是否反向注册:null:否;1:否;2:是',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='应用基本信息表';

-- ----------------------------
-- Table structure for pradar_app_point
-- ----------------------------
DROP TABLE IF EXISTS `pradar_app_point`;
CREATE TABLE `pradar_app_point` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '埋点ID',
  `app_info_id` bigint(20) DEFAULT NULL COMMENT '应用ID',
  `method` varchar(1024) DEFAULT NULL COMMENT '埋点方法',
  `method_comment` varchar(4096) DEFAULT NULL COMMENT '删除标识',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `deleted` tinyint(4) DEFAULT NULL COMMENT '删除标识',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pradar_app_warn
-- ----------------------------
DROP TABLE IF EXISTS `pradar_app_warn`;
CREATE TABLE `pradar_app_warn` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_point_id` bigint(20) DEFAULT NULL COMMENT '埋点ip',
  `warn_interval` int(11) DEFAULT NULL COMMENT '告警间隔，单位min',
  `span_threshold` int(11) DEFAULT NULL COMMENT '阈值：触发告警,单位ms',
  `span_frequency` int(11) DEFAULT NULL COMMENT '耗时触发频率，单位次',
  `error_frequency` int(11) DEFAULT NULL COMMENT '错误触发频率,单位次',
  `warn_level` tinyint(1) DEFAULT NULL COMMENT '告警级别，1：error， 2：warning',
  `deleted` tinyint(1) DEFAULT NULL,
  `gmt_created` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `app_name` varchar(128) DEFAULT '' COMMENT '应用名',
  `method` varchar(1024) DEFAULT NULL COMMENT '埋点方法',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='告警';

-- ----------------------------
-- Table structure for pradar_biz_key_config
-- ----------------------------
DROP TABLE IF EXISTS `pradar_biz_key_config`;
CREATE TABLE `pradar_biz_key_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `biz_key` varchar(200) DEFAULT NULL COMMENT '业务key,不区分大小写',
  `create_time` datetime DEFAULT NULL COMMENT '插入时间',
  `modify_time` datetime DEFAULT NULL COMMENT '最后更新时间',
  `deleted` tinyint(1) DEFAULT NULL COMMENT '1，有效，0，删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_biz_key` (`biz_key`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='业务字段采集配置表';

-- ----------------------------
-- Table structure for pradar_user_login
-- ----------------------------
DROP TABLE IF EXISTS `pradar_user_login`;
CREATE TABLE `pradar_user_login` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(64) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `dept` varchar(256) DEFAULT NULL,
  `user_type` tinyint(1) DEFAULT NULL COMMENT '1：测试， 2：开发， 3：运维， 4：管理',
  `is_deleted` tinyint(1) DEFAULT NULL,
  `gmt_created` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户登录表';

-- ----------------------------
-- Table structure for t_abstract_data
-- ----------------------------
DROP TABLE IF EXISTS `t_abstract_data`;
CREATE TABLE `t_abstract_data` (
  `TAD_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '抽数表id',
  `TDC_ID` bigint(20) NOT NULL COMMENT '数据库表配置id',
  `TABLE_NAME` varchar(500) NOT NULL COMMENT '数据表名',
  `SQL_DDl` text NOT NULL COMMENT '建表语句',
  `ABSTRACT_SQL` tinytext NOT NULL COMMENT '取数逻辑sql(存储文件路径或者sql语句)',
  `DEAL_SQL` tinytext NOT NULL COMMENT '处理数据逻辑sql(存储文件路径或者sql语句)',
  `PRINCIPAL_NO` varchar(10) DEFAULT NULL COMMENT '负责人工号',
  `SQL_TYPE` int(11) NOT NULL DEFAULT '0' COMMENT '0表示sql类型为纯sql输入,1表示为文本类型',
  `DB_STATUS` int(11) DEFAULT '1' COMMENT '数据状态(0代表删除,1代表使用,默认为1)',
  `USE_YN` int(11) DEFAULT '1' COMMENT '该表抽数启用状态(0表示未启用,1表示启用,默认启用)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
  `LOAD_STATUS` int(11) DEFAULT '0' COMMENT '抽数状态(0表示未开始,1表示正在运行,2表示运行成功,3停止运行,4表示运行失败)',
  PRIMARY KEY (`TAD_ID`) USING BTREE,
  KEY `TAD_INDEX1` (`TABLE_NAME`(191)) USING BTREE,
  KEY `TAD_INDEX2` (`PRINCIPAL_NO`) USING BTREE,
  KEY `TAD_INDEX3` (`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抽数表';

-- ----------------------------
-- Table structure for t_ac_account
-- ----------------------------
DROP TABLE IF EXISTS `t_ac_account`;
CREATE TABLE `t_ac_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uid` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `tags` bigint(20) DEFAULT '0' COMMENT '标签',
  `features` longtext COMMENT '扩展字段',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `customer_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX_ACCOUNT_UID` (`uid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='账户表';

-- ----------------------------
-- Table structure for t_ac_account_balance
-- ----------------------------
DROP TABLE IF EXISTS `t_ac_account_balance`;
CREATE TABLE `t_ac_account_balance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `acc_id` bigint(20) DEFAULT NULL COMMENT '账户ID（外键）',
  `book_id` bigint(20) DEFAULT NULL COMMENT '账本ID（外键）',
  `amount` decimal(25,5) DEFAULT '0.00000' COMMENT '当前发生金额',
  `balance` decimal(25,5) DEFAULT '0.00000' COMMENT '可用余额',
  `lock_balance` decimal(25,5) DEFAULT '0.00000' COMMENT '冻结余额',
  `subject` int(11) DEFAULT NULL COMMENT '账本科目',
  `direct` tinyint(4) DEFAULT NULL COMMENT '记账方向',
  `remark` varchar(200) DEFAULT NULL COMMENT '备注',
  `parent_book_id` bigint(20) DEFAULT '0' COMMENT '父类ID',
  `scene_code` varchar(30) DEFAULT NULL COMMENT '业务代码',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态',
  `acc_time` datetime DEFAULT NULL COMMENT '记账时间',
  `outer_id` varchar(100) DEFAULT NULL COMMENT '外部交易资金流水NO',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `tags` bigint(20) DEFAULT '0' COMMENT '标签',
  `features` longtext COMMENT '扩展字段',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `resource_type` tinyint(4) DEFAULT NULL COMMENT '数据来源,1=压测报告、2=业务活动流量验证、3=脚本调试',
  `resource_id` bigint(20) DEFAULT NULL COMMENT '来源ID。压测报告取报告ID、流量验证取业务活动ID、脚本调试取脚本ID',
  `resource_name` varchar(64) DEFAULT NULL COMMENT '来源名称。压测报告取场景名称、流量验证取业务活动名称、脚本调试取脚本名称',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建者',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX_ACCOUNT_BALANCE_ACC_ID_BOOK_ID` (`acc_id`,`book_id`) USING BTREE,
  KEY `IDX_ACCOUNT_BALANCE_OUTER_ID` (`outer_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=23303 DEFAULT CHARSET=utf8 COMMENT='账户账本明细表';

-- ----------------------------
-- Table structure for t_ac_account_book
-- ----------------------------
DROP TABLE IF EXISTS `t_ac_account_book`;
CREATE TABLE `t_ac_account_book` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户主键',
  `acc_id` bigint(20) DEFAULT NULL COMMENT '账户ID（外键）',
  `parent_book_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '父类ID',
  `balance` decimal(25,5) DEFAULT '0.00000' COMMENT '余额',
  `lock_balance` decimal(25,5) DEFAULT '0.00000' COMMENT '冻结金额',
  `total_balance` decimal(25,5) DEFAULT '0.00000' COMMENT '总金额',
  `subject` int(11) DEFAULT NULL COMMENT '科目',
  `direct` tinyint(4) DEFAULT NULL COMMENT '记账方向，借或贷',
  `rule` varchar(500) DEFAULT NULL COMMENT '规则',
  `rule_balance` decimal(25,5) DEFAULT '0.00000' COMMENT '规则余额',
  `start_time` datetime(3) DEFAULT NULL COMMENT '生效时间',
  `end_time` datetime(3) DEFAULT NULL COMMENT '失效时间',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态',
  `version` int(10) DEFAULT NULL COMMENT '版本',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `tags` bigint(20) DEFAULT '0' COMMENT '标签',
  `features` longtext COMMENT '扩展字段',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `IDX_ACCOUNT_BOOK_ACC_ID` (`acc_id`) USING BTREE,
  KEY `IDX_ACCOUNT_BOOK_UID` (`customer_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8 COMMENT='账户账本表';

-- ----------------------------
-- Table structure for t_activity_node_service_state
-- ----------------------------
DROP TABLE IF EXISTS `t_activity_node_service_state`;
CREATE TABLE `t_activity_node_service_state` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT 'ID',
  `activity_id` bigint(20) NOT NULL COMMENT '业务ID',
  `service_name` varchar(256) COLLATE utf8mb4_bin NOT NULL COMMENT '服务名称',
  `owner_app` varchar(256) COLLATE utf8mb4_bin NOT NULL COMMENT '所属应用',
  `state` tinyint(1) NOT NULL COMMENT '状态',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) COLLATE utf8mb4_bin DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `id` (`id`) USING BTREE,
  KEY `activity_id` (`activity_id`) USING BTREE,
  KEY `owner_app` (`owner_app`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='业务活动节点服务开关表';

-- ----------------------------
-- Table structure for t_agent_config
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_config`;
CREATE TABLE `t_agent_config` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除字段, 1 已删除, 0 未删除, 默认 0, 无符号',
  `operator` varchar(32) NOT NULL COMMENT '操作人',
  `type` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '配置类型0：全局配置，1：应用配置',
  `zh_key` varchar(255) DEFAULT NULL COMMENT '中文配置key',
  `en_key` varchar(255) NOT NULL COMMENT '英文配置key',
  `default_value` varchar(255) NOT NULL COMMENT '配置默认值',
  `desc` varchar(1024) DEFAULT NULL COMMENT '配置描述',
  `effect_type` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '配置作用范围 0：探针配置，1：agent配置',
  `effect_mechanism` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '生效机制0：重启生效，1：立即生效',
  `effect_min_version` varchar(64) DEFAULT NULL COMMENT '配置生效最低版本',
  `effect_min_version_num` bigint(20) unsigned DEFAULT NULL COMMENT '生效最低版本对应的数值',
  `effect_max_version` varchar(64) DEFAULT NULL COMMENT '配置生效最大版本（废弃）',
  `effect_max_version_num` bigint(20) unsigned DEFAULT NULL COMMENT '生效最高版本对应的数值（废弃）',
  `editable` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '是否可编辑0：可编辑，1：不可编辑',
  `value_type` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '值类型0：文本，1：单选',
  `value_option` varchar(1024) DEFAULT NULL COMMENT '值类型为单选时的可选项，多个可选项之间用,分隔',
  `project_name` varchar(255) DEFAULT NULL COMMENT '应用名称（应用配置时才生效）',
  `user_app_key` varchar(255) DEFAULT NULL COMMENT '用户信息（应用配置时才生效）',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_zh_key_pn_uak` (`zh_key`,`project_name`,`user_app_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8mb4 COMMENT='agent配置管理';

-- ----------------------------
-- Table structure for t_agent_plugin
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_plugin`;
CREATE TABLE `t_agent_plugin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `plugin_type` varchar(50) DEFAULT NULL COMMENT '中间件类型：HTTP_CLIENT、JDBC、ORM、DB、JOB、MESSAGE、CACHE、POOL、JNDI、NO_SQL、RPC、SEARCH、MQ、SERIALIZE、OTHER',
  `plugin_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '中间件名称：Redis、Mysql、Es',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_agent_plugin_lib_support
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_plugin_lib_support`;
CREATE TABLE `t_agent_plugin_lib_support` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `plugin_id` bigint(20) NOT NULL COMMENT '插件id',
  `lib_name` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'jar包名称',
  `lib_version_regexp` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT 'agent支持的中间件版本的正则表达式',
  `is_ignore` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 不忽略 1： 忽略',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_libname_index` (`lib_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_agent_report
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_report`;
CREATE TABLE `t_agent_report` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `application_id` bigint(20) DEFAULT '0' COMMENT '应用id',
  `application_name` varchar(64) DEFAULT '' COMMENT '应用名',
  `agent_id` varchar(600) NOT NULL,
  `ip_address` varchar(1024) DEFAULT '',
  `progress_id` varchar(20) DEFAULT '' COMMENT '进程号',
  `agent_version` varchar(1024) DEFAULT '' COMMENT 'agent版本号',
  `simulator_version` varchar(1024) DEFAULT NULL COMMENT 'simulator版本',
  `cur_upgrade_batch` varchar(64) DEFAULT '0' COMMENT '升级批次 根据升级内容生成MD5',
  `status` tinyint(2) DEFAULT '0' COMMENT '节点状态 0:未知,1:启动中,2:升级待重启,3:运行中,4:异常,5:休眠,6:卸载',
  `agent_error_info` varchar(1024) DEFAULT NULL COMMENT 'agent的错误信息',
  `simulator_error_info` varchar(1024) DEFAULT NULL COMMENT 'simulator错误信息',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_applicationId_agentId_envCode_tenantId` (`application_id`,`agent_id`,`env_code`,`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9456126 DEFAULT CHARSET=utf8mb4 COMMENT='探针心跳数据';

-- ----------------------------
-- Table structure for t_agent_version
-- ----------------------------
DROP TABLE IF EXISTS `t_agent_version`;
CREATE TABLE `t_agent_version` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `operator` varchar(32) NOT NULL COMMENT '操作人',
  `first_version` varchar(64) NOT NULL COMMENT '大版本号',
  `version` varchar(64) NOT NULL COMMENT '版本号',
  `version_num` bigint(20) unsigned NOT NULL COMMENT '版本号对应的数值',
  `file_path` varchar(1024) NOT NULL COMMENT '文件存储路径',
  `version_features` varchar(2048) NOT NULL COMMENT '版本特性',
  `is_deleted` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '逻辑删除字段, 1 已删除, 0 未删除, 默认 0, 无符号',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `version_unique` (`version`) USING BTREE COMMENT '版本号唯一索引'
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COMMENT='agent版本管理';

-- ----------------------------
-- Table structure for t_alarm_list
-- ----------------------------
DROP TABLE IF EXISTS `t_alarm_list`;
CREATE TABLE `t_alarm_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `war_packages` varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT 'WAR包名',
  `ip` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT 'IP',
  `alarm_collects` varchar(2000) COLLATE utf8_bin DEFAULT NULL COMMENT '告警汇总',
  `alarm_date` datetime DEFAULT NULL COMMENT '告警时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` int(11) NOT NULL DEFAULT '0' COMMENT '是否已删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='告警列表';

-- ----------------------------
-- Table structure for t_ap
-- ----------------------------
DROP TABLE IF EXISTS `t_ap`;
CREATE TABLE `t_ap` (
  `name` varchar(300) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_app_agent_config_report
-- ----------------------------
DROP TABLE IF EXISTS `t_app_agent_config_report`;
CREATE TABLE `t_app_agent_config_report` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `agent_id` varchar(50) NOT NULL DEFAULT '' COMMENT 'AgentId',
  `application_id` bigint(50) NOT NULL DEFAULT '0' COMMENT '应用id',
  `application_name` varchar(50) NOT NULL DEFAULT '' COMMENT '应用名',
  `config_type` tinyint(3) NOT NULL DEFAULT '0' COMMENT '配置类型 0:开关',
  `config_key` varchar(50) NOT NULL DEFAULT '' COMMENT '配置KEY',
  `config_value` varchar(50) NOT NULL DEFAULT '' COMMENT '配置值',
  `commit` varchar(500) NOT NULL DEFAULT '' COMMENT '备注',
  `customer_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '租户id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `CUSTOMER_ID_TYPE_INDEX` (`customer_id`,`config_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13497 DEFAULT CHARSET=utf8mb4 COMMENT='agent配置上报详情';

-- ----------------------------
-- Table structure for t_app_business_table_info
-- ----------------------------
DROP TABLE IF EXISTS `t_app_business_table_info`;
CREATE TABLE `t_app_business_table_info` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `APPLICATION_ID` bigint(20) NOT NULL COMMENT '应用ID',
  `table_name` text COMMENT 'jar名称',
  `url` varchar(255) DEFAULT NULL COMMENT 'Pradar插件名称',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `IS_DELETED` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用业务表';

-- ----------------------------
-- Table structure for t_app_middleware_info
-- ----------------------------
DROP TABLE IF EXISTS `t_app_middleware_info`;
CREATE TABLE `t_app_middleware_info` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `APPLICATION_ID` bigint(20) NOT NULL COMMENT '应用ID',
  `JAR_NAME` varchar(20) DEFAULT NULL COMMENT 'jar名称',
  `PLUGIN_NAME` varchar(20) DEFAULT NULL COMMENT 'Pradar插件名称',
  `JAR_TYPE` varchar(20) DEFAULT NULL COMMENT 'Jar类型',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `ACTIVE` tinyint(1) DEFAULT '0' COMMENT '是否增强成功 0:有效;1:未生效',
  `HIDDEN` tinyint(1) DEFAULT '0' COMMENT '是否隐藏 0:隐藏;1:不隐藏',
  `IS_DELETED` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=569 DEFAULT CHARSET=utf8mb4 COMMENT='应用中间件列表信息';

-- ----------------------------
-- Table structure for t_app_remote_call
-- ----------------------------
DROP TABLE IF EXISTS `t_app_remote_call`;
CREATE TABLE `t_app_remote_call` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `interface_name` varchar(1024) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '接口名称',
  `interface_type` tinyint(4) DEFAULT NULL COMMENT '接口类型',
  `server_app_name` varchar(2048) CHARACTER SET utf8 DEFAULT NULL COMMENT '服务端应用名',
  `APPLICATION_ID` bigint(20) NOT NULL COMMENT '应用id',
  `APP_NAME` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '应用name',
  `type` tinyint(4) DEFAULT '0' COMMENT '配置类型，0：未配置，1：白名单配置;2：返回值mock;3:转发mock',
  `mock_return_value` longtext CHARACTER SET utf8 COLLATE utf8_bin COMMENT 'mock返回值',
  `CUSTOMER_ID` bigint(20) DEFAULT NULL COMMENT '租户id',
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_synchronize` tinyint(1) DEFAULT '0' COMMENT '是否同步',
  `interface_child_type` varchar(50) NOT NULL DEFAULT '' COMMENT '接口子类型',
  `remark` varchar(50) NOT NULL DEFAULT '' COMMENT '备注',
  `manual_tag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否手动录入 0:否;1:是',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `md5` varchar(50) DEFAULT '0' COMMENT '应用名，接口名称，接口类型，租户id,环境code求md5',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`ID`) USING BTREE,
  UNIQUE KEY `unique_idx_md5` (`md5`) USING BTREE,
  KEY `T_REMOTE_CALL_INDEX1` (`CUSTOMER_ID`) USING BTREE,
  KEY `T_APP_ID_IDX` (`APPLICATION_ID`) USING BTREE,
  KEY `T_APP_NAME_IDX` (`APP_NAME`) USING BTREE,
  KEY `idx_app_name_env_tenant` (`tenant_id`,`env_code`,`APP_NAME`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=906923 DEFAULT CHARSET=utf8mb4 COMMENT='远程调用表';

-- ----------------------------
-- Table structure for t_app_remote_call_template_mapping
-- ----------------------------
DROP TABLE IF EXISTS `t_app_remote_call_template_mapping`;
CREATE TABLE `t_app_remote_call_template_mapping` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `interfaceType` varchar(50) DEFAULT '' COMMENT '接口类型',
  `template` varchar(50) DEFAULT '' COMMENT '对应的模板',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '0.可用，1不可用',
  `remark` varchar(500) DEFAULT NULL COMMENT '标记',
  `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `CUSTOMER_ID` bigint(20) DEFAULT '0' COMMENT '租户id 废弃',
  `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `eng_name` (`interfaceType`) USING BTREE,
  UNIQUE KEY `idx_interface_type_env_tenant` (`interfaceType`,`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='远程调用接口类型与模板映射';

-- ----------------------------
-- Table structure for t_application_api_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_application_api_manage`;
CREATE TABLE `t_application_api_manage` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `APPLICATION_ID` bigint(20) DEFAULT NULL COMMENT '应用主键',
  `APPLICATION_NAME` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '应用名称',
  `CUSTOMER_ID` bigint(20) DEFAULT NULL COMMENT '租户id',
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `api` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'api',
  `method` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '请求类型',
  `IS_AGENT_REGISTE` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:否;1:是',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=86156 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_application_ds_cache_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_application_ds_cache_manage`;
CREATE TABLE `t_application_ds_cache_manage` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `APPLICATION_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '应用主键',
  `APPLICATION_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '应用名称',
  `CACHE_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '中间件名称',
  `AGENT_SOURCE_TYPE` varchar(50) NOT NULL DEFAULT '' COMMENT 'agent上报的模板类型key',
  `COLONY` varchar(250) NOT NULL DEFAULT '' COMMENT '集群地址',
  `USER_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '源用户名',
  `PWD` varchar(50) NOT NULL DEFAULT '' COMMENT '源密码',
  `TYPE` varchar(50) NOT NULL COMMENT '缓存模式 ',
  `DS_TYPE` tinyint(2) DEFAULT '6' COMMENT '影子方案类型 ',
  `FILE_EXTEDN` text COMMENT '额外配置,存json',
  `CONFIG_JSON` text COMMENT '配置全部参数json化',
  `SOURCE` tinyint(2) NOT NULL DEFAULT '0' COMMENT '方案类型 0:amdb 1:手动录入',
  `STATUS` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0:启用；1:禁用',
  `CUSTOMER_ID` bigint(20) DEFAULT '0' COMMENT '租户id 废弃',
  `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `SHA_DOW_FILE_EXTEDN` text COMMENT '影子连接池额外配置,存json',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=261 DEFAULT CHARSET=utf8 COMMENT='缓存影子库表配置表';

-- ----------------------------
-- Table structure for t_application_ds_db_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_application_ds_db_manage`;
CREATE TABLE `t_application_ds_db_manage` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `APPLICATION_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '应用主键',
  `APPLICATION_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '应用名称',
  `CONN_POOL_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '连接池名称 druid, hikari,c3p0等',
  `AGENT_SOURCE_TYPE` varchar(50) NOT NULL DEFAULT '' COMMENT 'agent上报的模板类型key',
  `DB_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源名称',
  `URL` text NOT NULL COMMENT '业务数据源地址',
  `USER_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源用户名',
  `PWD` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源密码',
  `DS_TYPE` tinyint(2) DEFAULT '100' COMMENT '方案类型 0:影子库 1:影子表 2:影子server',
  `SHA_DOW_URL` varchar(250) NOT NULL DEFAULT '' COMMENT '影子数据源url',
  `SHA_DOW_USER_NAME` varchar(50) NOT NULL DEFAULT '' COMMENT '影子数据源用户名',
  `SHA_DOW_PWD` varchar(50) NOT NULL DEFAULT '' COMMENT '影子数据源密码',
  `FILE_EXTEDN` text COMMENT '连接池额外配置,存json',
  `SHA_DOW_FILE_EXTEDN` text COMMENT '影子连接池额外配置,存json',
  `CONFIG_JSON` text COMMENT '配置全部参数json化',
  `SOURCE` tinyint(2) NOT NULL DEFAULT '0' COMMENT '方案类型 0:amdb 1:手动录入',
  `STATUS` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0:启用；1:禁用',
  `CUSTOMER_ID` bigint(20) DEFAULT '0' COMMENT '租户id 废弃',
  `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=544 DEFAULT CHARSET=utf8 COMMENT='db连接池影子库表配置表';

-- ----------------------------
-- Table structure for t_application_ds_db_table
-- ----------------------------
DROP TABLE IF EXISTS `t_application_ds_db_table`;
CREATE TABLE `t_application_ds_db_table` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `app_id` bigint(20) NOT NULL COMMENT '应用id',
  `url` text NOT NULL COMMENT '业务数据源url',
  `user_name` varchar(50) NOT NULL DEFAULT '' COMMENT '数据源用户名',
  `biz_data_base` varchar(128) DEFAULT '' COMMENT '业务库',
  `biz_table` varchar(128) DEFAULT '' COMMENT '业务表',
  `shadow_table` varchar(128) DEFAULT NULL COMMENT '影子表',
  `is_check` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否选中 0=未选中，1=选中',
  `manual_tag` tinyint(2) DEFAULT '0' COMMENT '是否手动录入 0:否;1:是',
  `CUSTOMER_ID` bigint(20) DEFAULT '0' COMMENT '租户id 废弃',
  `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1502 DEFAULT CHARSET=utf8mb4 COMMENT='业务数据库表';

-- ----------------------------
-- Table structure for t_application_ds_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_application_ds_manage`;
CREATE TABLE `t_application_ds_manage` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `APPLICATION_ID` bigint(20) DEFAULT NULL COMMENT '应用主键',
  `APPLICATION_NAME` varchar(50) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '????',
  `DB_TYPE` tinyint(4) DEFAULT '0' COMMENT '存储类型 0:数据库 1:缓存',
  `DS_TYPE` tinyint(4) DEFAULT '0' COMMENT '方案类型 0:影子库 1:影子表 2:影子server',
  `URL` varchar(250) COLLATE utf8_bin DEFAULT NULL COMMENT '数据库url,影子表需填',
  `CONFIG` longtext COLLATE utf8_bin COMMENT 'xml配置',
  `PARSE_CONFIG` longtext COLLATE utf8_bin COMMENT '解析后配置',
  `STATUS` tinyint(4) DEFAULT '0' COMMENT '状态 0:启用；1:禁用',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id ,废弃',
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `env_code` varchar(20) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `sign` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=643 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_application_focus
-- ----------------------------
DROP TABLE IF EXISTS `t_application_focus`;
CREATE TABLE `t_application_focus` (
  `id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `app_name` varchar(50) CHARACTER SET utf8 NOT NULL COMMENT '应用名称',
  `interface_name` varchar(256) COLLATE utf8mb4_bin NOT NULL COMMENT '服务名称',
  `focus` tinyint(1) NOT NULL COMMENT '是否关注',
  `env_code` varchar(20) COLLATE utf8mb4_bin DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `focus_index` (`app_name`,`interface_name`) USING BTREE COMMENT '关注索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for t_application_middleware
-- ----------------------------
DROP TABLE IF EXISTS `t_application_middleware`;
CREATE TABLE `t_application_middleware` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `application_id` bigint(20) NOT NULL COMMENT '应用id',
  `application_name` varchar(255) COLLATE utf8mb4_bin DEFAULT '' COMMENT '应用名称',
  `artifact_id` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '项目名称',
  `group_id` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '项目组织名称',
  `version` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '版本号',
  `type` varchar(50) COLLATE utf8mb4_bin DEFAULT '' COMMENT '类型, 字符串形式',
  `status` tinyint(3) unsigned DEFAULT '0' COMMENT '状态, 3已支持, 2 未支持, 4 无需支持, 1 未知, 0 无',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
  `env_code` varchar(20) COLLATE utf8mb4_bin DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_aid` (`application_id`) USING BTREE,
  KEY `idx_a_name` (`application_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=391772 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='应用中间件';

-- ----------------------------
-- Table structure for t_application_mnt
-- ----------------------------
DROP TABLE IF EXISTS `t_application_mnt`;
CREATE TABLE `t_application_mnt` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `APPLICATION_ID` bigint(19) NOT NULL COMMENT '应用id',
  `APPLICATION_NAME` varchar(50) NOT NULL COMMENT '应用名称',
  `APPLICATION_DESC` varchar(200) DEFAULT NULL COMMENT '应用说明',
  `DDL_SCRIPT_PATH` varchar(200) NOT NULL COMMENT '影子库表结构脚本路径',
  `CLEAN_SCRIPT_PATH` varchar(200) NOT NULL COMMENT '数据清理脚本路径',
  `READY_SCRIPT_PATH` varchar(200) NOT NULL COMMENT '基础数据准备脚本路径',
  `BASIC_SCRIPT_PATH` varchar(200) NOT NULL COMMENT '铺底数据脚本路径',
  `CACHE_SCRIPT_PATH` varchar(200) NOT NULL COMMENT '缓存预热脚本地址',
  `CACHE_EXP_TIME` bigint(19) NOT NULL DEFAULT '0' COMMENT '缓存失效时间(单位秒)',
  `USE_YN` int(1) DEFAULT NULL COMMENT '是否可用(0表示启用,1表示未启用)',
  `AGENT_VERSION` varchar(16) DEFAULT NULL COMMENT 'java agent版本',
  `NODE_NUM` int(4) NOT NULL DEFAULT '1' COMMENT '节点数量',
  `ACCESS_STATUS` int(2) NOT NULL DEFAULT '1' COMMENT '接入状态； 0：正常 ； 1；待配置 ；2：待检测 ; 3：异常',
  `SWITCH_STATUS` varchar(255) NOT NULL DEFAULT 'OPENED' COMMENT 'OPENED："已开启",OPENING："开启中",OPEN_FAILING："开启异常",CLOSED："已关闭",CLOSING："关闭中",CLOSE_FAILING："关闭异常"',
  `EXCEPTION_INFO` text COMMENT '接入异常信息',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `ALARM_PERSON` varchar(64) DEFAULT NULL COMMENT '告警人',
  `PRADAR_VERSION` varchar(30) DEFAULT NULL COMMENT 'pradarAgent版本',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id ,废弃',
  `USER_ID` bigint(11) DEFAULT NULL COMMENT '所属用户',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_identifier_application_name` (`APPLICATION_NAME`,`customer_id`) USING BTREE,
  KEY `T_APLICATION_MNT_INDEX2` (`USE_YN`) USING BTREE,
  KEY `t_application_mnt_tenant_id_env_code_APPLICATION_ID_index` (`tenant_id`,`env_code`,`APPLICATION_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13452 DEFAULT CHARSET=utf8 COMMENT='应用管理表';

-- ----------------------------
-- Table structure for t_application_node_probe
-- ----------------------------
DROP TABLE IF EXISTS `t_application_node_probe`;
CREATE TABLE `t_application_node_probe` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `customer_id` bigint(20) unsigned NOT NULL DEFAULT '1' COMMENT '租户id',
  `application_name` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '应用名称',
  `agent_id` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT 'agentId',
  `operate` tinyint(3) unsigned DEFAULT '0' COMMENT '操作类型, 1 安装, 3 升级, 2 卸载, 0 无',
  `operate_result` tinyint(4) unsigned DEFAULT '99' COMMENT '操作结果, 0 失败, 1 成功, 99 无',
  `operate_id` bigint(20) unsigned DEFAULT '0' COMMENT '操作的id, 时间戳, 递增, agent 需要, 进行操作的时候会创建或更新',
  `probe_id` bigint(20) unsigned DEFAULT '0' COMMENT '对应的探针包记录id, 卸载的时候不用填',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT '' COMMENT '备注信息',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
  `env_code` varchar(20) COLLATE utf8mb4_bin DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `sign` varchar(255) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_an_ai_cid` (`application_name`,`agent_id`,`customer_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2078 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='应用节点探针操作表';

-- ----------------------------
-- Table structure for t_application_plugin_download_path
-- ----------------------------
DROP TABLE IF EXISTS `t_application_plugin_download_path`;
CREATE TABLE `t_application_plugin_download_path` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `application_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '应用id',
  `application_name` varchar(64) DEFAULT '' COMMENT '应用名',
  `path_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '类型 0:oss;1:ftp;2:nginx',
  `context` varchar(1000) NOT NULL COMMENT '配置内容',
  `salt` varchar(60) NOT NULL COMMENT '密码加密的密钥',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `valid_status` tinyint(4) DEFAULT '0' COMMENT '检测状态 0:未检测,1:检测失败,2:检测成功',
  `valid_error_info` varchar(10240) DEFAULT '' COMMENT '检测异常信息',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COMMENT='探针根目录';

-- ----------------------------
-- Table structure for t_application_plugin_upgrade
-- ----------------------------
DROP TABLE IF EXISTS `t_application_plugin_upgrade`;
CREATE TABLE `t_application_plugin_upgrade` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `application_id` bigint(20) DEFAULT '0' COMMENT '应用id',
  `application_name` varchar(64) DEFAULT '' COMMENT '应用名',
  `upgrade_batch` varchar(64) NOT NULL COMMENT '升级批次 根据升级内容生成MD5',
  `upgrade_context` varchar(10240) DEFAULT '' COMMENT '升级内容 格式 {pluginId,pluginId}',
  `upgrade_agent_id` varchar(40) DEFAULT NULL COMMENT '处理升级对应的agentId',
  `download_path` varchar(255) DEFAULT '' COMMENT '下载地址',
  `plugin_upgrade_status` tinyint(2) DEFAULT '0' COMMENT '升级状态 0 未升级 1升级成功 2升级失败 3已回滚',
  `node_num` int(4) NOT NULL DEFAULT '1' COMMENT '节点数量',
  `error_info` varchar(2048) DEFAULT NULL COMMENT '升级失败信息',
  `type` tinyint(2) DEFAULT NULL COMMENT '升级单类型 0 agent上报，1 主动升级',
  `remark` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `user_id` bigint(20) DEFAULT '0' COMMENT '操作人id',
  `user_name` varchar(64) DEFAULT '' COMMENT '操作人',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `IS_DELETED` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_applicationId_upgradeBatch_env` (`application_id`,`upgrade_batch`,`env_code`,`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=506 DEFAULT CHARSET=utf8mb4 COMMENT='应用升级单';

-- ----------------------------
-- Table structure for t_application_plugin_upgrade_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_application_plugin_upgrade_ref`;
CREATE TABLE `t_application_plugin_upgrade_ref` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `upgrade_batch` varchar(64) NOT NULL COMMENT '升级批次 根据升级内容生成MD5',
  `plugin_name` varchar(100) DEFAULT '' COMMENT '插件名称',
  `plugin_version` varchar(20) DEFAULT '' COMMENT '插件版本',
  `plugin_version_num` bigint(20) DEFAULT NULL COMMENT '版本对应数值',
  `remark` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uni_upgradeBatch_plugin_env` (`upgrade_batch`,`plugin_name`,`plugin_version`,`env_code`,`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=40693 DEFAULT CHARSET=utf8mb4 COMMENT='应用升级批次明细';

-- ----------------------------
-- Table structure for t_application_plugins_config
-- ----------------------------
DROP TABLE IF EXISTS `t_application_plugins_config`;
CREATE TABLE `t_application_plugins_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_id` bigint(20) NOT NULL COMMENT '应用id',
  `application_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '应用名称',
  `config_item` varchar(256) NOT NULL COMMENT '配置项',
  `config_key` varchar(255) DEFAULT NULL COMMENT '配置项key',
  `config_desc` varchar(256) NOT NULL COMMENT '配置说明',
  `config_value` varchar(256) DEFAULT NULL COMMENT '配置值',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户Id',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modifie_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `modifier_id` bigint(20) DEFAULT NULL COMMENT '更新人',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1466354156969371129 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_application_tag_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_application_tag_ref`;
CREATE TABLE `t_application_tag_ref` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `application_id` bigint(20) NOT NULL COMMENT '应用id',
  `application_name` varchar(64) DEFAULT '' COMMENT '应用名',
  `tag_id` bigint(20) NOT NULL COMMENT '标签id',
  `tag_name` varchar(64) NOT NULL COMMENT '标签名',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COMMENT='应用标签表';

-- ----------------------------
-- Table structure for t_base_config
-- ----------------------------
DROP TABLE IF EXISTS `t_base_config`;
CREATE TABLE `t_base_config` (
  `CONFIG_CODE` varchar(64) NOT NULL COMMENT '配置编码',
  `CONFIG_VALUE` longtext NOT NULL COMMENT '配置值',
  `CONFIG_DESC` varchar(128) NOT NULL COMMENT '配置说明',
  `USE_YN` int(11) DEFAULT '0' COMMENT '是否可用(0表示未启用,1表示启用)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
  `UPDATE_TIME` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` bigint(20) DEFAULT '-1' COMMENT '租户 id, 默认 -1',
  `env_code` varchar(100) DEFAULT 'system' COMMENT '环境标识',
  PRIMARY KEY (`CONFIG_CODE`) USING BTREE,
  UNIQUE KEY `unique_idx_config_code` (`CONFIG_CODE`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='tro配置表';

-- ----------------------------
-- Table structure for t_black_list
-- ----------------------------
DROP TABLE IF EXISTS `t_black_list`;
CREATE TABLE `t_black_list` (
  `BLIST_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `REDIS_KEY` varchar(200) DEFAULT NULL COMMENT 'redis的键',
  `PRINCIPAL_NO` varchar(10) DEFAULT NULL COMMENT '负责人工号(废弃不用)',
  `USE_YN` int(11) DEFAULT NULL COMMENT '是否可用(0表示未启动,1表示启动,2表示启用未校验)',
  `CUSTOMER_ID` bigint(20) DEFAULT NULL COMMENT '租户id',
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
  `type` tinyint(4) DEFAULT NULL COMMENT '黑名单类型',
  `value` varchar(1024) DEFAULT NULL COMMENT '黑名单数据',
  `APPLICATION_ID` bigint(20) NOT NULL COMMENT '应用id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '软删',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`BLIST_ID`) USING BTREE,
  KEY `T_BLACK_LIST_INDEX2` (`PRINCIPAL_NO`) USING BTREE,
  KEY `T_BLACK_LIST_INDEX3` (`USE_YN`) USING BTREE,
  KEY `idx_env_tenant` (`env_code`,`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7116 DEFAULT CHARSET=utf8 COMMENT='黑名单管理';

-- ----------------------------
-- Table structure for t_business_domain
-- ----------------------------
DROP TABLE IF EXISTS `t_business_domain`;
CREATE TABLE `t_business_domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT '业务域名称',
  `type` tinyint(4) DEFAULT NULL COMMENT '业务域类型（预定义，暂时不用）',
  `domain_order` tinyint(5) NOT NULL DEFAULT '100' COMMENT '业务域顺序',
  `domain_code` tinyint(50) NOT NULL COMMENT '业务域编码',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '业务域状态;0为可用,1为不可用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除 0:正常;1:删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=309 DEFAULT CHARSET=utf8mb4 COMMENT='业务域表';

-- ----------------------------
-- Table structure for t_business_link_manage_table
-- ----------------------------
DROP TABLE IF EXISTS `t_business_link_manage_table`;
CREATE TABLE `t_business_link_manage_table` (
  `LINK_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `LINK_NAME` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `ENTRACE` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '链路入口',
  `RELATED_TECH_LINK` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '业务链路绑定的技术链路',
  `LINK_LEVEL` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '业务链路级别: p0/p1/p2/p3 ',
  `PARENT_BUSINESS_ID` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '业务链路的上级业务链路id',
  `IS_CHANGE` tinyint(4) DEFAULT NULL COMMENT '是否有变更 0:正常；1:已变更',
  `IS_CORE` tinyint(4) DEFAULT NULL COMMENT '业务链路是否否核心链路 0:不是;1:是',
  `CUSTOMER_ID` bigint(20) DEFAULT NULL COMMENT '租户id',
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `BUSINESS_DOMAIN` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '业务域: 0:订单域", "1:运单域", "2:结算域 ',
  `CAN_DELETE` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否可以删除 0:可以删除;1:不可以删除',
  `TYPE` tinyint(4) NOT NULL DEFAULT '0' COMMENT '业务活动类型',
  `BIND_BUSINESS_ID` bigint(20) DEFAULT NULL COMMENT '绑定业务活动id',
  `SERVER_MIDDLEWARE_TYPE` varchar(20) DEFAULT NULL COMMENT '中间件类型（KAFKA,RABBITMQ,....）',
  `persistence` tinyint(4) DEFAULT '1',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `application_name` varchar(200) DEFAULT NULL COMMENT '应用名',
  `application_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`LINK_ID`) USING BTREE,
  KEY `T_LINK_MNT_INDEX1` (`LINK_NAME`) USING BTREE,
  KEY `T_LINK_MNT_INDEX2` (`ENTRACE`(255)) USING BTREE,
  KEY `T_LINK_MNT_INDEX3` (`CREATE_TIME`) USING BTREE,
  KEY `RELATED_TECH_LINK` (`RELATED_TECH_LINK`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1001285 DEFAULT CHARSET=utf8mb4 COMMENT='业务链路管理表';

-- ----------------------------
-- Table structure for t_cache_config_template
-- ----------------------------
DROP TABLE IF EXISTS `t_cache_config_template`;
CREATE TABLE `t_cache_config_template` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT '' COMMENT '中间件中文描述',
  `type` varchar(100) DEFAULT '' COMMENT '中间件所属类型',
  `eng_name` varchar(100) DEFAULT '' COMMENT '中间件英文名称',
  `cobm_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持自动梳理;0:不支持;1:支持',
  `shadowtdb_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持影子key;0:不支持;1:支持',
  `shadowttable_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持影子集群;0:不支持;1:支持',
  `config` varchar(500) DEFAULT NULL COMMENT '配置文本',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '1.可用，2不可用',
  `remark` varchar(500) DEFAULT NULL COMMENT '标记',
  `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `CUSTOMER_ID` bigint(20) DEFAULT '0' COMMENT '租户id 废弃',
  `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `eng_name` (`eng_name`) USING BTREE,
  UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='缓存配置模版表';

-- ----------------------------
-- Table structure for t_config_server
-- ----------------------------
DROP TABLE IF EXISTS `t_config_server`;
CREATE TABLE `t_config_server` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(100) COLLATE utf8mb4_bin NOT NULL COMMENT '配置的 key',
  `value` varchar(200) COLLATE utf8mb4_bin DEFAULT '' COMMENT '配置的值',
  `tenant_id` bigint(20) DEFAULT '-99' COMMENT '租户id, -99 表示无',
  `env_code` varchar(20) COLLATE utf8mb4_bin DEFAULT '' COMMENT '环境',
  `tenant_app_key` varchar(80) COLLATE utf8mb4_bin DEFAULT '' COMMENT '租户key',
  `is_tenant` tinyint(3) unsigned DEFAULT '1' COMMENT '是否是住户使用, 1 是, 0 否',
  `is_global` tinyint(3) unsigned DEFAULT '1' COMMENT '是否是全局的, 1 是, 0 否',
  `edition` tinyint(3) unsigned DEFAULT '6' COMMENT '归属版本, 1 开源版, 2 企业版, 6 开源版和企业版',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_k_uk_e` (`key`,`tenant_app_key`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='配置表-服务的配置';

-- ----------------------------
-- Table structure for t_connectpool_config_template
-- ----------------------------
DROP TABLE IF EXISTS `t_connectpool_config_template`;
CREATE TABLE `t_connectpool_config_template` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT '' COMMENT '中间件中文描述',
  `type` varchar(100) DEFAULT '' COMMENT '中间件所属类型',
  `eng_name` varchar(100) DEFAULT '' COMMENT '中间件英文名称',
  `cobm_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持自动梳理;0:不支持;1:支持',
  `shadowtable_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持影子表方案;0:不支持;1:支持',
  `shadowdb_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持影子库方案;0:不支持;1:支持',
  `shadowdbwithshadowtable_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持影子库和影子库方案;0:不支持;1:支持',
  `shadowdb_attribute` text COMMENT '影子库方案配置属性',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '1.可用，2不可用',
  `remark` varchar(500) DEFAULT NULL COMMENT '标记',
  `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `CUSTOMER_ID` bigint(20) DEFAULT '0' COMMENT '租户id 废弃',
  `USER_ID` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `gmt_create` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `eng_name` (`eng_name`) USING BTREE,
  UNIQUE KEY `idx_eng_name_env_tenant` (`eng_name`,`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='连接池配置模版表';

-- ----------------------------
-- Table structure for t_data_build
-- ----------------------------
DROP TABLE IF EXISTS `t_data_build`;
CREATE TABLE `t_data_build` (
  `DATA_BUILD_ID` bigint(20) NOT NULL COMMENT '数据构建id',
  `APPLICATION_ID` bigint(20) DEFAULT NULL COMMENT '应用id',
  `DDL_BUILD_STATUS` int(11) DEFAULT '0' COMMENT '影子库表结构脚本构建状态(0未启动 1正在执行 2执行成功 3执行失败)',
  `DDL_LAST_SUCCESS_TIME` datetime DEFAULT NULL COMMENT '影子库表结构脚本上一次执行成功时间',
  `CACHE_BUILD_STATUS` int(11) DEFAULT '0' COMMENT '缓存预热脚本执行状态',
  `CACHE_LAST_SUCCESS_TIME` datetime DEFAULT NULL COMMENT '缓存预热脚本上一次执行成功时间',
  `READY_BUILD_STATUS` int(11) DEFAULT '0' COMMENT '基础数据准备脚本执行状态',
  `READY_LAST_SUCCESS_TIME` datetime DEFAULT NULL COMMENT '基础数据准备脚本上一次执行成功时间',
  `BASIC_BUILD_STATUS` int(11) DEFAULT '0' COMMENT '铺底数据脚本执行状态',
  `BASIC_LAST_SUCCESS_TIME` datetime DEFAULT NULL COMMENT '铺底数据脚本上一次执行成功时间',
  `CLEAN_BUILD_STATUS` int(11) DEFAULT '0' COMMENT '数据清理脚本执行状态',
  `CLEAN_LAST_SUCCESS_TIME` datetime DEFAULT NULL COMMENT '数据清理脚本上一次执行成功时间',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`DATA_BUILD_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='压测数据构建表';

-- ----------------------------
-- Table structure for t_datasource_tag_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_datasource_tag_ref`;
CREATE TABLE `t_datasource_tag_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datasource_id` bigint(20) NOT NULL COMMENT '数据源id',
  `tag_id` bigint(20) NOT NULL COMMENT '标签id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_datasourceId_tagId` (`datasource_id`,`tag_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

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
-- Table structure for t_engine_plugin_files_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_engine_plugin_files_ref`;
CREATE TABLE `t_engine_plugin_files_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_id` bigint(20) NOT NULL COMMENT '外键关联-插件',
  `file_name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '文件名称',
  `file_path` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '文件路径',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压力引擎插件信息-文件';

-- ----------------------------
-- Table structure for t_engine_plugin_info
-- ----------------------------
DROP TABLE IF EXISTS `t_engine_plugin_info`;
CREATE TABLE `t_engine_plugin_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_type` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '插件类型',
  `plugin_name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '插件名称',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否启用',
  `gmt_create` datetime NOT NULL COMMENT '创建时间',
  `gmt_update` datetime NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `t_engine_plugin_info_plugin_type` (`plugin_type`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压力引擎插件信息';

-- ----------------------------
-- Table structure for t_engine_plugin_supported_versions
-- ----------------------------
DROP TABLE IF EXISTS `t_engine_plugin_supported_versions`;
CREATE TABLE `t_engine_plugin_supported_versions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `plugin_id` bigint(20) DEFAULT NULL COMMENT '外键关联-插件',
  `supported_version` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '版本',
  `file_ref_id` bigint(20) NOT NULL COMMENT '外键关联-插件文件',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压力引擎插件信息-版本';

-- ----------------------------
-- Table structure for t_exception_info
-- ----------------------------
DROP TABLE IF EXISTS `t_exception_info`;
CREATE TABLE `t_exception_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` varchar(512) DEFAULT NULL COMMENT '异常类型',
  `code` varchar(512) DEFAULT NULL COMMENT '异常编码',
  `description` varchar(512) DEFAULT NULL COMMENT '异常描述',
  `suggestion` varchar(512) DEFAULT NULL COMMENT '处理建议',
  `count` bigint(20) DEFAULT '0' COMMENT '发生次数',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '软删',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `agent_code` varchar(512) DEFAULT NULL COMMENT 'agentCode',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_fast_debug_config_info
-- ----------------------------
DROP TABLE IF EXISTS `t_fast_debug_config_info`;
CREATE TABLE `t_fast_debug_config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL COMMENT '调试名称',
  `request_url` varchar(256) NOT NULL COMMENT '完整url',
  `headers` varchar(1024) DEFAULT NULL,
  `cookies` varchar(2048) DEFAULT NULL COMMENT 'cookies',
  `body` longtext COMMENT '请求体',
  `http_method` varchar(128) DEFAULT NULL COMMENT '请求方法',
  `timeout` int(11) DEFAULT NULL COMMENT '响应超时时间',
  `is_redirect` tinyint(1) DEFAULT NULL COMMENT '是否重定向',
  `business_link_id` bigint(20) NOT NULL COMMENT '业务活动id',
  `content_type` varchar(512) DEFAULT NULL COMMENT 'contentType数据',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0：未调试，1，调试中',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '软删',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `modifier_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=270 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_fast_debug_exception
-- ----------------------------
DROP TABLE IF EXISTS `t_fast_debug_exception`;
CREATE TABLE `t_fast_debug_exception` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `result_id` bigint(20) DEFAULT NULL COMMENT '结果id',
  `trace_id` varchar(512) DEFAULT NULL COMMENT 'traceId',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `agent_Id` longtext COMMENT 'agentid',
  `rpc_id` varchar(512) DEFAULT NULL COMMENT 'rpc_id',
  `application_Name` varchar(512) NOT NULL COMMENT '应用名',
  `type` varchar(512) DEFAULT NULL COMMENT '异常类型',
  `code` varchar(512) DEFAULT NULL COMMENT '异常编码',
  `description` longtext COMMENT '异常描述',
  `suggestion` varchar(512) DEFAULT NULL COMMENT '处理建议',
  `time` varchar(512) DEFAULT NULL COMMENT '异常时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '软删',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_traceId` (`trace_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=283 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_fast_debug_machine_performance
-- ----------------------------
DROP TABLE IF EXISTS `t_fast_debug_machine_performance`;
CREATE TABLE `t_fast_debug_machine_performance` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_id` varchar(512) DEFAULT NULL COMMENT 'traceId',
  `rpc_id` varchar(512) DEFAULT NULL COMMENT 'rpcid',
  `log_type` tinyint(4) DEFAULT NULL COMMENT '服务端、客户端',
  `index` varchar(128) DEFAULT NULL COMMENT '性能类型，beforeFirst/beforeLast/afterFirst/afterLast/exceptionFirst/exceptionLast',
  `cpu_usage` decimal(10,4) DEFAULT '0.0000' COMMENT 'cpu利用率',
  `cpu_load` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT 'cpu load',
  `memory_usage` decimal(10,4) DEFAULT '0.0000' COMMENT '没存利用率',
  `memory_total` decimal(20,2) DEFAULT '0.00' COMMENT '堆内存总和',
  `io_wait` decimal(10,4) DEFAULT '0.0000' COMMENT 'io 等待率',
  `young_gc_count` bigint(20) DEFAULT NULL,
  `young_gc_time` bigint(20) DEFAULT NULL,
  `old_gc_count` bigint(20) DEFAULT NULL,
  `old_gc_time` bigint(20) DEFAULT NULL,
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_trace_id` (`trace_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2998609 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_fast_debug_result
-- ----------------------------
DROP TABLE IF EXISTS `t_fast_debug_result`;
CREATE TABLE `t_fast_debug_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `config_id` bigint(20) NOT NULL COMMENT '调试配置id',
  `name` varchar(256) NOT NULL COMMENT '调试名称',
  `business_link_name` varchar(256) NOT NULL COMMENT '业务活动名',
  `request_url` varchar(1024) NOT NULL COMMENT '完整url',
  `http_method` varchar(256) NOT NULL COMMENT '调试名称',
  `request` longtext,
  `leakage_check_data` longtext COMMENT '漏数检查数据,每次自己报存一份，并保持结果',
  `response` longtext COMMENT '请求返回体',
  `error_message` longtext,
  `trace_id` varchar(512) DEFAULT NULL COMMENT '通过response解析出来traceId',
  `call_time` bigint(20) DEFAULT NULL COMMENT '调用时长',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '软删',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '操作人',
  `debug_result` tinyint(1) DEFAULT NULL COMMENT '0:失败；1：成功；调试中根据config中status判断',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=900 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_fast_debug_stack_info
-- ----------------------------
DROP TABLE IF EXISTS `t_fast_debug_stack_info`;
CREATE TABLE `t_fast_debug_stack_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(255) DEFAULT NULL,
  `agent_id` varchar(255) DEFAULT NULL,
  `trace_id` varchar(512) DEFAULT NULL COMMENT 'traceId',
  `rpc_id` varchar(512) NOT NULL COMMENT 'rpcid',
  `level` varchar(64) DEFAULT NULL COMMENT '日志级别',
  `type` tinyint(4) DEFAULT NULL COMMENT '服务端，客户端',
  `content` longtext COMMENT 'stack信息',
  `is_stack` tinyint(1) DEFAULT NULL COMMENT '是否调用栈日志',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_traceId` (`trace_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2052227 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_file_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_file_manage`;
CREATE TABLE `t_file_manage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '文件名称',
  `file_size` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '文件大小：2MB',
  `file_ext` varchar(16) COLLATE utf8_bin DEFAULT NULL COMMENT '文件后缀',
  `file_type` tinyint(4) NOT NULL COMMENT '文件类型：0-脚本文件 1-数据文件 2-脚本jar文件 3-jmeter ext jar',
  `file_extend` text COLLATE utf8_bin COMMENT '{\n  “dataCount”:”数据量”,\n  “isSplit”:”是否拆分”,\n  “topic”:”MQ主题”,\n  “nameServer”:”mq nameServer”,\n}',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id ,废弃',
  `upload_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '上传时间',
  `upload_path` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '上传路径：相对路径',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0',
  `gmt_create` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `gmt_update` datetime(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `env_code` varchar(20) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `md5` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '文件MD5值',
  `sign` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5212 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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
-- Table structure for t_interface_performance_config
-- ----------------------------
DROP TABLE IF EXISTS `t_interface_performance_config`;
CREATE TABLE `t_interface_performance_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL COMMENT '压测场景名称',
  `request_url` varchar(256) NOT NULL COMMENT '请求完整URL',
  `headers` varchar(1024) DEFAULT NULL COMMENT 'headers',
  `cookies` varchar(2048) DEFAULT NULL COMMENT 'cookies',
  `body` longtext COMMENT '请求体',
  `http_method` varchar(128) DEFAULT NULL COMMENT '请求方法',
  `timeout` int(1) DEFAULT NULL COMMENT '响应超时时间',
  `is_redirect` tinyint(1) DEFAULT NULL COMMENT '是否重定向',
  `content_type` varchar(512) DEFAULT NULL COMMENT 'contentType数据',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0:未调试,1:调试中',
  `entrance_app_name` varchar(64) DEFAULT NULL COMMENT '关联入口应用',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT 'test' COMMENT '环境标识',
  `user_id` bigint(20) DEFAULT NULL COMMENT '归属人',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除(1-删除 0-未删除)',
  `remark` longtext COMMENT '备注',
  `creator_id` bigint(20) DEFAULT NULL COMMENT '创建人',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `modifier_id` bigint(20) DEFAULT NULL COMMENT '修改人',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_name_tenant_env` (`name`,`tenant_id`,`env_code`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=438 DEFAULT CHARSET=utf8mb4 COMMENT='接口压测配置表';

-- ----------------------------
-- Table structure for t_interface_performance_config_scene_relateship
-- ----------------------------
DROP TABLE IF EXISTS `t_interface_performance_config_scene_relateship`;
CREATE TABLE `t_interface_performance_config_scene_relateship` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `api_id` bigint(20) NOT NULL COMMENT 'apiId',
  `scene_id` bigint(20) DEFAULT NULL COMMENT '场景id',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(128) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT 'test' COMMENT '环境标识',
  `flow_id` bigint(20) DEFAULT NULL COMMENT '业务流程id',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_api_scene_id_env` (`api_id`,`scene_id`,`env_code`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=744 DEFAULT CHARSET=utf8mb4 COMMENT='接口配置场景关联表';

-- ----------------------------
-- Table structure for t_interface_performance_param
-- ----------------------------
DROP TABLE IF EXISTS `t_interface_performance_param`;
CREATE TABLE `t_interface_performance_param` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `config_id` bigint(20) NOT NULL COMMENT '接口压测配置ID',
  `param_name` varchar(128) NOT NULL COMMENT '参数名',
  `param_value` varchar(128) NOT NULL COMMENT '参数逻辑',
  `type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '1:自定义参数,2:数据源参数',
  `file_column_index` int(16) DEFAULT NULL COMMENT '文件列索引',
  `file_id` bigint(20) DEFAULT NULL COMMENT '关联文件Id',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户Id',
  `env_code` varchar(128) NOT NULL DEFAULT 'test' COMMENT '环境',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_param_name_tenant_env` (`param_name`,`type`,`file_id`,`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=737 DEFAULT CHARSET=utf8mb4 COMMENT='接口配置关联参数表';

-- ----------------------------
-- Table structure for t_interface_performance_result
-- ----------------------------
DROP TABLE IF EXISTS `t_interface_performance_result`;
CREATE TABLE `t_interface_performance_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `result_id` varchar(128) NOT NULL COMMENT '生成一个结果UUID',
  `config_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '接口压测Id(临时调试默认为1)',
  `request_url` varchar(1024) NOT NULL COMMENT '请求URL',
  `http_method` varchar(128) NOT NULL COMMENT '请求方式',
  `request` longtext COMMENT '请求体，包含url,body,header',
  `response` longtext COMMENT '请求返回体',
  `status` int(8) NOT NULL DEFAULT '200' COMMENT '返回状态码',
  `error_message` longtext COMMENT '错误信息',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_config_result_id` (`config_id`,`result_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3516 DEFAULT CHARSET=utf8mb4 COMMENT='接口压测-调试结果表';

-- ----------------------------
-- Table structure for t_interface_type_child
-- ----------------------------
DROP TABLE IF EXISTS `t_interface_type_child`;
CREATE TABLE `t_interface_type_child` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL DEFAULT '' COMMENT '中间件中文描述',
  `main_id` varchar(40) NOT NULL DEFAULT '' COMMENT '中间件主类型',
  `eng_name` varchar(100) NOT NULL DEFAULT '' COMMENT '中间件英文名称',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_eng_name` (`eng_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='远程调用中间件子类型表';

-- ----------------------------
-- Table structure for t_interface_type_config
-- ----------------------------
DROP TABLE IF EXISTS `t_interface_type_config`;
CREATE TABLE `t_interface_type_config` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `interface_type_id` bigint(20) NOT NULL COMMENT '中间件子类型id',
  `config_id` int(11) NOT NULL COMMENT '支持配置',
  `config_text` varchar(250) DEFAULT '' COMMENT '支持配置文本',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_eng_name_env_tenant` (`interface_type_id`,`config_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COMMENT='远程调用接口类型配置表';

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
-- Table structure for t_kither
-- ----------------------------
DROP TABLE IF EXISTS `t_kither`;
CREATE TABLE `t_kither` (
  `time` varchar(100) DEFAULT NULL,
  `count` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_leakcheck_config
-- ----------------------------
DROP TABLE IF EXISTS `t_leakcheck_config`;
CREATE TABLE `t_leakcheck_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `business_activity_id` bigint(20) NOT NULL COMMENT '业务活动id',
  `datasource_id` bigint(20) NOT NULL COMMENT '数据源id',
  `leakcheck_sql_ids` text NOT NULL COMMENT '漏数sql主键集合，逗号分隔',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_leakcheck_config_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_leakcheck_config_detail`;
CREATE TABLE `t_leakcheck_config_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `datasource_id` bigint(20) NOT NULL COMMENT '数据源id',
  `sql_content` varchar(255) NOT NULL COMMENT '漏数sql',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2209 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_leakverify_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_leakverify_detail`;
CREATE TABLE `t_leakverify_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `result_id` bigint(20) NOT NULL COMMENT '验证结果id',
  `leak_sql` varchar(500) NOT NULL COMMENT '漏数sql',
  `status` tinyint(1) DEFAULT '0' COMMENT '是否漏数 0:正常;1:漏数;2:未检测;3:检测失败',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=70936 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_leakverify_result
-- ----------------------------
DROP TABLE IF EXISTS `t_leakverify_result`;
CREATE TABLE `t_leakverify_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ref_type` tinyint(1) DEFAULT '0' COMMENT '引用类型 0:压测场景;1:业务流程;2:业务活动',
  `ref_id` bigint(20) NOT NULL COMMENT '引用id',
  `report_id` bigint(20) DEFAULT NULL COMMENT '报告id',
  `dbresource_id` bigint(20) NOT NULL COMMENT '数据源id',
  `dbresource_name` varchar(255) DEFAULT NULL COMMENT '数据源名称',
  `dbresource_url` varchar(500) DEFAULT NULL COMMENT '数据源地址',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16113 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_link_detection
-- ----------------------------
DROP TABLE IF EXISTS `t_link_detection`;
CREATE TABLE `t_link_detection` (
  `LINK_DETECTION_ID` bigint(20) NOT NULL COMMENT '主键id',
  `APPLICATION_ID` bigint(20) DEFAULT NULL COMMENT '应用id',
  `SHADOWLIB_CHECK` int(11) DEFAULT '0' COMMENT '影子库整体同步检测状态(0未启用,1正在检测,2检测成功,3检测失败)',
  `SHADOWLIB_ERROR` text COMMENT '影子库检测失败内容',
  `CACHE_CHECK` int(11) DEFAULT '0' COMMENT '缓存预热校验状态(0未启用,1正在检测,2检测成功,3检测失败)',
  `CACHE_ERROR` text COMMENT '缓存预热实时检测失败内容',
  `WLIST_CHECK` int(11) DEFAULT '0' COMMENT '白名单校验状态(0未启用,1正在检测,2检测成功,3检测失败)',
  `WLIST_ERROR` text COMMENT '白名单异常错误信息',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`LINK_DETECTION_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='链路检测表';

-- ----------------------------
-- Table structure for t_link_guard
-- ----------------------------
DROP TABLE IF EXISTS `t_link_guard`;
CREATE TABLE `t_link_guard` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据库唯一主键',
  `application_name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '项目名称',
  `application_id` bigint(20) DEFAULT NULL COMMENT '应用id',
  `method_info` varchar(500) COLLATE utf8_bin DEFAULT NULL COMMENT '出口信息',
  `groovy` longtext COLLATE utf8_bin COMMENT 'GROOVY脚本',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `is_enable` tinyint(4) DEFAULT '1' COMMENT '0:未启用；1:启用',
  `remark` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  `env_code` varchar(20) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `sign` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_app_id` (`application_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=311 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_link_manage_table
-- ----------------------------
DROP TABLE IF EXISTS `t_link_manage_table`;
CREATE TABLE `t_link_manage_table` (
  `LINK_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `LINK_NAME` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '链路名称',
  `ENTRACE` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '链路入口',
  `PT_ENTRACE` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '影子入口',
  `CHANGE_BEFORE` longtext CHARACTER SET utf8 COMMENT '技术链路变更前',
  `CHANGE_AFTER` longtext CHARACTER SET utf8 COMMENT '技术链路变更后',
  `CHANGE_REMARK` longtext CHARACTER SET utf8 COMMENT '变化差异',
  `IS_CHANGE` tinyint(4) DEFAULT NULL COMMENT '是否有变更 0:正常；1:已变更',
  `IS_JOB` tinyint(4) DEFAULT '0' COMMENT '任务类型 0:普通入口；1:定时任务',
  `CUSTOMER_ID` bigint(20) DEFAULT NULL COMMENT '租户id',
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `APPLICATION_NAME` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '应用名',
  `CHANGE_TYPE` tinyint(4) DEFAULT NULL COMMENT '变更类型: 1:无流量调用通知;2:添加调用关系通知',
  `CAN_DELETE` tinyint(4) DEFAULT '0' COMMENT '是否可以删除 0:可以删除;1:不可以删除',
  `features` text,
  `persistence` tinyint(4) DEFAULT '1',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  `application_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`LINK_ID`) USING BTREE,
  KEY `T_LINK_MNT_INDEX1` (`LINK_NAME`) USING BTREE,
  KEY `T_LINK_MNT_INDEX2` (`ENTRACE`(255)) USING BTREE,
  KEY `T_LINK_MNT_INDEX3` (`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1265 DEFAULT CHARSET=utf8mb4 COMMENT='链路管理表';

-- ----------------------------
-- Table structure for t_link_mnt
-- ----------------------------
DROP TABLE IF EXISTS `t_link_mnt`;
CREATE TABLE `t_link_mnt` (
  `LINK_ID` bigint(20) NOT NULL COMMENT '主键id',
  `LINK_NAME` varchar(200) DEFAULT NULL COMMENT '链路名称',
  `TECH_LINKS` text COMMENT '业务链路下属技术链路ids',
  `LINK_DESC` varchar(200) DEFAULT NULL COMMENT '链路说明',
  `LINK_TYPE` int(11) DEFAULT NULL COMMENT '1: 表示业务链路; 2: 表示技术链路; 3: 表示既是业务也是技术链路; ',
  `ASWAN_ID` varchar(200) DEFAULT NULL COMMENT '阿斯旺链路id',
  `LINK_ENTRENCE` varchar(200) DEFAULT NULL COMMENT '链路入口(http接口)',
  `RT_SA` varchar(10) DEFAULT NULL COMMENT '请求达标率(%)',
  `RT` varchar(10) DEFAULT NULL COMMENT '请求标准毫秒值',
  `TPS` varchar(20) DEFAULT NULL COMMENT '吞吐量(每秒完成事务数量)',
  `TARGET_SUCCESS_RATE` varchar(10) DEFAULT NULL COMMENT '目标成功率(%)',
  `DICT_TYPE` varchar(50) DEFAULT NULL COMMENT '字典分类',
  `LINK_RANK` varchar(20) DEFAULT NULL COMMENT '链路等级',
  `PRINCIPAL_NO` varchar(10) DEFAULT NULL COMMENT '链路负责人工号',
  `USE_YN` int(11) DEFAULT NULL COMMENT '是否可用(0表示未启用,1表示启用)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
  `LINK_MODULE` varchar(5) DEFAULT NULL COMMENT '链路模块 1下单 2订单 3开单 4中转 5派送 6签收 7CUBC结算 10非主流程链路',
  `VOLUME_CALC_STATUS` varchar(5) DEFAULT NULL COMMENT '是否计算单量',
  `TARGET_TPS` varchar(20) DEFAULT NULL COMMENT '目标TPS',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`LINK_ID`) USING BTREE,
  KEY `T_LINK_MNT_INDEX1` (`LINK_NAME`) USING BTREE,
  KEY `T_LINK_MNT_INDEX2` (`USE_YN`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='链路管理表';

-- ----------------------------
-- Table structure for t_link_service_mnt
-- ----------------------------
DROP TABLE IF EXISTS `t_link_service_mnt`;
CREATE TABLE `t_link_service_mnt` (
  `LINK_SERVICE_ID` bigint(20) NOT NULL COMMENT '主键id',
  `LINK_ID` bigint(20) DEFAULT NULL COMMENT '链路id',
  `INTERFACE_NAME` varchar(1000) DEFAULT NULL COMMENT '接口名称',
  `INTERFACE_DESC` varchar(1000) DEFAULT NULL COMMENT '接口说明',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`LINK_SERVICE_ID`) USING BTREE,
  KEY `T_LS_IDX1` (`LINK_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基础链路服务关联表';

-- ----------------------------
-- Table structure for t_link_topology_info
-- ----------------------------
DROP TABLE IF EXISTS `t_link_topology_info`;
CREATE TABLE `t_link_topology_info` (
  `TLTI_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '链路拓扑图主键ID',
  `LINK_ID` bigint(20) DEFAULT NULL COMMENT '基础链路id',
  `LINK_NAME` varchar(32) NOT NULL COMMENT '链路名称',
  `ENTRANCE_TYPE` varchar(32) DEFAULT NULL COMMENT '入口类型 job dubbo mq http',
  `LINK_ENTRANCE` varchar(256) DEFAULT NULL COMMENT '链路入口',
  `NAME_SERVER` varchar(1024) DEFAULT NULL COMMENT '队列的 nameserver',
  `LINK_TYPE` varchar(50) DEFAULT NULL COMMENT '链路类型',
  `LINK_GROUP` varchar(32) DEFAULT NULL COMMENT '链路分组',
  `FROM_LINK_IDS` varchar(256) DEFAULT NULL COMMENT '上级链路id',
  `TO_LINK_IDS` varchar(256) DEFAULT NULL COMMENT '下级链路id',
  `SHOW_FROM_LINK_ID` varchar(256) DEFAULT NULL COMMENT '只显示上级ID',
  `SHOW_TO_LINK_ID` varchar(256) DEFAULT NULL COMMENT '只显示下级ID',
  `SECOND_LINK_ID` varchar(20) DEFAULT NULL COMMENT '二级链路Id',
  `SECOND_LINK_NAME` varchar(32) DEFAULT NULL COMMENT '二级链路名称',
  `APPLICATION_NAME` varchar(64) DEFAULT NULL COMMENT '应用名称',
  `VOLUME_CALC_STATUS` varchar(2) DEFAULT NULL COMMENT '1 计算订单量  2 计算运单量 3 不计算单量',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
  `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`TLTI_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='透明流量链路拓扑';

-- ----------------------------
-- Table structure for t_login_record
-- ----------------------------
DROP TABLE IF EXISTS `t_login_record`;
CREATE TABLE `t_login_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(512) DEFAULT NULL COMMENT '登录用户',
  `ip` varchar(128) DEFAULT NULL COMMENT '登录ip',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13646 DEFAULT CHARSET=utf8mb4;

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
-- Table structure for t_middleware_jar
-- ----------------------------
DROP TABLE IF EXISTS `t_middleware_jar`;
CREATE TABLE `t_middleware_jar` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件中文名称',
  `type` varchar(25) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件类型',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '支持的包状态, 1 已支持, 2 待支持, 3 无需支持, 4 待验证',
  `artifact_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件名称',
  `group_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件组织名称',
  `version` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件版本',
  `agv` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT 'artifactId_groupId_version, 做唯一标识,',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标记',
  `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_agv` (`agv`) USING BTREE,
  KEY `idx_artifact_id` (`artifact_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=125406 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='中间件包表';

-- ----------------------------
-- Table structure for t_middleware_jar_copy1
-- ----------------------------
DROP TABLE IF EXISTS `t_middleware_jar_copy1`;
CREATE TABLE `t_middleware_jar_copy1` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件中文名称',
  `type` varchar(25) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件类型',
  `status` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '支持的包状态, 1 已支持, 2 待支持, 3 无需支持, 4 待验证',
  `artifact_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件名称',
  `group_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件组织名称',
  `version` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件版本',
  `agv` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT 'artifactId_groupId_version, 做唯一标识,',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '标记',
  `commit` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_agv` (`agv`) USING BTREE,
  KEY `idx_artifact_id` (`artifact_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=60849 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='中间件包表';

-- ----------------------------
-- Table structure for t_middleware_link_relate
-- ----------------------------
DROP TABLE IF EXISTS `t_middleware_link_relate`;
CREATE TABLE `t_middleware_link_relate` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `MIDDLEWARE_ID` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '中间件id',
  `TECH_LINK_ID` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '技术链路id',
  `BUSINESS_LINK_ID` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '业务链路id',
  `SCENE_ID` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '场景id',
  `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `t_middleware_link_relate1` (`MIDDLEWARE_ID`,`TECH_LINK_ID`) USING BTREE,
  KEY `t_middleware_link_relate2` (`TECH_LINK_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='链路中间件关联表';

-- ----------------------------
-- Table structure for t_middleware_summary
-- ----------------------------
DROP TABLE IF EXISTS `t_middleware_summary`;
CREATE TABLE `t_middleware_summary` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件中文名称',
  `type` varchar(25) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件类型',
  `total_num` int(11) NOT NULL DEFAULT '0' COMMENT '中间件总数',
  `supported_num` int(11) NOT NULL DEFAULT '0' COMMENT '已支持数量',
  `unknown_num` int(11) NOT NULL DEFAULT '0' COMMENT '未知数量',
  `not_supported_num` int(11) NOT NULL DEFAULT '0' COMMENT '无需支持的数量',
  `artifact_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件名称',
  `group_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件组织名称',
  `ag` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT 'artifactId_groupId, 做唯一标识,',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `commit` varchar(500) CHARACTER SET utf8 DEFAULT '' COMMENT '备注',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_ag` (`ag`) USING BTREE,
  KEY `idx_artifact_id` (`artifact_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4838 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='中间件信息表';

-- ----------------------------
-- Table structure for t_middleware_summary_copy1
-- ----------------------------
DROP TABLE IF EXISTS `t_middleware_summary_copy1`;
CREATE TABLE `t_middleware_summary_copy1` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件中文名称',
  `type` varchar(25) COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件类型',
  `total_num` int(11) NOT NULL DEFAULT '0' COMMENT '中间件总数',
  `supported_num` int(11) NOT NULL DEFAULT '0' COMMENT '已支持数量',
  `unknown_num` int(11) NOT NULL DEFAULT '0' COMMENT '未知数量',
  `not_supported_num` int(11) NOT NULL DEFAULT '0' COMMENT '无需支持的数量',
  `artifact_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件名称',
  `group_id` varchar(100) COLLATE utf8mb4_bin NOT NULL DEFAULT '' COMMENT '中间件组织名称',
  `ag` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT 'artifactId_groupId, 做唯一标识,',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `commit` varchar(500) CHARACTER SET utf8 DEFAULT '' COMMENT '备注',
  `status` tinyint(4) NOT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_ag` (`ag`) USING BTREE,
  KEY `idx_artifact_id` (`artifact_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=551 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='中间件信息表';

-- ----------------------------
-- Table structure for t_middleware_type
-- ----------------------------
DROP TABLE IF EXISTS `t_middleware_type`;
CREATE TABLE `t_middleware_type` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT '' COMMENT '中间件中文描述',
  `eng_name` varchar(100) NOT NULL DEFAULT '' COMMENT '中间件英文名称',
  `type` varchar(20) NOT NULL COMMENT '分类',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `value_order` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_eng_name` (`type`,`eng_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COMMENT='调用类型表';

-- ----------------------------
-- Table structure for t_migration_history
-- ----------------------------
DROP TABLE IF EXISTS `t_migration_history`;
CREATE TABLE `t_migration_history` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`) USING BTREE,
  KEY `t_migration_history_s_idx` (`success`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

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
-- Table structure for t_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `t_operation_log`;
CREATE TABLE `t_operation_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `module` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '操作模块-主模块',
  `sub_module` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '操作模块-字模块',
  `type` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '操作类型',
  `status` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '操作状态',
  `content` text COLLATE utf8_bin NOT NULL COMMENT '操作内容描述',
  `user_id` bigint(20) NOT NULL COMMENT '操作人id',
  `user_name` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '操作人名称',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NOT NULL COMMENT '结束时间',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `ip` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '登录ip',
  `env_code` varchar(20) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=31202 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_ops_script_batch_no
-- ----------------------------
DROP TABLE IF EXISTS `t_ops_script_batch_no`;
CREATE TABLE `t_ops_script_batch_no` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `batch_no` longtext COMMENT '批次号',
  `ops_script_id` bigint(20) NOT NULL COMMENT '运维脚本ID',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运维脚本批次号表';

-- ----------------------------
-- Table structure for t_ops_script_execute_result
-- ----------------------------
DROP TABLE IF EXISTS `t_ops_script_execute_result`;
CREATE TABLE `t_ops_script_execute_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `batch_id` bigint(20) DEFAULT NULL COMMENT '批次号id',
  `ops_script_id` bigint(20) NOT NULL COMMENT '运维脚本ID',
  `log_file_path` longtext COMMENT '日志文件路径',
  `upload_id` longtext CHARACTER SET utf8 COLLATE utf8_bin COMMENT 'uploadId 用于删除本地文件',
  `excutor_id` bigint(20) DEFAULT NULL COMMENT '执行人id',
  `execute_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运维脚本执行结果';

-- ----------------------------
-- Table structure for t_ops_script_file
-- ----------------------------
DROP TABLE IF EXISTS `t_ops_script_file`;
CREATE TABLE `t_ops_script_file` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `ops_script_id` bigint(20) NOT NULL COMMENT '运维脚本ID',
  `file_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 1=主要文件 2=附件',
  `file_name` varchar(50) DEFAULT NULL COMMENT '文件名称',
  `file_size` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '文件大小：2MB',
  `file_ext` varchar(16) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL COMMENT '文件后缀',
  `upload_id` longtext CHARACTER SET utf8 COLLATE utf8_bin COMMENT 'uploadId 用于删除本地文件',
  `file_path` longtext COMMENT '文件路径',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运维脚本文件';

-- ----------------------------
-- Table structure for t_ops_script_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_ops_script_manage`;
CREATE TABLE `t_ops_script_manage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(20) DEFAULT NULL COMMENT '脚本名称',
  `script_type` tinyint(1) DEFAULT NULL COMMENT '来源于字典。脚本类型：1=影子库表创建脚本 2=基础数据准备脚本 3=铺底数据脚本 4=影子库表清理脚本 5=缓存预热脚本',
  `customer_id` varchar(20) DEFAULT NULL COMMENT '租户ID',
  `user_id` varchar(20) DEFAULT NULL COMMENT '用户ID',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '执行状态 0=待执行,1=执行中,2=已执行',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='运维脚本主表';

-- ----------------------------
-- Table structure for t_performance_base_data
-- ----------------------------
DROP TABLE IF EXISTS `t_performance_base_data`;
CREATE TABLE `t_performance_base_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `agent_id` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'agentId',
  `app_name` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '应用名称',
  `app_ip` varchar(32) COLLATE utf8_bin NOT NULL COMMENT 'ip',
  `process_id` bigint(20) NOT NULL COMMENT '进程id',
  `process_name` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '进程名称',
  `timestamp` datetime NOT NULL COMMENT '时间戳 YYYY-MM-DD HH:mm:SS',
  `total_memory` decimal(10,2) NOT NULL COMMENT '总内存 MB',
  `perm_memory` decimal(10,2) NOT NULL COMMENT '永久内存 MB',
  `young_memory` decimal(10,2) NOT NULL COMMENT '年轻代内存 MB',
  `old_memory` decimal(10,2) NOT NULL COMMENT '老年代内存 MB',
  `young_gc_count` int(11) NOT NULL COMMENT 'Young Gc次数',
  `full_gc_count` int(11) NOT NULL COMMENT 'Full Gc次数',
  `young_gc_cost` bigint(20) NOT NULL COMMENT 'Young Gc耗时 ms',
  `full_gc_cost` bigint(20) NOT NULL COMMENT 'Full Gc耗时 ms',
  `env_code` varchar(20) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `agentId_appName_appIp_timestamp` (`agent_id`,`app_name`,`app_ip`,`timestamp`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_performance_criteria_config
-- ----------------------------
DROP TABLE IF EXISTS `t_performance_criteria_config`;
CREATE TABLE `t_performance_criteria_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `env` varchar(50) NOT NULL COMMENT '0-针对应用配置 1-全局配置',
  `app_id` bigint(20) NOT NULL COMMENT '应用ID',
  `app_name` varchar(50) NOT NULL COMMENT '应用名称',
  `type` tinyint(4) DEFAULT NULL COMMENT '标准类型1-小于、2-小于等于、3-等于、4-大于、5-大于等于',
  `value` varchar(5) DEFAULT NULL COMMENT '标准值',
  `standard` tinyint(1) DEFAULT NULL COMMENT '是否达标0-达标、1-不达标',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除 0-未删除、1-已删除',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='性能标准配置';

-- ----------------------------
-- Table structure for t_performance_thread_data
-- ----------------------------
DROP TABLE IF EXISTS `t_performance_thread_data`;
CREATE TABLE `t_performance_thread_data` (
  `agent_id` varchar(256) DEFAULT NULL COMMENT 'agent_id',
  `app_name` varchar(256) DEFAULT NULL COMMENT 'app_name',
  `timestamp` varchar(256) DEFAULT NULL COMMENT 'timestamp',
  `app_ip` varchar(256) DEFAULT NULL COMMENT 'app_ip',
  `thread_data` longtext COMMENT '线程栈数据',
  `base_id` bigint(20) NOT NULL COMMENT 'base_thread_关联关系',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`base_id`) USING BTREE,
  UNIQUE KEY `base_id` (`base_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_performance_thread_stack_data
-- ----------------------------
DROP TABLE IF EXISTS `t_performance_thread_stack_data`;
CREATE TABLE `t_performance_thread_stack_data` (
  `thread_stack` longtext COMMENT '线程堆栈',
  `thread_stack_link` bigint(20) NOT NULL COMMENT 'influxDB关联',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`thread_stack_link`) USING BTREE,
  UNIQUE KEY `thread_stack_link` (`thread_stack_link`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_pessure_test_task_activity_config
-- ----------------------------
DROP TABLE IF EXISTS `t_pessure_test_task_activity_config`;
CREATE TABLE `t_pessure_test_task_activity_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `pessure_process_id` bigint(20) NOT NULL COMMENT '压测任务流程ID',
  `name` varchar(20) NOT NULL COMMENT '业务活动名称',
  `type_name` varchar(19) NOT NULL COMMENT '业务活动类型',
  `scenario_id` varchar(50) DEFAULT NULL COMMENT '压测平台场景ID',
  `domain` varchar(50) NOT NULL COMMENT '业务域',
  `target_tps` tinyint(4) DEFAULT NULL COMMENT '目标TPS',
  `target_rt` varchar(5) DEFAULT NULL COMMENT '目标RT',
  `request_success_rate` int(11) DEFAULT NULL COMMENT '目标请求成功率',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除 0-未删除、1-已删除',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `env_code` varchar(20) DEFAULT 'test' COMMENT '环境code',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='压测任务业务活动配置';

-- ----------------------------
-- Table structure for t_pessure_test_task_process_config
-- ----------------------------
DROP TABLE IF EXISTS `t_pessure_test_task_process_config`;
CREATE TABLE `t_pessure_test_task_process_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) NOT NULL COMMENT '压测任务名称',
  `type` tinyint(1) NOT NULL COMMENT '压测任务类型',
  `engine_id` bigint(20) NOT NULL COMMENT '压测引擎ID',
  `engine_name` varchar(20) NOT NULL COMMENT '压测引擎名称',
  `process_id` bigint(20) NOT NULL COMMENT '业务流程ID',
  `process_name` varchar(20) NOT NULL COMMENT '业务流程名称',
  `scenario_id` varchar(20) DEFAULT NULL COMMENT 'PTS场景ID',
  `status` tinyint(1) DEFAULT '0' COMMENT '0-为启动、1-执行中、2-压测结束',
  `pessure_time` datetime DEFAULT NULL COMMENT '压测最新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除 0-未删除、1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表已废弃，压测任务业务流程配置 ';

-- ----------------------------
-- Table structure for t_plugin_dependent
-- ----------------------------
DROP TABLE IF EXISTS `t_plugin_dependent`;
CREATE TABLE `t_plugin_dependent` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `plugin_name` varchar(100) DEFAULT '' COMMENT '插件名称',
  `plugin_version` varchar(20) DEFAULT '' COMMENT '插件版本',
  `plugin_version_num` bigint(20) DEFAULT NULL COMMENT '版本对应数值',
  `dependent_plugin_name` varchar(100) DEFAULT '' COMMENT '插件名称',
  `dependent_plugin_version` varchar(20) DEFAULT '' COMMENT '插件版本',
  `dependent_plugin_version_num` bigint(20) DEFAULT NULL COMMENT '版本对应数值',
  `requisite` tinyint(2) DEFAULT '0' COMMENT '是否必须 0:必须 1:非必须',
  `remark` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `IS_DELETED` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_pluginName_pluginVersion` (`plugin_name`,`plugin_version`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1323 DEFAULT CHARSET=utf8mb4 COMMENT='插件依赖库';

-- ----------------------------
-- Table structure for t_plugin_library
-- ----------------------------
DROP TABLE IF EXISTS `t_plugin_library`;
CREATE TABLE `t_plugin_library` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `plugin_name` varchar(100) NOT NULL COMMENT '插件名称',
  `plugin_type` tinyint(3) DEFAULT '0' COMMENT '插件类型 0:插件 1:主版本 2:agent版本',
  `version` varchar(20) NOT NULL COMMENT '插件版本',
  `version_num` bigint(20) DEFAULT NULL COMMENT '版本对应数值',
  `is_custom_mode` tinyint(3) DEFAULT '0' COMMENT '是否为定制插件，0：否，1：是',
  `is_common_module` tinyint(3) DEFAULT '0' COMMENT '是否为公共模块，0：否，1：是',
  `update_description` varchar(1024) DEFAULT '' COMMENT '更新说明',
  `download_path` varchar(255) DEFAULT '' COMMENT '下载地址',
  `remark` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注',
  `ext` varchar(200) NOT NULL DEFAULT '' COMMENT 'jar包配置文件原始数据,当前是主版本时填',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `IS_DELETED` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `pluginName_version` (`plugin_name`,`version`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1075 DEFAULT CHARSET=utf8mb4 COMMENT='插件版本库';

-- ----------------------------
-- Table structure for t_plugin_tenant_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_plugin_tenant_ref`;
CREATE TABLE `t_plugin_tenant_ref` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `plugin_name` varchar(100) DEFAULT '' COMMENT '插件名称',
  `plugin_version` varchar(20) DEFAULT '' COMMENT '插件版本',
  `owning_tenant_id` bigint(20) NOT NULL COMMENT '所属租户id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `IS_DELETED` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `pluginName_pluginVersion_owningTenantId` (`plugin_name`,`plugin_version`,`owning_tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COMMENT='插件租户关联表';

-- ----------------------------
-- Table structure for t_prada_http_data
-- ----------------------------
DROP TABLE IF EXISTS `t_prada_http_data`;
CREATE TABLE `t_prada_http_data` (
  `TPHD_ID` bigint(20) NOT NULL COMMENT 'prada获取http接口表id',
  `APP_NAME` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '应用名称',
  `INTERFACE_NAME` varchar(1024) COLLATE utf8_bin NOT NULL COMMENT '接口名称',
  `INTERFACE_TYPE` int(11) DEFAULT '1' COMMENT '接口类型(1.http 2.dubbo 3.禁止压测 4.job)',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '同步数据时间',
  PRIMARY KEY (`TPHD_ID`) USING BTREE,
  KEY `TPHD_index1` (`APP_NAME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='表已废弃，prada获取http接口表';

-- ----------------------------
-- Table structure for t_pradar_zk_config
-- ----------------------------
DROP TABLE IF EXISTS `t_pradar_zk_config`;
CREATE TABLE `t_pradar_zk_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `zk_path` varchar(256) NOT NULL COMMENT 'zk路径',
  `type` varchar(64) DEFAULT NULL COMMENT '类型',
  `value` varchar(64) DEFAULT NULL COMMENT '数值',
  `remark` varchar(512) DEFAULT NULL COMMENT '配置说明',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'system' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_zk_path` (`zk_path`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COMMENT='zk配置信息表';

-- ----------------------------
-- Table structure for t_pressure_machine
-- ----------------------------
DROP TABLE IF EXISTS `t_pressure_machine`;
CREATE TABLE `t_pressure_machine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '压力机名称',
  `ip` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '压力机IP',
  `flag` varchar(125) COLLATE utf8_bin DEFAULT NULL COMMENT '标签',
  `cpu` int(11) NOT NULL COMMENT 'cpu核数',
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
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态 0：空闲 ；1：压测中  -1:离线',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_ip` (`ip`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_pressure_machine_log
-- ----------------------------
DROP TABLE IF EXISTS `t_pressure_machine_log`;
CREATE TABLE `t_pressure_machine_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `machine_id` bigint(20) NOT NULL COMMENT '机器id',
  `name` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '压力机名称',
  `ip` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '压力机IP',
  `flag` varchar(125) COLLATE utf8_bin DEFAULT NULL COMMENT '标签',
  `cpu` int(11) NOT NULL COMMENT 'cpu核数',
  `memory` bigint(20) NOT NULL COMMENT '内存，单位MB',
  `machine_usage` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '机器水位',
  `disk` bigint(20) NOT NULL COMMENT '磁盘，单位MB',
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
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '状态 0：空闲 ；1：压测中  -1:离线',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_pressure_machine_statistics
-- ----------------------------
DROP TABLE IF EXISTS `t_pressure_machine_statistics`;
CREATE TABLE `t_pressure_machine_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `machine_total` int(11) NOT NULL DEFAULT '0' COMMENT '总数量',
  `machine_pressured` int(11) NOT NULL DEFAULT '0' COMMENT '压测中数量',
  `machine_free` int(11) NOT NULL DEFAULT '0' COMMENT '空闲机器数量',
  `machine_offline` int(11) NOT NULL DEFAULT '0' COMMENT '离线机器数量',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `createTime` (`gmt_create`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_pressure_task
-- ----------------------------
DROP TABLE IF EXISTS `t_pressure_task`;
CREATE TABLE `t_pressure_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '负责人id',
  `operate_id` bigint(20) DEFAULT NULL COMMENT '操作用户ID',
  `resource_id` bigint(20) DEFAULT NULL COMMENT '资源Id',
  `job_id` bigint(20) DEFAULT NULL COMMENT '压测引擎任务Id',
  `report_id` bigint(20) DEFAULT NULL COMMENT '报告Id',
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  `scene_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '场景名称',
  `status` int(2) DEFAULT '1' COMMENT '压测任务状态',
  `is_deleted` int(1) DEFAULT '0' COMMENT '是否删除:0/正常，1、已删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP,
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `exception_msg` varchar(255) DEFAULT NULL COMMENT '异常信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2489 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_pressure_task_callback
-- ----------------------------
DROP TABLE IF EXISTS `t_pressure_task_callback`;
CREATE TABLE `t_pressure_task_callback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `resource_id` bigint(20) DEFAULT NULL COMMENT '资源Id',
  `call_back_type` int(4) DEFAULT NULL COMMENT '回调类型',
  `meta_source` text COMMENT '回调数据',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_resource_id` (`resource_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=443140 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_pressure_task_variety
-- ----------------------------
DROP TABLE IF EXISTS `t_pressure_task_variety`;
CREATE TABLE `t_pressure_task_variety` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务Id',
  `status` int(2) DEFAULT '1' COMMENT '压测任务状态',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `message` varchar(255) DEFAULT NULL COMMENT '异常信息',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_task_id` (`task_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6849 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_pressure_test_engine_config
-- ----------------------------
DROP TABLE IF EXISTS `t_pressure_test_engine_config`;
CREATE TABLE `t_pressure_test_engine_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(20) NOT NULL COMMENT '压测引擎名称',
  `type` varchar(5) NOT NULL COMMENT '压测引擎类型 PTS，SPT，JMETER',
  `access_key` varchar(100) DEFAULT NULL COMMENT 'access_key',
  `secret_key` varchar(100) DEFAULT NULL COMMENT 'secret_key',
  `region_id` varchar(50) DEFAULT NULL COMMENT '地域ID',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `status` tinyint(1) DEFAULT '0' COMMENT '0-可用 1-不可用',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除 0-未删除、1-已删除',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表已废弃，压测引擎配置';

-- ----------------------------
-- Table structure for t_pressure_time_record
-- ----------------------------
DROP TABLE IF EXISTS `t_pressure_time_record`;
CREATE TABLE `t_pressure_time_record` (
  `RECORD_ID` varchar(36) COLLATE utf8_bin NOT NULL COMMENT '压测记录id',
  `START_TIME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '开始压测时间',
  `END_TIME` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '结束压测时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`RECORD_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压测时间记录表';

-- ----------------------------
-- Table structure for t_probe
-- ----------------------------
DROP TABLE IF EXISTS `t_probe`;
CREATE TABLE `t_probe` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `version` varchar(30) COLLATE utf8mb4_bin NOT NULL COMMENT '版本号',
  `path` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '上传地址',
  `customer_id` bigint(20) unsigned NOT NULL DEFAULT '1' COMMENT '租户id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_ci_v_u` (`customer_id`,`version`,`gmt_update`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='探针包表';

-- ----------------------------
-- Table structure for t_quick_access
-- ----------------------------
DROP TABLE IF EXISTS `t_quick_access`;
CREATE TABLE `t_quick_access` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据库唯一主键',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '客户id',
  `quick_name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '快捷键名称',
  `quick_logo` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '快捷键logo',
  `url_address` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '实际地址',
  `order` int(11) DEFAULT NULL COMMENT '顺序',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除 0:未删除;1:已删除',
  `is_enable` tinyint(4) DEFAULT '1' COMMENT '0:未启用；1:启用',
  `tenant_id` bigint(20) NOT NULL DEFAULT '-1' COMMENT '租户id',
  `env_code` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT 'system' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_remote_call_config
-- ----------------------------
DROP TABLE IF EXISTS `t_remote_call_config`;
CREATE TABLE `t_remote_call_config` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `value_order` int(11) unsigned NOT NULL,
  `name` varchar(40) DEFAULT '' COMMENT '中间件中文描述',
  `eng_name` varchar(40) DEFAULT '' COMMENT '中间件英文描述',
  `description` varchar(100) DEFAULT NULL COMMENT '描述',
  `check_type` varchar(20) DEFAULT '' COMMENT 'agentCheckType',
  `IS_DELETED` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `is_system` tinyint(1) NOT NULL DEFAULT '0' COMMENT '系统预设，不允许修改',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_eng_name` (`eng_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='远程调用配置类型表';

-- ----------------------------
-- Table structure for t_report
-- ----------------------------
DROP TABLE IF EXISTS `t_report`;
CREATE TABLE `t_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '负责人id',
  `custom_id` bigint(20) DEFAULT NULL COMMENT '客户id',
  `amount` decimal(15,2) DEFAULT '0.00' COMMENT '流量消耗',
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  `scene_name` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '场景名称',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` int(2) DEFAULT '0' COMMENT '报表生成状态:0/就绪状态，1/生成中,2/完成生成',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '报告类型；0普通场景，1流量调试',
  `conclusion` int(1) DEFAULT '0' COMMENT '压测结论: 0/不通过，1/通过',
  `total_request` bigint(10) DEFAULT NULL COMMENT '请求总数',
  `pressure_type` int(2) DEFAULT '0' COMMENT '施压类型,0:并发,1:tps,2:自定义;不填默认为0',
  `avg_concurrent` decimal(10,2) DEFAULT NULL COMMENT '平均线程数',
  `tps` int(11) DEFAULT NULL COMMENT '目标tps',
  `avg_tps` decimal(10,2) DEFAULT NULL COMMENT '平均tps',
  `avg_rt` decimal(10,2) DEFAULT NULL COMMENT '平均响应时间',
  `concurrent` int(7) DEFAULT NULL COMMENT '最大并发',
  `success_rate` decimal(10,2) DEFAULT NULL COMMENT '成功率',
  `sa` decimal(10,2) DEFAULT NULL COMMENT 'sa',
  `operate_id` bigint(20) DEFAULT NULL COMMENT '操作用户ID',
  `features` longtext COLLATE utf8_bin COMMENT '扩展字段，JSON数据格式',
  `is_deleted` int(1) DEFAULT '0' COMMENT '是否删除:0/正常，1、已删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP,
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门id',
  `create_uid` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `lock` int(11) DEFAULT '1' COMMENT '1-解锁 9-锁定',
  `script_id` bigint(20) DEFAULT NULL COMMENT '脚本id',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户字段，customer_id,custom_id已废弃',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境标识',
  `script_node_tree` json DEFAULT NULL COMMENT '脚本节点树',
  `sign` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '数据签名',
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务Id',
  `resource_id` bigint(20) DEFAULT NULL COMMENT '资源Id',
  `job_id` bigint(20) DEFAULT NULL COMMENT '压测引擎任务Id',
  `calibration_status` int(11) DEFAULT '0' COMMENT '数据校准状态',
  `calibration_message` text COLLATE utf8_bin COMMENT '数据校准信息',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_script_id` (`script_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6626 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_report_application_summary
-- ----------------------------
DROP TABLE IF EXISTS `t_report_application_summary`;
CREATE TABLE `t_report_application_summary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_id` bigint(20) NOT NULL,
  `application_name` varchar(64) COLLATE utf8_bin NOT NULL,
  `machine_total_count` int(11) DEFAULT NULL COMMENT '总机器数',
  `machine_risk_count` int(11) DEFAULT NULL COMMENT '风险机器数',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_idx_report_appliacation` (`tenant_id`,`env_code`,`report_id`,`application_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7967608 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='报告应用统计数据';

-- ----------------------------
-- Table structure for t_report_bottleneck_interface
-- ----------------------------
DROP TABLE IF EXISTS `t_report_bottleneck_interface`;
CREATE TABLE `t_report_bottleneck_interface` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_id` bigint(20) NOT NULL,
  `application_name` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `sort_no` int(11) DEFAULT NULL,
  `interface_type` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '接口类型',
  `interface_name` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `tps` decimal(10,2) DEFAULT NULL,
  `rt` decimal(10,2) DEFAULT NULL,
  `node_count` int(11) DEFAULT NULL,
  `error_reqs` int(11) DEFAULT NULL,
  `bottleneck_weight` decimal(16,10) DEFAULT NULL,
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_report_id` (`tenant_id`,`env_code`,`report_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='瓶颈接口';

-- ----------------------------
-- Table structure for t_report_business_activity_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_report_business_activity_detail`;
CREATE TABLE `t_report_business_activity_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_id` bigint(20) NOT NULL COMMENT '报告ID',
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  `business_activity_id` bigint(20) NOT NULL COMMENT '业务活动ID',
  `business_activity_name` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '业务活动名称',
  `bind_ref` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `application_ids` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '应用ID',
  `request` bigint(10) DEFAULT NULL COMMENT '请求数',
  `sa` decimal(65,2) DEFAULT NULL COMMENT 'sa',
  `target_sa` decimal(65,2) DEFAULT NULL COMMENT '目标sa',
  `tps` decimal(65,2) DEFAULT NULL COMMENT 'tps',
  `target_tps` decimal(65,2) DEFAULT NULL COMMENT '目标tps',
  `avg_concurrence_num` decimal(65,2) DEFAULT NULL COMMENT '平均并发数',
  `rt` decimal(10,2) DEFAULT NULL COMMENT '响应时间',
  `target_rt` decimal(65,2) DEFAULT NULL COMMENT '目标rt',
  `rt_distribute` json DEFAULT NULL COMMENT '分布范围，格式json',
  `success_rate` decimal(65,2) DEFAULT NULL COMMENT '成功率',
  `target_success_rate` decimal(65,2) DEFAULT NULL COMMENT '目标成功率',
  `max_tps` decimal(65,2) DEFAULT NULL COMMENT '最大tps',
  `max_rt` decimal(65,2) DEFAULT NULL COMMENT '最大响应时间',
  `min_rt` decimal(65,2) DEFAULT NULL COMMENT '最小响应时间',
  `pass_flag` tinyint(4) DEFAULT NULL COMMENT '是否通过',
  `features` text COLLATE utf8_bin,
  `is_deleted` int(1) DEFAULT '0',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP,
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22013 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_report_machine
-- ----------------------------
DROP TABLE IF EXISTS `t_report_machine`;
CREATE TABLE `t_report_machine` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_id` bigint(20) NOT NULL,
  `application_name` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '应用名称',
  `machine_ip` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '机器ip',
  `agent_id` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'agentid',
  `machine_base_config` varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '机器基本信息',
  `machine_tps_target_config` text COLLATE utf8_bin COMMENT '机器tps对应指标信息',
  `risk_value` decimal(16,6) DEFAULT NULL COMMENT '风险计算值',
  `risk_flag` tinyint(4) DEFAULT NULL COMMENT '是否风险机器(0-否，1-是)',
  `risk_content` varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '风险提示内容',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_idx_report_appliacation_machine_tenant_env` (`tenant_id`,`env_code`,`report_id`,`application_name`,`machine_ip`) USING BTREE,
  KEY `idx_tenant_env` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5865217 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='报告机器数据';

-- ----------------------------
-- Table structure for t_report_summary
-- ----------------------------
DROP TABLE IF EXISTS `t_report_summary`;
CREATE TABLE `t_report_summary` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_id` bigint(20) NOT NULL,
  `bottleneck_interface_count` int(11) DEFAULT NULL COMMENT '瓶颈接口',
  `risk_machine_count` int(11) DEFAULT NULL COMMENT '风险机器数',
  `business_activity_count` int(11) DEFAULT NULL COMMENT '业务活动数',
  `unachieve_business_activity_count` int(11) DEFAULT NULL COMMENT '未达标业务活动数',
  `application_count` int(11) DEFAULT NULL COMMENT '应用数',
  `machine_count` int(11) DEFAULT NULL COMMENT '机器数',
  `warn_count` int(11) DEFAULT NULL COMMENT '告警次数',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) COLLATE utf8_bin NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_report_id` (`tenant_id`,`env_code`,`report_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=14127 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='报告数据汇总';

-- ----------------------------
-- Table structure for t_rpc_config_template
-- ----------------------------
DROP TABLE IF EXISTS `t_rpc_config_template`;
CREATE TABLE `t_rpc_config_template` (
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
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='rpc框架配置模版';

-- ----------------------------
-- Table structure for t_scene
-- ----------------------------
DROP TABLE IF EXISTS `t_scene`;
CREATE TABLE `t_scene` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `SCENE_NAME` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '场景名',
  `BUSINESS_LINK` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '场景所绑定的业务链路名集合',
  `SCENE_LEVEL` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '场景等级 :p0/p1/02/03',
  `IS_CORE` tinyint(4) DEFAULT '0' COMMENT '是否核心场景 0:不是;1:是',
  `IS_CHANGED` tinyint(4) DEFAULT '0' COMMENT '是否有变更 0:没有变更，1有变更',
  `CUSTOMER_ID` bigint(20) DEFAULT NULL COMMENT '租户id',
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  `type` tinyint(4) DEFAULT '0' COMMENT '场景类型，标识1为jmeter上传，默认0',
  `script_jmx_node` longtext COMMENT '存储树状结构',
  `script_deploy_id` bigint(20) DEFAULT NULL COMMENT '脚本实例id',
  `link_relate_num` int(11) DEFAULT '0' COMMENT '关联节点数',
  `total_node_num` int(11) DEFAULT '0' COMMENT '脚本总节点数',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `T_LINK_MNT_INDEX1` (`SCENE_NAME`) USING BTREE,
  KEY `T_LINK_MNT_INDEX3` (`CREATE_TIME`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2090 DEFAULT CHARSET=utf8mb4 COMMENT='场景表';

-- ----------------------------
-- Table structure for t_scene_big_file_slice
-- ----------------------------
DROP TABLE IF EXISTS `t_scene_big_file_slice`;
CREATE TABLE `t_scene_big_file_slice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `scene_id` bigint(20) DEFAULT NULL COMMENT '场景ID',
  `script_ref_id` bigint(20) DEFAULT NULL COMMENT '脚本ID',
  `file_path` varchar(2000) DEFAULT NULL COMMENT '文件路径',
  `file_name` varchar(1000) DEFAULT NULL COMMENT '文件名',
  `slice_count` int(20) DEFAULT NULL COMMENT '分片数量',
  `slice_info` text COMMENT '文件分片信息',
  `status` int(1) DEFAULT NULL COMMENT '状态：0-未分片；1-已分片；2-文件已更改',
  `file_update_time` timestamp(3) NULL DEFAULT NULL COMMENT '分片时文件最后更改时间',
  `create_time` timestamp(3) NULL DEFAULT NULL COMMENT '分片时间',
  `update_time` timestamp(3) NULL DEFAULT NULL COMMENT '更新时间',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=182 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_scene_business_activity_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_scene_business_activity_ref`;
CREATE TABLE `t_scene_business_activity_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  `business_activity_id` bigint(20) NOT NULL COMMENT '业务活动id',
  `business_activity_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '业务活动名称',
  `application_ids` varchar(1024) COLLATE utf8_bin DEFAULT NULL COMMENT '关联应用id，多个用,隔开',
  `goal_value` text COLLATE utf8_bin NOT NULL COMMENT '目标值，json',
  `bind_ref` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `create_name` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_name` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_scene_id` (`scene_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7767 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_scene_excluded_application
-- ----------------------------
DROP TABLE IF EXISTS `t_scene_excluded_application`;
CREATE TABLE `t_scene_excluded_application` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `scene_id` bigint(20) NOT NULL COMMENT 'cloud场景id',
  `application_id` bigint(20) NOT NULL COMMENT '应用id',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
  `tenant_id` bigint(20) NOT NULL,
  `env_code` varchar(50) COLLATE utf8mb4_bin NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_scnene_id` (`scene_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='场景排除检测应用表';

-- ----------------------------
-- Table structure for t_scene_jmeterlog_upload
-- ----------------------------
DROP TABLE IF EXISTS `t_scene_jmeterlog_upload`;
CREATE TABLE `t_scene_jmeterlog_upload` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  `report_id` bigint(20) NOT NULL COMMENT '报告ID',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `task_status` int(2) DEFAULT NULL COMMENT '压测任务状态：1-启动中；2-启动成功；3-压测失败；4-压测完成',
  `upload_status` int(2) DEFAULT NULL COMMENT '日志上传状态：0-未上传；1-上传中；2-上传完成',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime DEFAULT NULL COMMENT '修改时间',
  `upload_count` bigint(20) DEFAULT '0' COMMENT '已上传文件大小',
  `file_name` varchar(500) DEFAULT NULL COMMENT '文件名',
  `tenant_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_scene_id_report_id` (`scene_id`,`report_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2634 DEFAULT CHARSET=utf8mb4 COMMENT='压测上传jmeter日志任务状态表';

-- ----------------------------
-- Table structure for t_scene_link_relate
-- ----------------------------
DROP TABLE IF EXISTS `t_scene_link_relate`;
CREATE TABLE `t_scene_link_relate` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `ENTRANCE` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '链路入口',
  `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `BUSINESS_LINK_ID` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '业务链路ID',
  `TECH_LINK_ID` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '技术链路ID',
  `SCENE_ID` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '场景ID',
  `PARENT_BUSINESS_LINK_ID` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '当前业务链路ID的上级业务链路ID',
  `FRONT_UUID_KEY` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '前端数结构对象key',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  `script_identification` varchar(255) DEFAULT NULL COMMENT ' 脚本请求路径标识',
  `script_xpath_md5` varchar(64) DEFAULT NULL COMMENT '脚本请求xpath的MD5',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `T_LINK_MNT_INDEX2` (`ENTRANCE`(255)) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8074 DEFAULT CHARSET=utf8mb4 COMMENT='链路场景关联表';

-- ----------------------------
-- Table structure for t_scene_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_scene_manage`;
CREATE TABLE `t_scene_manage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '负责人id',
  `custom_id` bigint(20) DEFAULT NULL COMMENT '客户id',
  `scene_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '场景名称',
  `status` tinyint(4) NOT NULL COMMENT '参考数据字典 场景状态',
  `last_pt_time` timestamp NULL DEFAULT NULL COMMENT '最新压测时间',
  `pt_config` text COLLATE utf8_bin COMMENT '施压配置',
  `type` tinyint(2) NOT NULL DEFAULT '0' COMMENT '场景类型:0普通场景，1流量调试',
  `script_type` tinyint(4) DEFAULT NULL COMMENT '脚本类型：0-Jmeter 1-Gatling',
  `is_archive` tinyint(4) NOT NULL DEFAULT '0',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除：0-否 1-是',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `create_name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '创建人',
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后修改时间',
  `update_name` varchar(32) COLLATE utf8_bin DEFAULT NULL COMMENT '最后修改人',
  `features` text COLLATE utf8_bin COMMENT '扩展字段',
  `script_analysis_result` json DEFAULT NULL COMMENT '脚本解析结果',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门id',
  `create_uid` bigint(20) DEFAULT NULL COMMENT '创建人id',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户字段，customer_id,custom_id已废弃',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) COLLATE utf8_bin DEFAULT 'test' COMMENT '环境标识',
  `sign` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `ide_last_pt_time` (`last_pt_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1918 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_scene_scheduler_task
-- ----------------------------
DROP TABLE IF EXISTS `t_scene_scheduler_task`;
CREATE TABLE `t_scene_scheduler_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scene_id` bigint(20) NOT NULL COMMENT '场景id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `content` text COMMENT '启动场景参数',
  `is_executed` tinyint(4) DEFAULT '0' COMMENT '0：待执行，1:执行中；2:已执行',
  `execute_time` datetime NOT NULL COMMENT '压测场景定时执行时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_scene_script_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_scene_script_ref`;
CREATE TABLE `t_scene_script_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  `script_type` tinyint(4) NOT NULL COMMENT '脚本类型',
  `file_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '文件名称',
  `file_size` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '文件大小：2MB',
  `file_type` tinyint(4) NOT NULL COMMENT '文件类型：0-脚本文件 1-数据文件',
  `file_extend` text COLLATE utf8_bin COMMENT '{\n  “dataCount”:”数据量”,\n  “isSplit”:”是否拆分”,\n  “topic”:”MQ主题”,\n  “nameServer”:”mq nameServer”,\n}',
  `upload_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '上传时间',
  `upload_path` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT '上传路径：相对路径',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `create_name` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_name` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `file_md5` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '文件MD5',
  `sign` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_scene_id` (`scene_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6524 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_scene_sla_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_scene_sla_ref`;
CREATE TABLE `t_scene_sla_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  `sla_name` varchar(64) COLLATE utf8_bin NOT NULL COMMENT '规则名称',
  `business_activity_ids` varchar(512) COLLATE utf8_bin NOT NULL COMMENT '业务活动ID，多个用,隔开，-1表示所有',
  `target_type` tinyint(4) NOT NULL COMMENT '指标类型：0-RT 1-TPS 2-成功率 3-SA',
  `condition` text COLLATE utf8_bin NOT NULL COMMENT '警告/终止逻辑：json格式',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态：0-启用 1-禁用',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `create_name` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  `update_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_name` varchar(32) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_scene_id` (`scene_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2856 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_scene_tag_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_scene_tag_ref`;
CREATE TABLE `t_scene_tag_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scene_id` bigint(20) NOT NULL COMMENT '场景发布id',
  `tag_id` bigint(20) NOT NULL COMMENT '标签id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `index_sceneId_tagId` (`scene_id`,`tag_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=307 DEFAULT CHARSET=utf8mb4 COMMENT='场景标签关联';

-- ----------------------------
-- Table structure for t_schedule_record
-- ----------------------------
DROP TABLE IF EXISTS `t_schedule_record`;
CREATE TABLE `t_schedule_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scene_id` bigint(20) NOT NULL COMMENT '场景ID',
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务ID',
  `pod_num` int(11) DEFAULT NULL COMMENT 'pod数量',
  `pod_class` varchar(128) COLLATE utf8_bin DEFAULT NULL COMMENT 'pod种类',
  `status` tinyint(4) DEFAULT NULL COMMENT '调度结果 0-失败 1-成功',
  `cpu_core_num` decimal(6,2) DEFAULT NULL COMMENT 'CPU核数',
  `memory_size` decimal(8,2) DEFAULT NULL COMMENT '内存G',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '调度时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_scene_id` (`scene_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4267 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_schedule_record_engine_plugins_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_schedule_record_engine_plugins_ref`;
CREATE TABLE `t_schedule_record_engine_plugins_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `schedule_record_id` bigint(20) NOT NULL COMMENT '调度记录ID',
  `engine_plugin_file_path` varchar(200) COLLATE utf8_bin NOT NULL COMMENT '引擎插件存放文件夹',
  `gmt_create` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=653 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='调度记录引擎插件信息';

-- ----------------------------
-- Table structure for t_script_debug
-- ----------------------------
DROP TABLE IF EXISTS `t_script_debug`;
CREATE TABLE `t_script_debug` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `script_deploy_id` bigint(20) unsigned NOT NULL,
  `status` tinyint(3) unsigned DEFAULT '0' COMMENT '调试记录状态, 0 未启动(默认), 1 启动中, 2 请求中, 3 请求结束, 4 调试成功, 5 调试失败',
  `failed_type` tinyint(3) unsigned DEFAULT '0' COMMENT '失败类型, 10 启动通知超时失败, 20 漏数失败, 30 非200检查失败, 后面会扩展',
  `leak_status` tinyint(3) unsigned DEFAULT '0' COMMENT '检查漏数状态, 0:正常;1:漏数;2:未检测;3:检测失败',
  `remark` varchar(500) COLLATE utf8mb4_bin DEFAULT '' COMMENT '备注, 当调试失败时, 有失败信息',
  `request_num` int(10) unsigned DEFAULT '1' COMMENT '请求条数, 1-10000',
  `concurrency_num` int(10) unsigned DEFAULT '1' COMMENT '并发数',
  `cloud_scene_id` bigint(20) unsigned DEFAULT '0' COMMENT '对应的 cloud 场景id',
  `cloud_report_id` bigint(20) unsigned DEFAULT '0' COMMENT '对应的 cloud 报告id',
  `customer_id` bigint(20) unsigned DEFAULT NULL COMMENT '租户id',
  `user_id` bigint(20) NOT NULL COMMENT '租户下的用户id',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '逻辑删除字段, 0 未删除, 1 已删除',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) COLLATE utf8mb4_bin NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_si` (`script_deploy_id`) USING BTREE COMMENT '租户id, 用户id, 脚本id 联合索引'
) ENGINE=InnoDB AUTO_INCREMENT=869 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='脚本调试表';

-- ----------------------------
-- Table structure for t_script_execute_result
-- ----------------------------
DROP TABLE IF EXISTS `t_script_execute_result`;
CREATE TABLE `t_script_execute_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `script_deploy_id` bigint(20) NOT NULL COMMENT '实例id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `executor` varchar(256) DEFAULT NULL COMMENT '执行人',
  `success` tinyint(1) DEFAULT NULL COMMENT '执行结果',
  `result` longtext COMMENT '执行结果',
  `script_version` int(11) DEFAULT NULL COMMENT '脚本版本',
  `script_id` bigint(20) NOT NULL COMMENT '脚本id',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_script_deploy_id` (`script_deploy_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='脚本执行结果';

-- ----------------------------
-- Table structure for t_script_file_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_script_file_ref`;
CREATE TABLE `t_script_file_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `file_id` bigint(20) NOT NULL COMMENT '文件id',
  `script_deploy_id` bigint(20) NOT NULL COMMENT '脚本发布id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5031 DEFAULT CHARSET=utf8mb4 COMMENT='脚本文件关联表';

-- ----------------------------
-- Table structure for t_script_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_script_manage`;
CREATE TABLE `t_script_manage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT '名称',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `script_version` int(11) NOT NULL DEFAULT '0',
  `is_deleted` tinyint(1) DEFAULT '0',
  `feature` longtext COMMENT '拓展字段',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  `m_version` int(11) DEFAULT '0' COMMENT '脚本管理-版本',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`) USING BTREE COMMENT '名称需要唯一'
) ENGINE=InnoDB AUTO_INCREMENT=2208 DEFAULT CHARSET=utf8mb4 COMMENT='脚本表';

-- ----------------------------
-- Table structure for t_script_manage_deploy
-- ----------------------------
DROP TABLE IF EXISTS `t_script_manage_deploy`;
CREATE TABLE `t_script_manage_deploy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `script_id` bigint(20) DEFAULT NULL,
  `name` varchar(64) NOT NULL COMMENT '名称',
  `ref_type` varchar(16) DEFAULT NULL COMMENT '关联类型(业务活动), 1 业务活动, 2 业务流程',
  `ref_value` varchar(64) DEFAULT NULL COMMENT '关联值(活动id)',
  `type` tinyint(4) NOT NULL COMMENT '脚本类型;0为jmeter脚本',
  `status` tinyint(4) NOT NULL COMMENT '0代表新建，1代表调试通过；2.历史版本',
  `create_user_id` bigint(20) DEFAULT NULL COMMENT '操作人id',
  `create_user_name` varchar(64) DEFAULT NULL COMMENT '操作人名称',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `script_version` int(11) NOT NULL DEFAULT '0',
  `is_deleted` tinyint(1) DEFAULT '0',
  `feature` longtext COMMENT '拓展字段',
  `description` varchar(512) DEFAULT NULL,
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name_version` (`name`,`script_version`) USING BTREE COMMENT '名称加版本需要唯一'
) ENGINE=InnoDB AUTO_INCREMENT=3934 DEFAULT CHARSET=utf8mb4 COMMENT='脚本文件关联表';

-- ----------------------------
-- Table structure for t_script_tag_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_script_tag_ref`;
CREATE TABLE `t_script_tag_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `script_id` bigint(20) NOT NULL COMMENT '场景发布id',
  `tag_id` bigint(20) NOT NULL COMMENT '标签id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='脚本标签关联表';

-- ----------------------------
-- Table structure for t_shadow_job_config
-- ----------------------------
DROP TABLE IF EXISTS `t_shadow_job_config`;
CREATE TABLE `t_shadow_job_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `application_id` bigint(20) NOT NULL COMMENT '应用ID',
  `name` varchar(256) DEFAULT NULL,
  `type` tinyint(4) NOT NULL COMMENT 'JOB类型 0-quartz、1-elastic-job、2-xxl-job',
  `config_code` text COMMENT '配置代码',
  `status` tinyint(3) DEFAULT '1' COMMENT '0-可用 1-不可用',
  `active` tinyint(3) DEFAULT '1' COMMENT '0-可用 1-不可用',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '是否删除 0-未删除、1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(1024) DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_app_id` (`application_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=262 DEFAULT CHARSET=utf8mb4 COMMENT='影子JOB任务配置';

-- ----------------------------
-- Table structure for t_shadow_mq_consumer
-- ----------------------------
DROP TABLE IF EXISTS `t_shadow_mq_consumer`;
CREATE TABLE `t_shadow_mq_consumer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `topic_group` varchar(1000) DEFAULT NULL COMMENT 'topic+group，以#号拼接',
  `type` varchar(20) DEFAULT NULL COMMENT '白名单类型',
  `application_id` bigint(20) DEFAULT NULL COMMENT '应用id',
  `application_name` varchar(200) DEFAULT NULL COMMENT '应用名称，冗余',
  `status` int(11) DEFAULT '1' COMMENT '是否可用(0表示未启用,1表示已启用)',
  `deleted` int(11) DEFAULT '0' COMMENT '是否可用(0表示未删除,1表示已删除)',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `feature` text COMMENT '拓展字段',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `manual_tag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否手动录入 0:否;1:是',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  `sign` varchar(255) DEFAULT NULL COMMENT '数据签名',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=620 DEFAULT CHARSET=utf8mb4 COMMENT='影子消费者';

-- ----------------------------
-- Table structure for t_shadow_table_datasource
-- ----------------------------
DROP TABLE IF EXISTS `t_shadow_table_datasource`;
CREATE TABLE `t_shadow_table_datasource` (
  `SHADOW_DATASOURCE_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '影子表数据源id',
  `APPLICATION_ID` bigint(20) NOT NULL COMMENT '关联app_mn主键id',
  `DATABASE_IPPORT` varchar(128) NOT NULL COMMENT '数据库ip端口  xx.xx.xx.xx:xx',
  `DATABASE_NAME` varchar(128) NOT NULL COMMENT '数据库表明',
  `USE_SHADOW_TABLE` int(11) DEFAULT NULL COMMENT '是否使用 影子表 1 使用 0 不使用',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
  `UPDATE_TIME` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`SHADOW_DATASOURCE_ID`) USING BTREE,
  UNIQUE KEY `SHADOW_DATASOURCE_INDEX2` (`APPLICATION_ID`,`DATABASE_IPPORT`,`DATABASE_NAME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影子表数据源配置';

-- ----------------------------
-- Table structure for t_strategy_config
-- ----------------------------
DROP TABLE IF EXISTS `t_strategy_config`;
CREATE TABLE `t_strategy_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `strategy_name` varchar(128) COLLATE utf8_bin NOT NULL COMMENT '策略名称',
  `strategy_config` text COLLATE utf8_bin NOT NULL COMMENT '策略配置',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态',
  `is_deleted` tinyint(4) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `create_time` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` timestamp(3) NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '最后更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_tag_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_tag_manage`;
CREATE TABLE `t_tag_manage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(64) NOT NULL COMMENT '标签名称',
  `tag_type` tinyint(4) DEFAULT NULL COMMENT '标签类型;0为脚本标签;1为数据源标签',
  `tag_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '标签状态;0为可用',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COMMENT='标签库表';

-- ----------------------------
-- Table structure for t_tc_sequence
-- ----------------------------
DROP TABLE IF EXISTS `t_tc_sequence`;
CREATE TABLE `t_tc_sequence` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `value` bigint(20) DEFAULT NULL,
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `UK_NAME` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_tenant_config
-- ----------------------------
DROP TABLE IF EXISTS `t_tenant_config`;
CREATE TABLE `t_tenant_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) NOT NULL COMMENT '租户id',
  `env_code` varchar(512) NOT NULL COMMENT '环境代码，测试环境：test,生产环境：prod',
  `desc` varchar(1024) DEFAULT '' COMMENT '配置描述',
  `key` varchar(128) NOT NULL COMMENT '配置名',
  `value` longtext NOT NULL COMMENT '配置值',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0：停用；1：启用；',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_key_tenant_env` (`tenant_id`,`env_code`,`key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_tenant_data_sign_config
-- ----------------------------
DROP TABLE IF EXISTS `t_tenant_data_sign_config`;
CREATE TABLE `t_tenant_data_sign_config` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '是否开启数据签名 0:否;1:是',
  `tenant_id` bigint(20) DEFAULT '1' COMMENT '租户 id, 默认 1',
  `env_code` varchar(100) DEFAULT 'test' COMMENT '环境标识',
  `is_deleted` tinyint(2) unsigned NOT NULL DEFAULT '0',
  `sign` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='租户数据存储签名配置';

-- ----------------------------
-- Table structure for t_tenant_env_ref
-- ----------------------------
DROP TABLE IF EXISTS `t_tenant_env_ref`;
CREATE TABLE `t_tenant_env_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) NOT NULL COMMENT '租户id',
  `env_code` varchar(512) NOT NULL COMMENT '环境代码，测试环境：test,生产环境：prod',
  `env_name` varchar(1024) NOT NULL COMMENT '环境名称',
  `desc` varchar(1024) DEFAULT NULL COMMENT '描述',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `source` tinyint(4) DEFAULT '0' COMMENT '来源，1-手动，可以删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_tenant_code` (`tenant_id`,`env_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=172 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_tenant_info
-- ----------------------------
DROP TABLE IF EXISTS `t_tenant_info`;
CREATE TABLE `t_tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(512) NOT NULL COMMENT '租户key 唯一，同时也是userappkey',
  `name` varchar(512) NOT NULL COMMENT '租户名称',
  `nick` varchar(512) NOT NULL COMMENT '租户中文名称',
  `code` varchar(200) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态 0: 停用 1:正常 2：试用 3：欠费 ',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `create_by` bigint(20) NOT NULL COMMENT '创建者，用户ID',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_key` (`key`) USING BTREE,
  UNIQUE KEY `unique_code` (`code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=77 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_tenant_key
-- ----------------------------
DROP TABLE IF EXISTS `t_tenant_key`;
CREATE TABLE `t_tenant_key` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) NOT NULL COMMENT '租户ID',
  `tenant_code` varchar(128) NOT NULL COMMENT '租户code',
  `env_code` varchar(32) NOT NULL COMMENT '环境代码，测试环境：test,生产环境：prod',
  `type` varchar(8) NOT NULL COMMENT '秘钥类型(1、租户私有TENANT 2、平台私有PLATFORM 3、公共的PUBLIC)',
  `public_key` varchar(512) NOT NULL COMMENT '公钥',
  `private_key` varchar(512) NOT NULL COMMENT '私钥',
  `desc` varchar(1024) DEFAULT NULL COMMENT '描述',
  `version` int(8) NOT NULL COMMENT '版本号',
  `gmt_create` datetime DEFAULT NULL,
  `gmt_update` datetime DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=99 DEFAULT CHARSET=utf8mb4 COMMENT='租户秘钥表';

-- ----------------------------
-- Table structure for t_test
-- ----------------------------
DROP TABLE IF EXISTS `t_test`;
CREATE TABLE `t_test` (
  `name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_third_party
-- ----------------------------
DROP TABLE IF EXISTS `t_third_party`;
CREATE TABLE `t_third_party` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '第三方名称',
  `logo` varchar(1000) NOT NULL COMMENT '第三方logo',
  `code` varchar(20) NOT NULL COMMENT '给第三方租户的编码',
  `third_party` tinyint(4) unsigned NOT NULL COMMENT '租户的第三方, 比如, 1 uaa, 2 租户内部钉钉',
  `type` tinyint(4) unsigned DEFAULT '0' COMMENT '类型, 0 则是普通, 1 是钉钉, 公有钉钉或者租户内部钉钉, 使用同一套sdk',
  `tenant_id` bigint(20) unsigned NOT NULL COMMENT '租户id, 0 表示没有关联租户',
  `tenant_code` varchar(100) NOT NULL COMMENT '租户code, 空字符串表示没有关联租户',
  `client_id` varchar(100) NOT NULL COMMENT '第三方分配给takin的客户端id',
  `client_secret` varchar(100) NOT NULL COMMENT '第三方分配给takin的密钥',
  `url_authorize` varchar(1000) NOT NULL COMMENT '授权地址',
  `url_token` varchar(500) NOT NULL COMMENT '获取accessToken地址',
  `url_refresh` varchar(500) DEFAULT '' COMMENT '刷新accessToken地址',
  `url_profile` varchar(500) NOT NULL COMMENT '获取用户信息地址',
  `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '软删字段, 1 已删除, 0 未删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `phone` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT '' COMMENT '手机号',
  `takin_web_domain` varchar(128) DEFAULT NULL COMMENT '第三方登录的takin地址',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_t` (`tenant_code`) USING BTREE,
  KEY `idx_ct` (`code`,`third_party`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='第三方表';

-- ----------------------------
-- Table structure for t_trace_manage
-- ----------------------------
DROP TABLE IF EXISTS `t_trace_manage`;
CREATE TABLE `t_trace_manage` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_object` varchar(128) NOT NULL COMMENT '追踪对象',
  `report_id` bigint(20) DEFAULT NULL COMMENT '报告id',
  `agent_id` varchar(128) DEFAULT NULL,
  `server_ip` varchar(16) DEFAULT NULL COMMENT '服务器ip',
  `process_id` int(11) DEFAULT NULL COMMENT '进程id',
  `create_time` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `feature` text COMMENT '拓展字段',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COMMENT='方法追踪表';

-- ----------------------------
-- Table structure for t_trace_manage_deploy
-- ----------------------------
DROP TABLE IF EXISTS `t_trace_manage_deploy`;
CREATE TABLE `t_trace_manage_deploy` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trace_manage_id` bigint(20) NOT NULL COMMENT '追踪对象id',
  `trace_deploy_object` varchar(128) NOT NULL COMMENT '追踪对象实例名称',
  `sample_id` varchar(128) DEFAULT NULL COMMENT '追踪凭证',
  `level` int(11) DEFAULT NULL COMMENT '级别',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父id',
  `has_children` int(11) DEFAULT '2' COMMENT '0:没有;1:有;2未知',
  `line_num` int(11) DEFAULT NULL COMMENT '行号',
  `avg_cost` decimal(20,3) DEFAULT NULL COMMENT '平均耗时',
  `p50` decimal(20,3) DEFAULT NULL COMMENT 'p50',
  `p90` decimal(20,3) DEFAULT NULL COMMENT 'p90',
  `p95` decimal(20,3) DEFAULT NULL COMMENT 'p95',
  `p99` decimal(20,3) DEFAULT NULL COMMENT 'p99',
  `min` decimal(20,3) DEFAULT NULL COMMENT 'min',
  `max` decimal(20,3) DEFAULT NULL COMMENT 'max',
  `status` int(11) NOT NULL COMMENT '状态0:待采集;1:采集中;2:采集结束',
  `create_time` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `update_time` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `feature` text COMMENT '拓展字段',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COMMENT='方法追踪实例表';

-- ----------------------------
-- Table structure for t_trace_node_info
-- ----------------------------
DROP TABLE IF EXISTS `t_trace_node_info`;
CREATE TABLE `t_trace_node_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `app_name` varchar(512) DEFAULT NULL COMMENT '应用名',
  `trace_id` varchar(512) DEFAULT NULL COMMENT 'traceId',
  `rpc_id` varchar(512) DEFAULT NULL COMMENT 'rpcid',
  `log_type` tinyint(4) DEFAULT NULL COMMENT '日志级别，1服务端；2：客户端',
  `trace_app_name` varchar(512) DEFAULT NULL COMMENT '入口的app名称',
  `up_app_name` varchar(512) DEFAULT NULL COMMENT '上游的app名称',
  `start_time` bigint(20) DEFAULT NULL COMMENT '开始时间的时间戳',
  `cost` bigint(20) DEFAULT NULL COMMENT '耗时',
  `middleware_name` varchar(512) DEFAULT NULL COMMENT '中间件名称',
  `service_name` varchar(512) DEFAULT NULL COMMENT '服务名',
  `method_name` varchar(512) DEFAULT NULL COMMENT '方法名',
  `remote_ip` varchar(128) DEFAULT NULL COMMENT '远程IP',
  `result_code` varchar(128) DEFAULT NULL COMMENT '结果编码',
  `request` longtext COMMENT '请求内容',
  `response` longtext COMMENT '响应内容',
  `callback_msg` longtext COMMENT '附加信息',
  `cluster_test` tinyint(1) DEFAULT NULL COMMENT '1是压测流量，0是业务流量',
  `host_ip` varchar(512) DEFAULT NULL COMMENT '日志所属机器ip',
  `agent_id` varchar(512) DEFAULT NULL COMMENT 'agent id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '软删',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `rpc_type` tinyint(4) DEFAULT NULL COMMENT 'rpc类型',
  `port` tinyint(4) DEFAULT NULL COMMENT '端口',
  `is_upper_unknown_node` tinyint(1) DEFAULT NULL COMMENT '是否下游有未知节点',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=47016 DEFAULT CHARSET=utf8mb4 COMMENT='调用栈节点表';

-- ----------------------------
-- Table structure for t_tro_authority
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_authority`;
CREATE TABLE `t_tro_authority` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `role_id` varchar(50) DEFAULT NULL COMMENT '角色id',
  `resource_id` varchar(255) NOT NULL COMMENT '资源id',
  `action` varchar(255) DEFAULT NULL COMMENT '操作类型(0:all,1:query,2:create,3:update,4:delete,5:start,6:stop,7:export,8:enable,9:disable,10:auth)',
  `status` tinyint(1) DEFAULT '0' COMMENT '是否启用 0:启用;1:禁用',
  `object_type` tinyint(1) DEFAULT NULL COMMENT '对象类型:0:角色 1:用户(4.5.0版本后废弃不用)',
  `object_id` varchar(255) DEFAULT NULL COMMENT '对象id:角色,用户(4.5.0版本后废弃不用)',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `scope` varchar(255) DEFAULT NULL COMMENT '权限范围：0:全部 1:本部门 2:本部门及以下 3:自己及以下 3:仅自己',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=831 DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- ----------------------------
-- Table structure for t_tro_dbresource
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_dbresource`;
CREATE TABLE `t_tro_dbresource` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` tinyint(4) DEFAULT '0' COMMENT '0:MYSQL',
  `name` varchar(64) NOT NULL COMMENT '数据源名称',
  `jdbc_url` varchar(500) NOT NULL COMMENT '数据源地址',
  `username` varchar(64) NOT NULL COMMENT '数据源用户',
  `password` varchar(200) NOT NULL COMMENT '数据源密码',
  `features` longtext COMMENT '扩展字段，k-v形式存在',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8 COMMENT='数据源';

-- ----------------------------
-- Table structure for t_tro_dept
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_dept`;
CREATE TABLE `t_tro_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id主键',
  `name` varchar(20) NOT NULL COMMENT '部门名称',
  `code` varchar(50) DEFAULT NULL COMMENT '部门编码',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级部门id',
  `level` varchar(50) DEFAULT NULL COMMENT '部门层级',
  `path` varchar(50) DEFAULT NULL COMMENT '部门路径',
  `sequence` int(11) DEFAULT '0' COMMENT '排序',
  `ref_id` varchar(255) DEFAULT NULL COMMENT '第三方平台唯一标识',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_name_tenant` (`name`,`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=113 DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

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
-- Table structure for t_tro_resource_bak_4_10_0
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_resource_bak_4_10_0`;
CREATE TABLE `t_tro_resource_bak_4_10_0` (
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
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_value` (`value`(191)) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_tro_role
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_role`;
CREATE TABLE `t_tro_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色id主键',
  `application_id` bigint(20) DEFAULT NULL COMMENT '应用id(4.5.0版本后废弃不用)',
  `name` varchar(20) NOT NULL COMMENT '角色名称',
  `alias` varchar(255) DEFAULT NULL COMMENT '角色别名',
  `code` varchar(20) DEFAULT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态(0:启用 1:禁用)',
  `features` longtext COMMENT '扩展字段，k-v形式存在',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_idx_name_tenant_env` (`tenant_id`,`env_code`,`name`) USING BTREE,
  KEY `idx_application_id` (`application_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- Table structure for t_tro_role_user_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_role_user_relation`;
CREATE TABLE `t_tro_role_user_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `role_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_role_id` (`role_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=124 DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- ----------------------------
-- Table structure for t_tro_trace_entry
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_trace_entry`;
CREATE TABLE `t_tro_trace_entry` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'id',
  `app_name` varchar(128) NOT NULL COMMENT '应用',
  `entry` varchar(250) NOT NULL COMMENT '入口',
  `method` varchar(50) DEFAULT NULL COMMENT '方法',
  `status` varchar(20) NOT NULL COMMENT '状态',
  `start_time` bigint(20) NOT NULL COMMENT '开始时间',
  `end_time` bigint(20) NOT NULL COMMENT '结束时间',
  `process_time` bigint(20) NOT NULL COMMENT '耗时',
  `trace_id` varchar(30) NOT NULL COMMENT 'traceId',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `start_time` (`start_time`,`end_time`) USING BTREE,
  KEY `entry` (`entry`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='trace入口列表';

-- ----------------------------
-- Table structure for t_tro_user
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_user`;
CREATE TABLE `t_tro_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '登录账号',
  `nick` varchar(64) COLLATE utf8_bin DEFAULT NULL COMMENT '用户名称',
  `key` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '用户key',
  `salt` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '盐值',
  `password` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '登录密码',
  `status` tinyint(1) DEFAULT '0' COMMENT '状态 0:启用  1： 冻结',
  `user_type` int(11) DEFAULT '0' COMMENT '用户类型，1:普通用户 2：租户管理员 0：超级管理员',
  `model` tinyint(1) DEFAULT '0' COMMENT '模式 0:体验模式，1:正式模式',
  `role` tinyint(1) DEFAULT '0' COMMENT '角色 0:管理员，1:体验用户 2:正式用户',
  `phone` varchar(50) COLLATE utf8_bin DEFAULT '' COMMENT '手机号',
  `customer_id` bigint(20) DEFAULT NULL,
  `is_delete` tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `is_super` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否有超管权限，1=是，0=否，默认0',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_name` (`name`,`tenant_id`) USING BTREE,
  KEY `idx_phone` (`phone`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=235 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_tro_user_dept_relation
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_user_dept_relation`;
CREATE TABLE `t_tro_user_dept_relation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `user_id` bigint(20) DEFAULT NULL,
  `dept_id` bigint(20) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_dept_id` (`dept_id`) USING BTREE,
  KEY `idx_user_id` (`user_id`) USING BTREE,
  KEY `idx_tenant_id` (`tenant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=235 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_tro_version
-- ----------------------------
DROP TABLE IF EXISTS `t_tro_version`;
CREATE TABLE `t_tro_version` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` varchar(50) NOT NULL COMMENT '版本号',
  `description` varchar(500) DEFAULT NULL COMMENT '版本描述',
  `publisher` varchar(100) DEFAULT NULL COMMENT '发布人',
  `url` varchar(300) NOT NULL COMMENT '升级文档地址',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `version_unique` (`version`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_upload_interface_data
-- ----------------------------
DROP TABLE IF EXISTS `t_upload_interface_data`;
CREATE TABLE `t_upload_interface_data` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '抽数表id',
  `APP_NAME` varchar(64) NOT NULL COMMENT 'APP名',
  `INTERFACE_VALUE` varchar(256) NOT NULL COMMENT '接口值',
  `INTERFACE_TYPE` int(11) DEFAULT '1' COMMENT '上传数据类型 查看字典  暂时 1 dubbo 2 job',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`ID`) USING BTREE,
  KEY `TAD_INDEX1` (`APP_NAME`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=6887285115861340161 DEFAULT CHARSET=utf8mb4 COMMENT='dubbo和job接口上传收集表';

-- ----------------------------
-- Table structure for t_user_config
-- ----------------------------
DROP TABLE IF EXISTS `t_user_config`;
CREATE TABLE `t_user_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) NOT NULL COMMENT '租户id',
  `env_code` varchar(512) NOT NULL COMMENT '环境代码，测试环境：test,生产环境：prod',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `key` varchar(128) NOT NULL COMMENT '配置名',
  `value` longtext NOT NULL COMMENT '配置值',
  `desc` varchar(1024) DEFAULT '' COMMENT '配置描述',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态：0：停用；1：启用；',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0: 正常 1： 删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_key_tenant_env` (`user_id`,`tenant_id`,`env_code`,`key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Table structure for t_user_third_party
-- ----------------------------
DROP TABLE IF EXISTS `t_user_third_party`;
CREATE TABLE `t_user_third_party` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) unsigned NOT NULL COMMENT '用户id',
  `third_party_id` bigint(20) unsigned NOT NULL COMMENT '第三方id',
  `external_id` varchar(255) NOT NULL COMMENT '第三方用户唯一标识',
  `is_deleted` tinyint(3) unsigned DEFAULT '0' COMMENT '软删字段, 1 已删除, 0 未删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_update` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_te` (`third_party_id`,`external_id`) USING BTREE,
  KEY `idx_tu` (`third_party_id`,`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COMMENT='用户关联第三方表';

-- ----------------------------
-- Table structure for t_warn_detail
-- ----------------------------
DROP TABLE IF EXISTS `t_warn_detail`;
CREATE TABLE `t_warn_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pt_id` bigint(20) NOT NULL COMMENT '压测编号：同压测报告ID',
  `sla_id` bigint(20) NOT NULL COMMENT 'SLA配置ID',
  `sla_name` varchar(1024) COLLATE utf8_bin NOT NULL COMMENT 'SLA配置名称',
  `business_activity_id` bigint(20) NOT NULL COMMENT '业务活动ID',
  `business_activity_name` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '业务活动名称',
  `warn_content` varchar(100) COLLATE utf8_bin NOT NULL COMMENT '告警内容',
  `warn_rule_detail` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `real_value` double(11,3) DEFAULT NULL COMMENT '压测实际值',
  `warn_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '告警时间',
  `create_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `bind_ref` varchar(1000) COLLATE utf8_bin DEFAULT NULL COMMENT '绑定关系，节点下pathMD5值',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_pt_id` (`pt_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=43463 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for t_white_list
-- ----------------------------
DROP TABLE IF EXISTS `t_white_list`;
CREATE TABLE `t_white_list` (
  `WLIST_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `INTERFACE_NAME` varchar(1000) DEFAULT NULL COMMENT '接口名称',
  `TYPE` varchar(20) DEFAULT NULL COMMENT '白名单类型',
  `DICT_TYPE` varchar(50) DEFAULT NULL COMMENT '字典分类',
  `APPLICATION_ID` bigint(20) DEFAULT NULL,
  `PRINCIPAL_NO` varchar(10) DEFAULT NULL COMMENT '负责人工号',
  `USE_YN` int(11) DEFAULT '1' COMMENT '是否可用(0表示未启动,1表示启动,2表示启用未校验)',
  `CUSTOMER_ID` bigint(20) DEFAULT NULL COMMENT '租户id',
  `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
  `CREATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UPDATE_TIME` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `QUEUE_NAME` varchar(100) DEFAULT NULL COMMENT '队列名称，TYPE=5时该字段才会有值',
  `MQ_TYPE` char(1) DEFAULT NULL COMMENT 'MQ类型, 1ESB 2IBM 3ROCKETMQ 4DPBOOT_ROCKETMQ',
  `IP_PORT` varchar(512) DEFAULT NULL COMMENT 'IP端口,如1.1.1.1:8080,集群时用逗号分隔;当且仅当TYPE=5,MQ_TYPE=(3,4)时才会有值',
  `HTTP_TYPE` int(11) DEFAULT NULL COMMENT 'HTTP类型：1页面 2接口',
  `PAGE_LEVEL` int(11) DEFAULT NULL COMMENT '页面分类：1普通页面加载 2简单查询页面/复杂界面 3复杂查询页面',
  `INTERFACE_LEVEL` int(11) DEFAULT NULL COMMENT '接口类型：1简单操作/查询 2一般操作/查询 3复杂操作 4涉及级联嵌套调用多服务操作 5调用外网操作 ',
  `JOB_INTERVAL` int(11) DEFAULT NULL COMMENT 'JOB调度间隔：1调度间隔≤1分钟 2调度间隔≤5分钟 3调度间隔≤15分钟 4调度间隔≤60分钟',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '是否已经删除 0:未删除;1:删除',
  `gmt_create` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `is_global` tinyint(1) DEFAULT '1' COMMENT '是否全局生效',
  `is_handwork` tinyint(1) DEFAULT '0' COMMENT '是否手工添加',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`WLIST_ID`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=853 DEFAULT CHARSET=utf8 COMMENT='白名单管理';

-- ----------------------------
-- Table structure for t_whitelist_effective_app
-- ----------------------------
DROP TABLE IF EXISTS `t_whitelist_effective_app`;
CREATE TABLE `t_whitelist_effective_app` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `wlist_id` bigint(20) NOT NULL COMMENT '白名单id',
  `interface_name` varchar(1024) NOT NULL COMMENT '接口名',
  `type` varchar(20) DEFAULT NULL COMMENT '白名单类型',
  `EFFECTIVE_APP_NAME` varchar(1024) NOT NULL COMMENT '生效应用',
  `customer_id` bigint(20) DEFAULT NULL COMMENT '租户id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '软删',
  `tenant_id` bigint(20) NOT NULL DEFAULT '1' COMMENT '租户id',
  `env_code` varchar(20) NOT NULL DEFAULT 'test' COMMENT '环境变量',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL,
  `email` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;
