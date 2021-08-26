package io.shulie.takin.web.biz.pojo.response.application;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.common.enums.shadow.ShadowMqConsumerType;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-02-04
 */
@Data
public class ShadowConsumerResponse extends AuthQueryResponseCommonExt {

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

    private Integer deleted ;

    private String feature ;
}
