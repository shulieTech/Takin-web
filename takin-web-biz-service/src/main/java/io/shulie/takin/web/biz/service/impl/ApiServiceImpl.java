package io.shulie.takin.web.biz.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeDTO;
import io.shulie.takin.web.biz.pojo.request.file.FileUploadRequest;
import io.shulie.takin.web.biz.pojo.response.common.FileUploadResponse;
import io.shulie.takin.web.biz.pojo.response.common.IsNewAgentResponse;
import io.shulie.takin.web.biz.service.ApiService;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.utils.AppCommonUtil;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author liuchuan
 * @date 2021/6/4 11:24 上午
 */
@Slf4j
@Service
public class ApiServiceImpl implements ApiService, ProbeConstants, AppConstants {

    /**
     * 上传文件的路径
     */
    private String uploadPath;

    @Autowired
    private DistributedLock distributedLock;

    @Autowired
    private ApplicationClient applicationClient;

    @Autowired
    private ApplicationService applicationService;

    @PostConstruct
    public void init() {
        uploadPath = ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_DATA_PATH);
    }

    @Override
    public FileUploadResponse uploadFile(FileUploadRequest request) {
        // 文件内容判断
        MultipartFile file = request.getFile();
        AppCommonUtil.isCommonError(file.isEmpty(), "文件没有数据!");

        // 原来的文件名称
        String originalName = file.getOriginalFilename();
        String lockKey = String.format(LockKeyConstants.LOCK_UPLOAD_FILE,
            WebPluginUtils.traceTenantId(), originalName, file.getSize());
        AppCommonUtil.isCommonError(!distributedLock.tryLockZeroWait(lockKey), TOO_FREQUENTLY);

        // 保存的文件名称 时间戳-文件名
        String fileName = String.format("%s-%s", System.currentTimeMillis(), originalName);
        // 上传文件的路径, 举例: /data/path/probe/20210610/
        String uploadPath = CommonUtil.getUploadPath(this.uploadPath, request.getFolder());
        // 保存文件的绝对路径, 上传路径 + 文件名
        String filePath = uploadPath + fileName;
        File dest = new File(filePath);

        try {
            // 文件零拷贝
            this.copyFileZero(file, dest);

            // 组合响应数据
            FileUploadResponse response = new FileUploadResponse();
            response.setOriginalName(originalName);
            response.setFileName(fileName);
            response.setFilePath(filePath);
            return response;

        } catch (Exception e) {
            // 文件上传失败时, 删除预先创建文件
            dest.deleteOnExit();

            log.error("文件上传 --> 错误, 错误信息: {}", e.getMessage(), e);
            throw AppCommonUtil.getCommonError("文件上传失败! 错误信息: " + e.getMessage());

        } finally {
            distributedLock.unLockSafely(lockKey);
        }
    }

    @Override
    public IsNewAgentResponse isNewAgentByApplication(Long applicationId) {
        // 根据配置返回
        IsNewAgentResponse response = this.getIsNewAgentResponseByConfig();
        if (response != null) {
            return response;
        }

        // 获得应用名称
        String applicationName = applicationService.getApplicationNameByApplicationId(applicationId);

        // 获得 agent 版本号, 去重列表
        Set<String> agentVersions = this.listAgentVersionFromAmdbByApplicationName(applicationName);

        // 返回响应数据
        return this.getIsNewAgentResponse(agentVersions);
    }

    /**
     * 根据配置返回是否是新旧 agent
     *
     * @return 新旧 agent 响应
     */
    public IsNewAgentResponse getIsNewAgentResponseByConfig() {
        int newAgent = Integer.parseInt(
            ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_APPLICATION_NEW_AGENT));
        if (newAgent == NEW_AGENT_NONE) {
            return null;
        }

        if (newAgent != YES && newAgent != NO) {
            return null;
        }

        IsNewAgentResponse response = new IsNewAgentResponse();
        response.setIsNew(newAgent);
        return response;
    }

    /**
     * 是否是新版 agent 响应数据
     *
     * @param agentVersions agent 版本号列表
     * @return 响应数据
     */
    private IsNewAgentResponse getIsNewAgentResponse(Set<String> agentVersions) {
        IsNewAgentResponse response = new IsNewAgentResponse();
        response.setAgentVersions(agentVersions);
        if (agentVersions.isEmpty()) {
            response.setIsNew(YES);
        }

        // 判断是否有新版本
        for (String agentVersion : agentVersions) {
            // 只要有一个新版, 就是新版
            if (AppCommonUtil.isNewAgentVersion(agentVersion)) {
                response.setIsNew(YES);
                return response;
            }
        }

        return response;
    }

    /**
     * 通过应用名称向 amdb 查询 agent 版本列表
     *
     * @param applicationName 应用名称
     * @return agent 版本列表
     */
    private Set<String> listAgentVersionFromAmdbByApplicationName(String applicationName) {
        Integer initSize = 1;
        Integer size = 150;

        // 查询老的 agent 列表
        ApplicationNodeQueryDTO applicationNodeQueryDTO = new ApplicationNodeQueryDTO();
        applicationNodeQueryDTO.setAppNames(applicationName);
        applicationNodeQueryDTO.setPageSize(initSize);
        PagingList<ApplicationNodeDTO> applicationNodeDtoPage =
            applicationClient.pageApplicationNodes(applicationNodeQueryDTO);
        if (applicationNodeDtoPage == null || applicationNodeDtoPage.getTotal() == 0) {
            return Collections.emptySet();
        }

        // agent 版本列表
        // set 自动去重
        Set<String> agentVersions = new HashSet<>();

        long total = applicationNodeDtoPage.getTotal();
        long offset = total % size + 1;
        for (long i = 0; i < offset; i++) {
            applicationNodeQueryDTO.setPageSize(size);
            PagingList<ApplicationNodeDTO> applicationNodePage =
                applicationClient.pageApplicationNodes(applicationNodeQueryDTO);
            if (applicationNodePage != null) {
                List<ApplicationNodeDTO> list = applicationNodePage.getList();
                for (ApplicationNodeDTO applicationNode : list) {
                    agentVersions.add(applicationNode.getAgentVersion());
                }
            }
        }

        return agentVersions;
    }

    /**
     * 零拷贝
     *
     * @param file 源文件
     * @param dest 目标文件
     * @throws IOException io异常
     */
    private void copyFileZero(MultipartFile file, File dest) throws IOException {
        if (!dest.getParentFile().exists()) {
            // 创建文件夹
            AppCommonUtil.isCommonError(!dest.getParentFile().mkdirs(), "文件夹创建失败!");
        }

        // 创建文件
        AppCommonUtil.isCommonError(!dest.createNewFile(), "文件创建失败!");

        // 文件通道, 进行文件操作
        FileInputStream destFileInputStream = new FileInputStream(dest);
        FileChannel destChannel = destFileInputStream.getChannel();

        // 这里的"rw"是指支持读和写
        RandomAccessFile randomAccessFile = new RandomAccessFile(dest, FILE_PERMISSION_RW);
        FileChannel channel = randomAccessFile.getChannel();
        // 零拷贝
        MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, file.getSize());
        map.put(file.getBytes());

        // 关闭目标文件相关
        randomAccessFile.close();
        channel.close();

        // 关闭源文件通道
        destFileInputStream.close();
        destChannel.close();
    }

}
