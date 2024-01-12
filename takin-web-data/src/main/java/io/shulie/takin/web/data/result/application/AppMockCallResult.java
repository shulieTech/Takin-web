package io.shulie.takin.web.data.result.application;

import lombok.Data;

import java.io.Serializable;


@Data
public class AppMockCallResult implements Serializable {
    private Long appId;
    private String appName;
    private String mockName;
    //返回值mock 转发mock groovy脚本mock
    private String mockType;
    private String mockScript;
    private String mockStatus;
}
