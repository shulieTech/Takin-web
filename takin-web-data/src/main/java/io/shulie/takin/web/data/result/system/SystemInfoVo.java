package io.shulie.takin.web.data.result.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 系统信息VO
 * @author caijianying
 */
@Data
public class SystemInfoVo implements Serializable {
    @ApiModelProperty(value = "显示的数据集合")
    private List<SystemInfoItemVo> itemVos;
}
