package io.shulie.takin.web.common.agent.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.common.constant.Constants;
import io.shulie.takin.web.common.agent.AgentZipResolverSupport;
import io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum;
import io.shulie.takin.web.common.pojo.bo.agent.AgentModuleInfo;
import io.shulie.takin.web.common.pojo.bo.agent.PluginCreateBO;
import io.shulie.takin.web.common.util.FileUtil;
import org.springframework.stereotype.Component;

/**
 * @Description agent部分zip包解析器
 * @Author ocean_wll
 * @Date 2021/11/10 8:49 下午
 */
@Component
public class AgentZipResolver extends AgentZipResolverSupport {

    @Override
    public List<String> checkFile0(String filePath) {
        return new ArrayList<>();
    }

    @Override
    public List<PluginCreateBO> processFile0(String agentPkgPath, List<AgentModuleInfo> dependenciesInfo) {
        List<PluginCreateBO> resultList = new ArrayList<>();
        // agent模块只有一个包，所有只取第一条记录
        PluginCreateBO pluginCreateBO = new PluginCreateBO();
        AgentModuleInfo agentModuleInfo = dependenciesInfo.get(0);
        pluginCreateBO.setPluginName(agentModuleInfo.getModuleId());
        pluginCreateBO.setPluginType(PluginTypeEnum.AGENT.getCode());
        pluginCreateBO.setPluginVersion(agentModuleInfo.getModuleVersion());
        pluginCreateBO.setDependenciesInfo(agentModuleInfo.getDependenciesInfoStr());
        pluginCreateBO.setIsCustomMode(agentModuleInfo.getCustomized());

        //数据转存
        String destFilePath = getUploadPath(pluginCreateBO) + "simulator-agent.zip";
        FileUtil.copyFile(new File(agentPkgPath), new File(destFilePath));
        pluginCreateBO.setDownloadPath(destFilePath);
        resultList.add(pluginCreateBO);
        return resultList;
    }

    @Override
    public PluginTypeEnum getPluginType() {
        return PluginTypeEnum.AGENT;
    }

    @Override
    public String getZipBaseDirName() {
        return Constants.AGENT_ZIP_BASE_DIR;
    }
}
