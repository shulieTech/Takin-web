alter table `trodb`.`t_scene` add index idx_deploy_id(`script_deploy_id`);
alter table `trodb`.`t_script_file_ref` add index idx_deploy_id(`script_deploy_id`);

alter table `trodb`.`t_report_summary` add column `mock_count` int not null default 0 comment '挡板数量';

CREATE TABLE `trodb`.`t_report_mock` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_id` bigint(20) NOT NULL,
  `start_time` datetime NOT NULL,
  `end_time` datetime NOT NULL,
  `app_name` varchar(256) NOT NULL,
  `mock_name` varchar(256) NOT NULL,
  `mock_type` varchar(128) NOT NULL COMMENT '挡板 返回值mock groovy脚本mock 转发mock',
  `mock_script` text NOT NULL COMMENT 'mock脚本',
  `mock_status` varchar(16) NOT NULL COMMENT '启用 禁用',
  `failure_count` bigint(20) NOT NULL DEFAULT '0',
  `success_count` bigint(20) NOT NULL DEFAULT '0',
  `avg_rt` decimal(10,2) NOT NULL COMMENT '平均RT',
  `tenant_id` bigint(20) DEFAULT NULL,
  `env_code` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

alter table `trodb`.`t_report_mock` add unique unique_idx_mock (report_id, app_name, mock_name);