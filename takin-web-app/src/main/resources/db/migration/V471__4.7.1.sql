-- 调试机器性能分析
CREATE TABLE IF NOT EXISTS `t_fast_debug_machine_performance`
(
    `id`             bigint(20)     NOT NULL AUTO_INCREMENT,
    `trace_id`       varchar(512)            DEFAULT NULL COMMENT 'traceId',
    `rpc_id`         varchar(512)            DEFAULT NULL COMMENT 'rpcid',
    `log_type`       tinyint(4)              DEFAULT NULL COMMENT '服务端、客户端',
    `index`          varchar(128)            DEFAULT NULL COMMENT '性能类型，beforeFirst/beforeLast/afterFirst/afterLast/exceptionFirst/exceptionLast',
    `cpu_usage`      decimal(10, 4)          DEFAULT '0.0000' COMMENT 'cpu利用率',
    `cpu_load`       decimal(10, 2) NOT NULL DEFAULT '0.00' COMMENT 'cpu load',
    `memory_usage`   decimal(10, 4)          DEFAULT '0.0000' COMMENT '没存利用率',
    `memory_total`   decimal(20, 2)          DEFAULT '0.00' COMMENT '堆内存总和',
    `io_wait`        decimal(10, 4)          DEFAULT '0.0000' COMMENT 'io 等待率',
    `young_gc_count` bigint(20)              DEFAULT NULL,
    `young_gc_time`  bigint(20)              DEFAULT NULL,
    `old_gc_count`   bigint(20)              DEFAULT NULL,
    `old_gc_time`    bigint(20)              DEFAULT NULL,
    `is_deleted`     tinyint(1)     NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
    `gmt_create`     datetime                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`     datetime                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 调试堆栈异常信息------
CREATE TABLE IF NOT EXISTS `t_fast_debug_stack_info`
(
    `id`         bigint(20)   NOT NULL AUTO_INCREMENT,
    `app_name`   varchar(255)          DEFAULT NULL,
    `agent_id`   varchar(255)          DEFAULT NULL,
    `trace_id`   varchar(512)          DEFAULT NULL COMMENT 'traceId',
    `rpc_id`     varchar(512) NOT NULL COMMENT 'rpcid',
    `level`      varchar(64)           DEFAULT NULL COMMENT '日志级别',
    `type`       tinyint(4)            DEFAULT NULL COMMENT '服务端，客户端',
    `content`    longtext COMMENT 'stack信息',
    `is_stack`   tinyint(1)            DEFAULT NULL COMMENT '是否调用栈日志',
    `is_deleted` tinyint(1)   NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
    `gmt_create` datetime              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update` datetime              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    KEY `index_traceId` (`trace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 快速调试配置表------
CREATE TABLE IF NOT EXISTS `t_fast_debug_config_info`
(
    `id`               bigint(20)   NOT NULL AUTO_INCREMENT,
    `name`             varchar(256) NOT NULL unique COMMENT '调试名称',
    `request_url`      varchar(256) NOT NULL COMMENT '完整url',
    `headers`          varchar(2048)         DEFAULT NULL COMMENT '请求头',
    `cookies`          varchar(2048)         DEFAULT NULL COMMENT 'cookies',
    `body`             longtext              DEFAULT NULL COMMENT '请求体',
    `http_method`      varchar(128)          DEFAULT NULL COMMENT '请求方法',
    `timeout`          int(1)                DEFAULT NULL COMMENT '响应超时时间',
    `is_redirect`      tinyint(1)            DEFAULT NULL COMMENT '是否重定向',
    `business_link_id` bigint(20)   NOT NULL COMMENT '业务活动id',
    `content_type`     varchar(512)          DEFAULT NULL COMMENT 'contentType数据',
    `status`           tinyint(4)   NOT NULL DEFAULT 0 COMMENT '0：未调试，1，调试中',
    `gmt_create`       datetime(3)           DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `gmt_modified`     datetime(3)           DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `is_deleted`       tinyint(1)            DEFAULT 0 COMMENT '软删',
    `customer_id`      bigint(20)            DEFAULT NULL COMMENT '租户id',
    `creator_id`       bigint(20)            DEFAULT NULL COMMENT '创建人',
    `modifier_id`      bigint(20)            DEFAULT NULL COMMENT '修改人',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4;

-- 快速调试结果表--------
CREATE TABLE IF NOT EXISTS `t_fast_debug_result`
(
    `id`                 bigint(20)    NOT NULL AUTO_INCREMENT,
    `config_id`          bigint(20)    NOT NULL COMMENT '调试配置id',
    `name`               varchar(256)  NOT NULL COMMENT '调试名称',
    `business_link_name` varchar(256)  NOT NULL COMMENT '业务活动名',
    `request_url`        varchar(1024) NOT NULL COMMENT '完整url',
    `http_method`        varchar(256)  NOT NULL COMMENT '调试名称',
    `request`            longtext      DEFAULT NULL COMMENT '请求体，包含url,body,header',
    `leakage_check_data` longtext      DEFAULT NULL COMMENT '漏数检查数据,每次自己报存一份，并保持结果',
    `response`           longtext      DEFAULT NULL COMMENT '请求返回体',
    `error_message`      varchar(1024) DEFAULT NULL COMMENT '错误原因',
    `trace_id`           varchar(512)  DEFAULT NULL COMMENT '通过response解析出来traceId',
    `call_time`          bigint(20)    DEFAULT NULL COMMENT '调用时长',
    `gmt_create`         datetime(3)   DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `gmt_modified`       datetime(3)   DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `is_deleted`         tinyint(1)    DEFAULT 0 COMMENT '软删',
    `customer_id`        bigint(20)    DEFAULT NULL COMMENT '租户id',
    `creator_id`         bigint(20)    DEFAULT NULL COMMENT '操作人',
    `debug_result`       tinyint(1)    DEFAULT NULL COMMENT '0:失败；1：成功；调试中根据config中status判断',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 快速调试异常表----------
CREATE TABLE IF NOT EXISTS `t_fast_debug_exception`
(
    `id`               bigint(20)   NOT NULL AUTO_INCREMENT,
    `result_id`        bigint(20)   DEFAULT NULL COMMENT '结果id',
    `trace_id`         varchar(512) DEFAULT NULL COMMENT 'traceId',
    `customer_id`      bigint(20)   DEFAULT NULL COMMENT '租户id',
    `agent_Id`         longtext COMMENT 'agentid',
    `rpc_id`           varchar(512) DEFAULT NULL COMMENT 'rpc_id',
    `application_Name` varchar(512) NOT NULL COMMENT '应用名',
    `type`             varchar(512) DEFAULT NULL COMMENT '异常类型',
    `code`             varchar(512) DEFAULT NULL COMMENT '异常编码',
    `description`      longtext COMMENT '异常描述',
    `suggestion`       varchar(512) DEFAULT NULL COMMENT '处理建议',
    `time`             varchar(512) DEFAULT NULL COMMENT '异常时间',
    `is_deleted`       tinyint(1)   DEFAULT '0' COMMENT '软删',
    `gmt_create`       datetime(3)  DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `gmt_modified`     datetime(3)  DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `index_traceId` (`trace_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


-- 节点信息表----------
CREATE TABLE IF NOT EXISTS `t_trace_node_info`
(
    `id`              bigint(20) NOT NULL AUTO_INCREMENT,
    `app_name`        varchar(512) DEFAULT NULL COMMENT '应用名',
    `trace_id`        varchar(512) DEFAULT NULL COMMENT 'traceId',
    `rpc_id`          varchar(512) DEFAULT NULL COMMENT 'rpcid',
    `log_type`        tinyint(4)   DEFAULT NULL COMMENT 'log_type',
    `trace_app_name`  varchar(512) DEFAULT NULL COMMENT '入口的app名称',
    `up_app_name`     varchar(512) DEFAULT NULL COMMENT '上游的app名称',
    `start_time`      bigint(20)   DEFAULT NULL COMMENT '开始时间的时间戳',
    `cost`            bigint(20)   DEFAULT NULL COMMENT '耗时',
    `middleware_name` varchar(512) DEFAULT NULL COMMENT '中间件名称',
    `service_name`    varchar(512) DEFAULT NULL COMMENT '服务名',
    `method_name`     varchar(512) DEFAULT NULL COMMENT '方法名',
    `remote_ip`       varchar(128) DEFAULT NULL COMMENT '远程IP',
    `result_code`     varchar(128) DEFAULT NULL COMMENT '结果编码',
    `request`         longtext     DEFAULT NULL COMMENT '请求内容',
    `response`        longtext     DEFAULT NULL COMMENT '响应内容',
    `callback_msg`    longtext     DEFAULT NULL COMMENT '附加信息',
    `cluster_test`    tinyint(1)   DEFAULT NULL COMMENT '1是压测流量，0是业务流量',
    `host_ip`         varchar(512) DEFAULT NULL COMMENT '日志所属机器ip',
    `agent_id`        varchar(512) DEFAULT NULL COMMENT 'agent id',
    `gmt_create`      datetime(3)  DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `gmt_modified`    datetime(3)  DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `is_deleted`      tinyint(1)   DEFAULT 0 COMMENT '软删',
    `customer_id`     bigint(20)   DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- t_exception_info----------
CREATE TABLE IF NOT EXISTS `t_exception_info`
(
    `id`           bigint(20) NOT NULL AUTO_INCREMENT,
    `type`         varchar(512) DEFAULT NULL COMMENT '异常类型',
    `code`         varchar(512) DEFAULT NULL COMMENT '异常编码',
    `agent_code`   varchar(512) DEFAULT NULL COMMENT 'agent异常编码',
    `description`  varchar(512) DEFAULT NULL COMMENT '异常描述',
    `suggestion`   varchar(512) DEFAULT NULL COMMENT '处理建议',
    `count`        bigint(20)   DEFAULT 0 COMMENT '发生次数',
    `gmt_create`   datetime(3)  DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `gmt_modified` datetime(3)  DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `is_deleted`   tinyint(1)   DEFAULT 0 COMMENT '软删',
    `customer_id`  bigint(20)   DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

BEGIN;
-- 菜单权限问题-------
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (400, NULL, 0, 'debugTool', '调试工具', NULL, '', 9000, '[]', NULL, NULL, NULL, '2021-01-14 11:19:50',
        '2021-01-14 11:19:50', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (401, 12, 0, 'debugTool_linkDebug', '链路调试', NULL, '[\"/debugTool/linkDebug\"]', 9001, '[]', NULL, NULL, NULL,
        '2021-01-14 11:22:03', '2021-01-14 11:31:48', 0);
INSERT IGNORE INTO `t_tro_resource`(`id`, `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,
                                     `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`)
VALUES (402, 12, 0, 'debugTool_linkDebug_detail', '链路调试详情', NULL, '[\"/debugTool/linkDebug/detail\"]', 9002, '[]', NULL,
        NULL, NULL, '2021-01-14 11:23:19', '2021-01-14 11:32:13', 0);
COMMIT;