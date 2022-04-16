package com.pamirs.takin.cloud.entity.domain.vo.scenemanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/17 下午4:43
 */
@Data
@ApiModel(value = "SceneManageVO", description = "场景基本信息")
public class SceneManageVO {

    @ApiModelProperty(name = "sceneName", value = "压测场景名称")
    private String sceneName;

}
