INSERT IGNORE INTO `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('ES_SERVER', '{\"businessNodes\": \"192.168.1.210:9200,192.168.1.193:9200\",\"performanceTestNodes\": \"192.168.1.210:9200,192.168.1.193:9200\"}', '影子ES配置模板', 0, '2021-04-13 21:21:08', '2021-04-13 21:21:11');
INSERT IGNORE INTO `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('Hbase_SERVER', '{\"dataSourceBusiness\":{ \"nodes\":\"192.168.2.241:6379,192.168.2.241:6380\", \"database\":\"aaaa\"},\"dataSourceBusinessPerformanceTest\":{ \"nodes\":\"192.168.1.241:6379,192.168.1.241:6380\", \"password\":\"123456\",  \"database\":\"aaaa\"}}', '影子Hbase配置模板', 0, '2021-04-13 21:23:02', '2021-04-13 21:23:05');

-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count1 INT;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'is_global');

IF count1 = 0 THEN

alter table t_white_list
    add column `is_global` tinyint(1) DEFAULT 1 COMMENT '是否全局生效';

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'is_handwork');

IF count1 = 0 THEN

alter table t_white_list
    add column `is_handwork` tinyint(1) DEFAULT 0 COMMENT '是否手工添加';

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'gmt_create');

IF count1 = 0 THEN

alter table t_white_list
    add column `gmt_create` datetime(3)   DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间';

END IF;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'gmt_modified');

IF count1 = 0 THEN

alter table t_white_list
    add column `gmt_modified` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间';

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束

-- 插入开始
DROP PROCEDURE IF EXISTS insert_data;

DELIMITER $$

CREATE PROCEDURE insert_data()

BEGIN

DECLARE count1 INT;

SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'appWhiteList');

IF count1 = 0 THEN

-- 添加菜单
INSERT INTO `t_tro_resource`( `parent_id`, `type`, `code`, `name`, `alias`, `value`, `sequence`, `action`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES (12, 0, 'appWhiteList', '白名单列表', NULL, '[\"/api/whitelist/list\"]', 7900, '[]', NULL, NULL, NULL, '2021-04-14 11:39:00', '2021-04-14 11:39:51', 0);


END IF;

END $$

DELIMITER ;

CALL insert_data();

DROP PROCEDURE IF EXISTS insert_data;
-- 插入结束


-- 数据源管理
CREATE TABLE IF NOT EXISTS `t_whitelist_effective_app`
(
    `id`          bigint(11)   NOT NULL AUTO_INCREMENT,
    `wlist_id` bigint(20)  NOT NULL COMMENT '白名单id',
    `interface_name` varchar(1024) NOT NULL COMMENT '接口名',
    `EFFECTIVE_APP_NAME` varchar(1024)  NOT NULL COMMENT '生效应用',
    `customer_id` bigint(20)   DEFAULT NULL COMMENT '租户id',
    `user_id`     bigint(20)   DEFAULT NULL COMMENT '用户id',
    `gmt_create`    datetime(3)   DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
    `gmt_modified`  datetime(3)   DEFAULT CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    `is_deleted`    tinyint(1)    DEFAULT 0 COMMENT '软删',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;