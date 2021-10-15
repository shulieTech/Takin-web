package io.shulie.takin.web.ext.entity;

import java.util.List;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/8/2 2:41 下午
 */
@Data
public class AuthQueryParamCommonExt extends TenantCommonExt {
    private List<Long> userIdList;
}
