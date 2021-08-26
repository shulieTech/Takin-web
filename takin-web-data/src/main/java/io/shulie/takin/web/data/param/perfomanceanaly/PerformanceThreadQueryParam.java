package io.shulie.takin.web.data.param.perfomanceanaly;

import java.util.List;

import lombok.Data;

/**
* @author qianshui
 * @date 2020/11/10 下午3:59
 */
@Data
public class PerformanceThreadQueryParam {

    private String baseId;

    private List<String> baseIds;
}
