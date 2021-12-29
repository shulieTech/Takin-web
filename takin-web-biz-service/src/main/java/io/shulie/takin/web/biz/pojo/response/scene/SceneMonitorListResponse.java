package io.shulie.takin.web.biz.pojo.response.scene;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 第三方登录服务表(SceneMonitor)controller 详情响应类
 *
 * @author liuchuan
 * @date 2021-12-29 10:26:30
 */
@ApiModel("出参类-列表出参")
@Data
public class SceneMonitorListResponse {

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("跳转链接")
    private String url;

}
