package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-17 09:59
 */

@Data
@ApiModel(value = "压力机删除入参模型")
public class PressureMachineDeleteRequest implements Serializable {

    private static final long serialVersionUID = 642932377834012998L;

    @ApiModelProperty(value = "id")
    private Long id;
}
