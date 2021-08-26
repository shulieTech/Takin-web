package io.shulie.takin.web.data.param.application;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 6:03 下午
 */
@Data
public class ApplicationDsDeleteParam {
    private List<Long> idList;
}
