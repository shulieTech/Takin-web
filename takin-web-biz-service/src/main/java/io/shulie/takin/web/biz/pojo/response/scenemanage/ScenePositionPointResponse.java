package io.shulie.takin.web.biz.pojo.response.scenemanage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("位点信息Response")
@Data
public class ScenePositionPointResponse implements Serializable {
    /**
     * 脚本名称
     */
    @ApiModelProperty(value = "脚本名称")
    private String scriptName;
    /**
     * 脚本大小
     */
    @ApiModelProperty(value = "脚本大小")
    private String scriptSize;
    /**
     * 已压测大小
     */
    @ApiModelProperty(value = "已压测大小")
    private String pressedSize;

}
