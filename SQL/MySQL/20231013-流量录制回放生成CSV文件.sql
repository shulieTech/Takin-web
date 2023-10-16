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
