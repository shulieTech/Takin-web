package io.shulie.takin.web.api.response;

/**
 * @author caijianying
 */
public class ErrorInfo {

    private String code;

    /**
     * 错误信息
     */
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
