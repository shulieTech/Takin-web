package io.shulie.takin.web.amdb.bean.query.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/19 10:19 下午
 */
@Data
public class ApplicationErrorQueryDTO {
    /**
     * 应用名称(单个)
     */
    private String appName;
    /**
     * 用户标识
     */
    private String userAppKey;
    /**
     * 环境编码
     */
    private String envCode;
}
