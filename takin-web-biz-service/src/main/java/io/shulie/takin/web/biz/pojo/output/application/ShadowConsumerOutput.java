package io.shulie.takin.web.biz.pojo.output.application;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.common.enums.shadow.ShadowMqConsumerType;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @date 2021/8/6 11:52
 */
@Data
public class ShadowConsumerOutput extends AuthQueryResponseCommonExt {
    private Long id;

    /**
     * AMDB梳理的没有入库，所有没有id
     */
    private String unionId;

    private ShadowMqConsumerType type;

    private String topicGroup;

    private Boolean enabled;

    private Date gmtCreate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
