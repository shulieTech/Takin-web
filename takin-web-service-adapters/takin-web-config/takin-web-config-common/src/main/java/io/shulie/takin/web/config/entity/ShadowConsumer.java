package io.shulie.takin.web.config.entity;

import java.io.Serializable;


/**
 * @author shiyajian
 * create: 2020-09-15
 */
public class ShadowConsumer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String type;

    private String group;

    private String topic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
