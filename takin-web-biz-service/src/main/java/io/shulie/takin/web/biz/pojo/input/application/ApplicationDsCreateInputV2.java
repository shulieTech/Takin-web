package io.shulie.takin.web.biz.pojo.input.application;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.swagger.annotations.ApiModel;
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
@ApiModel
public class ApplicationDsCreateInputV2 extends UserCommonExt {


    /**
     * 应用id
     */
    @NotNull
    @ApiModelProperty(name = "applicationId", value = "应用id",required =true)
    private Long applicationId;

    /**
     * 中间件类型
     */
    @NotNull
    @ApiModelProperty(name = "middlewareType", value = "中间件类型",required =true)
    private String middlewareType;

    @ApiModelProperty(name = "cacheType", value = "缓存模式")
    private String cacheType;

    @ApiModelProperty(name = "url", value = "业务数据源")
    private String url;

    /**
     * 方案类型 DsTypeEnum.Class
     */
    @NotNull
    @ApiModelProperty(name = "dsType", value = "方案类型",required =true)
    private Integer dsType;

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
    @ApiModelProperty(name = "extInfo", value = "其他配置信息",required =true)
    private String extInfo;

    /**
     * 其他配置信息
     */
    @ApiModelProperty(name = "agentSourceType",required =true)
    private String agentSourceType;

    @ApiModelProperty(name = "connectionPool",value = "中间件名称",required =true)
    private String connectionPool;

    /**
     * 应用名称
     */
    @ApiModelProperty(name = "applicationName", value = "应用名称",required =true)
    private String applicationName;


    @ApiModelProperty(name = "username", value = "业务数据源用户名")
    private String username;


    /**
     * 解析后配置
     * 老数据切换用
     */
    private String parseConfig;


}
