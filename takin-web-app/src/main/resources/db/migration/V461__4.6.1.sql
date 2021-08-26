-- 新增脚本执行表
CREATE TABLE IF NOT EXISTS `t_script_execute_result` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `script_deploy_id` bigint(20) NOT NULL COMMENT '实例id',
  `script_id` bigint(20) NOT NULL COMMENT '脚本id',
  `script_version` int(11) NOT NULL DEFAULT '0',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) COMMENT '创建时间',
  `executor` varchar(256) DEFAULT NULL COMMENT '执行人',
  `success` tinyint(1) DEFAULT NULL COMMENT '执行结果',
  `result` longtext  DEFAULT NULL COMMENT '执行结果',
  PRIMARY KEY (`id`),
  KEY `idx_script_deploy_id` (`script_deploy_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 脚本实例表添加操作信息
-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_script_manage_deploy' AND COLUMN_NAME = 'create_user_id');

IF count = 0 THEN

ALTER TABLE `t_script_manage_deploy` ADD COLUMN `create_user_id` bigint(0) NULL COMMENT '操作人id' AFTER `status`;


END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_script_manage_deploy' AND COLUMN_NAME = 'description');

IF count = 0 THEN

ALTER TABLE `t_script_manage_deploy` ADD COLUMN `description` varchar(1024) NULL COMMENT '描述';


END IF;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

WHERE table_schema = DATABASE() AND TABLE_NAME = 't_script_manage_deploy' AND COLUMN_NAME = 'create_user_name');

IF count = 0 THEN

ALTER TABLE `t_script_manage_deploy` ADD COLUMN `create_user_name` varchar(64) NULL COMMENT '操作人名称' AFTER `create_user_id`;

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束

-- 添加数据 t_base_config
INSERT IGNORE INTO `t_base_config`
(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`)
VALUES ('DUMP_MEMORY_FILE_PATH', '/data/dump', 'dump内存文件路径配置', 1, now(),now());

-- 修改字段开始
DROP PROCEDURE IF EXISTS change_field;

DELIMITER $$

CREATE PROCEDURE change_field()

BEGIN

DECLARE count INT;

SET count = (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS

-- 修改数据库字段大小
WHERE table_schema = DATABASE() AND TABLE_NAME = 't_base_config' AND COLUMN_NAME = 'CONFIG_VALUE');

IF count > 0 THEN

alter table t_base_config modify CONFIG_VALUE LONGTEXT;

END IF;

END $$

DELIMITER ;

CALL change_field();

DROP PROCEDURE IF EXISTS change_field;
-- 修改字段结束


-- 添加数据 t_base_config
INSERT IGNORE INTO `t_base_config`
(`CONFIG_CODE`, `CONFIG_VALUE`, `CONFIG_DESC`, `USE_YN`, `CREATE_TIME`, `UPDATE_TIME`)
VALUES ('SHELL_SCRIPT_DOWNLOAD_SAMPLE', '[
	{
    	"name":"影子库结构脚本",
        "url":"/opt/tro/script/shell/sample/ddl.sh"
    },
    {
    	"name":"数据库清理脚本",
        "url":"/opt/tro/script/shell/sample/clean.sh"
    },
    {
    	"name":"基础数据准备脚本",
        "url":"/opt/tro/script/shell/sample/ready.sh"
    },
    {
    	"name":"铺底数据脚本",
        "url":"/opt/tro/script/shell/sample/basic.sh"
    },
    {
    	"name":"缓存预热脚本",
        "url":"/opt/tro/script/shell/sample/cache.sh"
    }

]', 'shell脚本样例下载配置', 1, now(),now());

CREATE TABLE IF NOT EXISTS `t_scene_scheduler_task` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scene_id` bigint(20) NOT NULL COMMENT '场景id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `content` text COMMENT '启动场景参数',
  `is_executed` tinyint(2) DEFAULT '0' COMMENT '0：待执行，1:执行中；2:已执行',
  `execute_time` datetime NOT NULL COMMENT '压测场景定时执行时间',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '状态 0: 正常 1： 删除',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE IF NOT EXISTS `t_scene_tag_ref` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `scene_id` bigint(20) NOT NULL COMMENT '场景发布id',
  `tag_id` bigint(20) NOT NULL COMMENT '标签id',
  `gmt_create` datetime(3) DEFAULT CURRENT_TIMESTAMP(3),
  `gmt_update` datetime(3) DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `index_sceneId_tagId` (`scene_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
