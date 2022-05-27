package io.shulie.takin.web.biz.service.interfaceperformance;

import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneActionResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.shulie.takin.web.data.model.mysql.SceneEntity;

/**
 * @Author: vernon
 * @Date: 2022/5/20 10:40
 * @Description: 压测配置
 */
public interface PerformancePressureService {
    /**
     * 新增
     *
     * @param input
     * @return
     */
    ResponseResult<Long> add(PerformanceConfigCreateInput input) throws Throwable;

    /**
     * 更新
     *
     * @param input
     * @return
     */
    ResponseResult<Boolean> update(PerformanceConfigCreateInput input) throws Throwable;


    Boolean update(PerformanceDataFileRequest input);

    /**
     * 删除
     *
     * @param configId
     */
    ResponseResult delete(Long configId) throws Throwable;

    /**
     * 查询详情
     *
     * @param
     * @return
     */
    ResponseResult<SceneDetailResponse> query(Long apiId) throws Throwable;


    /**
     * @param apiId apiid
     * @return 业务流程详情
     */
    SceneEntity bizFlowDetailByApiId(Long apiId);

    /**
     * 启动压测
     *
     * @param param
     * @return
     */
    ResponseResult<SceneActionResp> start(SceneActionParam param);

    /**
     * 上传数据文件
     *
     * @param request
     * @return
     */
    public ResponseResult uploadDataFile(BusinessFlowDataFileRequest request);

    /**
     * 生成脚本
     *
     * @param id 场景id
     * @return 返回脚本内容
     */
    String scriptGenerator(Long id);
}
