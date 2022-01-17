package io.shulie.takin.web.biz.pojo.request.scene;

import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.shulie.takin.web.common.constant.AppConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/8 3:05 下午
 */
@Data
@ApiModel("入参类 --> 场景运行的报告列表入参类")
public class ListSceneReportRequest {

    @ApiModelProperty(value = "场景ids", required = true)
    @NotEmpty(message = "场景ids" + AppConstants.MUST_BE_NOT_NULL)
    private List<Long> sceneIds;

    public void setSceneIds(List<Long> sceneIds) {
        // 大于50个要截取
        if (sceneIds != null && sceneIds.size() > 50) {
            sceneIds = sceneIds.subList(0, 50);
        }

        this.sceneIds = sceneIds;
    }

}