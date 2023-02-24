CREATE TABLE aiops_shard_r.t_traffic_record
(
    `task_id` Int64,
    `trace_id` String,
    `span_id` String,
    `app_name` String,
    `service_name` String,
    `request_address` String,
    `machine_room` String,
    `service_type` Int64,
    `kind` Int8,
    `request_header` String,
    `request_body` String,
    `response_header` String,
    `response_body` String,
    `clean_request_service_name` String,
    `clean_request_header` String,
    `clean_request_body` String,
    `clean_response_header` String,
    `clean_response_body` String,
    `record_time` DateTime64(3, 'Asia/Shanghai'),
    `gmt_create` DateTime('Asia/Shanghai')
)
ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster-aiops/t_traffic_record/{r_shard}', '{replica}')
PRIMARY KEY (task_id)
PARTITION BY toYYYYMMDD(gmt_create)
ORDER BY (task_id)
TTL gmt_create + toIntervalDay(7);

CREATE TABLE aiops_shard_r.t_traffic_record_all as aiops_shard_r.t_traffic_record ENGINE = Distributed('cluster-aiops', '', 't_traffic_record', sipHash64(task_id));