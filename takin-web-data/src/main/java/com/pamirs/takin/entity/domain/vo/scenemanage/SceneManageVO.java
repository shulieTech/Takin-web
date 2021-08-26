package com.pamirs.takin.entity.domain.vo.scenemanage;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/4/17 下午4:43
 */
@Data
@ApiModel(value = "SceneManageVO", description = "场景基本信息")
public class SceneManageVO implements Serializable {

    private static final long serialVersionUID = 8025167061737917751L;

    @ApiModelProperty(name = "sceneName", value = "压测场景名称")
    private String sceneName;

}
