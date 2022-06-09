package io.shulie.takin.web.biz.pojo.request.linkmanage;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("业务流程匹配业务活动入参")
public class SceneLinkRelateRequest implements Serializable {

    @ApiModelProperty("关联关系主键")
    private Long id;

    @NotNull(message = "业务流程ID 不能为空")
    @ApiModelProperty("业务流程ID")
    private Long businessFlowId;

    @NotNull(message = "脚本请求路径标识 不能为空")
    @ApiModelProperty("脚本请求路径标识")
    private String identification;

    @NotNull(message = "脚本请求路径标识 不能为空")
    @ApiModelProperty("脚本路径的MD5")
    private String xpathMd5;

    @ApiModelProperty("节点上的名称")
    private String testName;

    /*****业务活动相关*******/

    @ApiModelProperty("应用名")
    private String applicationName;

    @ApiModelProperty("入口entrance")
    private String entrance;

    @ApiModelProperty("业务活动ID")
    private Long businessActivityId;

    @ApiModelProperty("业务活动名称")
    private String activityName;

    @NotNull(message = "请求类型 不能为空")
    @ApiModelProperty("请求类型")
    private SamplerTypeEnum samplerType;

    @NotNull(message = "业务活动类型 不能为空")
    @ApiModelProperty("业务活动类型")
    private Integer businessType;

    /*****业务活动相关*******/
    @ApiModelProperty("请求ptah")
    private String path;
    @ApiModelProperty("入口ptah")
    private String entracePath;
}
