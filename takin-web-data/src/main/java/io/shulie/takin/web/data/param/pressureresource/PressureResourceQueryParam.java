package io.shulie.takin.web.data.param.pressureresource;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
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

    private List<Long> userIdList;
}
