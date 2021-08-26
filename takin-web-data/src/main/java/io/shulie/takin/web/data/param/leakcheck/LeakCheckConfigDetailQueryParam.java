package io.shulie.takin.web.data.param.leakcheck;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/31 2:33 下午
 */
@Data
public class LeakCheckConfigDetailQueryParam {
    private List<Long> datasourceIds;
    private List<Long> ids;
}
