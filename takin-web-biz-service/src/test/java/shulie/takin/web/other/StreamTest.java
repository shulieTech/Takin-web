package shulie.takin.web.other;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import io.shulie.takin.web.common.constant.AppConstants;
import org.junit.Test;

/**
 * @author liuchuan
 * @date 2021/5/18 2:01 下午
 */
public class StreamTest {

    @Test
    public void testTimestamp() {
        Date date = new Date();
        DateTime date1 = DateUtil.date();
        System.out.println(System.currentTimeMillis());
        System.out.println(new Date().getTime());
    }

    @Test
    public void testSub() {
        String a = "/nfs_dir/83/test.jmx";
        String substring = a.substring(1);
        System.out.println(substring.substring(substring.indexOf("/") + 1));
    }

    @Test
    public void testJoin() {
        List<String> strings = Arrays.asList("");
        System.out.println(
            strings.stream().filter(StrUtil::isNotBlank).collect(Collectors.joining(AppConstants.COMMA)));
    }

    @Test
    public void testJoin2() {
        String a = "a|b|";
        String[] split = a.split("\\|");
        System.out.println(Arrays.toString(split));
    }

}
