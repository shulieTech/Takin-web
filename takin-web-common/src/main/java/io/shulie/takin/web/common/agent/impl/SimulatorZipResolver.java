package io.shulie.takin.web.common.agent.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.pamirs.takin.common.constant.Constants;
import io.shulie.takin.web.common.agent.AgentZipResolverSupport;
import io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum;
import io.shulie.takin.web.common.pojo.bo.agent.AgentModuleInfo;
import io.shulie.takin.web.common.pojo.bo.agent.PluginCreateBO;
import io.shulie.takin.web.common.util.FileUtil;
import org.springframework.stereotype.Component;

/**
 * @Description simulator zip包解析器
 * @Author ocean_wll
 * @Date 2021/11/10 8:49 下午
 */
@Component
public class SimulatorZipResolver extends AgentZipResolverSupport {

    private final static Map<String, String> NEED_EXIST = new HashMap<String, String>();

    static {
        NEED_EXIST.put(
            joinFileSeparator(Constants.SIMULATOR_ZIP_BASE_DIR, "bootstrap", "instrument-simulator-messager.jar"),
            "instrument-simulator-messager.jar 缺失!");
        NEED_EXIST.put(
            joinFileSeparator(Constants.SIMULATOR_ZIP_BASE_DIR, "bootstrap", "simulator-bootstrap-api-1.0.0.jar"),
            "simulator-bootstrap-api-1.0.0.jar 缺失!");
        NEED_EXIST.put(joinFileSeparator(Constants.SIMULATOR_ZIP_BASE_DIR, "bootstrap",
                "simulator-internal-bootstrap-api-1.0.0.jar"),
            "simulator-internal-bootstrap-api-1.0.0.jar 缺失!");
        NEED_EXIST.put(joinFileSeparator(Constants.SIMULATOR_ZIP_BASE_DIR, "instrument-simulator-agent.jar"),
            "instrument-simulator-agent.jar 缺失!");
        NEED_EXIST.put(joinFileSeparator(Constants.SIMULATOR_ZIP_BASE_DIR, "config", "simulator.properties"),
            "配置文件 simulator.properties 缺失!");
        NEED_EXIST.put(joinFileSeparator(Constants.SIMULATOR_ZIP_BASE_DIR, "config", "version"),
            "version文件缺失!");
        NEED_EXIST.put(joinFileSeparator(Constants.SIMULATOR_ZIP_BASE_DIR, "lib", "instrument-simulator-core.jar"),
            "instrument-simulator-core.jar 缺失!");
        NEED_EXIST.put(joinFileSeparator(Constants.SIMULATOR_ZIP_BASE_DIR, "provider",
                "instrument-simulator-management-provider.jar"),
            "instrument-simulator-management-provider.jar 缺失!");
    }

    @Override
    public List<String> checkFile0(String filePath) {
        List<String> errorList = new ArrayList<>();
        try (ZipFile zip = new ZipFile(new File(filePath))) {
            for (Map.Entry<String, String> entry : NEED_EXIST.entrySet()) {
                ZipEntry zipEntry = zip.getEntry(entry.getKey());
                if (zipEntry == null) {
                    errorList.add(entry.getValue());
                }
            }
        } catch (IOException e) {
            // ignore
        }

        return errorList;
    }

    @Override
    public List<PluginCreateBO> processFile0(String agentPkgPath, List<AgentModuleInfo> dependenciesInfo) {
        List<PluginCreateBO> resultList = new ArrayList<>();
        // agent模块只有一个包，所有只取第一条记录
        PluginCreateBO pluginCreateBO = new PluginCreateBO();
        AgentModuleInfo agentModuleInfo = dependenciesInfo.get(0);
        pluginCreateBO.setPluginName(agentModuleInfo.getModuleId());
        pluginCreateBO.setPluginType(PluginTypeEnum.SIMULATOR.getCode());
        pluginCreateBO.setDependenciesInfo(agentModuleInfo.getDependenciesInfoStr());
        pluginCreateBO.setPluginVersion(agentModuleInfo.getModuleVersion());
        pluginCreateBO.setIsCustomMode(agentModuleInfo.getCustomized());

        //数据转存
        String destFilePath = getUploadPath(pluginCreateBO) + "simulator.zip";
        FileUtil.copyFile(new File(agentPkgPath), new File(destFilePath));
        pluginCreateBO.setDownloadPath(destFilePath);
        resultList.add(pluginCreateBO);
        return resultList;
    }

    @Override
    public PluginTypeEnum getPluginType() {
        return PluginTypeEnum.SIMULATOR;
    }

    @Override
    public String getZipBaseDirName() {
        return Constants.SIMULATOR_ZIP_BASE_DIR;
    }

    /**
     * 拼接路径
     *
     * @param paths 路径item
     * @return 完整的路径
     */
    private static String joinFileSeparator(String... paths) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String s : paths) {
            if (!first) {
                builder.append(File.separator);
            }
            builder.append(s);
            first = false;
        }
        return builder.toString();
    }
}
