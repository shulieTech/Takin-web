package io.shulie.takin.web.biz.utils;

import com.google.common.collect.Lists;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.ext.entity.e2e.E2eExceptionConfigInfoExt;
import io.shulie.takin.web.ext.util.E2ePluginUtils;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Slf4j
public final class E2eExceptionConfigInfoUtil {

    public static List<E2eExceptionConfigInfoExt> getConfig() {
        List<E2eExceptionConfigInfoExt> bottleneckConfig = null;
        if (WebPluginUtils.checkUserData() && E2ePluginUtils.checkE2ePlugin()) {
            bottleneckConfig = E2ePluginUtils.getExceptionConfig(WebPluginUtils.getCustomerId());
        }
        return bottleneckConfig;
    }
}
