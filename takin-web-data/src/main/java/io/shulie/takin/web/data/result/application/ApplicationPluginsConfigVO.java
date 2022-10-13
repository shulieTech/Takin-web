package io.shulie.takin.web.data.result.application;

import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import lombok.Data;

import java.util.Date;

/**
 * (ApplicationPluginsConfig)实体类
 *
 * @author caijy
 * @date 2021-05-18 16:48:12
 */
@Data
public class ApplicationPluginsConfigVO extends AuthQueryResponseCommonExt {
    private String id;
    private String configValueName;
    /**
     * 应用ID
     */
    private String applicationId;

    /**
     * 应用名称
     */
    private String applicationName;

    /**
     * 配置项
     */
    private String configItem;

    /**
     * 配置说明
     */
    private String configDesc;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifieTime;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 修改人ID
     */
    private Long modifierId;

}
