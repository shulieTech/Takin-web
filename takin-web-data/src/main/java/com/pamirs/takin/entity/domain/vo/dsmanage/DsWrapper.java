package com.pamirs.takin.entity.domain.vo.dsmanage;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/3/13 上午10:48
 */
@Data
@ApiModel(value = "DsWrapper", description = "影子库表包装对象")
public class DsWrapper {
    @ApiModelProperty(name = "dsType", value = "库表类型，0：影子库 1：影子表")
    private Byte dsType;

    @ApiModelProperty(name = "result", value = "结果集")
    private List result;
}
