package io.shulie.takin.web.biz.service.fastagentaccess.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.web.biz.pojo.request.file.FileUploadRequest;
import io.shulie.takin.web.biz.pojo.response.common.FileUploadResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentUploadResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentVersionListResponse;
import io.shulie.takin.web.biz.service.ApiService;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentUploadService;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentVersionService;
import io.shulie.takin.web.biz.utils.AppCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * agent上传实现类
 *
 * @author ocean_wll
 * @date 2021/8/12 10:34 上午
 */
@Service
@Slf4j
public class AgentUploadServiceImpl implements AgentUploadService {

    /**
     * agent包文件夹名
     */
    private final static String DIR_NAME = "agent-file";

    /**
     * version路径
     */
    private final static String VERSION_PATH = "agent/simulator/config/version";

    @Autowired
    private ApiService apiService;

    @Autowired
    private AgentVersionService agentVersionService;

    @Override
    public AgentUploadResponse upload(MultipartFile file) {
        FileUploadResponse fileUploadResponse;
        FileUploadRequest fileUploadRequest = new FileUploadRequest();
        fileUploadRequest.setFile(file);
        fileUploadRequest.setFolder(DIR_NAME);
        // 上传文件
        fileUploadResponse = apiService.uploadFile(fileUploadRequest);
        AgentUploadResponse agentUploadResponse = new AgentUploadResponse();
        BeanUtil.copyProperties(fileUploadResponse, agentUploadResponse);

        try {
            // 获取agent版本信息
            String agentVersion = findVersion(fileUploadResponse.getFilePath());
            agentUploadResponse.setVersion(agentVersion);

            // 查询当前版本是否已存在
            AgentVersionListResponse dbData = agentVersionService.queryLatestOrFixedVersion(agentVersion);
            agentUploadResponse.setExist(dbData != null);
        } catch (Exception e) {
            // 在获取版本号是抛异常需要将已上传文件删除
            if (fileUploadResponse != null && StringUtils.hasLength(fileUploadResponse.getFilePath())) {
                File existFile = new File(fileUploadResponse.getFilePath());
                try {
                    existFile.delete();
                } catch (SecurityException exception) {
                    existFile.deleteOnExit();
                }
            }
            throw e;
        }
        return agentUploadResponse;
    }

    /**
     * 获取agent zip包中的版本信息
     *
     * @param filePath 文件路径
     * @return version
     */
    private String findVersion(String filePath) {
        String version = null;
        try (
            ZipFile zf = new ZipFile(filePath);
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            ZipInputStream zin = new ZipInputStream(in)
        ) {
            ZipEntry ze;
            while ((ze = zin.getNextEntry()) != null) {
                // 获取zip中的文件名
                String fileName = ze.getName();
                // 如果是version文件则打印版本号
                if (StringUtils.hasLength(fileName) && fileName.endsWith(VERSION_PATH)) {
                    version = readVersionFile(zf, ze);
                    break;
                }
            }
            zin.closeEntry();
        } catch (IOException e) {
            log.error("agent包解析异常 --> 错误, 错误信息: {}", e.getMessage(), e);
            throw AppCommonUtil.getCommonError("agent包解析异常!");
        }
        return version;
    }

    /**
     * 读取zip包中的version文件
     *
     * @param zf ZipFile对象
     * @param ze ZipEntry对象
     * @return 版本号
     */
    private String readVersionFile(ZipFile zf, ZipEntry ze) {
        String version;
        try (
            BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)))
        ) {
            // 版本号在文件中对应一行记录
            version = br.readLine();
        } catch (IOException e) {
            log.error("agent版本获取异常 --> 错误, 错误信息: {}", e.getMessage(), e);
            throw AppCommonUtil.getCommonError("agent版本获取异常!");
        }
        return version;
    }

}
