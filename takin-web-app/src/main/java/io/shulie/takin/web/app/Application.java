package io.shulie.takin.web.app;

import com.pamirs.takin.common.util.SpringContextUtil;
import io.shulie.takin.web.common.util.RedisHelper;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author shulie
 * @date 2019-07-23 18:30
 */
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@MapperScan({
    "com.pamirs.takin.*.dao",
    "io.shulie.takin.web.data.mapper.mysql",
    "io.shulie.takin.web.data.dao.statistics",
    "io.shulie.takin.web.data.convert.*",
    "io.shulie.takin.cloud.data.mapper.mysql",
    "io.shulie.takin.cloud.data.dao.statistics",
    "com.pamirs.takin.cloud.*.dao"
})
@EnableAspectJAutoProxy
@SpringBootApplication
@ComponentScan(basePackages = {"com.pamirs.takin", "io.shulie.takin"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, value = {ApplicationFilter.class}))
@EnableCaching
@ServletComponentScan
@EnableRetry
public class Application {

    /**
     * 这里引用, 提前实例化
     */
    @Autowired
    private RedisHelper redisHelper;

    /**
     * 这里引用, 提前实例化
     */
    @Autowired
    private ConfigServerHelper configServerHelper;

    public static void main(String[] args) {
//        PreparedStatementHandler.init();
        ApplicationContext applicationContext = new SpringApplicationBuilder().sources(Application.class).run(args);
        SpringContextUtil.setApplicationContext(applicationContext);


    }
}
