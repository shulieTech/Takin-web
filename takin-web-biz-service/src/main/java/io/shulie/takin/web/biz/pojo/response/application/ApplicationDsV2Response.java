package io.shulie.takin.web.biz.pojo.response.application;

import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 9:44 上午
 */
@Data
@ApiModel("影子库表列表返回类")
public class ApplicationDsV2Response extends AuthQueryResponseCommonExt {
    /**
     * 配置id
     */
    @ApiModelProperty("配置id")
    private Long id;

    /**
     * 应用id
     */
    @ApiModelProperty("应用id")
    private String applicationId;

    /**
     * 中间件类型
     */
    @ApiModelProperty("中间件类型")
    private String middlewareType;

    /**
     * 隔离类型
     */
    @ApiModelProperty("隔离类型")
    private String dsType;

    /**
     * 连接池地址
     */
    @ApiModelProperty("连接池地址")
    private String url;

    /**
     * 连接池名称
     */
    @ApiModelProperty("连接池名称")
    private String connectionPool;

    /**
     * 附加信息
     */
    @ApiModelProperty("附加信息")
    private String extMsg;

    /**
     /**
     * 是否是手动录入的
     */
    @ApiModelProperty("是否是手动录入的")
    private Boolean isManual;

    @ApiModelProperty("agent上报类型")
    private String agentSourceType;

    @ApiModelProperty("缓存模式")
    private String cacheType;

    @ApiModelProperty("是否展示新详情页面")
    private Boolean isNewPage =  false;

    @ApiModelProperty("是否是新的数据通道")
    private Boolean isNewData = false;

    @ApiModelProperty("禁用标识 0=启用/1=禁用")
    private Integer status;
}
