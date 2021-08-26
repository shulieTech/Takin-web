package io.shulie.takin.web.data.result.system;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * 系统信息明细VO
 * @author caijianying
 */
@Data
public class SystemInfoItemVo implements Serializable {
    @ApiModelProperty(value = "标题")
    private String title;
    @ApiModelProperty(value = "信息数据")
    private Map<String,String> dataMap;
}
