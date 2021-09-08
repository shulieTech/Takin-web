package io.shulie.takin.web.biz.pojo.input.application;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author 南风
 * @date 2021/09/02 3:23 下午
 */
@Data
@Valid
public class ApplicationDsUpdateInputV2 {

    /**
     * 配置id
     */
    @NotNull
    private Long id;

    /**
     * 应用id
     */
    @NotNull
    @ApiModelProperty(name = "applicationId", value = "应用id")
    private Long applicationId;

    /**
     * 中间件类型
     */
    @NotNull
    @ApiModelProperty(name = "middlewareType", value = "中间件类型")
    private String middlewareType;

    /**
     * 方案类型 DsTypeEnum.Class
     */
    @NotNull
    @ApiModelProperty(name = "dsType", value = "方案类型")
    private String dsType;

    @ApiModelProperty(name = "shaDowUrl", value = "影子数据源")
    private String shaDowUrl;

    @ApiModelProperty(name = "shaDowUserName", value = "影子数据源用户名")
    private String shaDowUserName;

    @ApiModelProperty(name = "shaDowPassword", value = "影子数据源密码")
    private String shaDowPassword;


    /**
     * 其他配置信息
     */
    @NotNull
    @ApiModelProperty(name = "extInfo", value = "其他配置信息")
    private String extInfo;


}
