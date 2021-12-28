package io.shulie.takin.web.biz.service.fastagentaccess;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentVersionCreateRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentVersionQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentVersionListResponse;

import java.io.File;
import java.util.List;

/**
 * agent版本管理(AgentVersion)service
 *
 * @author liuchuan
 * @date 2021-08-11 19:43:38
 */
public interface AgentVersionService {

    /**
     * 查询最新版本或者指定版本的agent信息
     * 传version则查询指定版本，不传version则查询最新版本
     *
     * @param version 版本号（传：查询指定版本；不传：查询最新版本）
     * @return AgentVersionListResponse
     */
    AgentVersionListResponse queryLatestOrFixedVersion(String version);

    /**
     * 根据版本号删除记录
     *
     * @param version 版本号
     */
    void deleteByVersion(String version);

    /**
     * 新增记录
     *
     * @param createRequest 新增请求
     * @return 影响记录数
     */
    Integer create(AgentVersionCreateRequest createRequest);

    /**
     * 发布新的探针版本
     *
     * @param createRequest 新增请求
     */
    void release(AgentVersionCreateRequest createRequest);

    /**
     * 获取大版本列表
     *
     * @return 字符串集合
     */
    List<String> getFirstVersionList();

    /**
     * 获取所有版本列表
     *
     * @return 字符串集合
     */
    List<String> getAllVersionList();

    /**
     * 列表查询
     *
     * @param queryRequest 查询条件
     * @return PagingList
     */
    PagingList<AgentVersionListResponse> list(AgentVersionQueryRequest queryRequest);

    /**
     * 根据版本获取agent包
     *
     * @param version 版本号
     * @return File
     */
    File getFile(String version);

    /**
     * 获取应用的agent包
     *
     * @param projectName 应用名
     * @param userAppKey  租户id
     * @param userId      用户id
     * @param version     agent版本
     * @param envCode     环境标识
     * @return File
     */
    File getProjectFile(String projectName, String userAppKey, String userId, String version, String envCode);

    /**
     * 获取agent安装脚本
     *
     * @param projectName 应用名
     * @param version     agent版本
     * @param urlPrefix   http://domain:host
     * @return File
     */
    File getInstallScript(String projectName, String version, String urlPrefix);

}
