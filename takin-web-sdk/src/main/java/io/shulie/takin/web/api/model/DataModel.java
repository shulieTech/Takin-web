package io.shulie.takin.web.api.model;

import java.io.Serializable;

/**
 * @author caijianying
 */
public class DataModel implements Serializable {
    /**
     * 实际
     */
    private Object result;
    /**
     * 目标
     */
    private Object value;



    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
