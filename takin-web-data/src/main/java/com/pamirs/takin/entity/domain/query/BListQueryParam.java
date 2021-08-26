package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/6/9 下午3:03
 */
@Data
public class BListQueryParam implements Serializable {

    private static final long serialVersionUID = 2274059677599648592L;

    private String redisKey;

    private Integer currentPage;

    private Integer pageSize;
}
