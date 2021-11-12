package io.shulie.takin.web.common.agent;

import java.util.ArrayList;
import java.util.List;

import io.shulie.takin.web.common.pojo.bo.agent.AgentModuleInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

/**
 * @Description 解析module.properties文件
 *
 * 格式：module-id=module-aerospike;module-version=1.0.0;dependencies-info=pradar-core@1.0,datasource-common@1.0,
 * simulator@1.0.0;customized=false;update-info=更新内容1111
 * @Author ocean_wll
 * @Date 2021/11/11 9:43 上午
 */
public class ModulePropertiesResolver {

    private static final String MODULE_ID = "module-id";
    private static final String MODULE_VERSION = "module-version";
    private static final String DEPENDENCIES_INFO = "dependencies-info";
    private static final String CUSTOMIZED = "customized";
    private static final String UPDATE_INFO = "update-info";

    /**
     * 解析module.properties文件内容
     *
     * @param allModuleInfo 全部的模块信息
     * @return AgentModuleInfo集合
     */
    public static List<AgentModuleInfo> resolver(String allModuleInfo) {
        List<AgentModuleInfo> result = new ArrayList<>();
        if (StringUtils.isBlank(allModuleInfo)) {
            return result;
        }
        String[] moduleInfos = allModuleInfo.split("\n");
        for (String moduleInfo : moduleInfos) {
            AgentModuleInfo agentModuleInfo = singleResolver(moduleInfo);
            if (agentModuleInfo != null) {
                result.add(agentModuleInfo);
            }
        }
        return result;
    }

    /**
     * 解析一条记录
     *
     * @param moduleInfo 配置信息
     * @return AgentModuleInfo
     */
    public static AgentModuleInfo singleResolver(String moduleInfo) {
        if (StringUtils.isBlank(moduleInfo)) {
            return null;
        }
        if (!moduleInfo.contains(MODULE_ID)
            || !moduleInfo.contains(MODULE_VERSION)
            || !moduleInfo.contains(DEPENDENCIES_INFO)
            || !moduleInfo.contains(CUSTOMIZED)
            || !moduleInfo.contains(UPDATE_INFO)) {
            return null;
        }
        String[] moduleInfoArray = moduleInfo.split(";");
        AgentModuleInfo agentModuleInfo = new AgentModuleInfo();

        for (String moduleInfoItem : moduleInfoArray) {
            if (moduleInfoItem.startsWith(MODULE_ID)) {
                agentModuleInfo.setModuleId(moduleInfoItem.substring(MODULE_ID.length() + 1));
            } else if (moduleInfoItem.startsWith(MODULE_VERSION)) {
                agentModuleInfo.setModuleVersion(moduleInfoItem.substring(MODULE_VERSION.length() + 1));
            } else if (moduleInfoItem.startsWith(CUSTOMIZED)) {
                agentModuleInfo.setCustomized(Boolean.parseBoolean(moduleInfoItem.substring(CUSTOMIZED.length() + 1)));
            } else if (moduleInfoItem.startsWith(UPDATE_INFO)) {
                agentModuleInfo.setUpdateInfo(moduleInfoItem.substring(UPDATE_INFO.length() + 1));
            } else if (moduleInfoItem.startsWith(DEPENDENCIES_INFO)) {
                agentModuleInfo.setDependenciesInfoStr(moduleInfoItem.substring(DEPENDENCIES_INFO.length() + 1));
                agentModuleInfo.setDependenciesInfo(
                    dealDependsInfo(moduleInfoItem.substring(DEPENDENCIES_INFO.length() + 1)));
            }
        }

        return agentModuleInfo;
    }

    /**
     * 解析依赖信息
     *
     * @param dependsInfo 依赖信息
     * @return List<AgentModuleInfo>
     */
    public static List<AgentModuleInfo> dealDependsInfo(String dependsInfo) {
        List<AgentModuleInfo> result = new ArrayList<>();
        if (StringUtils.isBlank(dependsInfo)) {
            return result;
        }
        String[] moduleInfos = dependsInfo.split(",");
        for (String moduleInfo : moduleInfos) {
            AgentModuleInfo agentModuleInfo = new AgentModuleInfo();
            String[] moduleInfoArray = moduleInfo.split("@");
            agentModuleInfo.setModuleId(moduleInfoArray[0]);
            agentModuleInfo.setModuleVersion(moduleInfoArray[1]);
            result.add(agentModuleInfo);
        }
        return result;
    }

    /**
     * 将插件的依赖拼接成String 如 kafka@2.0.0,redisson@1.0.1
     *
     * @param moduleInfo AgentModuleInfo对象
     * @return 返回 kafka@2.0.0,redisson@1.0.1 字符串
     */
    public static String joinDependenciesInfo(AgentModuleInfo moduleInfo) {
        if (moduleInfo == null || CollectionUtils.isEmpty(moduleInfo.getDependenciesInfo())) {
            return null;
        }
        StringBuilder stringBuilder = new StringBuilder();
        moduleInfo.getDependenciesInfo().forEach(item -> stringBuilder.append(item.getModuleId()).append("@")
            .append(item.getModuleVersion()).append(","));

        return stringBuilder.substring(0, stringBuilder.toString().length() - 1);
    }

}
