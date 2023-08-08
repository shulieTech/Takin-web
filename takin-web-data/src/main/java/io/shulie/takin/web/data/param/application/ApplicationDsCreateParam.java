package io.shulie.takin.web.data.param.application;

import java.util.Date;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/9 8:26 下午
 */
@Data
public class ApplicationDsCreateParam extends UserCommonExt {
    private Long id;
    private Long applicationId;
    private String applicationName;
    private Integer dbType;
    private Integer dsType;
    private String url;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;
    private String config;
    private String parseConfig;
    /**
     * 配置方式，0字段方式，1json方式
     */
    private Integer configType;
    /**
     * 是否手动添加
     */
    private boolean isManual;
}
