package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shulie
 */
@Data
public class TraceManageCreateRequest implements Serializable {
    private static final long serialVersionUID = 390682552754253293L;

    /**
     * 追踪实例id，如果是第一次，则没有，如果是某个方法的子方法，则有
     */
    @ApiModelProperty(value = "追踪实例id")
    private Long traceManageDeployId;

    /**
     * 追踪对象
     */
    @ApiModelProperty(value = "追踪对象")
    @NotNull
    private String traceObject;

    /**
     * 报告id
     */
    @NotNull
    @ApiModelProperty(value = "报告id")
    private Long reportId;

    /**
     * 进程名称
     */
    @NotNull
    @ApiModelProperty(value = "进程名称")
    private String processName;

}
