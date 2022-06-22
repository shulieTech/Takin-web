package io.shulie.takin.adapter.api.model.request.report;

import io.shulie.takin.cloud.ext.content.trace.PagingContextExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author shiyajian
 * create: 2020-10-20
 */

@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class ReportQueryReq extends PagingContextExt {
    /**
     * 场景主键
     */
    @ApiModelProperty(name = "sceneId", value = "场景ID")
    private Long sceneId;
    /**
     * 报告主键
     */
    @ApiModelProperty(name = "reportId", value = "报告ID")
    private Long reportId;
    /**
     * 场景名称
     */
    @ApiModelProperty(name = "sceneName", value = "场景名称")
    private String sceneName;

    /**
     * 压测开始时间
     */
    @ApiModelProperty(value = "压测开始时间")
    private String startTime;

    /**
     * 压测结束时间
     */
    @ApiModelProperty(value = "压测结束时间")
    private String endTime;
    /**
     * 报告类型
     * <ul>
     *     <li>0:普通场景</li>
     *     <li>1:流量调试</li>
     * </ul>
     */
    @ApiModelProperty(value = "报告类型")
    private Integer type;

}
