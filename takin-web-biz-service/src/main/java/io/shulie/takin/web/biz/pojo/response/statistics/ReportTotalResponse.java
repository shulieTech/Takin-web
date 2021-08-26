package io.shulie.takin.web.biz.pojo.response.statistics;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/11/30 9:21 下午
 */
@Data
public class ReportTotalResponse {
    @ApiModelProperty(value = "总数")
    private Integer count;
    @ApiModelProperty(value = "成功数")
    private Integer success;
    @ApiModelProperty(value = "失败数")
    private Integer fail;
}
