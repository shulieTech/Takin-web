package com.pamirs.takin.entity.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.DateToStringFormatSerialize;
import com.pamirs.takin.entity.domain.entity.TSecondLinkMnt;

/**
 * @author shulie
 * @description 二级链路 + 检测信息
 * @create 2018/7/6 17:20
 */
public class TSecondLinkWithDetection extends TSecondLinkMnt {

    //检测信息
    private Integer detection;

    // @Field lastCheckTime : 上一次压测检测时间
    @JsonSerialize(using = DateToStringFormatSerialize.class)
    private Date lastCheckTime;

    //二级链路的状态
    private Integer status;

    //是否存在一级链路
    private boolean existFirstLink;

    /**
     * 判断是否存在一级链路
     *
     * @return
     */
    public boolean isExistFirstLink() {
        return existFirstLink;
    }

    /**
     * 设置是否存在一级链路
     *
     * @param existFirstLink
     */
    public void setExistFirstLink(boolean existFirstLink) {
        this.existFirstLink = existFirstLink;
    }

    /**
     * Gets the value of detection.
     *
     * @return the value of detection
     * @author shulie
     * @version 1.0
     */
    public Integer getDetection() {
        return detection;
    }

    /**
     * Sets the detection.
     *
     * <p>You can use getDetection() to get the value of detection</p>
     *
     * @param detection detection
     * @author shulie
     * @version 1.0
     */
    public void setDetection(Integer detection) {
        this.detection = detection;
    }

    /**
     * Gets the value of lastCheckTime.
     *
     * @return the value of lastCheckTime
     * @author shulie
     * @version 1.0
     */
    public Date getLastCheckTime() {
        return lastCheckTime;
    }

    /**
     * Sets the lastCheckTime.
     *
     * <p>You can use getLastCheckTime() to get the value of lastCheckTime</p>
     *
     * @param lastCheckTime lastCheckTime
     * @author shulie
     * @version 1.0
     */
    public void setLastCheckTime(Date lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    /**
     * Gets the value of status.
     *
     * @return the value of status
     * @author shulie
     * @version 1.0
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Sets the status.
     *
     * <p>You can use getStatus() to get the value of status</p>
     *
     * @param status status
     * @author shulie
     * @version 1.0
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TSecondLinkWithDetection{" +
            "detection=" + detection +
            ", lastCheckTime=" + lastCheckTime +
            '}';
    }

}
