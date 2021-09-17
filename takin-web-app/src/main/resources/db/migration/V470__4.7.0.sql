-- 数据源管理
CREATE TABLE IF NOT EXISTS `t_tro_dbresource`
(
    `id`          bigint(11)   NOT NULL AUTO_INCREMENT,
    `type`        tinyint(2)   DEFAULT '0' COMMENT '0:MYSQL',
    `name`        varchar(64)  NOT NULL COMMENT '数据源名称',
    `jdbc_url`    varchar(500) NOT NULL COMMENT '数据源地址',
    `username`    varchar(64)  NOT NULL COMMENT '数据源用户',
    `password`    varchar(200) NOT NULL COMMENT '数据源密码',
    `features`    longtext COMMENT '扩展字段，k-v形式存在',
    `customer_id` bigint(20)   DEFAULT NULL COMMENT '租户id',
    `user_id`     bigint(20)   DEFAULT NULL COMMENT '用户id',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- 标签引用表
CREATE TABLE IF NOT EXISTS `t_datasource_tag_ref`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT,
    `datasource_id` bigint(20) NOT NULL COMMENT '数据源id',
    `tag_id`        bigint(20) NOT NULL COMMENT '标签id',
    `gmt_create`    datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
    `gmt_update`    datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    PRIMARY KEY (`id`),
    UNIQUE KEY `index_datasourceId_tagId` (`datasource_id`, `tag_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- 漏数配置表
CREATE TABLE IF NOT EXISTS `t_leakcheck_config`
(
    `id`                   bigint(11) NOT NULL AUTO_INCREMENT,
    `business_activity_id` bigint(11) NOT NULL COMMENT '业务活动id',
    `datasource_id`        bigint(11) NOT NULL COMMENT '数据源id',
    `leakcheck_sql_ids`    text       NOT NULL COMMENT '漏数sql主键集合，逗号分隔',
    `customer_id`          bigint(20)   DEFAULT NULL COMMENT '租户id',
    `user_id`              bigint(20)   DEFAULT NULL COMMENT '用户id',
    `remark`               varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time`          datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`          datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`           tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- sql配置详情表
CREATE TABLE IF NOT EXISTS `t_leakcheck_config_detail`
(
    `id`            bigint(11)   NOT NULL AUTO_INCREMENT,
    `datasource_id` bigint(11)   NOT NULL COMMENT '数据源id',
    `sql_content`   varchar(255) NOT NULL COMMENT '漏数sql',
    `customer_id`   bigint(20)   DEFAULT NULL COMMENT '租户id',
    `user_id`       bigint(20)   DEFAULT NULL COMMENT '用户id',
    `remark`        varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time`   datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`    tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- 验证结果表
CREATE TABLE IF NOT EXISTS `t_leakverify_result`
(
    `id`              bigint(11) NOT NULL AUTO_INCREMENT,
    `ref_type`        tinyint(1)   DEFAULT '0' COMMENT '引用类型 0:压测场景;1:业务流程;2:业务活动',
    `ref_id`          bigint(11) NOT NULL COMMENT '引用id',
    `report_id`       bigint(11)   DEFAULT NULL COMMENT '报告id',
    `dbresource_id`   bigint(11) NOT NULL COMMENT '数据源id',
    `dbresource_name` varchar(255) DEFAULT NULL COMMENT '数据源名称',
    `dbresource_url`  varchar(500) DEFAULT NULL COMMENT '数据源地址',
    `customer_id`     bigint(20)   DEFAULT NULL COMMENT '租户id',
    `user_id`         bigint(20)   DEFAULT NULL COMMENT '用户id',
    `remark`          varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time`     datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`      tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- 验证结果详情表
CREATE TABLE IF NOT EXISTS `t_leakverify_detail`
(
    `id`          bigint(11)   NOT NULL AUTO_INCREMENT,
    `result_id`   bigint(11)   NOT NULL COMMENT '验证结果id',
    `leak_sql`    varchar(500) NOT NULL COMMENT '漏数sql',
    `status`      tinyint(1)   DEFAULT '0' COMMENT '是否漏数 0:正常;1:漏数;2:未检测;3:检测失败',
    `remark`      varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `is_deleted`  tinyint(1)   DEFAULT '0' COMMENT '是否有效 0:有效;1:无效',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- 插入开始
DROP PROCEDURE IF EXISTS insert_data;
DELIMITER $$
CREATE PROCEDURE insert_data()
BEGIN

DECLARE count1 INT;
SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'configCenter_dataSourceConfig');

IF count1 = 0 THEN

-- 添加菜单
-- 增加数据源配置菜单
INSERT IGNORE INTO `t_tro_resource` (`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`,                                       `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES (11, 0, 'configCenter_dataSourceConfig', '数据源配置', NULL, '[\"/api/datasource/list\"]', 7600, ' [2,3,4]', NULL, NULL, NULL, '2021-01-06 15:17:40', '2021-01-06 15:19:12', 0);

END IF;

END $$
DELIMITER ;
CALL insert_data();
DROP PROCEDURE IF EXISTS insert_data;
-- 插入结束

-- 字段更新开始
DROP PROCEDURE IF EXISTS update_field;
DELIMITER $$
CREATE PROCEDURE update_field()
BEGIN

DECLARE count1 INT;
SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource' AND COLUMN_NAME = 'code');

IF count1 > 0 THEN

UPDATE `t_tro_resource`
SET `action` = '[2,3,4,5]'
WHERE `code` = 'debugTool_linkDebug';

END IF;

END $$
DELIMITER ;
CALL update_field();
DROP PROCEDURE IF EXISTS update_field;
-- 字段更新结束

-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

DECLARE count INT;
SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tag_manage' AND COLUMN_NAME = 'tag_type');

IF count > 0 THEN

alter table t_tag_manage
    modify column `tag_type` tinyint(4) DEFAULT NULL COMMENT '标签类型;0为脚本标签;1为数据源标签';

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束

BEGIN;

-- 增加可验证的数据源类型对应的数据字典
INSERT IGNORE INTO `t_dictionary_type`(`ID`, `TYPE_NAME`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`,
                                        `MODIFY_USER_CODE`, `PARENT_CODE`, `TYPE_ALIAS`, `IS_LEAF`)
VALUES ('f644eb266aba4a2186341b708f33kklll', '可验证的数据源类型', 'Y', '2021-01-06', '2021-01-06', '000000', '000000', NULL,
        'VERIFY_DATASOURCE_TYPE', NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`,
                                        `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`,
                                        `NOTE_INFO`, `VERSION_NO`)
VALUES ('tc77d5f788dc45dsfds039d5b1b4fdc01', 'f644eb266aba4a2186341b708f33kklll', 0, 'MYSQL', '0', 'ZH_CN', 'Y',
        '2021-01-06', '2021-01-06', NULL, NULL, NULL, 0);

-- 增加业务域类型对应的数据字典
INSERT IGNORE INTO `t_dictionary_type`(`ID`, `TYPE_NAME`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`,
                                        `MODIFY_USER_CODE`, `PARENT_CODE`, `TYPE_ALIAS`, `IS_LEAF`)
VALUES ('51a309asdsdfads8779fd5fb08200f03u', '业务域类型', 'Y', '2021-01-15', '2021-01-15', '000000', '000000', NULL,
        'domain', NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`,
                                        `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`,
                                        `NOTE_INFO`, `VERSION_NO`)
VALUES ('6438e36d424c4f54ab4f0773d338f9a1', '51a309asdsdfads8779fd5fb08200f03u', 0, '订单域', '0', 'ZH_CN', 'Y',
        '2021-01-15', '2021-01-15', NULL, NULL, NULL, 0);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`,
                                        `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`,
                                        `NOTE_INFO`, `VERSION_NO`)
VALUES ('6438e36d424c4f54ab4f0773d338f9a2', '51a309asdsdfads8779fd5fb08200f03u', 1, '运单域', '1', 'ZH_CN', 'Y',
        '2021-01-15', '2021-01-15', NULL, NULL, NULL, 0);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`,
                                        `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`,
                                        `NOTE_INFO`, `VERSION_NO`)
VALUES ('6438e36d424c4f54ab4f0773d338f9a3', '51a309asdsdfads8779fd5fb08200f03u', 2, '结算域', '2', 'ZH_CN', 'Y',
        '2021-01-15', '2021-01-15', NULL, NULL, NULL, 0);
COMMIT;