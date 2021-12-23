/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
	IF NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_tro_resource' AND column_name='IS_SUPER')
	THEN
ALTER TABLE t_tro_resource ADD IS_SUPER tinyint(1) default false COMMENT '只有超级管理员才能看到';
END IF;
END $$
DELIMITER ;
CALL add_column;


alter table t_tro_user_dept_relation  modify column user_id bigint;

alter table t_tro_user_dept_relation  modify column dept_id bigint;

drop index idx_dept_id on t_tro_user_dept_relation;
drop index idx_user_id on t_tro_user_dept_relation ;
create index idx_dept_id
    on t_tro_user_dept_relation (dept_id);
create index idx_user_id
    on t_tro_user_dept_relation (user_id);

alter table t_tro_role_user_relation  modify column user_id bigint;

alter table t_tro_role_user_relation  modify column role_id bigint;

drop index idx_role_id on t_tro_role_user_relation;
drop index idx_user_id on t_tro_role_user_relation ;

create index idx_role_id
    on t_tro_role_user_relation (role_id);

create index idx_user_id
    on t_tro_role_user_relation (user_id);


update t_tro_resource set IS_SUPER = 1 where code in('configCenter_middlewareManage','configCenter_bigDataConfig','admins_admin','flowAccount')
