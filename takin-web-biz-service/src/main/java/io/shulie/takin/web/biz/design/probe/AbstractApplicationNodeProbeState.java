package io.shulie.takin.web.biz.design.probe;

import io.shulie.takin.web.biz.utils.AppCommonUtil;
import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.enums.probe.ApplicationNodeProbeOperateEnum;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.pojo.dto.probe.ApplicationNodeProbeOperateDTO;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import io.shulie.takin.web.data.dao.ApplicationNodeProbeDAO;
import io.shulie.takin.web.data.param.application.CreateApplicationNodeProbeParam;
import io.shulie.takin.web.data.param.probe.UpdateOperateResultParam;
import io.shulie.takin.web.data.result.application.ApplicationNodeProbeResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 应用节点探针状态
 *
 * @author liuchuan
 * @date 2021/6/8 3:36 下午
 */
public abstract class AbstractApplicationNodeProbeState implements ProbeConstants {

    @Autowired
    private ApplicationNodeProbeDAO applicationNodeProbeDAO;

    /**
     * 匹配到对应的状态实现类
     *
     * @param dto 匹配所需要的数据
     * @return 是否匹配到
     */
    public abstract boolean match(MatchApplicationNodeProbeStateDTO dto);

    /**
     * 根据操作类型, 操作
     *
     * @param applicationNodeProbeOperateDTO 操作所需参数
     */
    public void handle(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        // 根据类型, 操作
        ApplicationNodeProbeOperateEnum applicationNodeProbeOperateEnum =
            ApplicationNodeProbeOperateEnum.getByCode(applicationNodeProbeOperateDTO.getOperateType());
        if (applicationNodeProbeOperateEnum == null) {
            throw new UnsupportedOperationException("操作失败, 操作不存在, 请重试!");
        }

        switch (applicationNodeProbeOperateEnum) {
            case INSTALL:
                this.install(applicationNodeProbeOperateDTO);
                break;

            case UNINSTALL:
                this.uninstall(applicationNodeProbeOperateDTO);
                break;

            case UPGRADE:
                this.upgrade(applicationNodeProbeOperateDTO);
                break;

            default:
                throw new UnsupportedOperationException("操作失败, 请重试!");
        }
    }

    /**
     * 探针状态
     *
     * @return 探针状态
     */
    public abstract Integer probeState();

    /**
     * 探针状态文字描述
     *
     * @return 探针状态文字描述
     */
    public abstract String probeStateDesc();

    /**
     * 获得探针版本号
     *
     * @param probeVersion 探针版本号
     * @return 探针版本号
     */
    public String probeVersion(String probeVersion) {
        return probeVersion;
    }

    /**
     * 操作结果描述
     *
     * @param dto 大数据探针状态
     * @return 操作结果描述
     */
    public String operateStateDesc(MatchApplicationNodeProbeStateDTO dto) {
        return "";
    }

    /**
     * 1 安装
     *
     * @param applicationNodeProbeOperateDTO 操作所需参数
     */
    public void install(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        throw new UnsupportedOperationException("该状态不能进行安装操作!");
    }

    /**
     * 2 卸载
     *
     * @param applicationNodeProbeOperateDTO 操作所需参数
     */
    public void uninstall(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        throw new UnsupportedOperationException("该状态不能进行卸载操作!");
    }

    /**
     * 升级
     *
     * @param applicationNodeProbeOperateDTO 操作所需参数
     */
    public void upgrade(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        throw new UnsupportedOperationException("该状态不能进行升级操作!");
    }

    /**
     * 操作
     *
     * @param applicationNodeProbeOperateDTO 操作所需参数
     */
    protected void operate(ApplicationNodeProbeOperateDTO applicationNodeProbeOperateDTO) {
        // 查询本地探针操作记录
        ApplicationNodeProbeResult result =
            applicationNodeProbeDAO.getByApplicationNameAndAgentId(applicationNodeProbeOperateDTO.getApplicationName(),
                applicationNodeProbeOperateDTO.getAgentId());

        if (result == null) {
            // 有, 就更新, 没有, 创建
            CreateApplicationNodeProbeParam createApplicationNodeProbeParam = new CreateApplicationNodeProbeParam();
            createApplicationNodeProbeParam.setOperateId(System.currentTimeMillis());
            BeanUtils.copyProperties(applicationNodeProbeOperateDTO, createApplicationNodeProbeParam);
            createApplicationNodeProbeParam.setOperate(applicationNodeProbeOperateDTO.getOperateType());
            AppCommonUtil.isError(!applicationNodeProbeDAO.create(createApplicationNodeProbeParam),
                ExceptionCode.AGENT_APPLICATION_NODE_PROBE_UPDATE_OPERATE_RESULT_ERROR, "创建节点操作记录失败!");
            return;
        }

        UpdateOperateResultParam updateOperateResultParam = new UpdateOperateResultParam();
        updateOperateResultParam.setOperate(applicationNodeProbeOperateDTO.getOperateType());
        updateOperateResultParam.setOperateId(System.currentTimeMillis());
        updateOperateResultParam.setProbeId(applicationNodeProbeOperateDTO.getProbeId());
        updateOperateResultParam.setOperateResult(PROBE_OPERATE_RESULT_NONE);
        updateOperateResultParam.setId(result.getId());

        AppCommonUtil.isError(!applicationNodeProbeDAO.updateById(updateOperateResultParam),
            ExceptionCode.AGENT_APPLICATION_NODE_PROBE_UPDATE_OPERATE_RESULT_ERROR, "更新节点操作记录失败!");
    }

    @Override
    public String toString() {
        return this.probeStateDesc();
    }

}
