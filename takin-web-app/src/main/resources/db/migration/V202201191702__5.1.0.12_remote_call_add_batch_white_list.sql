ALTER TABLE `t_rpc_config_template`
    ADD  `return_fix_mock_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持固定值返回mock;0:不支持;1:支持',
		ADD  `fix_return_mock` text CHARACTER SET utf8 COMMENT '固定值转发mock文本';

ALTER TABLE `t_http_client_config_template`
    ADD  `return_fix_mock_enable` tinyint(3) DEFAULT '0' COMMENT '是否支持固定值返回mock;0:不支持;1:支持',
		ADD  `fix_return_mock` text CHARACTER SET utf8 COMMENT '固定值转发mock文本';

update t_rpc_config_template set return_fix_mock_enable = 1 where name = 'feign';
