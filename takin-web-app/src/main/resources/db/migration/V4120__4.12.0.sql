CREATE TABLE IF NOT EXISTS `t_shadow_mq_consumer`
(
    `id`               bigint(32) NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `topic_group`      varchar(1000) DEFAULT NULL COMMENT 'topic+group，以#号拼接',
    `type`             varchar(20)   DEFAULT NULL COMMENT '白名单类型',
    `application_id`   bigint(19)    DEFAULT NULL COMMENT '应用id',
    `application_name` varchar(200)  DEFAULT NULL COMMENT '应用名称，冗余',
    `status`           int(1)        DEFAULT 1 COMMENT '是否可用(0表示未启用,1表示已启用)',
    `deleted`          int(1)        DEFAULT 0 COMMENT '是否可用(0表示未删除,1表示已删除)',
    `customer_id`      bigint(20)    DEFAULT NULL COMMENT '租户id',
    `user_id`          bigint(20)    DEFAULT NULL COMMENT '用户id',
    `feature`          text          DEFAULT NULL COMMENT '拓展字段',
    `create_time`      datetime      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='影子消费者';

-- 插入开始
DROP PROCEDURE IF EXISTS insert_data;
DELIMITER $$
CREATE PROCEDURE insert_data()
BEGIN

DECLARE count1 INT;
SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'configCenter_bigDataConfig');

IF count1 = 0 THEN

-- 添加菜单
INSERT IGNORE INTO `t_tro_resource`(`parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES (11, 0, 'configCenter_bigDataConfig', '开关配置', NULL, '[\"/api/pradar/switch/list\"]', 7700, '[3]', NULL, NULL, NULL, '2021-02-22 14:22:49', '2021-02-22 14:28:22', 0);

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

UPDATE `t_tro_resource` SET `sequence` = 9001 WHERE `code` = 'debugTool';
UPDATE `t_tro_resource` SET `sequence` = 9000 WHERE `code` = 'debugTool_linkDebug';

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_api_manage' AND COLUMN_NAME = 'user_id');


IF count1 > 0 THEN

-- 初始化入口规则配置表的租户信息，用户信息
update t_application_api_manage set customer_id=1,user_id=1 where customer_id is null;

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
SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = DATABASE() AND TABLE_NAME = 't_application_api_manage' AND COLUMN_NAME = 'IS_AGENT_REGISTE');

IF count = 0 THEN

alter table t_application_api_manage add column `IS_AGENT_REGISTE` tinyint(4) DEFAULT '0' COMMENT '是否有效 0:否;1:是';

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束

DROP PROCEDURE
    IF
    EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN
	DECLARE
count1 INT;
	DECLARE
count2 INT;

	SET count1 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_application_mnt' AND index_name = 'index_identifier_application_name' );

IF count1 > 0 THEN

alter table t_application_mnt drop index `index_identifier_application_name`;

alter table t_application_mnt add unique key `index_identifier_application_name` (`APPLICATION_NAME`,`CUSTOMER_ID`);

END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE
    IF
    EXISTS change_index;

BEGIN;

INSERT IGNORE INTO `t_dictionary_type`(`ID`, `TYPE_NAME`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `PARENT_CODE`, `TYPE_ALIAS`, `IS_LEAF`) VALUES ('f644eb266aba4a2186341b708f33wer2', '影子消费者', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 'SHADOW_CONSUMER', NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('tc77d5f788dc45dsfds039d5b1b4fdc10', 'f644eb266aba4a2186341b708f33wer2', 0, 'ROCKETMQ', 'ROCKETMQ', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('tc77d5f788dc45dsfds039d5b1b4fdc11', 'f644eb266aba4a2186341b708f33wer2', 1, 'KAFKA', 'KAFKA', 'ZH_CN', 'Y', '2021-02-18', '2021-02-18', '000000', '000000', NULL, 0);
COMMIT;