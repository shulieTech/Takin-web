package com.pamirs.takin.entity.domain.vo.dsmanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/3/12 下午3:19
 */
@Data
@ApiModel(value = "DsVo", description = "影子库表配置入参")
public class DsVo {
    @ApiModelProperty(name = "id", value = "配置id")
    private Long id;

    @ApiModelProperty(name = "applicationId", value = "应用id")
    private Long applicationId;

    @ApiModelProperty(name = "applicationName", value = "应用名称")
    private String applicationName;

    @ApiModelProperty(name = "dbType", value = "存储类型，0：数据库 1：缓存")
    private Byte dbType;

    @ApiModelProperty(name = "dsType", value = "方案类型，0：影子库 1：影子表 2：影子server")
    private Byte dsType;

    @ApiModelProperty(name = "url", value = "数据库url")
    private String url;

    @ApiModelProperty(name = "status", value = "状态")
    private Byte status;

    @ApiModelProperty(name = "config", value = "配置")
    private String config;

    @ApiModelProperty(name = "userId", value = "用户ID")
    private Long userId;
}
