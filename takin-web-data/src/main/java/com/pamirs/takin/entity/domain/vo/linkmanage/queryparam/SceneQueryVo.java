package com.pamirs.takin.entity.domain.vo.linkmanage.queryparam;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景查询入参
 *
 * @author vernon
 * @date 2019/12/2 10:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "SceneQueryVo", description = "场景查询入参")
public class SceneQueryVo extends PagingDevice {
    @ApiModelProperty(name = "sceneId", value = "场景id")
    private Long sceneId;
    @ApiModelProperty(name = "sceneName", value = "业务流程名字")
    private String sceneName;
    /*  @ApiModelProperty(name = "linkName", value = "链路名字")
      private String linkName;*/
    @ApiModelProperty(name = "entrace", value = "入口")
    private String entrace;
    @ApiModelProperty(name = "ischanged", value = "是否变更")
    private String ischanged;

    @ApiModelProperty(name = "businessName", value = "业务活动名字")
    private String businessName;

    @ApiModelProperty(name = "middleWareType", value = "中间件类型")
    private String middleWareType;

    @ApiModelProperty(name = "middleWareName", value = "中间件名字")
    private String middleWareName;

    @ApiModelProperty(name = "version", value = "中间件版本")
    private String middleWareVersion;

    /**
     * 业务活动类型
     */
    private Integer businessType;

}
