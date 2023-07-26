ALTER TABLE aiops_shard_r.t_app_base_data ADD COLUMN young_gc_count Int64;
ALTER TABLE aiops_shard_r.t_app_base_data ADD COLUMN young_gc_cost Int64;
ALTER TABLE aiops_shard_r.t_app_base_data ADD COLUMN full_gc_count Int64;
ALTER TABLE aiops_shard_r.t_app_base_data ADD COLUMN full_gc_cost Int64;

ALTER TABLE aiops_shard_r.t_app_base_data_all ADD COLUMN young_gc_count Int64;
ALTER TABLE aiops_shard_r.t_app_base_data_all ADD COLUMN young_gc_cost Int64;
ALTER TABLE aiops_shard_r.t_app_base_data_all ADD COLUMN full_gc_count Int64;
ALTER TABLE aiops_shard_r.t_app_base_data_all ADD COLUMN full_gc_cost Int64;