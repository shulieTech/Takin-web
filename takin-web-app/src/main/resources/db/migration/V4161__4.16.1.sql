-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception_notice_config' AND COLUMN_NAME = 'content_type');

IF count = 0 THEN

alter table e_patrol_exception_notice_config add column content_type varchar(50) DEFAULT NULL COMMENT '请求参数类型' after max_number_of_times;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception_notice_config' AND COLUMN_NAME = 'template');

IF count = 0 THEN

alter table e_patrol_exception_notice_config add column template text DEFAULT NULL COMMENT '推送模版' after content_type;

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束

INSERT IGNORE INTO`t_dictionary_type`(`ID`, `TYPE_NAME`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `PARENT_CODE`, `TYPE_ALIAS`, `IS_LEAF`) VALUES ('91a309asdsdfads2323fd5fb08200ikld', '推送内容模板参数', 'Y', '2021-05-14', '2021-05-14', '000000', '000000', NULL, 'PUSH_MESSAGE_PARAM', NULL);
INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('iodld5f788dc45dsfds098d5b1b4fcv01', '91a309asdsdfads2323fd5fb08200ikld', 1, '<slp>巡检面板</slp>', 'BOARD_NAME', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);
INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('iodld5f788dc45dsfds098d5b1b4fcv02', '91a309asdsdfads2323fd5fb08200ikld', 2, '<slp>巡检场景</slp>', 'PATROL_SCENE_NAME', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);
INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('iodld5f788dc45dsfds098d5b1b4fcv03', '91a309asdsdfads2323fd5fb08200ikld', 3, '<slp>巡检任务</slp>', 'PATROL_TASK', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);
INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('iodld5f788dc45dsfds098d5b1b4fcv04', '91a309asdsdfads2323fd5fb08200ikld', 4, '<slp>瓶颈结果</slp>', 'EXCEPTION_TYPE', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);
INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('iodld5f788dc45dsfds098d5b1b4fcv05', '91a309asdsdfads2323fd5fb08200ikld', 5, '<slp>瓶颈程度</slp>', 'EXCEPTION_LEVEL', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);
INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('iodld5f788dc45dsfds098d5b1b4fcv06', '91a309asdsdfads2323fd5fb08200ikld', 6, '<slp>瓶颈ID</slp>', 'EXCEPTION_ID', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);
INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('iodld5f788dc45dsfds098d5b1b4fcv07', '91a309asdsdfads2323fd5fb08200ikld', 7, '<slp>持续时间</slp>', 'LAST_TIME', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);
INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('iodld5f788dc45dsfds098d5b1b4fcv08', '91a309asdsdfads2323fd5fb08200ikld', 8, '<slp>瓶颈详情</slp>', 'DETAIL_URL', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);
INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('iodld5f788dc45dsfds098d5b1b4fcv09', '91a309asdsdfads2323fd5fb08200ikld', 9, '<slp>发现时间</slp>', 'START_TIME', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);

-- 字段修改开始
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_scene_check' AND COLUMN_NAME = 'error_detail');

IF count > 0 THEN

alter table e_patrol_scene_check modify column `error_detail` varchar(2048) DEFAULT NULL COMMENT '错误详情';

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;
-- 字段修改结束

INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('202104281025590e2e02000000000003', '202104281025590e2e00000000000002', 0, '全部', '0', 'ZH_CN', 'Y', '2021-05-19', NULL, NULL, NULL, NULL, 0);
UPDATE `t_dictionary_data` SET `DICT_TYPE` = '202104281025590e2e00000000000002', `VALUE_ORDER` = 1, `VALUE_NAME` = '卡慢', `VALUE_CODE` = '1', `LANGUAGE` = 'ZH_CN', `ACTIVE` = 'Y', `CREATE_TIME` = '2021-04-23', `MODIFY_TIME` = NULL, `CREATE_USER_CODE` = NULL, `MODIFY_USER_CODE` = NULL, `NOTE_INFO` = NULL, `VERSION_NO` = NULL WHERE `ID` = '202104281025590e2e02000000000001';
UPDATE `t_dictionary_data` SET `DICT_TYPE` = '202104281025590e2e00000000000002', `VALUE_ORDER` = 2, `VALUE_NAME` = '接口异常', `VALUE_CODE` = '2', `LANGUAGE` = 'ZH_CN', `ACTIVE` = 'Y', `CREATE_TIME` = '2021-04-23', `MODIFY_TIME` = NULL, `CREATE_USER_CODE` = NULL, `MODIFY_USER_CODE` = NULL, `NOTE_INFO` = NULL, `VERSION_NO` = NULL WHERE `ID` = '202104281025590e2e02000000000002';
INSERT IGNORE INTO`t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('202104281025590e2e04000000000003', '202104281025590e2e00000000000005', 0, '自定义', '2', 'ZH_CN', 'Y', '2021-04-23', NULL, NULL, NULL, NULL, NULL);

-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count1 INT;
DECLARE count2 INT;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'business_id');

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'chain_id');

IF count1 > 0 THEN

	IF count2 = 0 THEN

alter  table `e_patrol_exception` change business_id chain_id bigint not null comment '任务标识';

END IF;

END IF;


SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'business_name');

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'activity_name');

IF count1 > 0 THEN

	IF count2 = 0 THEN

alter  table `e_patrol_exception` change business_name activity_name varchar(100) not null comment '任务名称';

END IF;

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'business_type');

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'patrol_type');

IF count1 > 0 THEN

	IF count2 = 0 THEN

alter  table `e_patrol_exception` change business_type patrol_type int not null comment '任务类型';

END IF;

END IF;


SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'business_rpc_type');

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'rpc_type');

IF count1 > 0 THEN

	IF count2 = 0 THEN

alter  table `e_patrol_exception` change business_rpc_type rpc_type varchar(100) null comment '业务入口';

END IF;

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'activity_id');

IF count1 = 0 THEN
alter  table `e_patrol_exception` add column activity_id bigint null comment '业务活动';
END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'board_id');

IF count1 = 0 THEN
alter  table `e_patrol_exception` DROP board_id;
END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 'e_patrol_exception' AND COLUMN_NAME = 'board_name');

IF count1 = 0 THEN

alter  table `e_patrol_exception` DROP board_name;

END IF;


END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束
