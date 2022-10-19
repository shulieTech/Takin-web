package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceDetailInput;
import io.shulie.takin.web.biz.service.pressureresource.vo.CommandTaskVo;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;

import java.util.List;

/**
 * 压测资源配置
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
public interface PressureResourceCommonService {
    /**
     * 自动处理压测准备
     */
    void processAutoPressureResource();

    /**
     * 自动处理压测准备-关联应用-数据库-表
     */
    void processAutoPressureResourceRelate(PressureResourceEntity resource);

    /**
     * 自动处理压测准备-远程调用自动梳理
     */
    void processAutoPressureResourceRelate_remoteCall(PressureResourceEntity resource);

    /**
     * 推送变更到Redis
     *
     * @param resoureIds
     */
    void pushRedis(Long... resoureIds);

    /**
     * 推送变更到Redis
     *
     * @param taskVo
     */
    void pushRedisCommand(CommandTaskVo taskVo);

    /**
     * 删除数据
     *
     * @param taskVo
     */
    void deleteCommandTask(CommandTaskVo taskVo);

    /**
     * 从Redis里面获取数据
     */
    List<CommandTaskVo> getTaskFormRedis();

    /**
     * 从Redis里面获取数据
     */
    List<Long> getResourceIdsFormRedis();

    /**
     * 修改以后同步数据源相关到应用里面
     *
     * @param resouceId
     */
    void syncDs(Long resouceId);

    void processNotify(List<PressureResourceDetailInput> detailInputs);
}
