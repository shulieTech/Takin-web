package io.shulie.takin.web.data.param.domain;

import java.util.List;

import lombok.Data;

/**
 * @Author: fanxx
 * @Date: 2021/12/7 4:29 下午
 * @Description:
 */
@Data
public class BusinessDomainDeleteParam {
    private List<Long> ids;
}
