package io.shulie.takin.web.app;

import java.util.Arrays;
import java.util.List;

import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.common.util.DataTransformUtil;
import org.junit.Test;

/**
 * @author liuchuan
 * @date 2021/11/23 2:09 下午
 */
public class DataTransformUtilTest {

    @Test
    public void test() {
        List<Object> strings = Arrays.asList("1");
        System.out.println(JsonHelper.json2List(JsonHelper.bean2Json(strings), Long.class));
        System.out.println(DataTransformUtil.list2list(strings, Long.class));
    }

}
