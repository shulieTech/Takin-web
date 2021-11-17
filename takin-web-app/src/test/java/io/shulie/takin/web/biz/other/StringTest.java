package io.shulie.takin.web.biz.other;

import org.junit.Test;

/**
 * @author liuchuan
 * @date 2021/11/11 9:46 下午
 */
public class StringTest {

    @Test
    public void testSub() {
        String a = "/pradar/config/";
        String b = "/pradar/config/rt/test";
        System.out.printf("%s-1/system/%s%n", a, b.substring(a.length()));
    }

}
