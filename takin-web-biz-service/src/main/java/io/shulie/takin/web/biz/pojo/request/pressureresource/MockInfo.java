package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/16 12:32 PM
 */
@Data
public class MockInfo {
    @ApiModelProperty("类型 0-json格式 1-脚本格式")
    private String type;

    @ApiModelProperty("mock数据")
    private String mockValue;

    @ApiModelProperty("响应时间")
    private Integer responseTime;
}
