package com.pamirs.takin.entity.domain.vo;

/**
 * 邵奇
 *
 * @author 298403
 */
public class TopologyLink {

    /**
     * 开始链路id
     */
    private String from;

    /**
     * 结束链路id
     */
    private String to;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "TopologyLink{" +
            "from='" + from + '\'' +
            ", to='" + to + '\'' +
            '}';
    }

    /**
     * 重写hashcode
     *
     * @return
     */
    @Override
    public int hashCode() {
        return from.hashCode() * to.hashCode();
    }

    /**
     * 判断是否相等
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj.getClass() == TopologyLink.class) {
            TopologyLink vo = (TopologyLink)obj;
            // 比较每个属性的值 一致时才返回true
            if (vo.from.equals(this.from) && vo.to.equals(this.to)) {
                return true;
            }
        }
        return false;
    }

}
