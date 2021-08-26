package io.shulie.takin.web.biz.service.perfomanceanaly;

import io.shulie.takin.channel.bean.CommandPacket;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.TraceManageCreateRequest;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.TraceManageDeployQueryRequest;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.TraceManageQueryListRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.TraceManageCreateResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.TraceManageResponse;

import java.util.List;

/**
 * @author zhaoyong
 * 方法分析相关
 */
public interface TraceManageService {
    /**
     * 创建方法追踪
     *
     * @return
     */
    TraceManageCreateResponse createTraceManage(TraceManageCreateRequest traceManageCreateRequest) throws Exception;

    /**
     * 查询方法追踪详情
     *
     * @return
     */
    TraceManageResponse queryTraceManageDeploy(TraceManageDeployQueryRequest traceManageDeployQueryRequest);

    /**
     * agent上传trace信息
     *
     * @return
     */
    void uploadTraceInfo(CommandPacket commandPacket);

    /**
     * 查询所有当前agent的最终信息
     *
     * @return
     */
    List<TraceManageResponse> queryTraceManageList(TraceManageQueryListRequest traceManageQueryListRequest);
}
