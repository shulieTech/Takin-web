package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@ToString(callSuper = true)
public class MockDetailVO {
    @ApiModelProperty("平均响应时间")
    private String responseTime;

    @ApiModelProperty("请求模板")
    private List<String> request;
}

