package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description 插件状态对象
 * @Author ocean_wll
 * @Date 2021/8/19 2:53 下午
 */
@Data
public class PluginLoadListResponse {

    @ApiModelProperty("模块名")
    private String moduleId;

    @ApiModelProperty("加载状态")
    private String status;

    @ApiModelProperty("错误信息")
    private String errorMessage;
}
