package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.shulie.takin.web.common.annocation.Trimmed;
import io.shulie.takin.web.common.annocation.Trimmed.TrimmerType;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 无涯
 * @date 2021/5/29 12:18 上午
 */
@Data
@NoArgsConstructor
public class AppRemoteCallCreateV2Request extends UserCommonExt {

    /**
     * 应用id
     */
    @ApiModelProperty(name = "applicationId", value = "应用id")
    @NotNull(message = "应用id不能为空")
    private Long applicationId;

    /**
     * 接口名称
     */
    @ApiModelProperty(name = "interfaceName", value = "接口名称")
    @Trimmed(value = TrimmerType.SIMPLE)
    @NotEmpty(message = "接口名称不能为空")
    private String interfaceName;

    /**
     * 接口类型
     */
    @ApiModelProperty(name = "interfaceType", value = "接口类型")
    @NotNull(message = "接口类型不能为空")
    private String interfaceType;

    /**
     * 服务端应用名
     */
    @ApiModelProperty(name = "serverAppName", value = "服务端应用名")
    private String serverAppName;

    /**
     * 配置类型，0：未配置，1：白名单配置;2：返回值mock;3:转发mock
     */
    @ApiModelProperty(name = "type", value = "配置类型")
    @NotNull(message = "配置类型不能为空")
    private Integer type;

    /**
     * mock内容
     */
    @ApiModelProperty(name = "mockValue", value = "mock内容")
    private String mockValue;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(name = "isManual", value = "是否手动录入")
    private Boolean isManual = true;

}
