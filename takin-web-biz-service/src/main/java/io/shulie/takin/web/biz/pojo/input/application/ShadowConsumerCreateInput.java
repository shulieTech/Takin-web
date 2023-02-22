package io.shulie.takin.web.biz.pojo.input.application;

import io.shulie.takin.web.common.annocation.Trimmed;
import io.shulie.takin.web.common.annocation.Trimmed.TrimmerType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author shiyajian
 * create: 2021-02-05
 */
@Data
public class ShadowConsumerCreateInput {

    @Trimmed(value = TrimmerType.SIMPLE)
    private String topicGroup;

    @NotNull
    @Trimmed(value = TrimmerType.SIMPLE)
    private String topic;

    @NotNull
    @Trimmed(value = TrimmerType.SIMPLE)
    private String group;

    @Trimmed(value = TrimmerType.SIMPLE)
    private String customizeTopic;

    @Trimmed(value = TrimmerType.SIMPLE)
    private String customizeGroup;

    @NotNull
    private String type;

    @NotNull
    private Long applicationId;

    /**
     * 老版本用
     * 是否可用
     */
    private Integer status;

    /**
     * 快速接入版本用
     * 1 消费/ 0 不消费影子topic
     */
    private String shadowconsumerEnable;

    /**
     * 来源标识
     */
    private Integer manualTag;

    public String getTopicGroup() {
        return topic + "#" + group;
    }
}
