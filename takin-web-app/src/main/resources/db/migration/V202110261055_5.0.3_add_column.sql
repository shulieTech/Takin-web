/* 添加字段 */
DELIMITER $$
DROP PROCEDURE IF EXISTS `add_column` $$
CREATE PROCEDURE add_column()
BEGIN
    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene_link_relate' AND column_name = 'script_identification')
    THEN
        ALTER TABLE t_scene_link_relate
            ADD script_identification VARCHAR(255) COMMENT ' 脚本请求路径标识';
    END IF;

    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene_link_relate' AND column_name = 'script_xpath_md5')
    THEN
        ALTER TABLE t_scene_link_relate
            ADD script_xpath_md5 VARCHAR(64) COMMENT '脚本请求xpath的MD5';
    END IF;

    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene_link_relate' AND column_name = 'tenant_id')
        THEN
    ALTER TABLE t_scene_link_relate
        ADD tenant_id bigint(20) COMMENT '租户id';
    END IF;

    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene_link_relate' AND column_name = 'env_code')
        THEN
    ALTER TABLE t_scene_link_relate
        ADD env_code VARCHAR(255) COMMENT '环境code';
    END IF;

    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene' AND column_name = 'type')
    THEN
        ALTER TABLE t_scene
            ADD type tinyint(4) COMMENT '场景类型，标识1为jmeter上传，默认0';
    END IF;

    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene' AND column_name = 'script_jmx_node')
    THEN
        ALTER TABLE t_scene
            ADD script_jmx_node text COMMENT '存储树状结构';
    END IF;

    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene' AND column_name = 'script_deploy_id')
    THEN
        ALTER TABLE t_scene
            ADD script_deploy_id bigint(20) COMMENT '脚本实例id';
    END IF;

    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene' AND column_name = 'link_relate_num')
    THEN
        ALTER TABLE t_scene
            ADD link_relate_num int(11) COMMENT '关联节点数';
    END IF;

    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_scene' AND column_name = 'total_node_num')
    THEN
        ALTER TABLE t_scene
            ADD total_node_num int(11) COMMENT '脚本总节点数';
    END IF;



# 文件MD5
    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_file_manage' AND column_name = 'md5')
    THEN
        ALTER TABLE t_file_manage
            ADD md5 int(11) COMMENT '文件MD5值';
    END IF;


    IF NOT EXISTS(SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = 't_script_manage' AND column_name = 'm_version')
    THEN
        ALTER TABLE t_script_manage
            ADD m_version int(11) COMMENT '脚本管理-版本';
    END IF;
END $$
DELIMITER ;
CALL add_column;
