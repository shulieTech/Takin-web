package com.pamirs.takin.entity.domain.vo;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 说明: job dubbo 接口抓取 数据上传 防漏
 *
 * @author 298403
 * @version v1.0
 * @date Create in 2019/1/14
 */
public class TUploadInterfaceVo {

    /**
     * app name
     */
    @NotBlank
    private String appName;

    /**
     * 详情列表
     */
    @NotNull
    private List<TUploadInterfaceDetailVo> appDetails = new ArrayList<>();

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<TUploadInterfaceDetailVo> getAppDetails() {
        return appDetails;
    }

    public void setAppDetails(List<TUploadInterfaceDetailVo> appDetails) {
        this.appDetails = appDetails;
    }

    @Override
    public String toString() {
        return "TUploadInterfaceVo{" +
            "appName='" + appName + '\'' +
            ", appDetails=" + appDetails +
            '}';
    }

}
