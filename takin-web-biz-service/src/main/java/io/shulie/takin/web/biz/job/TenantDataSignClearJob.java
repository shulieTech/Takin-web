package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.constant.TakinWebContext;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.common.constant.CacheConstants;
import io.shulie.takin.web.data.dao.application.TenantDataSignConfigDAO;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


@Component
@ElasticSchedulerJob(jobName = "tenantDataSignClearJob", cron = "0/5 * * * * ? *", description = "签名数据重制任务")
@Slf4j
public class TenantDataSignClearJob implements SimpleJob {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;

    @Autowired
    private TenantDataSignConfigDAO configDAO;

    @Resource
    private DistributedLock distributedLock;


    private String[] tables = new String[]{
            "t_agent_config",
            "t_app_remote_call",
            "t_application_ds_cache_manage",
            "t_application_ds_db_manage",
            "t_application_ds_db_table",
            "t_application_ds_manage",
            "t_application_node_probe",
            "t_application_plugin_download_path",
            "t_application_plugin_upgrade",
            "t_application_plugin_upgrade_ref",
            "t_application_plugins_config",
            "t_black_list",
            "t_file_manage",
            "t_leakcheck_config",
            "t_leakcheck_config_detail",
            "t_leakverify_detail",
            "t_leakverify_result",
            "t_link_guard",
            "t_script_manage",
            "t_script_manage_deploy",
            "t_shadow_job_config",
            "t_shadow_mq_consumer"
    };

    private List<String> tableList = Lists.newArrayList(tables);

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("[数据重制job 开始执行]");
        String envCode = WebPluginUtils.traceEnvCode();
        if (distributedLock.tryLock(CacheConstants.CACHE_KEY_TENANT_DATA_SIGN_CLEAN_STATUS + "_" + envCode + "lock",
                0L, 10L, TimeUnit.MINUTES)) {
            String cacheKey = CacheConstants.CACHE_KEY_TENANT_DATA_SIGN_CLEAN_STATUS + "_" + envCode;
            //获取重制队列
            Set members = redisTemplate.opsForSet().members(cacheKey);
            try {
                if (members != null && members.size() > 0) {
                    log.info("[数据重制job 正在执行中]");
                    //清除数据
                    Stream<CompletableFuture<Void>> completableFutureStream = tableList.stream().map(tableName ->
                            CompletableFuture.runAsync(() -> {
                                TakinWebContext.setTable(tableName);
                                configDAO.clearSign(Lists.newArrayList(members), envCode);
                            }, jobThreadPool));
                    CompletableFuture.allOf(completableFutureStream.toArray(CompletableFuture[]::new)).thenRun(() -> {
                        //从重制队列中移除
                        log.info("[数据重制job 数据清理完成,清空队列]");
                        redisTemplate.opsForSet().remove(cacheKey, members);
                    });
                }
            } finally {
                log.info("[数据重制job 正在执行结束]");
                distributedLock.unLock(CacheConstants.CACHE_KEY_TENANT_DATA_SIGN_CLEAN_STATUS + "_" + envCode + "lock");
            }
        }
    }

}
