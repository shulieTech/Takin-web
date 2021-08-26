package io.shulie.takin.web.common.pojo.dto.probe;

import io.shulie.takin.web.common.constant.ProbeConstants;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/8 5:24 下午
 */
@Data
public class MatchApplicationNodeProbeStateDTO {

    /**
     * 大数据探针状态
     * 0-已安装,1-未安装,2-安装中,3-卸载中,4-安装失败,99-未知状态
     */
    private Integer amdbProbeState;

    /**
     * 探针的操作, 1 安装, 2 卸载, 3 升级
     */
    private Integer probeOperate;

    /**
     * 探针的操作结果
     */
    private Integer probeOperateResult = ProbeConstants.PROBE_OPERATE_RESULT_NONE;

}
