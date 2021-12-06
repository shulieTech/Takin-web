package io.shulie.takin.web.app.conf;

import java.util.Optional;

import javax.sql.DataSource;

import com.dangdang.ddframe.job.event.JobEventConfiguration;
import io.shulie.takin.job.ElasticJobProperties;
import io.shulie.takin.job.ElasticRegCenterConfig;
import io.shulie.takin.job.config.ElasticJobConfig;
import io.shulie.takin.job.config.zk.ZkClientConfig;
import io.shulie.takin.job.factory.SpringJobSchedulerFactory;
import io.shulie.takin.job.parser.JobConfParser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author 无涯
 * @date 2021/6/14 12:59 上午
 */
@Configuration
@EnableConfigurationProperties(ElasticJobProperties.class)
public class ElasticJobAutoConfig {
    @Autowired
    private Environment environment;
    //@Bean
    //@ConditionalOnMissingBean(JobEventConfiguration.class)
    //public SpringJobSchedulerFactory springJobSchedulerFactory(
    //    ElasticJobProperties elasticJobProperties,
    //    ZookeeperRegistryCenter regCenter,
    //    JobEventConfiguration jobEventConfiguration) {
    //    return new SpringJobSchedulerFactory(elasticJobProperties, regCenter, jobEventConfiguration);
    //}
    //
    @Bean
    @ConditionalOnMissingBean(JobEventConfiguration.class)
    public SpringJobSchedulerFactory springJobSchedulerFactory(ElasticJobProperties elasticJobProperties,DataSource dataSource) {
        String zkAddr = environment.getProperty("takin.config.zk.addr");
        if (StringUtils.isEmpty(zkAddr)) {
            throw new RuntimeException("配置中心zk地址没有填写，请核对校验`takin.config.zk.addr`");
        }
        String env = Optional.ofNullable(environment.getProperty("env")).orElse("prod");

        ElasticJobConfig elasticJobConfig = new ElasticJobConfig();
        ZkClientConfig zkClientConfig = new ZkClientConfig();
        zkClientConfig.setZkServers(zkAddr);
        zkClientConfig.setNamespace("takin-web-job-" + env);
        elasticJobConfig.setZkClientConfig(zkClientConfig);
        ElasticRegCenterConfig elasticRegCenterConfig = new ElasticRegCenterConfig(elasticJobConfig);
        return new SpringJobSchedulerFactory(elasticJobProperties, elasticRegCenterConfig.regCenter());
    }

    @Bean
    public JobConfParser jobConfParser(SpringJobSchedulerFactory springJobSchedulerFactory){
        return new JobConfParser(springJobSchedulerFactory);
    }
}
