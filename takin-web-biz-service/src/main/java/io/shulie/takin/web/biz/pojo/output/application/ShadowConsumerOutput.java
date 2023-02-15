package io.shulie.takin.web.biz.pojo.output.application;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * @author by: hezhongqi
 * @date 2021/8/6 11:52
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ShadowConsumerOutput extends AuthQueryResponseCommonExt {
    private Long id;

    /**
     * AMDB梳理的没有入库，所有没有id
     */
    private String unionId;

    private String type;

    private String topicGroup;

    private String topic;

    private String group;

    private String customizeTopic;

    private String customizeGroup;

    private Boolean enabled;

    private Date gmtCreate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtUpdate;

    private Integer deleted;

    private String feature;

    /**
     * 是否是手动录入的
     */
    private Boolean isManual;

    /**
     * 1 消费/ 0 不消费影子topic
     */
    private String shadowconsumerEnable;
}
