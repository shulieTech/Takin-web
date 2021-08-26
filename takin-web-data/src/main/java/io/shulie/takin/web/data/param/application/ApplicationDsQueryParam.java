package io.shulie.takin.web.data.param.application;

import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 10:46 上午
 */
@Data
public class ApplicationDsQueryParam extends AuthQueryParamCommonExt {
    private Long applicationId;
    private Integer status;
    private Integer isDeleted;
    private String url;
}
