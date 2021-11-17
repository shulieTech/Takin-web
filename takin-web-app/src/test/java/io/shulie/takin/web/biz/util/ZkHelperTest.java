package io.shulie.takin.web.biz.util;

import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.utils.ZkHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author liuchuan
 * @date 2021/11/11 9:12 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ZkHelperTest {

    @Autowired
    private ZkHelper zkHelper;

    @Test
    public void test() {
        zkHelper.addPersistentNode("/pradar/config/rt/hdfsDisable2", "test");
        zkHelper.addPersistentNode("/pradar/config/rt/hdfsDisable2", "test");
    }

}
