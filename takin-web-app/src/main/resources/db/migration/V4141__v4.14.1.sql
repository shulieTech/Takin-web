-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;
DELIMITER $$
CREATE PROCEDURE change_field()
BEGIN

DECLARE count1 INT;

SET count1 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_whitelist_effective_app' AND COLUMN_NAME = 'type');

IF count1 = 0 THEN

ALTER TABLE `t_whitelist_effective_app` ADD COLUMN `type` VARCHAR(20) NULL DEFAULT NULL COMMENT '白名单类型' AFTER `INTERFACE_NAME`;

END IF;

END $$
DELIMITER ;
CALL change_field();
DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束

-- kafka集群模板
INSERT IGNORE INTO `t_base_config`(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`) VALUES ('KAFKA_CLUSTER', '{\"key\": \"PT_业务主题\",\"topic\": \"PT_业务主题\",\"topicTokens\": \"PT_业务主题:影子主题token\",\"group\": \"\",\"systemIdToken\": \"\"}', '影子kafka集群配置模板', 0, NOW(), NOW());