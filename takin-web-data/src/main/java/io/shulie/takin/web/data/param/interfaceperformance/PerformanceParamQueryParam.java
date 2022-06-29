package io.shulie.takin.web.data.param.interfaceperformance;

import lombok.Data;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 2:56 下午
 */
@Data
public class PerformanceParamQueryParam {
    private Long configId;

    private List<Long> fileIds;
}
