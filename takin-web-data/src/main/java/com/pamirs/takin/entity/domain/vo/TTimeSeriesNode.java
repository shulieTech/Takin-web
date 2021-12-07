package com.pamirs.takin.entity.domain.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TTimeSeries类
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月17日
 */
public class TTimeSeriesNode extends TBusinessTopology {
    //tScenarios
    private List<TScenario> tScenarios;
    //timestmpAndTps
    private Map<Long, Integer> timestmpAndTps = new HashMap<>();

    /**
     * 2018年5月17日
     *
     * @return the tScenarios
     * @author shulie
     * @version 1.0
     */
    public List<TScenario> gettScenarios() {
        return tScenarios;
    }

    /**
     * 2018年5月17日
     *
     * @param tScenarios the tScenarios to set
     * @author shulie
     * @version 1.0
     */
    public void settScenarios(List<TScenario> tScenarios) {
        this.tScenarios = tScenarios;
    }

    /**
     * 2018年5月17日
     *
     * @return the timestmpAndTps
     * @author shulie
     * @version 1.0
     */
    public Map<Long, Integer> getTimestmpAndTps() {
        return timestmpAndTps;
    }

    /**
     * 2018年5月17日
     *
     * @param timestmpAndTps the timestmpAndTps to set
     * @author shulie
     * @version 1.0
     */
    public void setTimestmpAndTps(Map<Long, Integer> timestmpAndTps) {
        this.timestmpAndTps = timestmpAndTps;
    }

}
