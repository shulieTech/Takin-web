package io.shulie.takin.web.biz.utils.agentupgradeonline;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.ZipUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.shulie.takin.web.common.util.FileUtil;
import io.shulie.takin.web.data.result.agentUpgradeOnline.PluginLibraryDetailResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @Description agent包工具类
 * @Author ocean_wll
 * @Date 2021/11/15 4:31 下午
 */
public class AgentPkgUtil {

    /**
     * 线程池迁移agent包
     */
    private static ThreadPoolExecutor middlewareAggregationThreadPool;

    static {
        final int coreSize = Runtime.getRuntime().availableProcessors();
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("middleware-aggregation-%d").build();
        middlewareAggregationThreadPool = new ThreadPoolExecutor(coreSize, coreSize * 2, 0, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100), nameThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    /**
     * 聚合 agent、simulator、middleware包。当agent为null时则聚合simulator和middleware包
     *
     * @param agent          agent包
     * @param simulator      simulator包
     * @param middlewareList 中间件包
     * @param baseDirPath    基础目录
     * @return 聚合以后的文件路径
     */
    public static String aggregation(PluginLibraryDetailResult agent, PluginLibraryDetailResult simulator,
        List<PluginLibraryDetailResult> middlewareList, String baseDirPath) {

        // 判断是打agent包还是simulator包
        boolean isAgent = agent != null;

        if (simulator == null || CollectionUtils.isEmpty(middlewareList)
            || StringUtils.isEmpty(baseDirPath)) {
            return null;
        }
        String agentPath = baseDirPath + File.separator + "completeAgent" + File.separator + LocalDate.now().toString()
            .replaceAll("-", "");
        String simulatorPath = isAgent ? agentPath + File.separator + "simulator-agent" + File.separator + "agent" :
            baseDirPath + File.separator + "completeSimulator" + File.separator + LocalDate.now().toString()
                .replaceAll("-", "");
        String middlewarePath = simulatorPath + File.separator + "simulator" + File.separator + "modules";

        // 解压agent包
        if (isAgent) {
            ZipUtil.unzip(agent.getDownloadPath(), agentPath);
        }

        // 解压simulator包
        FileUtil.deleteFile(simulatorPath + File.separator + "simulator");
        ZipUtil.unzip(simulator.getDownloadPath(), simulatorPath);

        // 拷贝所有的module包
        FileUtil.deleteFile(middlewarePath);
        CountDownLatch countDownLatch = new CountDownLatch(middlewareList.size());

        for (PluginLibraryDetailResult middleware : middlewareList) {
            middlewareAggregationThreadPool.execute(() -> {
                DestJarInfo destJarInfo = getDestJarInfo(middleware);
                FileUtil.copyFile(new File(middleware.getDownloadPath()), new File(
                    middlewarePath + File.separator + destJarInfo.getJarDirName() + File.separator
                        + destJarInfo.getJarName()));
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            // ignore
        }

        // 将聚合好的数据打包成一个zip包，并将原文件删除
        File file = isAgent ? ZipUtil.zip(agentPath + File.separator + "simulator-agent",
            agentPath + File.separator + System.currentTimeMillis() + "-simulator-agent.zip") :
            ZipUtil.zip(simulatorPath + File.separator + "simulator",
                simulatorPath + File.separator + System.currentTimeMillis() + "-simulator.zip");

        if (isAgent) {
            FileUtil.deleteFile(agentPath + File.separator + "simulator-agent");
        } else {
            FileUtil.deleteFile(simulatorPath + File.separator + "simulator");
        }

        return file.getAbsolutePath();
    }

    /**
     * 获取jar的信息
     *
     * @param middleware 基本信息
     * @return DestJarInfo对象
     */
    private static DestJarInfo getDestJarInfo(PluginLibraryDetailResult middleware) {
        String jarDirName = middleware.getPluginName();
        if (!StringUtils.isEmpty(jarDirName) && jarDirName.startsWith("module-")) {
            jarDirName = jarDirName.substring(7);
        }
        String jarName = middleware.getDownloadPath().substring(
            middleware.getDownloadPath().lastIndexOf(File.separator) + 1);
        return new DestJarInfo(jarDirName, jarName);
    }

    @Data
    @AllArgsConstructor
    private static class DestJarInfo {

        /**
         * jar包文件夹名称
         */
        private String jarDirName;

        /**
         * jar包文件名称
         */
        private String jarName;
    }
}
