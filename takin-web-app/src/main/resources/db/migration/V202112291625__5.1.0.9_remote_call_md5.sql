/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
	IF NOT EXISTS( SELECT * FROM  information_schema.columns WHERE table_schema= DATABASE() AND table_name='t_app_remote_call' AND column_name='md5')
	THEN
ALTER TABLE t_app_remote_call
    ADD COLUMN `md5` varchar(50)  NOT NULL default '0' COMMENT '应用名，接口名称，接口类型，租户id,环境code求md5';
END IF;
END $$
DELIMITER ;
CALL add_column;

DELETE FROM t_app_remote_call
WHERE id NOT IN (
    SELECT t.id FROM(
                        SELECT MIN( id ) AS id FROM t_app_remote_call
                        GROUP BY
                            APP_NAME,
                            interface_name,
                            interface_type,
                            APPLICATION_ID,
                            interface_type,
                            type
                    ) t
);

delete from t_app_remote_call where md5 in(select md5 from (select md5,count(1) as count from t_app_remote_call  group by md5) a where a.count > 1)

ALTER TABLE t_app_remote_call
    ADD UNIQUE KEY `unique_idx_md5` (`md5`) USING BTREE;


