package io.shulie.takin.web.biz.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pamirs.takin.entity.domain.dto.ApplicationSwitchStatusDTO;
import com.pamirs.takin.entity.domain.dto.NodeUploadDataDTO;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.query.ApplicationQueryParam;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import com.pamirs.takin.entity.domain.vo.JarVersionVo;
import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationVisualInfoDTO;
import io.shulie.takin.web.biz.pojo.openapi.response.application.ApplicationListResponse;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationVisualInfoQueryRequest;
import io.shulie.takin.web.common.common.Response;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-03-16 15:23
 */
public interface ApplicationService {

    void configureTasks();

    List<ApplicationListResponse> getApplicationList(String appNames);

    /**
     * 获取应用列表
     *
     * @param param -
     * @return -
     */
    Response<List<ApplicationVo>> getApplicationList(ApplicationQueryParam param);

    List<ApplicationVo> getApplicationListVo(ApplicationQueryParam param);

    /**
     * 添加接入状态进行过滤
     *
     * @param param        -
     * @param accessStatus -
     * @return -
     */
    Response getApplicationList(ApplicationQueryParam param, Integer accessStatus);

    /**
     * 应用列表（全量应用列表，无鉴权）
     *
     * @return -
     */
    Response getApplicationList();

    /**
     * 获取应用信息
     *
     * @param appId -
     * @return -
     */
    Response<ApplicationVo> getApplicationInfo(String appId);

    Response getApplicationInfoForError(String appId);

    /**
     * 添加应用(控制台手动新增)
     *
     * @param param -
     * @return -
     */
    Response addApplication(ApplicationVo param);

    /**
     * 添加应用(agent注册)
     *
     * @param param -
     * @return -
     */
    Response addAgentRegisterApplication(ApplicationVo param);

    /**
     * 添加应用
     *
     * @param param -
     * @return -
     */
    Response modifyApplication(ApplicationVo param);

    /**
     * 删除应用
     *
     * @param appId -
     * @return -
     */
    Response deleteApplication(String appId);

    /**
     * 重新计算
     *
     * @param uid -
     * @return -
     */
    Response calculateUserSwitch(Long uid);

    ApplicationSwitchStatusDTO agentGetUserSwitchInfo();

    /**
     * 上传接入状态
     *
     * @param param -
     * @return -
     */
    Response uploadAccessStatus(NodeUploadDataDTO param);

    /**
     * 上传应用中间件信息
     *
     * @param requestMap -
     * @return -
     */
    Response uploadMiddleware(Map<String, String> requestMap);

    /**
     * 上传应用中间件增强状态
     *
     * @param requestMap -
     * @return -
     */
    Response uploadMiddlewareStatus(Map<String, JarVersionVo> requestMap, String appName);

    /**
     * 修改应用状态（需鉴权）
     *
     * @param id            -
     * @param accessStatus  -
     * @param exceptionInfo -
     */
    void modifyAccessStatus(String id, Integer accessStatus, String exceptionInfo);

    /**
     * 修改应用状态（无需鉴权）
     *
     * @param applicationIds -
     * @param accessStatus   -
     */
    void modifyAccessStatusWithoutAuth(List<Long> applicationIds, Integer accessStatus);

    List<TApplicationMnt> getAllApplications();

    List<TApplicationMnt> getApplicationsByUserIdList(List<Long> userIdList);

    String getIdByName(String applicationName);

    String getUserSwitchStatusForVo();

    List<String> getApplicationName();

    /**
     * 应用管理相关配置导出
     * 影子库/表, 挡板, job, 白名单, 影子消费知
     *
     * @param response      响应
     * @param applicationId 应用id
     */
    void exportApplicationConfig(HttpServletResponse response, Long applicationId);

    /**
     * 导入应用配置
     *
     * @param file          导入的 excel
     * @param applicationId 应用id
     * @return 错误信息
     */
    Response importApplicationConfig(MultipartFile file, Long applicationId);

    Response<String> buildExportDownLoadConfigUrl(String appId, HttpServletRequest request);

    Response<Boolean> appDsConfigIsNewVersion();

    /**
     * 同步应用状态
     */
    void syncApplicationAccessStatus();

    /**
     * 通过 applicationId 获得 applicationName
     *
     * @param applicationId 应用id
     * @return applicationName 应用名称
     */
    String getApplicationNameByApplicationId(Long applicationId);

    TApplicationMnt queryTApplicationMntByName(String appName);

    /**
     * 一键卸载所有应用
     */
    void uninstallAllAgent(List<String> appIds);

    /**
     * 修改应用节点数
     *
     * @param numParamList 数据集合
     */
    void modifyAppNodeNum(List<NodeNumParam> numParamList);

    /**
     * 应用监控查询接口
     *
     * @param request 包含应用名称及服务名称
     */
    List<ApplicationVisualInfoDTO> getApplicationVisualInfo(ApplicationVisualInfoQueryRequest request);

    /**
     * 关注服务
     *
     * @param request 应用名➕服务名➕是否关注
     */
    void attendApplicationService(ApplicationVisualInfoQueryRequest request) throws Exception;
}
