package io.shulie.takin.web.biz.service.agentupgradeonline;

import java.io.File;
import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentLibraryCreateRequest;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.PluginLibraryListQueryRequest;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.AgentPluginUploadResponse;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginInfo;
import io.shulie.takin.web.biz.pojo.response.agentupgradeonline.PluginLibraryListResponse;
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

    /**
     * 发布新版本
     *
     * @param createRequest 请求参数
     */
    void release(AgentLibraryCreateRequest createRequest);

    /**
     * 根据插件ID查询对应的插件文件
     *
     * @param pluginId 插件Id
     * @return 插件文件
     */
    File getPluginFile(Long pluginId);

    /**
     * 根据插件名查询列表
     *
     * @param pluginName 插件名称
     * @return PluginInfo
     */
    List<PluginInfo> queryByPluginName(String pluginName);

    /**
     * 查询所有的插件列表名称
     *
     * @return PluginInfo集合
     */
    List<String> queryAllPluginNames();

    /**
     * 列表查询
     *
     * @param query 查询条件
     * @return PluginLibraryListResponse集合
     */
    PagingList<PluginLibraryListResponse> list(PluginLibraryListQueryRequest query);

}
