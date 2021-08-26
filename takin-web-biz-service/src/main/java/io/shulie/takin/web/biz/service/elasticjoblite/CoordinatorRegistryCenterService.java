package io.shulie.takin.web.biz.service.elasticjoblite;

import javax.annotation.PostConstruct;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2021/1/7 8:22 下午
 */
@Component
@Slf4j
public class CoordinatorRegistryCenterService {

    @Autowired
    private Environment environment;

    private CoordinatorRegistryCenter registryCenter;

    @PostConstruct
    public void init() {
        String zkAddr = environment.getProperty("takin.config.zk.addr");
        if (StringUtils.isEmpty(zkAddr)) {
            throw new RuntimeException("配置中心zk地址没有填写，请核对校验`takin.config.zk.addr`");
        }
        registryCenter = new ZookeeperRegistryCenter(
            new ZookeeperConfiguration(zkAddr, "verify-job"));
        registryCenter.init();
    }

    public CoordinatorRegistryCenter getRegistryCenter() {
        return registryCenter;
    }
}
