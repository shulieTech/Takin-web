-- 应用表
create table IF NOT EXISTS t_application_mnt (
                                                 id                bigint unsigned auto_increment
                                                 primary key,
                                                 APPLICATION_ID    bigint(19)                             not null comment '应用id',
    APPLICATION_NAME  varchar(50)                            not null comment '应用名称',
    APPLICATION_DESC  varchar(200)                           null comment '应用说明',
    DDL_SCRIPT_PATH   varchar(200)                           not null comment '影子库表结构脚本路径',
    CLEAN_SCRIPT_PATH varchar(200)                           not null comment '数据清理脚本路径',
    READY_SCRIPT_PATH varchar(200)                           not null comment '基础数据准备脚本路径',
    BASIC_SCRIPT_PATH varchar(200)                           not null comment '铺底数据脚本路径',
    CACHE_SCRIPT_PATH varchar(200)                           not null comment '缓存预热脚本地址',
    CACHE_EXP_TIME    bigint(19)   default 0                 not null comment '缓存失效时间(单位秒)',
    USE_YN            int(1)                                 null comment '是否可用(0表示启用,1表示未启用)',
    AGENT_VERSION     varchar(16)                            null comment 'java agent版本',
    NODE_NUM          int(4)       default 1                 not null comment '节点数量',
    ACCESS_STATUS     int(2)       default 1                 not null comment '接入状态； 0：正常 ； 1；待配置 ；2：待检测 ; 3：异常',
    SWITCH_STATUS     varchar(255) default 'OPENED'          not null comment 'OPENED："已开启",OPENING："开启中",OPEN_FAILING："开启异常",CLOSED："已关闭",CLOSING："关闭中",CLOSE_FAILING："关闭异常"',
    EXCEPTION_INFO    text                                   null comment '接入异常信息',
    CREATE_TIME       datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    UPDATE_TIME       datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '修改时间',
    ALARM_PERSON      varchar(64)                            null comment '告警人',
    PRADAR_VERSION    varchar(30)                            null comment 'pradarAgent版本',
    CUSTOMER_ID       bigint                                 null comment '租户id',
    USER_ID           bigint(11)                             null comment '所属用户',
    constraint index_identifier_application_name
    unique (APPLICATION_NAME, CUSTOMER_ID)
    )
    comment '应用管理表' charset = utf8;

-- 报告汇总表
CREATE TABLE IF NOT EXISTS `t_report_summary`
(
    `id`                                bigint(20) NOT NULL AUTO_INCREMENT,
    `report_id`                         bigint(20) NOT NULL,
    `bottleneck_interface_count`        int(11) DEFAULT NULL COMMENT '瓶颈接口',
    `risk_machine_count`                int(11) DEFAULT NULL COMMENT '风险机器数',
    `business_activity_count`           int(11) DEFAULT NULL COMMENT '业务活动数',
    `unachieve_business_activity_count` int(11) DEFAULT NULL COMMENT '未达标业务活动数',
    `application_count`                 int(11) DEFAULT NULL COMMENT '应用数',
    `machine_count`                     int(11) DEFAULT NULL COMMENT '机器数',
    `warn_count`                        int(11) DEFAULT NULL COMMENT '告警次数',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_report_id` (`report_id`) USING BTREE
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8 COLLATE = utf8_bin;

-- 报告机器列表
CREATE TABLE IF NOT EXISTS `t_report_machine`
(
    `id`                        bigint(20) NOT NULL AUTO_INCREMENT,
    `report_id`                 bigint(20) NOT NULL,
    `application_name`          varchar(64) COLLATE utf8_bin  DEFAULT NULL COMMENT '应用名称',
    `machine_ip`                varchar(32) COLLATE utf8_bin  DEFAULT NULL COMMENT '机器ip',
    `machine_base_config`       varchar(512) COLLATE utf8_bin DEFAULT NULL COMMENT '机器基本信息',
    `machine_tps_target_config` text COLLATE utf8_bin COMMENT '机器tps对应指标信息',
    `risk_value`                decimal(16, 6)                DEFAULT NULL COMMENT '风险计算值',
    `risk_flag`                 tinyint(4)                    DEFAULT NULL COMMENT '是否风险机器(0-否，1-是)',
    `risk_content`              varchar(256) COLLATE utf8_bin DEFAULT NULL COMMENT '风险提示内容',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_report_application_machine` (`report_id`, `application_name`, `machine_ip`) USING BTREE
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8
    COLLATE = utf8_bin;

-- 报告瓶颈列表
CREATE TABLE IF NOT EXISTS `t_report_bottleneck_interface`
(
    `id`                bigint(20) NOT NULL AUTO_INCREMENT,
    `report_id`         bigint(20) NOT NULL,
    `application_name`  varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `sort_no`           int(11)                       DEFAULT NULL,
    `interface_type`    varchar(32) COLLATE utf8_bin  DEFAULT NULL COMMENT '接口类型',
    `interface_name`    varchar(512) COLLATE utf8_bin DEFAULT NULL,
    `tps`               decimal(10, 2)                DEFAULT NULL,
    `rt`                decimal(10, 2)                DEFAULT NULL,
    `node_count`        int(11)                       DEFAULT NULL,
    `error_reqs`        int(11)                       DEFAULT NULL,
    `bottleneck_weight` decimal(16, 10)               DEFAULT NULL,
    PRIMARY KEY (`id`),
    KEY `idx_report_id` (`report_id`)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8
    COLLATE = utf8_bin;

-- 应用汇总表
CREATE TABLE IF NOT EXISTS `t_report_application_summary`
(
    `id`                  bigint(20)                   NOT NULL AUTO_INCREMENT,
    `report_id`           bigint(20)                   NOT NULL,
    `application_name`    varchar(64) COLLATE utf8_bin NOT NULL,
    `machine_total_count` int(11) DEFAULT NULL COMMENT '总机器数',
    `machine_risk_count`  int(11) DEFAULT NULL COMMENT '风险机器数',
    PRIMARY KEY (`id`),
    UNIQUE KEY `unique_idx_report_appliacation` (`report_id`, `application_name`)
    ) ENGINE = InnoDB
    DEFAULT CHARSET = utf8
    COLLATE = utf8_bin;

CREATE TABLE IF NOT EXISTS `t_quick_access` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '数据库唯一主键',
    `custom_id` bigint(20) DEFAULT NULL COMMENT '客户id',
    `quick_name` varchar(255) COLLATE utf8_bin NOT NULL COMMENT '快捷键名称',
    `quick_logo` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '快捷键logo',
    `url_address` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '实际地址',
    `order` int(10) DEFAULT NULL COMMENT '顺序',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除 0:未删除;1:已删除',
    `is_enable` tinyint(4) DEFAULT '1' COMMENT '0:未启用；1:启用',
    PRIMARY KEY (`id`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `t_middleware_link_relate` (
    `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `MIDDLEWARE_ID` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '中间件id',
    `TECH_LINK_ID` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '技术链路id',
    `BUSINESS_LINK_ID` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '业务链路id',
    `SCENE_ID` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '场景id',
    `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`ID`) USING BTREE,
    KEY `t_middleware_link_relate1` (`MIDDLEWARE_ID`,`TECH_LINK_ID`) USING BTREE,
    KEY `t_middleware_link_relate2` (`TECH_LINK_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='链路中间件关联表';

CREATE TABLE IF NOT EXISTS `t_middleware_info` (
    `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `MIDDLEWARE_TYPE` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '中间件类型',
    `MIDDLEWARE_NAME` varchar(500) CHARACTER SET utf8 DEFAULT NULL COMMENT '中间件名称',
    `MIDDLEWARE_VERSION` varchar(100) DEFAULT NULL COMMENT '中间件版本号',
    `IS_DELETED` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    `CREATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `UPDATE_TIME` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`ID`) USING BTREE,
    KEY `t_middleware_info` (`MIDDLEWARE_TYPE`) USING BTREE,
    KEY `MIDDLEWARE_NAME` (`MIDDLEWARE_NAME`(255)) USING BTREE,
    KEY `MIDDLEWARE_VERSION` (`MIDDLEWARE_VERSION`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='中间件信息表';

CREATE TABLE IF NOT EXISTS `t_scene_link_relate` (
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
    PRIMARY KEY (`ID`) USING BTREE,
    KEY `T_LINK_MNT_INDEX2` (`ENTRANCE`(255)) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='链路场景关联表';

CREATE TABLE IF NOT EXISTS `t_data_build` (
    `DATA_BUILD_ID` bigint(19) NOT NULL COMMENT '数据构建id',
    `APPLICATION_ID` bigint(19) DEFAULT NULL COMMENT '应用id',
    `DDL_BUILD_STATUS` int(1) DEFAULT '0' COMMENT '影子库表结构脚本构建状态(0未启动 1正在执行 2执行成功 3执行失败)',
    `DDL_LAST_SUCCESS_TIME` datetime DEFAULT NULL COMMENT '影子库表结构脚本上一次执行成功时间',
    `CACHE_BUILD_STATUS` int(1) DEFAULT '0' COMMENT '缓存预热脚本执行状态',
    `CACHE_LAST_SUCCESS_TIME` datetime DEFAULT NULL COMMENT '缓存预热脚本上一次执行成功时间',
    `READY_BUILD_STATUS` int(1) DEFAULT '0' COMMENT '基础数据准备脚本执行状态',
    `READY_LAST_SUCCESS_TIME` datetime DEFAULT NULL COMMENT '基础数据准备脚本上一次执行成功时间',
    `BASIC_BUILD_STATUS` int(1) DEFAULT '0' COMMENT '铺底数据脚本执行状态',
    `BASIC_LAST_SUCCESS_TIME` datetime DEFAULT NULL COMMENT '铺底数据脚本上一次执行成功时间',
    `CLEAN_BUILD_STATUS` int(1) DEFAULT '0' COMMENT '数据清理脚本执行状态',
    `CLEAN_LAST_SUCCESS_TIME` datetime DEFAULT NULL COMMENT '数据清理脚本上一次执行成功时间',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
    PRIMARY KEY (`DATA_BUILD_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='压测数据构建表';

CREATE TABLE IF NOT EXISTS `t_link_detection` (
    `LINK_DETECTION_ID` bigint(19) NOT NULL COMMENT '主键id',
    `APPLICATION_ID` bigint(19) DEFAULT NULL COMMENT '应用id',
    `SHADOWLIB_CHECK` int(1) DEFAULT '0' COMMENT '影子库整体同步检测状态(0未启用,1正在检测,2检测成功,3检测失败)',
    `SHADOWLIB_ERROR` text COMMENT '影子库检测失败内容',
    `CACHE_CHECK` int(1) DEFAULT '0' COMMENT '缓存预热校验状态(0未启用,1正在检测,2检测成功,3检测失败)',
    `CACHE_ERROR` text COMMENT '缓存预热实时检测失败内容',
    `WLIST_CHECK` int(1) DEFAULT '0' COMMENT '白名单校验状态(0未启用,1正在检测,2检测成功,3检测失败)',
    `WLIST_ERROR` text COMMENT '白名单异常错误信息',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
    PRIMARY KEY (`LINK_DETECTION_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='链路检测表';


CREATE TABLE IF NOT EXISTS `t_link_mnt` (
    `LINK_ID` bigint(19) NOT NULL COMMENT '主键id',
    `LINK_NAME` varchar(200) DEFAULT NULL COMMENT '链路名称',
    `TECH_LINKS` text COMMENT '业务链路下属技术链路ids',
    `LINK_DESC` varchar(200) DEFAULT NULL COMMENT '链路说明',
    `LINK_TYPE` int(1) DEFAULT NULL COMMENT '1: 表示业务链路; 2: 表示技术链路; 3: 表示既是业务也是技术链路; ',
    `ASWAN_ID` varchar(200) DEFAULT NULL COMMENT '阿斯旺链路id',
    `LINK_ENTRENCE` varchar(200) DEFAULT NULL COMMENT '链路入口(http接口)',
    `RT_SA` varchar(10) DEFAULT NULL COMMENT '请求达标率(%)',
    `RT` varchar(10) DEFAULT NULL COMMENT '请求标准毫秒值',
    `TPS` varchar(20) DEFAULT NULL COMMENT '吞吐量(每秒完成事务数量)',
    `TARGET_SUCCESS_RATE` varchar(10) DEFAULT NULL COMMENT '目标成功率(%)',
    `DICT_TYPE` varchar(50) DEFAULT NULL COMMENT '字典分类',
    `LINK_RANK` varchar(20) DEFAULT NULL COMMENT '链路等级',
    `PRINCIPAL_NO` varchar(10) DEFAULT NULL COMMENT '链路负责人工号',
    `USE_YN` int(1) DEFAULT NULL COMMENT '是否可用(0表示未启用,1表示启用)',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
    `LINK_MODULE` varchar(5) DEFAULT NULL COMMENT '链路模块 1下单 2订单 3开单 4中转 5派送 6签收 7CUBC结算 10非主流程链路',
    `VOLUME_CALC_STATUS` varchar(5) DEFAULT NULL COMMENT '是否计算单量',
    `TARGET_TPS` varchar(20) DEFAULT NULL COMMENT '目标TPS',
    PRIMARY KEY (`LINK_ID`) USING BTREE,
    KEY `T_LINK_MNT_INDEX1` (`LINK_NAME`) USING BTREE,
    KEY `T_LINK_MNT_INDEX2` (`USE_YN`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='链路管理表';

CREATE TABLE IF NOT EXISTS `t_link_service_mnt` (
    `LINK_SERVICE_ID` bigint(19) NOT NULL COMMENT '主键id',
    `LINK_ID` bigint(19) DEFAULT NULL COMMENT '链路id',
    `INTERFACE_NAME` varchar(1000) DEFAULT NULL COMMENT '接口名称',
    `INTERFACE_DESC` varchar(1000) DEFAULT NULL COMMENT '接口说明',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
    `UPDATE_TIME` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`LINK_SERVICE_ID`) USING BTREE,
    KEY `T_LS_IDX1` (`LINK_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='基础链路服务关联表';

CREATE TABLE IF NOT EXISTS `t_shadow_table_datasource` (
    `SHADOW_DATASOURCE_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '影子表数据源id',
    `APPLICATION_ID` bigint(20) NOT NULL COMMENT '关联app_mn主键id',
    `DATABASE_IPPORT` varchar(128) NOT NULL COMMENT '数据库ip端口  xx.xx.xx.xx:xx',
    `DATABASE_NAME` varchar(128) NOT NULL COMMENT '数据库表明',
    `USE_SHADOW_TABLE` int(4) DEFAULT NULL COMMENT '是否使用 影子表 1 使用 0 不使用',
    `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
    `UPDATE_TIME` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`SHADOW_DATASOURCE_ID`) USING BTREE,
    UNIQUE KEY `SHADOW_DATASOURCE_INDEX2` (`APPLICATION_ID`,`DATABASE_IPPORT`,`DATABASE_NAME`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='影子表数据源配置';

CREATE TABLE IF NOT EXISTS `t_pressure_time_record` (
    `RECORD_ID` varchar(36) COLLATE utf8_bin NOT NULL COMMENT '压测记录id',
    `START_TIME` varchar(50) COLLATE utf8_bin NOT NULL COMMENT '开始压测时间',
    `END_TIME` varchar(50) COLLATE utf8_bin DEFAULT NULL COMMENT '结束压测时间',
    PRIMARY KEY (`RECORD_ID`) USING BTREE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='压测时间记录表';