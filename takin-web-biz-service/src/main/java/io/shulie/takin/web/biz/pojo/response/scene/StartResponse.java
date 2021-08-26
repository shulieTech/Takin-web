package io.shulie.takin.web.biz.pojo.response.scene;

import java.util.List;

import lombok.Data;
import io.swagger.annotations.ApiModelProperty;

/**
 * takin-cloud启动压测场景响应
 *
 * @author 张天赐
 */
@Data
public class StartResponse {

    @ApiModelProperty(value = "状态值")
    private Long data;

    @ApiModelProperty(value = "报告ID")
    private Long reportId;

    @ApiModelProperty(value = "错误信息")
    private List<String> msg;
}
