-- 插入开始
DROP PROCEDURE IF EXISTS insert_data;

DELIMITER $$

CREATE PROCEDURE insert_data()

BEGIN

DECLARE count1 INT;

SET count1 = (SELECT COUNT(*) FROM `t_tro_resource` WHERE `code` = 'patrolBoard');

IF count1 = 0 THEN

INSERT INTO `t_tro_resource`
(`parent_id`, `type`, `alias`, `features`, `customer_id`, `remark`, `create_time`, `update_time`, `is_deleted`, `code`, `name`, `value`, `sequence`, `action`)
VALUES (NULL, 0, NULL, NULL, NULL, NULL, NOW(), NOW(), 0, 'patrolBoard', '巡检看板', '[\"/api/patrol/board\"]', 9999, '[2,3,4]');


END IF;



END $$

DELIMITER ;

CALL insert_data();

DROP PROCEDURE IF EXISTS insert_data;
-- 插入结束

-- 更新数据开始
DROP PROCEDURE IF EXISTS update_data;

DELIMITER $$

CREATE PROCEDURE update_data()

BEGIN

DECLARE count3 INT;

SET count3 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource' AND COLUMN_NAME = 'action' AND COLUMN_NAME = 'code');


IF count3 > 0 THEN


UPDATE `t_tro_resource` SET `action` = '[2,3,4,5]' WHERE code = 'patrolManage';
UPDATE `t_tro_resource` SET `action` = '[3]' WHERE code = 'bottleneckConfig';
UPDATE `t_tro_resource` SET `action` = '[]' WHERE code = 'patrolScreen';
UPDATE `t_tro_resource` SET `action` = '[]' WHERE code = 'bottleneckTable';
UPDATE `t_tro_resource` SET `action` = '[2,3,4]', name='瓶颈通知' WHERE code = 'exceptionNoticeManage';


END IF;

SET count3 = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_tro_resource' AND COLUMN_NAME = 'action' AND COLUMN_NAME = 'code' AND COLUMN_NAME = 'name');


IF count3 > 0 THEN


UPDATE `t_tro_resource` SET `action` = '[2,3,4]', `name`='瓶颈通知' WHERE code = 'exceptionNoticeManage';

END IF;

END $$

DELIMITER ;

CALL update_data();

DROP PROCEDURE IF EXISTS update_data;
-- 更新数据结束