package com.pamirs.takin.entity.domain.dto.linkmanage;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author  vernon
 * @date 2019/11/29 15:57
 */
@Data
@ApiModel(value = "SceneDto", description = "场景出参")
public class SceneDto extends AuthQueryResponseCommonExt implements Serializable {
    @ApiModelProperty(name = "id", value = "场景id")
    private Long id;

    @ApiModelProperty(name = "sceneName", value = "场景名字")
    private String sceneName;

    @ApiModelProperty(name = "businessLinkCount", value = "业务活动条数")
    private int businessLinkCount;

    @ApiModelProperty(name = "techLinkCount", value = "系统流程条数")
    private int techLinkCount;

    @ApiModelProperty(name = "ischange", value = "是否变更")
    private String ischange;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty(name = "businessType", value = "虚拟业务活动 1")
    private Integer businessType;


}
