package com.pamirs.takin.entity.domain.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author 298403
 * @date 2019-1-15
 */
public class TUploadNeedVo {

    /**
     * appName
     */

    @NotBlank
    private String appName;

    /**
     * 上传大小
     */
    @NotNull
    private Integer size;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "TUploadNeedVo{" +
            "appName='" + appName + '\'' +
            ", size=" + size +
            '}';
    }

}
