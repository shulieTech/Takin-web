package com.pamirs.takin.entity.domain.dto.linkmanage;

import com.pamirs.takin.entity.domain.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author fanxx
 * @date 2020/8/17 下午9:42
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "InterfaceVo", description = "应用接口模型")
public class InterfaceVo extends BaseEntity {

    @ApiModelProperty(name = "id", value = "接口ID")
    private String id;

    @ApiModelProperty(name = "interfaceType", value = "接口类型")
    private String interfaceType;

    @ApiModelProperty(name = "interfaceName", value = "接口名称")
    private String interfaceName;
}
