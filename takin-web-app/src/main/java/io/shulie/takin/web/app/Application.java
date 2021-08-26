package io.shulie.takin.web.app;

import com.pamirs.takin.common.util.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
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
    "io.shulie.takin.web.data.convert.*"
})
@EnableAspectJAutoProxy
@SpringBootApplication
@ComponentScan(basePackages = {"com.pamirs.takin", "io.shulie.takin"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, value = {ApplicationFilter.class}))
public class Application {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new SpringApplicationBuilder().sources(Application.class).run(args);
        SpringContextUtil.setApplicationContext(applicationContext);

    }
}
