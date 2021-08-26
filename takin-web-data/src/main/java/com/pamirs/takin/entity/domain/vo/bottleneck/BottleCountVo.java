package com.pamirs.takin.entity.domain.vo.bottleneck;

import java.util.List;

/**
 * 链路拓扑图应用瓶颈统计
 *
 * @author shulie
 * @description
 * @create 2019-06-12 17:36:50
 */
public class BottleCountVo {

    /**
     * 总的瓶颈数量统计
     */
    private BottleLevelCountVo summary;

    /**
     * 链路头中瓶颈数量统计
     */
    private List<BottleLevelCountVo> linkRelated;

    /**
     * Gets the value of summary.
     *
     * @return the value of summary
     * @author shulie
     * @version 1.0
     */
    public BottleLevelCountVo getSummary() {
        return summary;
    }

    /**
     * Sets the summary.
     *
     * <p>You can use getSummary() to get the value of summary</p>
     *
     * @param summary summary
     * @author shulie
     * @version 1.0
     */
    public void setSummary(BottleLevelCountVo summary) {
        this.summary = summary;
    }

    /**
     * Gets the value of linkRelated.
     *
     * @return the value of linkRelated
     * @author shulie
     * @version 1.0
     */
    public List<BottleLevelCountVo> getLinkRelated() {
        return linkRelated;
    }

    /**
     * Sets the linkRelated.
     *
     * <p>You can use getLinkRelated() to get the value of linkRelated</p>
     *
     * @param linkRelated linkRelated
     * @author shulie
     * @version 1.0
     */
    public void setLinkRelated(List<BottleLevelCountVo> linkRelated) {
        this.linkRelated = linkRelated;
    }

    @Override
    public String toString() {
        return "BottleCountVo{" +
            "summary=" + summary +
            ", linkRelated=" + linkRelated +
            '}';
    }
}
