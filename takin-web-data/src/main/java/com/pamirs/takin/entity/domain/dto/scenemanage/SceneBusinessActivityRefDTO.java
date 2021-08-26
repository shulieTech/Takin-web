package com.pamirs.takin.entity.domain.dto.scenemanage;

import com.pamirs.takin.entity.domain.vo.scenemanage.SceneBusinessActivityRefVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
* @author qianshui
 * @date 2020/4/17 下午9:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SceneBusinessActivityRefDTO extends SceneBusinessActivityRefVO {

    private static final long serialVersionUID = -6384484202725660595L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "绑定关系")
    private String bindRef;

    @ApiModelProperty(value = "应用IDS")
    private String applicationIds;

}
