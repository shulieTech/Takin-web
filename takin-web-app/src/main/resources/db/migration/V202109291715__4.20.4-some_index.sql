-- t_application_node_probe 添加索引
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF NOT EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 't_application_node_probe' AND index_name = 'idx_an_ai_cid') THEN

ALTER TABLE `trodb`.`t_application_node_probe`
    ADD INDEX `idx_an_ai_cid`(`application_name`, `agent_id`, `customer_id`);

END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;

-- t_agent_config 添加索引
DROP PROCEDURE IF EXISTS change_index;
DELIMITER $$
CREATE PROCEDURE change_index () BEGIN

IF NOT EXISTS (SELECT * FROM information_schema.statistics WHERE table_schema = DATABASE() AND table_name = 't_agent_config' AND index_name = 'idx_zh_key_pn_uak') THEN

ALTER TABLE `trodb`.`t_agent_config`
    ADD INDEX `idx_zh_key_pn_uak`(`zh_key`, `project_name`, `user_app_key`);

END IF;

END $$
DELIMITER ;
CALL change_index ();
DROP PROCEDURE IF EXISTS change_index;