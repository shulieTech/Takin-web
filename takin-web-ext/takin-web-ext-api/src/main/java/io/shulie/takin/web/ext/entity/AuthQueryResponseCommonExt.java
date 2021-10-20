package io.shulie.takin.web.ext.entity;

import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/8/2 3:10 下午
 */
@Data
public class AuthQueryResponseCommonExt extends UserCommonExt {

    @ApiModelProperty(name = "canEdit", value = "是否可编辑")
    private Boolean canEdit = true;

    @ApiModelProperty(name = "canRemove", value = "是否可删除")
    private Boolean canRemove = true;

    @ApiModelProperty(name = "canEnableDisable", value = "是否启用禁用")
    private Boolean canEnableDisable = true;

    @ApiModelProperty(name = "canStartStop", value = "是否可启动停止")
    private Boolean canStartStop = true;

    @ApiModelProperty(value = "当前用户是否有下载权限")
    private Boolean canDownload = true;

    @ApiModelProperty("是否可以调试, 1 是, 0 否")
    private Integer canDebug = 1;

    /**
     * 控制访问权限方法
     */
    public void permissionControl(){
        WebPluginUtils.fillQueryResponse(this);
    }

}
