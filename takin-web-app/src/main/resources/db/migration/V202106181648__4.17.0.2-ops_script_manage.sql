CREATE TABLE IF NOT EXISTS `t_ops_script_manage`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `name`        varchar(20) DEFAULT NULL COMMENT '脚本名称',
    `script_type` tinyint(1) DEFAULT NULL COMMENT '来源于字典。脚本类型：1=影子库表创建脚本 2=基础数据准备脚本 3=铺底数据脚本 4=影子库表清理脚本 5=缓存预热脚本',
    `customer_id` varchar(20) DEFAULT NULL COMMENT '租户ID',
    `user_id`     varchar(20) DEFAULT NULL COMMENT '用户ID',
    `status`      tinyint(1) NOT NULL DEFAULT '0' COMMENT '执行状态 0=待执行,1=执行中,2=已执行',
    `is_deleted`   tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
    `gmt_create`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`  datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4  COMMENT='运维脚本主表';

CREATE TABLE IF NOT EXISTS `t_ops_script_batch_no`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `batch_no`      longtext DEFAULT NULL COMMENT '批次号',
    `ops_script_id` bigint(20) NOT NULL COMMENT '运维脚本ID',
    `is_deleted`     tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
    `gmt_create`    datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`    datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4  COMMENT='运维脚本批次号表';


CREATE TABLE IF NOT EXISTS `t_ops_script_execute_result`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
    `batch_id`      bigint(20) DEFAULT NULL COMMENT '批次号id',
    `ops_script_id` bigint(20) NOT NULL COMMENT '运维脚本ID',
    `log_file_path` longtext DEFAULT NULL COMMENT '日志文件路径',
    `upload_id` longtext CHARACTER SET utf8 COLLATE utf8_bin COMMENT 'uploadId 用于删除本地文件',
    `excutor_id`    bigint(20) DEFAULT NULL COMMENT '执行人id',
    `execute_time`  datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '执行时间',
    `is_deleted`     tinyint(1) NOT NULL DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
    `gmt_create`    datetime    DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_update`    datetime    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4  COMMENT='运维脚本执行结果';

CREATE TABLE IF NOT EXISTS `t_ops_script_file` (
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
     PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='运维脚本文件';


INSERT IGNORE INTO `t_dictionary_type` (`ID`,`TYPE_NAME`,`ACTIVE`,`CREATE_TIME`,`MODIFY_TIME`,`CREATE_USER_CODE`,`MODIFY_USER_CODE`,`PARENT_CODE`,`TYPE_ALIAS`,`IS_LEAF`) VALUES ('20210616opsscript0001','运维脚本类型','Y','2021-06-16',NULL,NULL,NULL,NULL,'OPS_SCRIPT_TYPE',NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`,`DICT_TYPE`,`VALUE_ORDER`,`VALUE_NAME`,`VALUE_CODE`,`LANGUAGE`,`ACTIVE`,`CREATE_TIME`,`MODIFY_TIME`,`CREATE_USER_CODE`,`MODIFY_USER_CODE`,`NOTE_INFO`,`VERSION_NO`) VALUES ('2021060319173opsscripttype1001','20210616opsscript0001',1,'影子库表创建脚本','1','ZH_CN','Y','2021-06-16',NULL,NULL,NULL,NULL,NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`,`DICT_TYPE`,`VALUE_ORDER`,`VALUE_NAME`,`VALUE_CODE`,`LANGUAGE`,`ACTIVE`,`CREATE_TIME`,`MODIFY_TIME`,`CREATE_USER_CODE`,`MODIFY_USER_CODE`,`NOTE_INFO`,`VERSION_NO`) VALUES ('2021060319173opsscripttype1002','20210616opsscript0001',2,'基础数据准备脚本','2','ZH_CN','Y','2021-06-16',NULL,NULL,NULL,NULL,NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`,`DICT_TYPE`,`VALUE_ORDER`,`VALUE_NAME`,`VALUE_CODE`,`LANGUAGE`,`ACTIVE`,`CREATE_TIME`,`MODIFY_TIME`,`CREATE_USER_CODE`,`MODIFY_USER_CODE`,`NOTE_INFO`,`VERSION_NO`) VALUES ('2021060319173opsscripttype1003','20210616opsscript0001',3,'铺底数据脚本','3','ZH_CN','Y','2021-06-16',NULL,NULL,NULL,NULL,NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`,`DICT_TYPE`,`VALUE_ORDER`,`VALUE_NAME`,`VALUE_CODE`,`LANGUAGE`,`ACTIVE`,`CREATE_TIME`,`MODIFY_TIME`,`CREATE_USER_CODE`,`MODIFY_USER_CODE`,`NOTE_INFO`,`VERSION_NO`) VALUES ('2021060319173opsscripttype1004','20210616opsscript0001',4,'影子库表清理脚本','4','ZH_CN','Y','2021-06-16',NULL,NULL,NULL,NULL,NULL);
INSERT IGNORE INTO `t_dictionary_data` (`ID`,`DICT_TYPE`,`VALUE_ORDER`,`VALUE_NAME`,`VALUE_CODE`,`LANGUAGE`,`ACTIVE`,`CREATE_TIME`,`MODIFY_TIME`,`CREATE_USER_CODE`,`MODIFY_USER_CODE`,`NOTE_INFO`,`VERSION_NO`) VALUES ('2021060319173opsscripttype1005','20210616opsscript0001',5,'缓存预热脚本','5','ZH_CN','Y','2021-06-16',NULL,NULL,NULL,NULL,NULL);

-- 更新字段
DROP PROCEDURE IF EXISTS update_field;

DELIMITER $$

CREATE PROCEDURE update_field()

BEGIN

DECLARE count INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_plugins_config' AND COLUMN_NAME = 'config_desc');

IF count > 0 THEN

UPDATE t_application_plugins_config SET config_desc = 'redis影子key有效期默认与业务key有效期一致。若想提前清理影子key, 也可自定义配置影子key的有效期。若设置时间比业务key有效期长，不生效，仍以业务key有效期为准。';

END IF;

END $$

DELIMITER ;

CALL update_field();

DROP PROCEDURE IF EXISTS update_field;

-- 插入开始
DROP PROCEDURE IF EXISTS insert_data;

DELIMITER $$

CREATE PROCEDURE insert_data()

BEGIN

DECLARE count1 INT;
DECLARE count2 INT;
DECLARE count3 INT;
DECLARE testId BIGINT;
DECLARE parentId BIGINT;

SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` where `name`='脚本管理');

IF count1 > 0 THEN

    SET testId = (SELECT id FROM `t_tro_resource` where `name`='脚本管理' LIMIT 1);

    SET count2 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'scriptManage');

    IF count2 = 0 THEN

        INSERT INTO `t_tro_resource` (`id`,`parent_id`,`type`,`code`,`name`,`alias`,`value`,`sequence`,`action`,`features`,`customer_id`,`remark`,`create_time`,`update_time`,`is_deleted`) VALUES (NULL,NULL,0,'scriptManage','脚本管理',NULL,'',16000,'[]',NULL,NULL,NULL,NOW(),NOW(),0);


        SET parentId = (select id from t_tro_resource where `name`='脚本管理' ORDER BY create_time desc LIMIT 1);

UPDATE `t_tro_resource` SET `parent_id`=parentId,`type`=0,`code`='scriptManage_test',`name`='测试脚本' WHERE `id`=testId;

SET count3 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'scriptManage_ops');

        IF count3 = 0 THEN

            INSERT INTO `t_tro_resource` (`id`,`parent_id`,`type`,`code`,`name`,`alias`,`value`,`sequence`,`action`,`features`,`customer_id`,`remark`,`create_time`,`update_time`,`is_deleted`) VALUES (NULL,parentId,0,'scriptManage_ops','运维脚本',NULL,'[\"/api/opsScriptManage\"]',15000,'[2,3,4]',NULL,NULL,NULL,NOW(),NOW(),0);


END IF;


END IF;

END IF;



END $$

DELIMITER ;

CALL insert_data();

DROP PROCEDURE IF EXISTS insert_data;
-- 插入结束

