package io.shulie.takin.web.biz.design.probe;

import java.util.List;
import java.util.Map;

import io.shulie.takin.web.common.constant.ProbeConstants;
import io.shulie.takin.web.common.pojo.dto.probe.MatchApplicationNodeProbeStateDTO;
import io.shulie.takin.web.common.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 应用节点探针状态工厂
 * 操作用
 *
 * @author liuchuan
 * @date 2021/6/8 3:36 下午
 */
@Slf4j
@Service
public class ApplicationNodeProbeStateFactory implements ProbeConstants {

    /**
     * 状态实例列表
     */
    @Autowired
    private List<AbstractApplicationNodeProbeState> stateList;

    /**
     * 状态实例 map
     * 名字 -> 实例
     */
    @Autowired
    private Map<String, AbstractApplicationNodeProbeState> stateMap;

    /**
     * 通过状态, 操作, 操作结果, 获取状态实例
     *
     * @param matchApplicationNodeProbeStateDTO 所需入参
     * @return 状态实例
     */
    public AbstractApplicationNodeProbeState getState(MatchApplicationNodeProbeStateDTO matchApplicationNodeProbeStateDTO) {
        log.info("探针状态工厂 --> 匹配探针状态, 入参: {}", JsonUtil.bean2Json(matchApplicationNodeProbeStateDTO));

        AbstractApplicationNodeProbeState abstractApplicationNodeProbeState = this.stateList.stream()
            .filter(state -> state.match(matchApplicationNodeProbeStateDTO))
            .findFirst().orElse(null);

        // 如果没有匹配到, 给一个未安装状态
        if (abstractApplicationNodeProbeState == null) {
            abstractApplicationNodeProbeState = stateMap.get(ENGLISH_DESC_NOT_INSTALLED);
        }

        if (abstractApplicationNodeProbeState != null) {
            log.info("探针状态工厂 --> 探针状态: {}", abstractApplicationNodeProbeState.probeStateDesc());
        }

        return abstractApplicationNodeProbeState;
    }

}
