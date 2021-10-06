package com.pamirs.takin.common.http;

/**
 * http请求返回结果集
 * 封装了http请求的响应信息
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月17日
 */
public class HttpResult {
    //响应码
    private Integer resultCode;
    //结果信息
    private String resultMsg;
    //响应数据
    private Object data;

    /**
     * 2018年5月21日
     *
     * @return the resultCode
     * @author shulie
     * @version 1.0
     */
    public Integer getResultCode() {
        return resultCode;
    }

    /**
     * 2018年5月21日
     *
     * @param resultCode the resultCode to set
     * @author shulie
     * @version 1.0
     */
    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    /**
     * 2018年5月21日
     *
     * @return the resultMsg
     * @author shulie
     * @version 1.0
     */
    public String getResultMsg() {
        return resultMsg;
    }

    /**
     * 2018年5月21日
     *
     * @param resultMsg the resultMsg to set
     * @author shulie
     * @version 1.0
     */
    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    /**
     * 2018年5月21日
     *
     * @return the data
     * @author shulie
     * @version 1.0
     */
    public Object getData() {
        return data;
    }

    /**
     * 2018年5月21日
     *
     * @param data the data to set
     * @author shulie
     * @version 1.0
     */
    public void setData(Object data) {
        this.data = data;
    }

}
