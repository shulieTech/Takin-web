package io.shulie.takin.web.api.response;

import java.io.Serializable;
import java.util.List;

/**
 * @author caijianying
 */
public class SceneStartResponse implements Serializable {
    /**
     * 报告ID
     */
    private Long data;
    /**
     * 错误信息
     */
    private List<String> msg;

    public Long getData() {
        return data;
    }

    public void setData(Long data) {
        this.data = data;
    }

    public List<String> getMsg() {
        return msg;
    }

    public void setMsg(List<String> msg) {
        this.msg = msg;
    }
}
