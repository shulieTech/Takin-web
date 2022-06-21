package io.shulie.takin.web.biz.job;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.checker.EngineEnvChecker;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckResult;
import io.shulie.takin.web.biz.checker.StartConditionChecker.CheckStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ElasticSchedulerJob(jobName = "pressureEnvInspectionJob", cron = "0 * * * * ?", description = "检测压力机环境是否正常")
@Slf4j
public class PressureEnvInspectionJob implements SimpleJob, InitializingBean {

    public static final String SCHEDULED_PRESSURE_ENV_KEY = "scheduled:pressure:env:check";

    private static final String WARNING_PERCENT_DEFAULT = "20";

    private static final BigDecimal WARNING_PERCENT_MIN = new BigDecimal(WARNING_PERCENT_DEFAULT);

    private static final BigDecimal WARNING_PERCENT_MAX = new BigDecimal("95");

    @Resource
    private EngineEnvChecker engineEnvChecker;

    @Resource
    private RedisClientUtil redisClientUtil;

    @Value("${data.path}")
    private String nfsMountPoint;

    @Value("${nfs.warning.percent:" + WARNING_PERCENT_DEFAULT + "}")
    private BigDecimal warningPercent;

    @Override
    public void execute(ShardingContext shardingContext) {
        List<String> errorMessage = new ArrayList<>(2);
        CheckResult check = engineEnvChecker.check(null);
        if (CheckStatus.FAIL.ordinal() == check.getStatus()) {
            errorMessage.add(check.getMessage());
        }
        try {
            nfsSpaceCheck();
        } catch (Exception e) {
            errorMessage.add(e.getMessage());
        }
        if (errorMessage.isEmpty()) {
            redisClientUtil.delete(SCHEDULED_PRESSURE_ENV_KEY);
        } else {
            redisClientUtil.setString(SCHEDULED_PRESSURE_ENV_KEY, StringUtils.join(errorMessage, "\n"));
        }
    }

    // 检测nfs空间是否足够
    private void nfsSpaceCheck() {
        if (!StringUtils.isBlank(nfsMountPoint)) {
            File file = new File(nfsMountPoint);
            if (file.exists()) {
                double usePercent = (1 - (1.0d * file.getUsableSpace() / file.getTotalSpace())) * 100;
                BigDecimal bigDecimal = new BigDecimal(String.valueOf(usePercent));
                bigDecimal = bigDecimal.setScale(2, RoundingMode.HALF_EVEN);
                if (bigDecimal.compareTo(warningPercent) >= 0) {
                    throw new RuntimeException(String.format("NFS磁盘资源已占用%s%%, 请及时清理", bigDecimal.toPlainString()));
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        if (warningPercent.compareTo(WARNING_PERCENT_MIN) < 0 || warningPercent.compareTo(WARNING_PERCENT_MAX) > 0) {
            warningPercent = new BigDecimal(WARNING_PERCENT_DEFAULT);
        }
    }
}
