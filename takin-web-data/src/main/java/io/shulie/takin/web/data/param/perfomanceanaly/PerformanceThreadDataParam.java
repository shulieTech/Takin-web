package io.shulie.takin.web.data.param.perfomanceanaly;

import io.shulie.takin.web.common.vo.perfomanceanaly.PerformanceThreadDataVO;
import lombok.Data;

/**
* @author qianshui
 * @date 2020/11/4 下午2:35
 */
@Data
public class PerformanceThreadDataParam  extends PerformanceThreadDataVO {
    private Long threadStackLink;
}
