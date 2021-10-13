
alter table e_patrol_scene_chain
    add column chainSource INT(2) COMMENT '场景链路来源：0.巡检 1.其他';

alter table e_patrol_exception
    add column data_source INT(2) COMMENT '异常数据来源：0.巡检 1.其他';

alter table e_patrol_scene_chain
    add column eagleId varchar(256) COMMENT '边ID';