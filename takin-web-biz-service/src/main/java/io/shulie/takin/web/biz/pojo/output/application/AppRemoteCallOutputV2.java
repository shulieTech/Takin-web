package io.shulie.takin.web.biz.pojo.output.application;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/5/29 12:18 上午
 */
@Data
public class AppRemoteCallOutputV2 extends UserCommonExt {
    @ApiModelProperty(name = "id", value = "id")
    private Long id;


    /**
     * 应用id
     */
    @ApiModelProperty(name = "applicationId", value = "应用id")
    private String applicationId;

    /**
     * 接口名称
     */
    @ApiModelProperty(name = "interfaceName", value = "接口名称")
    private String interfaceName;

    /**
     * 接口类型
     */
    @ApiModelProperty(name = "interfaceType", value = "接口类型")
    private String interfaceType;


    /**
     * 配置类型，0：未配置，1：白名单配置;2：返回值mock;3:转发mock
     */
    @ApiModelProperty(name = "type", value = "配置类型：0：未配置，1：白名单配置;2：返回值mock;3:转发mock")
    private Integer type;


    /**
     * mock内容
     */
    @ApiModelProperty(name = "mockValue", value = "mock内容")
    private String mockValue;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;
}
