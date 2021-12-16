package com.pamirs.takin.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/11/11 10:10 上午
 */
public class ModuleResolverTest {

    public static void main(String[] args) throws IOException {
        String filePath = "/Users/ocean_wll/upload/agent_upgrade_online_package/simulator-agent.zip";
        ZipFile zipFile = new ZipFile(new File(filePath));
        ZipEntry entry = zipFile.getEntry("simulator-agent/module.properties");

        InputStream is = zipFile.getInputStream(entry);
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int len;
        while ((len = is.read(buf)) > 0) {
            sb.append(new String(buf, 0, len));
        }

        //List<AgentModuleInfo> moduleInfoList = ModulePropertiesResolver.resolver(sb.toString());
        System.out.println(sb.toString());
        //System.out.println(moduleInfoList);
        System.out.println(11);

    }
}
