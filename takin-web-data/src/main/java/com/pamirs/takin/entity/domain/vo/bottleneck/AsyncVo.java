package com.pamirs.takin.entity.domain.vo.bottleneck;

import java.util.List;

/**
 * 异步处理
 *
 * @author shulie
 * @description
 * @create 2019-06-12 17:21:25
 */
public class AsyncVo {
    /**
     * 瓶颈等级(1、严重，2、普通，3、正常)
     */
    private String type;
    /**
     * 瓶颈内容
     */
    private List<BottleNeckVo> detailList;

    public AsyncVo() {}

    public AsyncVo(String type, List<BottleNeckVo> detailList) {
        this.type = type;
        this.detailList = detailList;
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
     * Gets the value of detailList.
     *
     * @return the value of detailList
     * @author shulie
     * @version 1.0
     */
    public List<BottleNeckVo> getDetailList() {
        return detailList;
    }

    /**
     * Sets the detailList.
     *
     * <p>You can use getDetailList() to get the value of detailList</p>
     *
     * @param detailList detailList
     * @author shulie
     * @version 1.0
     */
    public void setDetailList(List<BottleNeckVo> detailList) {
        this.detailList = detailList;
    }

    @Override
    public String toString() {
        return "AsyncVo{" +
            "type='" + type + '\'' +
            ", detailList=" + detailList +
            '}';
    }
}
