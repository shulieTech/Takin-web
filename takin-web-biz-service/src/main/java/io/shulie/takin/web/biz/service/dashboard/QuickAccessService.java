package io.shulie.takin.web.biz.service.dashboard;

import java.util.List;

import io.shulie.takin.web.biz.pojo.response.dashboard.QuickAccessResponse;

public interface QuickAccessService {
    /**
     * 列出用户设置的快捷入口
     *
     * @return 快捷入口列表
     */
    List<QuickAccessResponse> list();
}
