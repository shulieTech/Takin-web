package io.shulie.takin.web.config.sync.api;

import java.util.List;

import io.shulie.takin.web.config.entity.AllowList;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * 白名单 ( dubbo 、http )
 * 当应用发起调用的时候，只有白名单里面的接口，才能调用成功，否则熔断
 * 为了避免实际压测中，因为梳理遗漏，导致流量发送到一个未知接口可能带来的各种问题
 *
 * @author shiyajian
 * create: 2020-09-01
 */
public interface AllowListSyncService {

    void syncAllowList(TenantCommonExt commonExt, String applicationName, List<AllowList> allows);

}
