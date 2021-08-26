package io.shulie.takin.web.config.entity;

import java.io.Serializable;

import io.shulie.takin.web.config.enums.ShadowConsumerType;

/**
 * @author shiyajian
 * create: 2020-09-15
 */
public class ShadowConsumer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private ShadowConsumerType type;

    private String group;

    private String topic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ShadowConsumerType getType() {
        return type;
    }

    public void setType(ShadowConsumerType type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
