package io.shulie.takin.web.app;

import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2022/7/5 9:36 上午
 */
@Component
@Slf4j
public class ClearDsDataConfig implements ApplicationRunner {

    @Autowired
    private List<AbstractAgentConfigCache> cacheList;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //log.info("spring启动成功后重新清理缓存");
        //cacheList.forEach(AbstractAgentConfigCache::reset);
    }
}
