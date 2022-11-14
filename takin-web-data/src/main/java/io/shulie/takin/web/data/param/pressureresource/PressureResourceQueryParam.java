package io.shulie.takin.web.data.param.pressureresource;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 2:56 下午
 */
@Data
public class PressureResourceQueryParam extends PageBaseDTO {
    private String name;

    @ApiModelProperty("来源ID")
    private Long sourceId;

    private List<Long> userIdList;
}
