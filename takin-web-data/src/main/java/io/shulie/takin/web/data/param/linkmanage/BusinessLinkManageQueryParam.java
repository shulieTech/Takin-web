package io.shulie.takin.web.data.param.linkmanage;

import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/15 10:45 上午
 */
@Data
public class BusinessLinkManageQueryParam extends AuthQueryParamCommonExt {
    private String bussinessActiveName;
    private Boolean persistence;
}
