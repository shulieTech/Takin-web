package com.pamirs.takin.entity.domain.vo.dsmanage;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/3/13 上午9:56
 */
@Data
@ApiModel(value = "DsDb", description = "影子库配置入参")
public class DsDb {
    @ApiModelProperty(name = "id", value = "配置id")
    private Long id;

    @ApiModelProperty(name = "applicationId", value = "应用id")
    private Long applicationId;

    @ApiModelProperty(name = "dsType", value = "库表类型，0：影子库 1：影子表")
    private Byte dsType;

    @ApiModelProperty(name = "relation", value = "关联关系")
    private String relation;

    @ApiModelProperty(name = "status", value = "状态")
    private Byte status;

    @ApiModelProperty(name = "config", value = "xml配置")
    private String config;

    @ApiModelProperty(name = "updateTime", value = "修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty(name = "canEdit", value = "是否可编辑")
    private Boolean canEdit = true;

    @ApiModelProperty(name = "canRemove", value = "是否可删除")
    private Boolean canRemove = true;

    @ApiModelProperty(name = "canEnableDisable", value = "是否启用禁用")
    private Boolean canEnableDisable = true;
}

