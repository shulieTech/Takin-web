package io.shulie.takin.web.app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import org.junit.Test;

/**
 * @author liuchuan
 * @date 2021/11/23 2:09 下午
 */
public class DataTransformUtilTest {

    @Test
    public void testSb() {
        StringBuilder sb = new StringBuilder();
        sb.append("test, ");
        sb.delete(sb.length() - 2, sb.length());
        System.out.println(sb.toString());
    }

    @Test
    public void test() {
        List<Object> strings = Arrays.asList("1");
        System.out.println(JsonHelper.json2List(JsonHelper.bean2Json(strings), Long.class));
        System.out.println(DataTransformUtil.list2list(strings, Long.class));
    }

    @Test
    public void testStream() {
        List<ApplicationDetailResult> list = new ArrayList<>();
        Map<String, ApplicationDetailResult> map1 = list.stream()
            .collect(Collectors.toMap(ApplicationDetailResult::getApplicationName, Function.identity(),
                (v1, v2) -> v2));

        Map<String, List<ApplicationDetailResult>> collect1 = list.stream()
            .collect(Collectors.groupingBy(ApplicationDetailResult::getApplicationName));
        System.out.println(map1);
        System.out.println(collect1);

        ApplicationDetailResult applicationDetailResult = new ApplicationDetailResult();
        applicationDetailResult.setApplicationName("a");
        ApplicationDetailResult applicationDetailResult2 = new ApplicationDetailResult();
        applicationDetailResult2.setApplicationName("b");
        list.add(applicationDetailResult);
        list.add(applicationDetailResult2);
        Map<String, ApplicationDetailResult> map2 = list.stream()
            .collect(Collectors.toMap(ApplicationDetailResult::getApplicationName, Function.identity(),
                (v1, v2) -> v2));
        Map<String, List<ApplicationDetailResult>> collect2 = list.stream()
            .collect(Collectors.groupingBy(ApplicationDetailResult::getApplicationName));
        System.out.println(map2);
        System.out.println(collect2);

    }
}
