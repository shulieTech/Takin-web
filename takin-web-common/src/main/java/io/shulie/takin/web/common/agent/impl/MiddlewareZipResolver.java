package io.shulie.takin.web.common.agent.impl;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.annotation.Resource;

import cn.hutool.core.util.ZipUtil;
import com.google.common.io.Files;
import com.pamirs.takin.common.constant.Constants;
import io.shulie.takin.web.common.agent.AgentZipResolverSupport;
import io.shulie.takin.web.common.agent.ModulePropertiesResolver;
import io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum;
import io.shulie.takin.web.common.pojo.bo.agent.AgentModuleInfo;
import io.shulie.takin.web.common.pojo.bo.agent.PluginCreateBO;
import io.shulie.takin.web.common.util.FileUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Description 中间件探针包解析器
 * @Author ocean_wll
 * @Date 2021/11/10 8:50 下午
 */
@Component
public class MiddlewareZipResolver extends AgentZipResolverSupport {

    @Resource
    private ThreadPoolExecutor middlewareResolverThreadPool;

    @Override
    public List<String> checkFile0(String filePath) {
        List<String> errorList = new CopyOnWriteArrayList<>();
        List<AgentModuleInfo> moduleInfos = ModulePropertiesResolver.resolver(filePath, getZipBaseDirName());
        CountDownLatch countDownLatch = new CountDownLatch(moduleInfos.size());
        try (ZipFile zip = new ZipFile(new File(filePath))) {
            for (AgentModuleInfo agentModuleInfo : moduleInfos) {
                middlewareResolverThreadPool.execute(() -> {
                    ZipEntry zipEntry = zip.getEntry(
                        getZipBaseDirName() + File.separator + agentModuleInfo.getModuleId());
                    if (zipEntry == null) {
                        errorList.add(agentModuleInfo.getModuleId() + "模块不存在！！");
                    }
                    countDownLatch.countDown();
                });
            }
            countDownLatch.await(60, TimeUnit.SECONDS);
        } catch (Exception e) {
            // ignore
        }

        return errorList;
    }

    @Override
    public List<PluginCreateBO> processFile0(String agentPkgPath, List<AgentModuleInfo> dependenciesInfo) {
        List<PluginCreateBO> createBOList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(dependenciesInfo.size());

        // 解压zip文件
        File file = new File(agentPkgPath);
        File tempDir = ZipUtil.unzip(file, Files.createTempDir().getAbsoluteFile());

        for (AgentModuleInfo agentModuleInfo : dependenciesInfo) {
            middlewareResolverThreadPool.execute(() -> {

                PluginCreateBO pluginCreateBO = new PluginCreateBO();

                pluginCreateBO.setPluginName(agentModuleInfo.getModuleId());
                pluginCreateBO.setDependenciesInfo(agentModuleInfo.getDependenciesInfoStr());
                pluginCreateBO.setPluginType(PluginTypeEnum.MIDDLEWARE.getCode());
                pluginCreateBO.setIsCustomMode(agentModuleInfo.getCustomized());
                pluginCreateBO.setPluginVersion(agentModuleInfo.getModuleVersion());

                OriginalFileInfo originalFileInfo = findPluginFileName(tempDir.getAbsolutePath(),
                    agentModuleInfo.getModuleId());

                if (originalFileInfo != null) {
                    // 数据转存
                    String destFilePath = getUploadPath(pluginCreateBO) + originalFileInfo.jarName;
                    FileUtil.copyFile(new File(originalFileInfo.jarPath + File.separator + originalFileInfo.jarName),
                        new File(destFilePath));
                    pluginCreateBO.setDownloadPath(destFilePath);
                    createBOList.add(pluginCreateBO);
                }

                countDownLatch.countDown();
            });
        }

        try {
            // 最多阻塞 2 分钟
            countDownLatch.await(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }

        // 删除临时文件
        FileUtil.deleteFile(tempDir.getAbsolutePath());
        return createBOList;
    }

    @Override
    public PluginTypeEnum getPluginType() {
        return PluginTypeEnum.MIDDLEWARE;
    }

    @Override
    public String getZipBaseDirName() {
        return Constants.MIDDLEWARE_ZIP_BASE_DIR;
    }

    /**
     * 获取插件对应的jar文件名
     *
     * @param tempDirPath 解压后的临时文件目录
     * @param pluginName  插件名
     * @return 插件对应的jar文件名
     */
    private OriginalFileInfo findPluginFileName(String tempDirPath, String pluginName) {
        String pluginFileName;
        if (StringUtils.isEmpty(pluginName) || !pluginName.startsWith("module-")) {
            pluginFileName = pluginName;
        } else {
            pluginFileName = pluginName.substring(7);
        }

        String jarPath = tempDirPath + File.separator + getZipBaseDirName() + File.separator + pluginFileName;
        File jarDir = new File(jarPath);
        if (!jarDir.exists()) {
            return null;
        }

        String[] fileNames = new File(jarPath).list();
        if (fileNames == null || fileNames.length < 1) {
            return null;
        }

        return new OriginalFileInfo(fileNames[0], jarPath);
    }

    @Data
    @AllArgsConstructor
    private static class OriginalFileInfo {

        /**
         * jar的文件名
         */
        private String jarName;

        /**
         * jar的文件夹路径
         */
        private String jarPath;
    }
}
