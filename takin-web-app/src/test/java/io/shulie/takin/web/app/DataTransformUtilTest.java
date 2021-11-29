package io.shulie.takin.web.app;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.common.util.DataTransformUtil;
import jodd.util.concurrent.ThreadFactoryBuilder;
import org.junit.Test;

/**
 * @author liuchuan
 * @date 2021/11/23 2:09 下午
 */
public class DataTransformUtilTest {

    @Test
    public void test() throws IOException {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("job-%d").get();
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5,
            nameThreadFactory);
        executor.scheduleWithFixedDelay(()->{
            System.out.println(Thread.currentThread().getName());
        },0, 5,TimeUnit.SECONDS);

        System.in.read();
    }

}
