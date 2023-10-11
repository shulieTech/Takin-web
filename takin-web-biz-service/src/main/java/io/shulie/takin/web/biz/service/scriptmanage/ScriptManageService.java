package io.shulie.takin.web.biz.service.scriptmanage;

import com.pamirs.takin.entity.domain.dto.scenemanage.ScriptCheckDTO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.*;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.*;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;

import java.util.List;

/**
 * @author zhaoyong
 */
public interface ScriptManageService {
    /**
     * 根据场景id获取相关文件压缩包
     *
     * @return
     */
    String getZipFileUrl(Long scriptDeployId);

    /**
     * 创建脚本
     */
    Long createScriptManage(ScriptManageDeployCreateRequest scriptManageDeployCreateRequest);

    /**
     * 根据关联活动和脚本上传路径，校验脚本信息
     *
     * @return
     */
    ScriptCheckDTO checkAndUpdateScript(String refType, String refValue,Integer mVersion, String scriptFileUploadPath);

    /**
     * 分页查询脚本列表
     *
     * @return
     */
    PagingList<ScriptManageDeployResponse> pageQueryScriptManage(
        ScriptManageDeployPageQueryRequest scriptManageDeployPageQueryRequest);

    /**
     * 删除脚本发布实例，如果实例完全被删除，同时删除脚本
     *
     * 删除脚本发布实例，同时删除该脚本实例对应的脚本下的所有脚本实例，同时删除脚本
     * 就是把所有的都删掉了
     */
    void deleteScriptManage(Long scriptDeployId);

    /**
     * 创建脚本发布实例和标签的关联关系
     */
    void createScriptTagRef(ScriptTagCreateRefRequest scriptTagCreateRefRequest);

    /**
     * 查询所有标签
     *
     * @return
     */
    List<TagManageResponse> queryScriptTagList();

    /**
     * 查询脚本实例详情
     *
     * @return
     */
    ScriptManageDeployDetailResponse getScriptManageDeployDetail(Long scriptDeployId);

    /**
     * 修改脚本文件 && 这里 todo 可能需要修改
     */
    Long updateScriptManage(ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest);


    /**
     * 修改脚本文件的 数据文件，不修改文件
     */
    List<FileManageEntity> updateScriptCssManage(ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest);

    /**
     * 查询所有业务流程，再将所有关联的脚本id附带出来
     *
     * @return
     */
    List<ScriptManageSceneManageResponse> getAllScenes(String businessFlowName);

    /**
     * 查询所有业务活动，再将所有关联的脚本id附带出来
     *
     * @param activityName 业务活动名称
     * @return 脚本关联的业务活动列表
     */
    List<ScriptManageActivityResponse> listAllActivities(String activityName);

    /**
     * 解析脚本文件
     *
     * @return
     */
    String explainScriptFile(String scriptFileUploadPath);

    /**
     * 获取文件下载路径
     *
     * @return
     */
    String getFileDownLoadUrl(String filePath);

    /**
     * 获取支持的jmeter插件列表名称
     *
     * @return
     */
    List<SupportJmeterPluginNameResponse> getSupportJmeterPluginNameList(SupportJmeterPluginNameRequest nameRequest);

    /**
     * 获取所有的jmeter插件列表名称
     *
     * @return jmeter插件列表名称
     */
    List<SupportJmeterPluginNameResponse> getAllJmeterPluginNameList();

    /**
     * 获取支持的jmeter插件版本列表
     *
     * @return
     */
    SupportJmeterPluginVersionResponse getSupportJmeterPluginVersionList(
        SupportJmeterPluginVersionRequest versionRequest);

    void setFileList(ScriptManageDeployDetailResponse result);

    /**
     * 根据脚本id查询脚本实例id列表
     *
     * @return
     */
    List<ScriptManageDeployResponse> listScriptDeployByScriptId(Long scriptId);

    /**
     * 将该脚本实例回滚到最新版本
     */
    String rollbackScriptDeploy(Long scriptDeployId);

    /**
     * 根据脚本实例id查询对应脚本文件xml内容
     *
     * @return
     */
    List<ScriptManageXmlContentResponse> getScriptManageDeployXmlContent(List<Long> scriptManageDeployIds);

    /**
     * 大文件同步数据
     *
     * @param partRequest 请求参数对象
     * @return
     */
    WebPartResponse bigFileSyncRecord(WebPartRequest partRequest);

    /**
     * 根据脚本实例id获得业务活动列表
     *
     * @param scriptDeployId 脚本实例id
     * @return 业务活动列表
     */
    List<BusinessLinkResult> listBusinessActivityByScriptDeployId(Long scriptDeployId);

    /**
     * 根据脚本发布id, 获得业务活动ids
     *
     * @param scriptDeployId 脚本发布id
     * @return 业务活动ids
     */
    List<Long> listBusinessActivityIdsByScriptDeployId(Long scriptDeployId);

    /**
     * 通过脚本， 获得压缩后的文件名称
     *
     * @param scriptDeployId 脚本发布id
     * @return 压缩文件名称
     */
    String getZipFileNameByScriptDeployId(Long scriptDeployId);

    /**
     * 查询所有有签名值的文件
     */
    List<FileManageEntity> getAllFile();

}
