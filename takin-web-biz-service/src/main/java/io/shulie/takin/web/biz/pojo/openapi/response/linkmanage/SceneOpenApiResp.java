package io.shulie.takin.web.biz.pojo.openapi.response.linkmanage;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
@ApiModel(value = "SceneOpenApiResp", description = "场景出参")
public class SceneOpenApiResp extends AuthQueryResponseCommonExt implements Serializable {

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

}
