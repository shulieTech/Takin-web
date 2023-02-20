package io.shulie.takin.web.biz.job;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.util.StrUtil;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.adapter.api.entrypoint.watchman.CloudWatchmanApi;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanBatchRequest;
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.web.biz.checker.EngineEnvChecker;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.ext.api.tenant.WebTenantExtApi;
import io.shulie.takin.web.ext.entity.tenant.EngineType;
import io.shulie.takin.web.ext.entity.tenant.TenantEngineExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
//@ElasticSchedulerJob(jobName = "pressureEnvInspectionJob", cron = "0 * * * * ?", description = "检测压力机环境是否正常")
@Slf4j
public class PressureEnvInspectionJob implements InitializingBean {

    private static final String CLUSTER = "cluster";
    private static final String NFS = "nfs";
    private static final String CLUSTER_NAME = "集群";
    private static final String NFS_NAME = "NFS";
    private static final String COMMON_ENV_KEY = "scheduled:pressure:env:check:common";
    private static final String PRIVATE_ENV_KEY = "scheduled:pressure:env:check:private";
    public static final String COMMON_CLUSTER_ENV_KEY = COMMON_ENV_KEY + ":" + CLUSTER;
    public static final String PRIVATE_CLUSTER_ENV_KEY = PRIVATE_ENV_KEY + ":" + CLUSTER;
    public static final String COMMON_NFS_ENV_KEY = COMMON_ENV_KEY + ":" + NFS;
    public static final String PRIVATE_NFS_ENV_KEY = PRIVATE_ENV_KEY + ":" + NFS;

    private static final String WARNING_PERCENT_DEFAULT = "20";

    private static final BigDecimal WARNING_PERCENT_MIN = new BigDecimal(WARNING_PERCENT_DEFAULT);

    private static final BigDecimal WARNING_PERCENT_MAX = new BigDecimal("95");

    @Resource
    private RedisClientUtil redisClientUtil;

    @Resource
    private PluginManager pluginManager;

    @Resource
    private CloudWatchmanApi cloudWatchmanApi;

    @Value("${nfs.warning.percent:" + WARNING_PERCENT_DEFAULT + "}")
    private BigDecimal warningPercent;

    @XxlJob("pressureEnvInspectionJobExecute")
//    @Override
    public void execute() {
        List<TenantEngineExt> tenantEngineList = pluginManager.getExtension(WebTenantExtApi.class).getAllTenantEngineList();
        if (CollectionUtils.isEmpty(tenantEngineList)) {
            return;
        }

        // 公网集群Id集合
        List<String> commonEngines = tenantEngineList.stream()
            .filter(ext -> EngineType.COMMON == ext.getType())
            .map(TenantEngineExt::getEngineId).collect(Collectors.toList());

        // 租户私网集群
        Map<String, List<String>> privateEngines = tenantEngineList.stream()
            .filter(ext -> EngineType.PRIVATE == ext.getType())
            .collect(Collectors.groupingBy(ext -> ext.getTenantId() + "_" + ext.getEnvCode(),
                Collectors.mapping(TenantEngineExt::getEngineId, Collectors.toList())));

        // 所有集群Id集合
        List<String> watchmanIdList = tenantEngineList.stream()
            .map(TenantEngineExt::getEngineId).collect(Collectors.toList());

        // 有效的集群集合
        Map<String, WatchmanStatusResponse> statusListMap =
            cloudWatchmanApi.statusBatch(new WatchmanBatchRequest(watchmanIdList));

        // 公网集群状态
        List<EngineEnvResponse> commonStatus = statusListMap.entrySet().stream()
            .filter(entry -> commonEngines.contains(entry.getKey()))
            .map(entry -> new EngineEnvResponse(EngineType.COMMON, entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        // 私网按租户区分
        MultiValueMap<String, EngineEnvResponse> privateStatus = new LinkedMultiValueMap<>();
        for (Entry<String, List<String>> entry : privateEngines.entrySet()) {
            String tenantIdAndEnvCode = entry.getKey();
            for (String engineId : entry.getValue()) {
                if (statusListMap.containsKey(engineId)) {
                    EngineEnvResponse response = new EngineEnvResponse(EngineType.PRIVATE,
                        engineId, statusListMap.get(engineId));
                    privateStatus.add(tenantIdAndEnvCode, response);
                }
            }
        }
        processCluster(commonStatus, privateStatus);
        processNfs(commonStatus, privateStatus);
    }

    // 计算并汇总租户的提示信息
    public String reduceSumming() {
        AtomicInteger index = new AtomicInteger(1);
        String clusterMessage = reportMessage(clusterMessage(), index);
        String nfsMessage = reportMessage(nfsMessage(), index);
        if (StringUtils.isNotBlank(clusterMessage)) {
            clusterMessage += ";  ";
        }
        return clusterMessage.concat(nfsMessage);
    }

    private String reportMessage(EnvInspectionTypeMessage typeMessage, AtomicInteger index) {
        String finalMessage = typeMessage.getMessage().values().stream()
            .filter(StringUtils::isNotBlank)
            .flatMap(message -> Arrays.stream(message.split(StrUtil.CRLF)))
            .map(message -> index.getAndIncrement() + "、" + message)
            .collect(Collectors.joining("; "));
        if (StringUtils.isBlank(finalMessage)) {
            return "";
        }
        return finalMessage;
    }

    // 提取集群提示信息
    private EnvInspectionTypeMessage clusterMessage() {
        Map<String, String> clusterMessage = new HashMap<>(4);
        clusterMessage.put(EngineType.COMMON.getName(), extraCommonMessage(false));
        clusterMessage.put(EngineType.PRIVATE.getName(), extraPrivateMessage(false));
        return new EnvInspectionTypeMessage(CLUSTER_NAME, clusterMessage);
    }

    // 提取nfs提示信息
    private EnvInspectionTypeMessage nfsMessage() {
        Map<String, String> nfsMessage = new HashMap<>(4);
        nfsMessage.put(EngineType.COMMON.getName(), extraCommonMessage(true));
        nfsMessage.put(EngineType.PRIVATE.getName(), extraPrivateMessage(true));
        return new EnvInspectionTypeMessage(NFS_NAME, nfsMessage);
    }

    // 提取公网信息
    private String extraCommonMessage(boolean nfs) {
        String redisKey = nfs ? COMMON_NFS_ENV_KEY : COMMON_CLUSTER_ENV_KEY;
        return redisClientUtil.getString(redisKey);
    }

    // 提取私网信息
    private String extraPrivateMessage(boolean nfs) {
        String redisKey = nfs ? PRIVATE_NFS_ENV_KEY : PRIVATE_CLUSTER_ENV_KEY;
        return (String)redisClientUtil.hmget(redisKey,
            WebPluginUtils.traceTenantId() + "_" + WebPluginUtils.traceEnvCode());
    }

    // 处理集群信息
    private void processCluster(List<EngineEnvResponse> commonStatus,
        MultiValueMap<String, EngineEnvResponse> privateStatus) {
        EnvInspectionContext context = EnvInspectionContext.builder().nfs(false)
            .commonStatus(commonStatus).privateStatus(privateStatus)
            .messageFunction(EngineEnvChecker::extraErrorMessage).build();
        processMessage(context);
    }

    // 处理nfs信息
    private void processNfs(List<EngineEnvResponse> commonStatus,
        MultiValueMap<String, EngineEnvResponse> privateStatus) {
        EnvInspectionContext context = EnvInspectionContext.builder().nfs(true)
            .commonStatus(commonStatus).privateStatus(privateStatus)
            .messageFunction(this::extraNfsMessage).build();
        processMessage(context);
    }

    // 提示信息处理
    private void processMessage(EnvInspectionContext context) {
        List<EngineEnvResponse> commonStatus = context.getCommonStatus();
        MultiValueMap<String, EngineEnvResponse> privateStatus = context.getPrivateStatus();
        Function<EngineEnvResponse, String> messageFunction = context.getMessageFunction();
        boolean nfs = context.isNfs();
        String commonRedisKey = nfs ? COMMON_NFS_ENV_KEY : COMMON_CLUSTER_ENV_KEY;
        String privateRedisKey = nfs ? PRIVATE_NFS_ENV_KEY : PRIVATE_CLUSTER_ENV_KEY;

        // 处理公网集群信息
        List<String> commonMessage = commonStatus.stream().map(messageFunction)
            .filter(Objects::nonNull).distinct().collect(Collectors.toList());

        // 处理私网集群信息
        Map<String, List<String>> privateMessage = privateStatus.entrySet().stream()
            .collect(Collectors.toMap(Entry::getKey,
                entry -> entry.getValue().stream().map(messageFunction)
                    .filter(Objects::nonNull).distinct().collect(Collectors.toList())));

        if (CollectionUtils.isEmpty(commonMessage)) {
            redisClientUtil.delete(commonRedisKey);
        } else {
            redisClientUtil.setString(commonRedisKey, StringUtils.join(commonMessage, StrUtil.CRLF));
        }
        if (CollectionUtils.isEmpty(privateMessage)) {
            redisClientUtil.delete(privateRedisKey);
        } else {
            privateMessage.forEach((key, value) -> {
                if (CollectionUtils.isEmpty(value)) {
                    redisClientUtil.hmdelete(privateRedisKey, key);
                } else {
                    redisClientUtil.hmset(privateRedisKey, key, StringUtils.join(value, StrUtil.CRLF));
                }
            });
        }
    }

    private String extraNfsMessage(EngineEnvResponse response) {
        WatchmanStatusResponse status = response.getStatus();
        if (Objects.isNull(status)) {
            return null; // 调度器异常会显示在集群中，不需要重复显示
        }
        io.shulie.takin.cloud.model.resource.Resource resource = status.getResource();
        if (Objects.isNull(resource)) {
            return null; // 调度器异常会显示在集群中，不需要重复显示
        }
        String nfsServer = resource.getNfsServer();
        if (StringUtils.isBlank(nfsServer)) {
            return null;
        }
        EngineType type = response.getType();
        String namePrefix = resource.getName() + "(" + type.getName() + ")";
        String nfsInfo = "nfs" + (type == EngineType.PRIVATE ? "(" + nfsServer + ")" : "");
        Long nfsTotalSpace = resource.getNfsTotalSpace();
        Long nfsUsableSpace = resource.getNfsUsableSpace();
        double usePercent = (1 - (1.0d * nfsUsableSpace / nfsTotalSpace)) * 100;
        BigDecimal bigDecimal = new BigDecimal(String.valueOf(usePercent));
        bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_EVEN);
        if (bigDecimal.compareTo(warningPercent) >= 0) {
            return String.format("%s：%s资源已占用%s%%", namePrefix, nfsInfo, bigDecimal.toPlainString());
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() {
        if (warningPercent.compareTo(WARNING_PERCENT_MIN) < 0 || warningPercent.compareTo(WARNING_PERCENT_MAX) > 0) {
            warningPercent = new BigDecimal(WARNING_PERCENT_DEFAULT);
        }
    }

    @Data
    @Builder
    private static class EnvInspectionContext {
        private List<EngineEnvResponse> commonStatus;
        private MultiValueMap<String, EngineEnvResponse> privateStatus;
        private Function<EngineEnvResponse, String> messageFunction;
        private boolean nfs;
    }

    @Data
    @AllArgsConstructor
    public static class EnvInspectionTypeMessage {
        private String type; // nfs, cluster
        private Map<String, String> message; // key: 公私网 (EngineType)
    }

    @Data
    @AllArgsConstructor
    public static class EngineEnvResponse {
        private EngineType type;
        private String engineId;
        private WatchmanStatusResponse status;
    }
}
