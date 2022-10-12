package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceIsolateInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceQueryRequest;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceExtInfo;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceInfoVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceProcessStatusVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceVO;

import java.util.Map;

/**
 * 压测资源配置
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
public interface PressureResourceService {
    /**
     * 新增
     *
     * @param input
     */
    void add(PressureResourceInput input);

    /**
     * 新增
     *
     * @param id
     */
    void delete(Long id);


    /**
     * 修改
     *
     * @param input
     */
    void update(PressureResourceInput input);

    /**
     * 分页
     *
     * @param request
     * @return
     */
    PagingList<PressureResourceVO> list(PressureResourceQueryRequest request);

    /**
     * 详情查询
     *
     * @param request
     * @return
     */
    PressureResourceInfoVO detail(PressureResourceQueryRequest request);

    /**
     * 修改数据隔离方式
     *
     * @param isolateInput
     */
    void updateIsolate(PressureResourceIsolateInput isolateInput);

    /**
     * 处理进度
     *
     * @return
     */
    Map<String, Integer> progress(Long id);

    /**
     * 处理汇总信息-应用
     *
     * @return
     */
    PressureResourceExtInfo appInfo(Long id);

    /**
     * 处理汇总信息-数据源
     *
     * @return
     */
    PressureResourceExtInfo dsInfo(Long id);

    /**
     * 处理汇总信息-影子消费者
     *
     * @return
     */
    PressureResourceExtInfo mqInfo(Long id);
}
