package io.shulie.takin.web.biz.pojo.input.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class NodeCompareTargetInput {
    @ApiModelProperty("报告id列表")
    private List<Long> reportIds;
    @ApiModelProperty("节点id")
    private Long activityId;
}
