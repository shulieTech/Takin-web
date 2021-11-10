package io.shulie.takin.web.common.agent.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.shulie.takin.web.common.agent.AgentZipResolverSupport;

/**
 * @Description simulator zip包解析器
 * @Author ocean_wll
 * @Date 2021/11/10 8:49 下午
 */
public class SimulatorZipResolver extends AgentZipResolverSupport {

    private final static String SIMULATOR_HOME = "simulator";

    private final static Map<String, String> NEED_EXIST = new HashMap<String, String>();

    static {
        NEED_EXIST.put(joinFileSeparator(SIMULATOR_HOME, "bootstrap", "instrument-simulator-messager.jar"),
            "instrument-simulator-messager.jar 缺失!");
        NEED_EXIST.put(joinFileSeparator(SIMULATOR_HOME, "bootstrap", "simulator-bootstrap-api-1.0.0.jar"),
            "simulator-bootstrap-api-1.0.0.jar 缺失!");
        NEED_EXIST.put(joinFileSeparator(SIMULATOR_HOME, "bootstrap", "simulator-internal-bootstrap-api-1.0.0.jar"),
            "simulator-internal-bootstrap-api-1.0.0.jar 缺失!");
        NEED_EXIST.put(joinFileSeparator(SIMULATOR_HOME, "instrument-simulator-agent.jar"),
            "instrument-simulator-agent.jar 缺失!");
        NEED_EXIST.put(joinFileSeparator(SIMULATOR_HOME, "config", "simulator.properties"),
            "配置文件 simulator.properties 缺失!");
        NEED_EXIST.put(joinFileSeparator(SIMULATOR_HOME, "config", "version"),
            "缺失版本文件!");
        NEED_EXIST.put(joinFileSeparator(SIMULATOR_HOME, "lib", "instrument-simulator-core.jar"),
            "instrument-simulator-core.jar 缺失!");
        NEED_EXIST.put(joinFileSeparator(SIMULATOR_HOME, "provider", "instrument-simulator-management-provider.jar"),
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
    public String getZipBaseDirName() {
        return SIMULATOR_HOME;
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
