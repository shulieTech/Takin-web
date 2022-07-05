package io.shulie.takin.web.app.conf;

import com.dangdang.ddframe.job.event.JobEventConfiguration;

import com.dangdang.ddframe.job.lite.lifecycle.api.JobOperateAPI;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobStatisticsAPI;
import com.dangdang.ddframe.job.lite.lifecycle.internal.operate.JobOperateAPIImpl;
import com.dangdang.ddframe.job.lite.lifecycle.internal.statistics.JobStatisticsAPIImpl;
import com.dangdang.ddframe.job.reg.base.CoordinatorRegistryCenter;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;

import io.shulie.takin.job.parser.JobConfParser;
import io.shulie.takin.job.ElasticJobProperties;
import io.shulie.takin.job.ElasticRegCenterConfig;
import io.shulie.takin.job.config.ElasticJobConfig;
import io.shulie.takin.job.config.zk.ZkClientConfig;
import io.shulie.takin.job.factory.SpringJobSchedulerFactory;

/**
 * @author 无涯
 * @date 2021/6/14 12:59 上午
 */
@Configuration
@EnableConfigurationProperties(ElasticJobProperties.class)
public class ElasticJobAutoConfig {

    @Value("${env:prod}")
    private String env;

    @Value("${takin.config.zk.addr}")
    private String zkAddress;

    @Bean
    @ConditionalOnMissingBean(JobEventConfiguration.class)
    public SpringJobSchedulerFactory springJobSchedulerFactory(ElasticJobProperties elasticJobProperties) {
        ElasticJobConfig elasticJobConfig = new ElasticJobConfig();
        ZkClientConfig zkClientConfig = new ZkClientConfig();
        zkClientConfig.setZkServers(zkAddress);
        zkClientConfig.setNamespace("takin-web-job-" + env);
        elasticJobConfig.setZkClientConfig(zkClientConfig);
        ElasticRegCenterConfig elasticRegCenterConfig = new ElasticRegCenterConfig(elasticJobConfig);
        return new SpringJobSchedulerFactory(elasticJobProperties, elasticRegCenterConfig.regCenter());
    }

    @Bean
    public JobConfParser jobConfParser(SpringJobSchedulerFactory springJobSchedulerFactory) {
        return new JobConfParser(springJobSchedulerFactory);
    }

    // 初始化elasticJob页面操作类
    @Bean
    public CoordinatorRegistryCenter zkCenter() {
        ZookeeperConfiguration configuration = new
                ZookeeperConfiguration("192.168.1.185:2181", "takin-web-job-" + "_dev_28");
        CoordinatorRegistryCenter center = new ZookeeperRegistryCenter(configuration);
        center.init();
        return center;
    }

    @Bean
    public JobStatisticsAPI jobStatisticsAPI(CoordinatorRegistryCenter zkCenter) {
        return new JobStatisticsAPIImpl(zkCenter);
    }

    @Bean
    public JobOperateAPI jobOperateAPI(CoordinatorRegistryCenter zkCenter) {
        return new JobOperateAPIImpl(zkCenter);
    }
}
