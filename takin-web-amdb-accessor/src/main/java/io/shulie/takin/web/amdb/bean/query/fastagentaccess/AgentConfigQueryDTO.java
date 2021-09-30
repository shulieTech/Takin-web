package io.shulie.takin.web.amdb.bean.query.fastagentaccess;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 3:57 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AgentConfigQueryDTO extends PageBaseDTO {

    private static final long serialVersionUID = -5147797092761868318L;

    /**
     * 配置key
     */
    private String configKey;

    /**
     * 应用名
     */
    private String appName;

    /**
     * 配置状态 ture 校验成功 false校验失败
     */
    private Boolean status;
    /**
     * 租户的userAppKey
     */
    private String userAppKey;
    /**
     * 环境编码
     */
    private String envCode;
}
