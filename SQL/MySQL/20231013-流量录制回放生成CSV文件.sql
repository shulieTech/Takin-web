CREATE TABLE `t_script_csv_data_set` (
                                         `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                         `script_csv_data_set_name` varchar(200) DEFAULT NULL COMMENT '脚本csv组件名',
                                         `script_csv_file_name` varchar(200) DEFAULT NULL COMMENT '脚本csv文件名',
                                         `script_csv_variable_name` varchar(200) DEFAULT NULL COMMENT '脚本csv变量名',
                                         `ignore_first_line` tinyint(1) DEFAULT NULL COMMENT '是否忽略首行(0:否；1:是)',
                                         `script_deploy_id` bigint(20) DEFAULT NULL COMMENT '脚本实例ID',
                                         `business_flow_id` bigint(20) DEFAULT NULL COMMENT '业务流程ID',
                                         `file_manage_id` bigint(20) DEFAULT NULL COMMENT '关联的文件ID',
                                         `is_split` tinyint(1) DEFAULT 0 COMMENT '是否拆分',
                                         `is_order_split` tinyint(1) DEFAULT 0 COMMENT '是否按照顺序拆分',
                                         `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
                                         `UPDATE_TIME` datetime DEFAULT NULL COMMENT '变更时间',
                                         PRIMARY KEY (`id`) USING BTREE,
                                         KEY `business_flow_script_deploy_id` (`business_flow_id`,`script_deploy_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='csv组件表';

CREATE TABLE `t_script_csv_create_task` (
                                            `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                            `task_start_time` datetime DEFAULT NULL COMMENT '任务开始时间',
                                            `task_end_time` datetime DEFAULT NULL COMMENT '任务完成时间',
                                            `template_variable` varchar(1024) DEFAULT NULL COMMENT '模板变量(存储起止时间，应用名，接口，接口类型)',
                                            `template_content` text DEFAULT NULL COMMENT '模板内容，存对应变量生成的模板内容',
                                            `script_csv_variable_json_path` varchar(1024) DEFAULT NULL COMMENT '脚本csv变量的jsonPath映射',
                                            `current_create_schedule` varchar(1024) DEFAULT NULL COMMENT '当前生成进度',
                                            `create_status` tinyint(4) DEFAULT 1 COMMENT '生成状态(0：生成中，1：排队中，2：已生成，3已取消)',
                                            `remark` varchar(200) DEFAULT NULL COMMENT '备注',
                                            `alias_name` varchar(200) DEFAULT NULL COMMENT '别名',
                                            `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID（模块ID）',
                                            `business_flow_id` bigint(20) DEFAULT NULL COMMENT '业务流程ID',
                                            `link_id` bigint(20) DEFAULT NULL COMMENT '业务活动ID',
                                            `script_csv_data_set_id` bigint(20) DEFAULT NULL COMMENT 'csv组件表Id',
                                            `CREATE_TIME` datetime DEFAULT NULL COMMENT '插入时间',
                                            `tenant_id` bigint(20) not NULL COMMENT '租户id',
                                            `env_code` varchar(200) not NULL COMMENT '环境变量',

                                            PRIMARY KEY (`id`) USING BTREE,
                                            KEY `business_flow_script_deploy_id` (`business_flow_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='csv生成任务表';


alter table t_file_manage
    add create_type tinyint default 0 not null comment '生成类型（0:手动上传；1:在线生成）';

alter table t_file_manage
    add alias_name varchar(200) null comment '别名';

alter table t_file_manage
    add dept_id bigint null comment '部门ID（模块ID）';

alter table t_file_manage
    modify upload_path varchar(512) null comment '上传路径：相对路径';

alter table t_file_manage
    add script_csv_data_set_id bigint null comment '组件csvid';


UPDATE trodb.t_base_config SET CONFIG_VALUE = '[
    "appManage_2_create",
    "appManage_3_update",
    "appManage_4_delete",
    "appManage_6_enable_disable",
    "bottleneckConfig_3_update",
    "businessActivity_2_create",
    "businessActivity_3_update",
    "businessActivity_4_delete",
    "businessFlow_2_create",
    "businessFlow_3_update",
    "businessFlow_4_delete",
    "csvManage_3_update",
    "csvManage_4_delete",
    "configCenter_authorityConfig_2_create",
    "configCenter_authorityConfig_3_update",
    "configCenter_authorityConfig_4_delete",
    "configCenter_bigDataConfig_3_update",
    "configCenter_blacklist_2_create",
    "configCenter_blacklist_3_update",
    "configCenter_blacklist_4_delete",
    "configCenter_blacklist_6_enable_disable",
    "configCenter_dataSourceConfig_2_create",
    "configCenter_dataSourceConfig_3_update",
    "configCenter_dataSourceConfig_4_delete",
    "configCenter_entryRule_2_create",
    "configCenter_entryRule_3_update",
    "configCenter_entryRule_4_delete",
    "configCenter_middlewareManage_3_update",
    "configCenter_pressureMeasureSwitch_6_enable_disable",
    "configCenter_whitelistSwitch_6_enable_disable",
    "debugTool_linkDebug_2_create",
    "debugTool_linkDebug_3_update",
    "debugTool_linkDebug_4_delete",
    "debugTool_linkDebug_5_start_stop",
    "exceptionNoticeManage_2_create",
    "exceptionNoticeManage_3_update",
    "exceptionNoticeManage_4_delete",
    "patrolBoard_2_create",
    "patrolBoard_3_update",
    "patrolBoard_4_delete",
    "patrolManage_2_create",
    "patrolManage_3_update",
    "patrolManage_4_delete",
    "patrolManage_5_start_stop",
    "pressureTestManage_pressureTestScene_2_create",
    "pressureTestManage_pressureTestScene_3_update",
    "pressureTestManage_pressureTestScene_4_delete",
    "pressureTestManage_pressureTestScene_5_start_stop",
    "scriptManage_2_create",
    "scriptManage_3_update",
    "scriptManage_4_delete",
    "scriptManage_7_download"
]', CONFIG_DESC = '全部按钮名称', USE_YN = 0, CREATE_TIME = '2022-08-22 07:14:06', UPDATE_TIME = '2023-10-17 10:36:28' WHERE CONFIG_CODE = 'ALL_BUTTON' AND env_code = 'system' AND tenant_id = -1;


# 根据业务流程更新
update t_file_manage a set dept_id = (select dept_id from (select b.file_id,c.dept_id from t_scene c, t_script_file_ref b where c.script_deploy_id = b.script_deploy_id) d where d.file_id = a.id)
where dept_id is null

# 根据脚本表更新
update t_file_manage a set dept_id = (select dept_id from (select e.file_id,d.dept_id from (select b.dept_id,c.id from t_script_manage b,t_script_manage_deploy c where b.id = c.script_id
                                                                                           ) d, t_script_file_ref e  where d.id = e.script_deploy_id) f where f.file_id = a.id)
where dept_id is null