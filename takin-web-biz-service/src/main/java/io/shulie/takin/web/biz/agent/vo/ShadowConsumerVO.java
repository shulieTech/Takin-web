package io.shulie.takin.web.biz.agent.vo;

import java.util.Map;
import java.util.Set;

import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-02-07
 */
@Data
public class ShadowConsumerVO {

    private String type;

    private Map<String, Set<String>> topicGroups;

}
