-- 死锁问题
ALTER TABLE t_application_mnt
DROP PRIMARY KEY,
ADD  INDEX `idx`(`id`),
DROP KEY `idx_application_id`;
ALTER TABLE t_application_mnt
ADD PRIMARY KEY (`APPLICATION_ID`) USING BTREE;