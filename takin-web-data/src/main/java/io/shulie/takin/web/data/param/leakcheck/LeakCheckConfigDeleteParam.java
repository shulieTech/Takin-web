package io.shulie.takin.web.data.param.leakcheck;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/31 2:34 下午
 */
@Data
public class LeakCheckConfigDeleteParam {

    private List<Long> ids;
}
