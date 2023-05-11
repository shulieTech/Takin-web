CREATE TABLE aiops_shard_m.t_trace (
  `appName` String,
  `entranceId` Nullable(String),
  `entranceNodeId` Nullable(String),
  `traceId` String,
  `level` Nullable(Int8),
  `parentIndex` Nullable(Int8),
  `index` Nullable(Int8),
  `rpcId` String,
  `rpcType` Int8,
  `logType` Nullable(Int8),
  `traceAppName` Nullable(String),
  `upAppName` Nullable(String),
  `startTime` Int64,
  `cost` Int32,
  `middlewareName` Nullable(String),
  `serviceName` Nullable(String),
  `methodName` Nullable(String),
  `remoteIp` Nullable(String),
  `port` Nullable(Int32),
  `resultCode` Nullable(String),
  `requestSize` Nullable(String),
  `responseSize` Nullable(String),
  `request` Nullable(String),
  `response` Nullable(String),
  `clusterTest` Nullable(String),
  `callbackMsg` Nullable(String),
  `samplingInterval` Nullable(String),
  `localId` Nullable(String),
  `attributes` Nullable(String),
  `localAttributes` Nullable(String),
  `async` Nullable(String),
  `version` Nullable(String),
  `hostIp` Nullable(String),
  `agentId` Nullable(String),
  `startDate` DateTime,
  `createDate` DateTime DEFAULT toDateTime(now()),
  `timeMin` Nullable(Int64) DEFAULT 0,
  `entranceServiceType` Nullable(String),
  `parsedServiceName` String,
  `parsedMethod` String,
  `parsedAppName` Nullable(String),
  `parsedMiddlewareName` Nullable(String),
  `parsedExtend` Nullable(String),
  `dateToMin` Int64,
  `uploadTime` Nullable(Int64),
  `receiveHttpTime` Nullable(Int64),
  INDEX ix_traceid traceId TYPE minmax GRANULARITY 5,
  taskId Nullable(String),
  flag Nullable(String),
  flagMessage Nullable(String),
  receiveTime Nullable(Int64),
  processTime Nullable(Int64),
  saveCkTime DateTime DEFAULT now(),
  `userAppKey` String,
  `envCode` String,
  `userId` String
) 
ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster-aiops/t_trace/{m_shard}', '{replica}')
PARTITION BY toYYYYMMDD(startDate)
ORDER BY (appName,startDate,parsedServiceName,parsedMethod,rpcType) 
TTL startDate + toIntervalDay(3) 
SETTINGS index_granularity = 8192;


CREATE TABLE aiops_shard_m.t_pressure (
  `appName` String,
  `entranceId` Nullable(String),
  `entranceNodeId` Nullable(String),
  `traceId` String,
  `level` Nullable(Int8),
  `parentIndex` Nullable(Int8),
  `index` Nullable(Int8),
  `rpcId` String,
  `rpcType` Int8,
  `logType` Nullable(Int8),
  `traceAppName` Nullable(String),
  `upAppName` Nullable(String),
  `startTime` Int64,
  `cost` Int32,
  `middlewareName` Nullable(String),
  `serviceName` Nullable(String),
  `methodName` Nullable(String),
  `remoteIp` Nullable(String),
  `port` Nullable(Int32),
  `resultCode` Nullable(String),
  `requestSize` Nullable(String),
  `responseSize` Nullable(String),
  `request` Nullable(String),
  `response` Nullable(String),
  `clusterTest` Nullable(String),
  `callbackMsg` Nullable(String),
  `samplingInterval` Nullable(String),
  `localId` Nullable(String),
  `attributes` Nullable(String),
  `localAttributes` Nullable(String),
  `async` Nullable(String),
  `version` Nullable(String),
  `hostIp` Nullable(String),
  `agentId` Nullable(String),
  `startDate` DateTime,
  `createDate` DateTime DEFAULT toDateTime(now()),
  `timeMin` Nullable(Int64) DEFAULT 0,
  `entranceServiceType` Nullable(String),
  `parsedServiceName` String,
  `parsedMethod` String,
  `parsedAppName` Nullable(String),
  `parsedMiddlewareName` Nullable(String),
  `parsedExtend` Nullable(String),
  `dateToMin` Int64,
  taskId String,
  flag Nullable(String),
  flagMessage Nullable(String),
  receiveTime Nullable(Int64),
  processTime Nullable(Int64),
  saveCkTime DateTime DEFAULT now(),
  `userAppKey` String,
  `envCode` String,
  `userId` String,
  `uploadTime` Nullable(Int64),
  `receiveHttpTime` Nullable(Int64),
  INDEX ix_userAppKey userAppKey TYPE set(1000000) GRANULARITY 5,
  INDEX ix_traceid traceId TYPE set(0) GRANULARITY 5,
  INDEX ix_appName appName TYPE set(0) GRANULARITY 5,
  INDEX ix_parsedServiceName parsedServiceName TYPE set(0) GRANULARITY 5,
  INDEX ix_parsedMethod parsedMethod TYPE set(0) GRANULARITY 5,
  INDEX ix_cost cost TYPE minmax GRANULARITY 5
) 
ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster-aiops/t_pressure/{m_shard}', '{replica}')
PARTITION BY taskId
ORDER BY (taskId, traceId, cost, startDate, appName, rpcId) 
PRIMARY KEY (taskId, traceId, cost, startDate, appName, rpcId) 
TTL startDate + toIntervalDay(7) 
SETTINGS index_granularity = 8192;

CREATE TABLE aiops_shard_m.t_trace_all as aiops_shard_m.t_trace ENGINE = Distributed('cluster-aiops', '', 't_trace', sipHash64(traceId));
CREATE TABLE aiops_shard_m.t_trace_pressure as aiops_shard_m.t_pressure ENGINE = Distributed('cluster-aiops', '', 't_pressure', sipHash64(taskId));


-- 压力指标数据
CREATE TABLE aiops_shard_m.t_engine_metrics (
  `time` Int64,
  `transaction` String,
  `test_name` String,
  `count` Int64,
  `fail_count` Int64,
  `sent_bytes` Int64,
  `received_bytes` Int64,
  `rt` Decimal64 ( 4 ),
  `sum_rt` Decimal64 ( 4 ),
  `sa_count` Int64,
  `max_rt` Decimal64 ( 4 ),
  `min_rt` Decimal64 ( 4 ),
  `timestamp` Int64,
  `active_threads` Int16,
  `percent_data` String,
  `pod_no` String,
  `job_id` String,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = ReplicatedMergeTree ( '/clickhouse/tables/cluster-aiops/t_engine_metrics/{m_shard}', '{replica}' ) PARTITION BY job_id PRIMARY KEY ( transaction, test_name, pod_no, time ) 
ORDER BY( transaction, test_name, pod_no, time ) TTL createDate + toIntervalDay ( 3 );

CREATE TABLE aiops_shard_m.t_engine_metrics_all (
  `time` Int64,
  `transaction` String,
  `test_name` String,
  `count` Int64,
  `fail_count` Int64,
  `sent_bytes` Int64,
  `received_bytes` Int64,
  `rt` Decimal64 ( 4 ),
  `sum_rt` Decimal64 ( 4 ),
  `sa_count` Int64,
  `max_rt` Decimal64 ( 4 ),
  `min_rt` Decimal64 ( 4 ),
  `timestamp` Int64,
  `active_threads` Int16,
  `percent_data` String,
  `pod_no` String,
  `job_id` String,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = Distributed ('cluster-aiops','','t_engine_metrics',sipHash64 ( job_id ));

CREATE TABLE aiops_shard_m.t_engine_pressure (
  `time` Int64,
  `transaction` String,
  `avg_rt` Decimal64 ( 4 ),
  `avg_tps` Decimal64 ( 4 ),
  `test_name` String,
  `count` Int64,
  `create_time` Int64,
  `data_num` Int16,
  `data_rate` Decimal64 ( 4 ),
  `fail_count` Int64,
  `sent_bytes` Int64,
  `received_bytes` Int64,
  `sum_rt` Decimal64 ( 4 ),
  `sa` Decimal64 ( 4 ),
  `sa_count` Int64,
  `max_rt` Decimal64 ( 4 ),
  `min_rt` Decimal64 ( 4 ),
  `active_threads` Int16,
  `sa_percent` String,
  `status` Int16,
  `success_rate` Decimal64 ( 4 ),
  `job_id` String,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = ReplicatedMergeTree ( '/clickhouse/tables/cluster-aiops/t_engine_pressure/{m_shard}', '{replica}' ) PARTITION BY job_id PRIMARY KEY ( transaction, test_name, time ) 
ORDER BY( transaction, test_name, time ) TTL createDate + toIntervalDay ( 180 );

CREATE TABLE aiops_shard_m.t_engine_pressure_all (
  `time` Int64,
  `transaction` String,
  `avg_rt` Decimal64 ( 4 ),
  `avg_tps` Decimal64 ( 4 ),
  `test_name` String,
  `count` Int64,
  `create_time` Int64,
  `data_num` Int16,
  `data_rate` Decimal64 ( 4 ),
  `fail_count` Int64,
  `sent_bytes` Int64,
  `received_bytes` Int64,
  `sum_rt` Decimal64 ( 4 ),
  `sa` Decimal64 ( 4 ),
  `sa_count` Int64,
  `max_rt` Decimal64 ( 4 ),
  `min_rt` Decimal64 ( 4 ),
  `active_threads` Int16,
  `sa_percent` String,
  `status` Int16,
  `success_rate` Decimal64 ( 4 ),
  `job_id` String,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = Distributed ('cluster-aiops','','t_engine_pressure',sipHash64 ( job_id ));

-- 基础数据表
CREATE TABLE aiops_shard_m.t_app_base_data (
  `time` Int64,
  `agent_id` String,
  `app_ip` String,
  `app_name` String,
  `cpu_cores` Int16,
  `cpu_load` Decimal64 ( 2 ),
  `cpu_rate` Decimal64 ( 2 ),
  `disk` Int32,
  `env_code` String,
  `iowait` Decimal64 ( 2 ),
  `is_container_flag` Int16,
  `log_time` Int64,
  `mem_rate` Decimal64 ( 2 ),
  `memory` Int32,
  `net_bandwidth` Decimal64 ( 2 ),
  `net_bandwidth_rate` Decimal64 ( 2 ),
  `tenant_app_key` String,
  `user_id` Int64,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = ReplicatedMergeTree ( '/clickhouse/tables/cluster-aiops/t_app_base_data/{m_shard}', '{replica}' ) PARTITION BY intDiv(time,3600000) PRIMARY KEY ( time,agent_id, app_name, tenant_app_key, user_id )
ORDER BY(time, agent_id, app_name, tenant_app_key, user_id ) TTL createDate + toIntervalDay ( 3 );


CREATE TABLE aiops_shard_m.t_app_base_data_all (
  `time` Int64,
  `agent_id` String,
  `app_ip` String,
  `app_name` String,
  `cpu_cores` Int16,
  `cpu_load` Decimal64 ( 2 ),
  `cpu_rate` Decimal64 ( 2 ),
  `disk` Int32,
  `env_code` String,
  `iowait` Decimal64 ( 2 ),
  `is_container_flag` Int16,
  `log_time` Int64,
  `mem_rate` Decimal64 ( 2 ),
  `memory` Int32,
  `net_bandwidth` Decimal64 ( 2 ),
  `net_bandwidth_rate` Decimal64 ( 2 ),
  `tenant_app_key` String,
  `user_id` Int64,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = Distributed ('cluster-aiops','','t_app_base_data',sipHash64 ( app_name ));

CREATE TABLE aiops_shard_m.t_performance_base_data (
  `time` Int64,
  `timestamp` Int64,
  `total_memory` Int64,
  `perm_memory` Int64,
  `young_memory` Int64,
  `old_memory` Int64,
  `young_gc_count` Int16,
  `full_gc_count` Int16,
  `young_gc_cost` Int64,
  `full_gc_cost` Int64,
  `cpu_use_rate` Decimal64 ( 2 ),
  `total_buffer_pool_memory` Int64,
  `total_no_heap_memory` Int64,
  `thread_count` Int16,
  `base_id` Int64,
  `agent_id` String,
  `app_name` String,
  `app_ip` String,
  `process_id` String,
  `process_name` String,
  `env_code` String,
  `tenant_app_key` String,
  `tenant_id` Int64,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = ReplicatedMergeTree ( '/clickhouse/tables/cluster-aiops/t_performance_base_data/{m_shard}', '{replica}' ) PARTITION BY intDiv(time,3600000) PRIMARY KEY ( app_name,time )
ORDER BY( app_name,time ) TTL createDate + toIntervalDay ( 7 );


CREATE TABLE aiops_shard_m.t_performance_base_data_all (
  `time` Int64,
  `timestamp` Int64,
  `total_memory` Int64,
  `perm_memory` Int64,
  `young_memory` Int64,
  `old_memory` Int64,
  `young_gc_count` Int16,
  `full_gc_count` Int16,
  `young_gc_cost` Int64,
  `full_gc_cost` Int64,
  `cpu_use_rate` Decimal64 ( 2 ),
  `total_buffer_pool_memory` Int64,
  `total_no_heap_memory` Int64,
  `thread_count` Int16,
  `base_id` Int64,
  `agent_id` String,
  `app_name` String,
  `app_ip` String,
  `process_id` String,
  `process_name` String,
  `env_code` String,
  `tenant_app_key` String,
  `tenant_id` Int64,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = Distributed ('cluster-aiops','','t_performance_base_data',sipHash64 ( app_name ));

-- trace_metrics相关表
CREATE TABLE aiops_shard_m.trace_metrics (
  `time` Int64,
  `edgeId` String,
  `clusterTest` String,
  `service` String,
  `method` String,
  `appName` String,
  `rpcType` String,
  `middlewareName` String,
  `tenantAppKey` String,
  `envCode` String,
  `totalCount` Int64,
  `successCount` Int64,
  `totalRt` Int64,
  `errorCount` Int64,
  `hitCount` Int64,
  `totalTps` Int64,
  `total` Int64,
  `e2eSuccessCount` Int64,
  `e2eErrorCount` Int64,
  `maxRt` Int64,
  `avgRt` Decimal64 ( 2 ),
  `avgTps` Decimal64 ( 2 ),
  `traceId` String,
  `sqlStatement` String,
  `log_time` String,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = ReplicatedMergeTree ( '/clickhouse/tables/cluster-aiops/trace_metrics/{m_shard}', '{replica}' ) PARTITION BY intDiv(time,3600000) PRIMARY KEY (time, appName )
ORDER BY(time, appName ) TTL createDate + toIntervalDay ( 3 );

CREATE TABLE aiops_shard_m.trace_metrics_all (
  `time` Int64,
  `edgeId` String,
  `clusterTest` String,
  `service` String,
  `method` String,
  `appName` String,
  `rpcType` String,
  `middlewareName` String,
  `tenantAppKey` String,
  `envCode` String,
  `totalCount` Int64,
  `successCount` Int64,
  `totalRt` Int64,
  `errorCount` Int64,
  `hitCount` Int64,
  `totalTps` Int64,
  `total` Int64,
  `e2eSuccessCount` Int64,
  `e2eErrorCount` Int64,
  `maxRt` Int64,
  `avgRt` Decimal64 ( 2 ),
  `avgTps` Decimal64 ( 2 ),
  `traceId` String,
  `sqlStatement` String,
  `log_time` String,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = Distributed ('cluster-aiops','','trace_metrics',sipHash64 ( traceId ));

CREATE TABLE aiops_shard_m.trace_e2e_assert_metrics (
  `time` Int64,
  `nodeId` String,
  `exceptionType` String,
  `traceId` String,
  `parsedAppName` String,
  `parsedServiceName` String,
  `parsedMethod` String,
  `rpcType` String,
  `totalRt` Int32,
  `totalCount` Int32,
  `totalQps` Int32,
  `qps` Decimal64 ( 2 ),
  `rt` Decimal64 ( 2 ),
  `successCount` Int64,
  `errorCount` Int64,
  `clusterTest` String,
  `tenantAppKey` String,
  `envCode` String,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = ReplicatedMergeTree ( '/clickhouse/tables/cluster-aiops/trace_e2e_assert_metrics/{m_shard}', '{replica}' ) PARTITION BY traceId PRIMARY KEY ( traceId, nodeId, exceptionType ) 
ORDER BY ( traceId, nodeId, exceptionType ) TTL createDate + toIntervalDay ( 3 );

CREATE TABLE aiops_shard_m.trace_e2e_assert_metrics_all (
  `time` Int64,
  `nodeId` String,
  `exceptionType` String,
  `traceId` String,
  `parsedAppName` String,
  `parsedServiceName` String,
  `parsedMethod` String,
  `rpcType` String,
  `totalRt` Int32,
  `totalCount` Int32,
  `totalQps` Int32,
  `qps` Decimal64 ( 2 ),
  `rt` Decimal64 ( 2 ),
  `successCount` Int64,
  `errorCount` Int64,
  `clusterTest` String,
  `tenantAppKey` String,
  `envCode` String,
  `createDate` DateTime DEFAULT toDateTime (
  now()) 
) ENGINE = Distributed ('cluster-aiops','','trace_e2e_assert_metrics',sipHash64 ( traceId ));
