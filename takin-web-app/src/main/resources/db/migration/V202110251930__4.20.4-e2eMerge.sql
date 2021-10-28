alter table e_patrol_board
    add column is_merge tinyint(1) COMMENT '是否合并';

ALTER TABLE e_patrol_exception MODIFY activity_id BIGINT(20) NULL;

