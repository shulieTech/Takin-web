package io.shulie.takin.web.common.pojo.bo.agent;

import java.util.List;

import lombok.Data;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/11/11 10:00 上午
 */
@Data
public class AgentModuleInfo {

    /**
     * 模块名
     */
    private String moduleId;

    /**
     * 模块版本
     */
    private String moduleVersion;

    /**
     * 依赖模块
     */
    private List<AgentModuleInfo> dependenciesInfo;

    /**
     * 原始的依赖模块信息
     */
    private String dependenciesInfoStr;

    /**
     * 是否定制化插件
     */
    private Boolean customized;

    /**
     * 更新内容
     */
    private String updateInfo;
}
