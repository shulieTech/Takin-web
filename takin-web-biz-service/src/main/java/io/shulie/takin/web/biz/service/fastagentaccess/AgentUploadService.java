package io.shulie.takin.web.biz.service.fastagentaccess;

import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentUploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * agent包上传接口
 *
 * @author ocean_wll
 * @date 2021/8/12 10:32 上午
 */
public interface AgentUploadService {

    /**
     * 上传agent包
     *
     * @param file agent文件
     * @return AgentUploadResponse
     */
    AgentUploadResponse upload(MultipartFile file);
}
