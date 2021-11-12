package io.shulie.takin.web.data.param.agentupgradeonline;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/11/12 5:10 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PluginLibraryListQueryParam extends PageBaseDTO {

    /**
     * 租户信息
     */
    private String tenantId;

    /**
     * 插件名称
     */
    private String pluginName;
}
