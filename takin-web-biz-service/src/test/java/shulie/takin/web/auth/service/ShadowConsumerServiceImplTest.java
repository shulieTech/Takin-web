package shulie.takin.web.auth.service;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.Test;

/**
 * @author 无涯
 * @date 2021/6/28 3:35 下午
 */
public class ShadowConsumerServiceImplTest {
    @Test
    public void test() {
        Map<String, Set<String>> topicGroupMap = Maps.newHashMap();
        String[] topicGroup = "fanout-exchange#".split("#");
        Set<String> groups = topicGroupMap.get(topicGroup[0]);
        if (groups == null) {
            topicGroupMap.put(topicGroup[0], Sets.newHashSet(topicGroup[1]));
        } else {
            groups.add(topicGroup[1]);
        }
    }
}
