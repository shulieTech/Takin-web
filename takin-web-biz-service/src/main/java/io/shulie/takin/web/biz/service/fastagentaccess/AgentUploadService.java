package io.shulie.takin.web.biz.service.fastagentaccess;

import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentUploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Description agent包上传接口
 * @Author ocean_wll
 * @Date 2021/8/12 10:32 上午
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
