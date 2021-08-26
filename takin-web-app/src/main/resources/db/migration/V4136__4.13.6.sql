
DROP PROCEDURE IF EXISTS change_index;

DELIMITER $$
CREATE PROCEDURE change_index ()
BEGIN
DECLARE count1 INT;


	SET count1 = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_application_ds_manage' AND index_name = 'idx_app_id' );

IF count1 = 0 THEN

ALTER TABLE `t_application_ds_manage` ADD INDEX `idx_app_id`(`APPLICATION_ID`) USING BTREE;


END IF;

END $$

DELIMITER ;
CALL change_index ();
DROP PROCEDURE
    IF
    EXISTS change_index;


DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_black_list' AND COLUMN_NAME = 'value');

IF count = 0 THEN

ALTER TABLE `t_black_list` ADD COLUMN `value` varchar(1024) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '黑名单数据' AFTER `type`;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_link_guard' AND COLUMN_NAME = 'is_deleted');

IF count > 0 THEN

ALTER TABLE `t_link_guard` MODIFY COLUMN `is_deleted` tinyint(4) UNSIGNED NULL DEFAULT 0 COMMENT '是否有效 0:有效;1:无效' AFTER `update_time`;

END IF;

	SET count = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_link_guard' AND index_name = 'idx_app_id' );

IF count = 0 THEN

ALTER TABLE `t_link_guard` ADD INDEX `idx_app_id`(`application_id`) USING BTREE;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_shadow_job_config' AND COLUMN_NAME = 'is_deleted');

IF count > 0 THEN

ALTER TABLE `t_shadow_job_config` MODIFY COLUMN `is_deleted` tinyint(1) UNSIGNED NULL DEFAULT 0 COMMENT '是否删除 0-未删除、1-已删除' AFTER `user_id`;

END IF;

	SET count = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_shadow_job_config' AND index_name = 'idx_app_id' );

IF count = 0 THEN
ALTER TABLE `t_shadow_job_config` ADD INDEX `idx_app_id`(`application_id`) USING BTREE;
END IF;

	SET count = ( SELECT COUNT(*) FROM information_schema.statistics WHERE table_schema = DATABASE () AND TABLE_NAME = 't_shadow_mq_consumer' AND index_name = 'idx_app_id' );

IF count = 0 THEN
ALTER TABLE `t_shadow_mq_consumer` ADD INDEX `idx_app_id`(`application_id`) USING BTREE;
END IF;


SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'is_deleted');

IF count = 0 THEN

ALTER TABLE `t_white_list` ADD COLUMN `is_deleted` tinyint(1) NULL DEFAULT 0 COMMENT '是否已经删除 0:未删除;1:删除' AFTER `JOB_INTERVAL`;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'gmt_create');

IF count = 0 THEN

ALTER TABLE `t_white_list` ADD COLUMN `gmt_create` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间' AFTER `is_deleted`;

END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_white_list' AND COLUMN_NAME = 'gmt_modified');

IF count = 0 THEN

ALTER TABLE `t_white_list` ADD COLUMN `gmt_modified` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '修改时间' AFTER `gmt_create`;

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;
