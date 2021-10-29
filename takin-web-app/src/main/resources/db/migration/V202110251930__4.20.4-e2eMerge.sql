alter table e_patrol_board
    add column is_merge tinyint(1) COMMENT '是否合并';

ALTER TABLE e_patrol_exception MODIFY activity_id BIGINT(20) NULL;

ALTER TABLE t_link_manage_table ADD COLUMN `persistence` TINYINT DEFAULT 1;

ALTER TABLE t_business_link_manage_table ADD COLUMN `persistence` TINYINT DEFAULT 1;

