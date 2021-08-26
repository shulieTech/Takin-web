package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用于阿斯旺请求返回list
 *
 * @author shulie
 * @version v1.0
 * @2018年5月17日
 */
@Deprecated //todo service和controller都不需要包装这种对象，切面自动完成
public class Results<T> implements Serializable {

    //序列号
    private static final long serialVersionUID = 5022541880836487879L;
    //结果消息
    private String message;
    //结果
    private boolean success = true;
    //结果集
    private Collection<T> data = new ArrayList<>();

    /**
     * 2018年5月17日
     *
     * @return the message
     * @author shulie
     * @version 1.0
     */
    public String getMessage() {
        return message;
    }

    /**
     * 2018年5月17日
     *
     * @param message the message to set
     * @author shulie
     * @version 1.0
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 2018年5月17日
     *
     * @return the success
     * @author shulie
     * @version 1.0
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 2018年5月17日
     *
     * @param success the success to set
     * @author shulie
     * @version 1.0
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * 2018年5月17日
     *
     * @return the data
     * @author shulie
     * @version 1.0
     */
    public Collection<T> getData() {
        return data;
    }

    /**
     * 2018年5月17日
     *
     * @param data the data to set
     * @author shulie
     * @version 1.0
     */
    public void setData(Collection<T> data) {
        this.data = data;
    }

    /**
     * 2018年5月17日
     *
     * @return the 字符串
     * @author shulie
     * @version 1.0
     */
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
