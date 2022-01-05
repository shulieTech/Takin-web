package io.shulie.takin.web.biz.pojo.response.application;

import lombok.Data;

import java.util.Date;

@Data
public class ApplicationListByUpgradeResponse {

    /**
     * 不是表id
     * 防止前端long类型精度缺失, 使用字符串
     */
    private String id;

    private String userName;

    private String applicationName;

    private Integer nodeNum;

    private Date gmtUpdate;

}
