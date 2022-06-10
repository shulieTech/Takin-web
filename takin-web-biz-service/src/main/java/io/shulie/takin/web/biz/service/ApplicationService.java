package io.shulie.takin.web.biz.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pamirs.takin.common.constant.AppSwitchEnum;
import com.pamirs.takin.entity.domain.dto.ApplicationSwitchStatusDTO;
import com.pamirs.takin.entity.domain.dto.NodeUploadDataDTO;
import com.pamirs.takin.entity.domain.query.ApplicationQueryRequest;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import com.pamirs.takin.entity.domain.vo.JarVersionVo;
import com.pamirs.takin.entity.domain.vo.application.NodeNumParam;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.openapi.response.application.ApplicationListResponse;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationListByUpgradeRequest;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationQueryRequestV2;
import io.shulie.takin.web.biz.pojo.request.application.ApplicationVisualInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationListByUpgradeResponse;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationListResponseV2;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationVisualInfoResponse;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-03-16 15:23
 */
public interface ApplicationService {

    /**
     * 插件里使用
     * @param userIdList
     * @return
     */
    List<ApplicationDetailResult> getApplicationsByUserIdList(List<Long> userIdList);

    /**
     * 带租户
     */
    void configureTasks();

    List<ApplicationListResponse> getApplicationList(String appNames);

    /**
     * 获取应用列表
     *
     * @param param -
     * @return -
     */
    Response<List<ApplicationVo>> getApplicationList(ApplicationQueryRequest param);

    /**
     * 获取应用总数量
     * @return
     */
    Long getAccessErrorNum();

    List<ApplicationVo> getApplicationListVo(ApplicationQueryRequest param);

    /**
     * 添加接入状态进行过滤
     *
     * @param param        -
     * @param accessStatus -
     * @return -
     */
    Response getApplicationList(ApplicationQueryRequest param, Integer accessStatus);

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
     * @param ext -
     * @return -
     */
    Response calculateUserSwitch(TenantCommonExt ext);

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
     * 获取应用
     *
     * @return
     */
    List<ApplicationDetailResult> getAllApplications();


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

    /**
     * 根据名字查询
     * @param appName
     * @return
     */
    ApplicationDetailResult queryTApplicationMntByName(String appName);

    Long queryApplicationIdByAppName(String appName);

    /**
     * 一键卸载所有应用
     */
    void uninstallAllAgent(List<String> appIds);

    /**
     * 一键恢复探针
     * @param appIds
     */
    void resumeAllAgent(List<String> appIds);

    /**
     * 通过 applicationId 获得 应用
     * 并判断是否存在
     *
     * @param applicationId 应用id
     * @return 应用
     */
    ApplicationDetailResult getByApplicationIdWithCheck(Long applicationId);
    /**
     * 修改应用节点数
     *
     * @param numParamList 数据集合
     */
    void modifyAppNodeNum(List<NodeNumParam> numParamList);

    /**
     * 编辑静默开关
     * @param ext
     * @param enable
     * @return
     */
    Response userAppSilenceSwitch(TenantCommonExt ext, Boolean enable);


    /**
     * 获取静默开关状态
     * @return
     */
    Response userAppSilenceSwitchInfo();

    String getUserSilenceSwitchStatusForVo(TenantCommonExt ext);

    Response getApplicationReportConfigInfo(Integer bizType,String appName);

    Boolean silenceSwitchStatusIsTrue(TenantCommonExt ext, AppSwitchEnum appSwitchEnum);

    /**
     * 应用监控查询接口
     *
     * @param request 包含应用名称及服务名称
     */
    Response<List<ApplicationVisualInfoResponse>> getApplicationVisualInfo(ApplicationVisualInfoQueryRequest request);

    /**
     * 关注服务
     *
     * @param request 应用名➕服务名➕是否关注
     */
    void attendApplicationService(ApplicationVisualInfoQueryRequest request) throws Exception;

    void gotoActivityInfo(ActivityCreateRequest request);

    /**
     * 获取租户应用，用于amdb
     * @param commonExtList
     * @return
     */
    List<ApplicationDetailResult> getAllTenantApp(List<TenantCommonExt> commonExtList);

    /**
     * 应用列表
     *
     * @param request 请求入参
     * @return 应用列表
     */
    PagingList<ApplicationListResponseV2> pageApplication(ApplicationQueryRequestV2 request);

    PagingList<ApplicationListByUpgradeResponse> listApplicationByUpgrade(ApplicationListByUpgradeRequest request);

    String operateCheck(List<String> appIds, String operate);

    /**
     * 过滤不符合操作条件的appId
     * @param appIds
     * @param operate
     * @return
     */
    List<String> filterAppIds(List<String> appIds, String operate);
}
