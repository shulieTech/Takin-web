package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class TraceManageDeployQueryRequest implements Serializable {
    private static final long serialVersionUID = 3858196416002264786L;

    /**
     * 传入traceManageId 查询所有信息
     */
    @ApiModelProperty(value = "传入traceManageId 查询所有信息")
    private Long id;

    /**
     * 追踪凭证id
     */
    @ApiModelProperty(value = "追踪凭证id")
    private String sampleId;
}
