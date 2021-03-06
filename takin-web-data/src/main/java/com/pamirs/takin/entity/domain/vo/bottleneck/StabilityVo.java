package com.pamirs.takin.entity.domain.vo.bottleneck;

import java.util.List;

/**
 * TPS/RT 稳定性类
 *
 * @author shulie
 * @description
 * @create 2019-06-12 17:26:04
 */
public class StabilityVo {
    /**
     * 瓶颈等级(1、严重，2、普通，3、正常)
     */
    private String type;
    /**
     * 瓶颈内容
     */
    private List<BottleNeckVo> list;

    public StabilityVo() {
    }

    public StabilityVo(String type, List<BottleNeckVo> list) {
        this.type = type;
        this.list = list;
    }

    /**
     * Gets the value of type.
     *
     * @return the value of type
     * @author shulie
     * @version 1.0
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * <p>You can use getType() to get the value of type</p>
     *
     * @param type type
     * @author shulie
     * @version 1.0
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the value of list.
     *
     * @return the value of list
     * @author shulie
     * @version 1.0
     */
    public List<BottleNeckVo> getList() {
        return list;
    }

    /**
     * Sets the list.
     *
     * <p>You can use getList() to get the value of list</p>
     *
     * @param list list
     * @author shulie
     * @version 1.0
     */
    public void setList(List<BottleNeckVo> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "StabilityVo{" +
            "type='" + type + '\'' +
            ", list=" + list +
            '}';
    }
}
