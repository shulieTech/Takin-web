CREATE TABLE IF NOT EXISTS `t_app_remote_call` (
                                     `ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                     `interface_name` varchar(1024) CHARACTER SET utf8 DEFAULT NULL COMMENT '接口名称',
                                     `interface_type` tinyint(4) DEFAULT NULL COMMENT '接口类型',
                                     `server_app_name` varchar(2048) CHARACTER SET utf8 DEFAULT NULL COMMENT '服务端应用名',
                                     `APPLICATION_ID`  bigint(20)  NOT NULL  COMMENT '应用id',
                                     `APP_NAME`  varchar(500) CHARACTER SET utf8  DEFAULT NULL  COMMENT '应用name',
                                     `type` tinyint(4)  DEFAULT 0 COMMENT '配置类型，0：未配置，1：白名单配置;2：返回值mock;3:转发mock',
                                     `mock_return_value` varchar(1024) CHARACTER SET utf8 DEFAULT NULL COMMENT 'mock返回值',
                                     `CUSTOMER_ID` bigint(20) DEFAULT NULL COMMENT '租户id',
                                     `USER_ID` bigint(20) DEFAULT NULL COMMENT '用户id',
                                     `IS_DELETED` tinyint(4) DEFAULT 0 COMMENT '是否有效 0:有效;1:无效',
                                     `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
                                     `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
                                     `is_synchronize` tinyint(1) DEFAULT 0 COMMENT '是否同步',
                                     PRIMARY KEY (`ID`) USING BTREE,
                                     KEY `T_REMOTE_CALL_INDEX1` (`CUSTOMER_ID`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='远程调用表';

-- 字段添加
DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

DECLARE count1 INT;
DECLARE count2 INT;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = DATABASE() AND TABLE_NAME = 't_business_link_manage_table' AND COLUMN_NAME = 'TYPE');

SET count2 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema = DATABASE() AND TABLE_NAME = 't_business_link_manage_table' AND COLUMN_NAME = 'BIND_BUSINESS_ID');

IF count1 = 0 THEN

ALTER TABLE `t_business_link_manage_table`ADD COLUMN `TYPE` tinyint(4) NOT NULL DEFAULT 0 COMMENT '业务活动类型';

END IF;

IF count2 = 0 THEN

ALTER TABLE `t_business_link_manage_table`ADD COLUMN `BIND_BUSINESS_ID` bigint(20)  DEFAULT NULL  COMMENT '绑定业务活动id';

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;
-- 字段添加结束

BEGIN;
INSERT IGNORE INTO `t_dictionary_type`(`ID`, `TYPE_NAME`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `PARENT_CODE`, `TYPE_ALIAS`, `IS_LEAF`) VALUES ('2021060319173remotecall0001', '远程调用接口类型', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, 'REMOTE_CALL_TYPE', NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('2021060319173remotecallnode1001', '2021060319173remotecall0001', 0, 'HTTP', '0', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('2021060319173remotecallnode1002', '2021060319173remotecall0001', 0, 'DUBBO', '1', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_dictionary_data`(`ID`, `DICT_TYPE`, `VALUE_ORDER`, `VALUE_NAME`, `VALUE_CODE`, `LANGUAGE`, `ACTIVE`, `CREATE_TIME`, `MODIFY_TIME`, `CREATE_USER_CODE`, `MODIFY_USER_CODE`, `NOTE_INFO`, `VERSION_NO`) VALUES ('2021060319173remotecallnode1003', '2021060319173remotecall0001', 0, 'FEIGN', '2', 'ZH_CN', 'Y', '2021-06-03', NULL, NULL, NULL, NULL, NULL);
INSERT IGNORE INTO `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('REMOTE_CALL_ABLE_CONFIG', '{\"3\": \"\"}', '远程调用配置类型可用性配置', 0, '2021-06-03 16:06:14', '2021-06-10 19:52:37');
COMMIT;