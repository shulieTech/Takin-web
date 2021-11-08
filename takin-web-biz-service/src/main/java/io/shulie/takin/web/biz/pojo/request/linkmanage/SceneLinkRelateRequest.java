package io.shulie.takin.web.biz.pojo.request.linkmanage;

import io.shulie.takin.ext.content.emus.SamplerTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel("业务流程匹配业务活动入参")
public class SceneLinkRelateRequest implements Serializable {

    @ApiModelProperty("关联关系主键")
    private Long id;

    @NotNull
    @ApiModelProperty("业务流程ID")
    private Long sceneId;

    @NotNull
    @ApiModelProperty("脚本请求路径标识")
    private String scriptIdentification;

    @NotNull
    @ApiModelProperty("脚本路径的MD5")
    private String scriptXpathMd5;

    /*****业务活动相关*******/

    @ApiModelProperty("应用名")
    private String applicationName;

    @NotNull
    @ApiModelProperty("入口entrance")
    private String entrance;

    @ApiModelProperty("业务活动ID")
    private Long businessLinkId;

    @ApiModelProperty("业务活动名称")
    private String activityName;

    @NotNull
    @ApiModelProperty("请求类型")
    private SamplerTypeEnum type;

    @NotNull
    @ApiModelProperty("业务活动类型")
    private Integer businessType;

    /*****业务活动相关*******/
}
