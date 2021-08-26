package io.shulie.takin.web.biz.pojo.response.perfomanceanaly;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaoyong
 */
@Data
public class TraceManageCreateResponse implements Serializable {
    private static final long serialVersionUID = -5575050208179696548L;

    @ApiModelProperty(value = "追踪凭证")
    private String sampleId;
}
