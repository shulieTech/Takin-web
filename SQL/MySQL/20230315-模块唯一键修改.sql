ALTER TABLE `trodb`.`t_tro_dept`
DROP INDEX `idx_name_tid`,
ADD UNIQUE INDEX `idx_name_tid`(`tenant_id`, `parent_id`, `name`) USING BTREE;

ALTER TABLE `trodb`.`t_tro_dept`
    ADD UNIQUE INDEX `code`(`code`, `tenant_id`) USING BTREE;