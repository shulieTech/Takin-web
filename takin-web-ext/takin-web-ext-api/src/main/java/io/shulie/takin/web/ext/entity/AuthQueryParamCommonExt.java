package io.shulie.takin.web.ext.entity;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * @author fanxx
 * @date 2021/8/2 2:41 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthQueryParamCommonExt extends TenantCommonExt {

    private List<Long> userIdList;

    /**
     * 允许的id列表
     */
    private List<Long> deptIdList;
}
