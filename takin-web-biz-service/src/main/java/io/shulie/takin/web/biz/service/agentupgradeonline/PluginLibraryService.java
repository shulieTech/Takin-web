package io.shulie.takin.web.biz.service.agentupgradeonline;

import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.AgentPluginUploadResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 插件版本库(PluginLibrary)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:24:04
 */
public interface PluginLibraryService {

    /**
     * 插件上传
     *
     * @param file 文件
     * @return AgentPluginUploadResponse对象
     */
    AgentPluginUploadResponse upload(MultipartFile file);

}
