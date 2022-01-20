package io.shulie.takin.web.biz.service.elasticjoblite;

import javax.annotation.PostConstruct;

import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2021/1/7 8:22 下午
 */
@Component
@Slf4j
public class CoordinatorRegistryCenterService {

    @Value("${takin.config.zk.addr}")
    private String zkAddr;

    private CoordinatorRegistryCenter registryCenter;

    @PostConstruct
    public void init() {
        registryCenter = new ZookeeperRegistryCenter(new ZookeeperConfiguration(zkAddr, "verify-job"));
        registryCenter.init();
    }

    public CoordinatorRegistryCenter getRegistryCenter() {
        return registryCenter;
    }

    public void deleteJob() {

    }
}
