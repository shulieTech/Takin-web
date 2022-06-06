package io.shulie.takin.web.data.param.interfaceperformance;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 2:56 下午
 */
@Data
public class PerformanceConfigQueryParam extends PageBaseDTO {
    private String queryName;

    private List<Long> userIdList;
}
