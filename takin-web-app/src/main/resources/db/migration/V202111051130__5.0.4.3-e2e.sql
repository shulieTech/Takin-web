alter table e_patrol_board
    add column is_horizontally tinyint(1) COMMENT '是否横向';

alter table e_patrol_exception
    MODIFY edge_id varchar(2048) COMMENT '边ID';

alter table e_patrol_scene_chain
    MODIFY eagleId varchar(2048) COMMENT '边ID';