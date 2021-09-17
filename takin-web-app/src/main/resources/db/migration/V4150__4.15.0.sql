-- 数据表
create table IF NOT EXISTS e_patrol_activity_assert
(
    id               bigint auto_increment comment '主键' primary key,
    chain_id         bigint       null comment '链路ID',
    chain_type       int(2)       null comment '是否MQ：1-是；0-否',
    assert_name      varchar(255) null comment '断言名称',
    param_type       int(2)       null comment '断言参数类型：1-出参；2-入参',
    param_key        varchar(255) null comment '参数名',
    param_value      varchar(255) null comment '标准值',
    assert_condition int(255)     null comment '断言方式：1-等于；2-不等于；3-包含；4-不包含',
    mq_delay_time    bigint(255)  null comment 'mq延迟时间',
    create_time      timestamp    null comment '创建时间',
    modify_time      timestamp    null comment '修改时间',
    is_deleted       int(255)     null comment '是否删除：0-否；1-是',
    assert_code      varchar(255) null comment 'code'
) comment '断言表';

create table IF NOT EXISTS e_patrol_board
(
    id               bigint auto_increment comment '主键' primary key,
    customer_id      bigint                              null comment '用户ID',
    board_name       varchar(1000)                       null comment '看板名称',
    patrol_scene_num int                                 null comment '看板包含场景数量',
    board_status     int       default 0                 null comment '看板状态（是否大屏展示）：0-否；1-是',
    create_time      timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time      timestamp default CURRENT_TIMESTAMP null comment '更新时间',
    is_deleted       int(2)    default 0                 not null comment '是否已删除'
) comment '看板信息表' collate = utf8_bin;

create table IF NOT EXISTS e_patrol_board_scene
(
    id                bigint auto_increment comment '主键' primary key,
    patrol_board_id   bigint                              null comment '看板ID',
    patrol_board_name varchar(100)                        null comment '看板名称',
    patrol_scene_id   bigint(2)                           null comment '场景id',
    patrol_scene_name varchar(100)                        null comment '场景名称',
    create_time       timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time       timestamp default CURRENT_TIMESTAMP null comment '更新时间',
    is_deleted        int(2)    default 0                 not null comment '是否已删除'
) comment '看板-场景关联表' collate = utf8_bin;

create table IF NOT EXISTS e_patrol_exception
(
    id                bigint auto_increment comment '主键' primary key,
    status            int                  not null comment '状态',
    type              int                  not null comment '异常类型',
    level             int                  not null comment '异常程度',
    rt                double               null comment '响应时间',
    success_rate      double               null comment '成功率',
    start_time        datetime             not null comment '开始时间',
    end_time          datetime             null comment '结束时间',
    business_id       bigint               not null comment '任务标识',
    business_name     varchar(100)         not null comment '任务名称',
    business_type     int                  not null comment '任务类型',
    business_rpc_type varchar(100)         null comment '业务入口',
    scene_id          bigint               not null comment '巡检场景',
    scene_name        varchar(100)         not null comment '巡检场景名称',
    board_id          bigint               not null comment '巡检看板',
    board_name        varchar(100)         not null comment '巡检看板名称',
    is_finish         tinyint(1) default 0 not null comment '是否处理完毕',
    constraint e_patrol_exception_id_uindex unique (id)
) comment '巡检异常信息' collate = utf8_bin;

create table IF NOT EXISTS e_patrol_exception_config
(
    id              bigint auto_increment comment '主键' primary key,
    order_number    int              null comment '进行判断时的顺序',
    type_value      int              not null comment '异常类型:1,卡慢;2,异常;3,巡检异常;\n字典表的code.',
    level_value     int              null comment '等级:1.一般;2.严重;\n字典表的code.',
    threshold_value double           not null comment '阈值',
    contrast_factor int              not null comment '对比因子:1.大于;-1.小于;',
    remarks         varchar(10)      null comment '备注',
    is_deleted      int(2) default 0 not null comment '是否已删除'
) comment '巡检异常配置' collate = utf8_bin;
-- 说明
INSERT IGNORE INTO e_patrol_exception_config (id, order_number, type_value, level_value, threshold_value, contrast_factor, remarks, is_deleted)
VALUES (1, 1, 1, 1, 100.0, +1, '一般卡慢', 0),
       (2, 0, 1, 2, 230.0, +1, '严重卡慢', 0),
       (3, 1, 2, 1, 90.00, -1, '一般瓶颈', 0),
       (4, 0, 2, 2, 80.00, -1, '严重瓶颈', 0);

create table IF NOT EXISTS e_patrol_exception_notice_config
(
    id                       bigint auto_increment comment '主键' primary key,
    patrol_exception_type_id bigint                             not null comment '故障类型主键.0:全部;*:具体的关联主键;',
    patrol_board_id          bigint                             not null comment '场景面板ID:0:全部;*:具体的关联主键;',
    patrol_board_name        varchar(100)                       null comment '看板名称',
    patrol_scene_id          bigint                             not null comment '巡检场景ID:0:全部;*:具体的关联主键;',
    patrol_scene_name        varchar(100)                       null comment '场景名称',
    patrol_scene_chain_id    bigint                             not null comment '巡检节点ID:0:全部;*:具体的关联主键;',
    business_name            varchar(100)                       null comment '根据巡检节点生成,用于前台模糊查询.如果是全部,则为null.',
    channel                  bigint                             not null comment ':0.钉钉;1.微信群;',
    hook_url                 varchar(500)                       not null comment 'Hook地址',
    interval_threshold_value bigint                             not null comment '通知的间隔时间',
    max_number_of_times      bigint                             not null comment '最大通知次数.-1,代表不限.',
    create_user_id           bigint                             not null comment '创建者',
    create_user_name         varchar(100)                       null comment '创建者名称',
    modify_user_id           bigint                             not null comment '修改者',
    modify_user_name         varchar(100)                       null comment '修改者名称',
    create_time              datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time              datetime default CURRENT_TIMESTAMP not null comment '更新时间'
) comment '巡检异常通知配置' collate = utf8_bin;

create table IF NOT EXISTS e_patrol_exception_status_change_log
(
    id           bigint auto_increment comment '主键' primary key,
    exception_id bigint       not null comment '异常主键',
    type         int          not null comment '操作类型',
    time         datetime     not null comment '操作时间',
    category     int          not null comment '操作原因',
    detail       varchar(100) not null comment '详细原因',
    user_id      bigint       not null comment '操作人',
    user_name    varchar(100) not null comment '操作人名称'
)
    comment '异常信息状态变更日志' collate = utf8_bin;

create table IF NOT EXISTS e_patrol_scene
(
    id                bigint auto_increment comment '主键' primary key,
    customer_id       bigint                              null comment '租户id',
    patrol_scene_name varchar(100)                        null comment '巡检场景名称',
    ref_type          int(2)    default 1                 null comment '巡检场景类型：1-业务活动；2-业务流程',
    ref_num           int(2)                              null comment '巡检场景包含业务活动数量',
    patrol_status     int(2)    default 0                 null comment '巡检任务状态:0-正常巡检,1-巡检配置异常',
    task_status       int(2)    default 0                 null comment '任务启用状态：0-任务关闭，1-任务开启',
    ref_id            bigint                              null comment '业务活动或者业务流程ID',
    script_id         bigint                              null comment '业务活动/流程对应的脚本ID',
    patrol_period     bigint    default 5                 null comment '巡检周期：单位秒，默认5',
    scene_id          bigint                              null comment '压测场景ID',
    report_id         bigint                              null comment '压测报告ID',
    create_time       timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time       timestamp default CURRENT_TIMESTAMP null comment '更新时间',
    is_deleted        int(2)    default 0                 not null comment '是否已删除'
) comment '巡检场景信息表' collate = utf8_bin;

create table IF NOT EXISTS e_patrol_scene_chain
(
    id                        bigint auto_increment comment '主键' primary key,
    patrol_scene_id           bigint                              null comment '场景id',
    activity_id               bigint                              null comment '业务活动id',
    activity_name             varchar(1000)                       null comment '业务活动名称',
    patrol_type               int(2)    default 0                 null comment '巡检类型:0-业务巡检,1-技术巡检',
    parent_id                 bigint    default 0                 null comment '关联业务活动id,此字段只有在`patrol_type`=1 时，才会大于0',
    is_mq                     int(2)    default 0                 null comment '是否MQ类型，0-否，1-是',
    activity_order            int(2)                              null comment '排序',
    activity_status           int(2)                              null comment '状态，0-可用，1-不可用',
    create_time               timestamp default CURRENT_TIMESTAMP not null comment '创建时间',
    modify_time               timestamp default CURRENT_TIMESTAMP null comment '更新时间',
    is_deleted                int(2)    default 0                 not null comment '是否已删除',
    entrance_application_name varchar(255)                        null comment '入口应用名称',
    entrance_application_id   bigint                              null comment '入口应用ID',
    entrance_type             varchar(255)                        null comment '入口类型',
    entrance_rpc_type         varchar(255)                        null comment 'rpcType',
    entrance_service_name     varchar(255)                        null comment '入口服务名称',
    entrance_method           varchar(255)                        null comment '入口方法',
    entrance_name             varchar(255)                        null comment '入口名称',
    tech_node_id              varchar(255)                        null comment '技术节点ID'
) comment '场景链路信息表' collate = utf8_bin;

create table IF NOT EXISTS e_patrol_scene_check
(
    id                bigint auto_increment primary key,
    patrol_scene_id   bigint           null comment '巡检场景ID',
    patrol_chain_id   bigint           null comment '巡检链路ID',
    app_name          varchar(255)     null comment '应用名称',
    error_code        varchar(255)     null comment '错误码',
    error_description varchar(255)     null comment '错误描述',
    error_detail      varchar(2000)     null comment '错误详情',
    modify_date       datetime         null comment '修改时间',
    activity_name     varchar(255)     null comment '业务活动名称',
    is_deleted        int(2) default 0 not null comment '是否删除'
) comment '场景异常检查表' collate = utf8_bin;


-- 创建菜单
-- 插入开始
DROP PROCEDURE IF EXISTS insert_data;
DELIMITER $$
CREATE PROCEDURE insert_data()
BEGIN

DECLARE count1 INT;
SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'patrolManage');

IF count1 = 0 THEN

-- 添加菜单
INSERT INTO `t_tro_resource` (`parent_id`, `type`, `alias`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `code`, `name`, `value`, `sequence`, `action`) VALUES (NULL, 0, NULL, NULL, NULL, NULL, NOW(), NOW(), 0, 'patrolManage', '巡检管理', '[\"/api/patrol/manager\"]', 10000, '[2,3,5,6]');


END IF;

SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'patrolScreen');

IF count1 = 0 THEN

-- 添加菜单
    INSERT INTO `t_tro_resource` (`parent_id`, `type`, `alias`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `code`, `name`, `value`, `sequence`, `action`) VALUES    (NULL, 0, NULL, NULL, NULL, NULL, NOW(), NOW(), 0, 'patrolScreen', '巡检大屏', '[\"/api/patrol/screen\"]', 11000, '[2]');

END IF;

SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'bottleneckTable');

IF count1 = 0 THEN

INSERT INTO `t_tro_resource` (`parent_id`, `type`, `alias`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `code`, `name`, `value`, `sequence`, `action`) VALUES (NULL, 0, NULL, NULL, NULL, NULL, NOW(), NOW(), 0, 'bottleneckTable', '瓶颈列表', '[\"/api/patrol/manager/exception/query\"]', 12000, '[2,3,5,6]');

END IF;

SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'exceptionNoticeManage');

IF count1 = 0 THEN

    INSERT INTO `t_tro_resource` (`parent_id`, `type`, `alias`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `code`, `name`, `value`, `sequence`, `action`) VALUES    (NULL, 0, NULL, NULL, NULL, NULL, NOW(), NOW(), 0, 'exceptionNoticeManage', '异常通知管理', '[\"/api/patrol/manager/exception_notice\"]', 13000, '[2,3,5,6]');

END IF;

SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'bottleneckConfig');

IF count1 = 0 THEN

   INSERT INTO `t_tro_resource` (`parent_id`, `type`, `alias`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `code`, `name`, `value`, `sequence`, `action`) VALUES (NULL, 0, NULL, NULL, NULL, NULL, NOW(), NOW(), 0, 'bottleneckConfig', '瓶颈配置', '[\"/api/patrol/manager/exception_config\"]', 14000, '[2,3,5,6]');

END IF;

END $$
DELIMITER ;
CALL insert_data();
DROP PROCEDURE IF EXISTS insert_data;
-- 插入结束

BEGIN;
INSERT IGNORE INTO `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`)
VALUES ('E2E_SUCCESS_RATE', '99.99', '成功率', 0, NOW(), NOW()),
       ('WECHAT_HOOK_URL', 'https://work.weixin.qq.com/help?person_id=1&doc_id=1337', '微信机器人地址', 0, NOW(), NOW()),
       ('DINGDING_HOOK_URL', 'https://developers.dingtalk.com/document/app/custom-robot-access/title-72m-8ag-pqw', '钉钉机器人地址', 0, NOW(), NOW()),
       ('E2E_RT', '3000', '响应时间（单位：毫秒）', 0, NULL, NULL),
       ('NOTIFY_TYPE', 'DINGDING_HOOK_URL', '通知类型', 0, NOW(), NOW());


-- 字典表数据
INSERT IGNORE INTO `t_dictionary_type`
    (ID, ACTIVE, CREATE_TIME, MODIFY_TIME, CREATE_USER_CODE, MODIFY_USER_CODE, PARENT_CODE, IS_LEAF, TYPE_ALIAS, TYPE_NAME)
VALUES ('202104281025590e2e00000000000001', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, 'PATROL_TYPE', '巡检类型'),
       ('202104281025590e2e00000000000002', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, 'PATROL_EXCEPTION_TYPE', '巡检异常类型'),
       ('202104281025590e2e00000000000003', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, 'PATROL_EXCEPTION_LEVEL', '巡检异常程度'),
       ('202104281025590e2e00000000000004', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, 'PATROL_EXCEPTION_STATUS', '巡检异常状态'),
       ('202104281025590e2e00000000000005', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, 'NOTIFY_CHANNEL', '巡检异常通知渠道'),
       ('202104281025590e2e00000000000006', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, 'PATROL_EXCEPTION_STATUS_CHANGE_1', '巡检异常-状态[误判]更改原因');
#      ('202104281025590e2e00000000000005', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, 'PATROL_EXCEPTION_NOTIFY_CHANNEL', '巡检异常通知渠道'),
#      ('202104281025590e2e00000000000006', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, 'PATROL_EXCEPTION_STATUS_CHANGE_TYPE1_CATEGORY', '巡检异常-状态[误判]更改原因');
INSERT IGNORE INTO `t_dictionary_data`
    (ID, DICT_TYPE, VALUE_ORDER, VALUE_CODE, LANGUAGE, ACTIVE, CREATE_TIME, CREATE_USER_CODE, MODIFY_TIME, MODIFY_USER_CODE, NOTE_INFO, VERSION_NO, VALUE_NAME)
VALUES ('202104281025590e2e01000000000001', '202104281025590e2e00000000000001', 0, 0, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '业务'),
       ('202104281025590e2e01000000000002', '202104281025590e2e00000000000001', 0, 1, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '技术'),
       ('202104281025590e2e02000000000001', '202104281025590e2e00000000000002', 0, 1, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '卡慢'),
       ('202104281025590e2e02000000000002', '202104281025590e2e00000000000002', 1, 2, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '接口异常'),
       ('202104281025590e2e03000000000001', '202104281025590e2e00000000000003', 0, 1, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '一般'),
       ('202104281025590e2e03000000000002', '202104281025590e2e00000000000003', 1, 2, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '严重'),
       ('202104281025590e2e05000000000001', '202104281025590e2e00000000000004', 0, 1, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '待处理'),
       ('202104281025590e2e05000000000002', '202104281025590e2e00000000000004', 1, 2, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '已恢复'),
       ('202104281025590e2e05000000000003', '202104281025590e2e00000000000004', 2, 3, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '判定为误报'),
       ('202104281025590e2e04000000000001', '202104281025590e2e00000000000005', 0, 0, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '钉钉'),
       ('202104281025590e2e04000000000002', '202104281025590e2e00000000000005', 0, 1, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '企业微信'),
       ('202104231502560e2e06000000000001', '202104281025590e2e00000000000006', 0, 1, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '系统误判(规则不完善)'),
       ('202104231502560e2e06000000000002', '202104281025590e2e00000000000006', 1, 2, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '不可抗因素(机房断电等)'),
       ('202104231502560e2e06000000000003', '202104281025590e2e00000000000006', 2, 3, 'ZH_CN', 'Y', DATE(NOW()), NULL, NULL, NULL, NULL, NULL, '其它');
COMMIT;