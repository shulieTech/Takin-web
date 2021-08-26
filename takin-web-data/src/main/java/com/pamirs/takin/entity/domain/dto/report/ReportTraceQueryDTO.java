package com.pamirs.takin.entity.domain.dto.report;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 压测报告；统计返回
 *
 * @author qianshui
 * @date 2020/7/22 下午2:19
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("入参类 -- 报告查询入参类")
@Data
public class ReportTraceQueryDTO extends PageBaseDTO {

    private static final long serialVersionUID = 8928035842416997931L;

    @ApiModelProperty("场景id")
    private Long sceneId;

    @ApiModelProperty("报告id，如果是压测报告中有此参数")
    private Long reportId;

    @ApiModelProperty("开始压测的时间戳")
    private Long startTime;

    @ApiModelProperty("查询条件，null 为全部，1为成功，0为失败, 2 断言失败")
    private Integer type;

}
