package io.shulie.takin.web.biz.pojo.bo;

import lombok.Data;

/**
 * 配置列表查询对象
 *
 * @author ocean_wll
 * @date 2021/8/17 11:52 上午
 */
@Data
public class ConfigListQueryBO {

    /**
     * 应用名
     */
    private String projectName;

    /**
     * 生效机制0：重启生效，1：立即生效
     *
     * @see io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigEffectMechanismEnum
     */
    private Integer effectMechanism;

    /**
     * 英文配置key
     */
    private String enKey;

    /**
     * 配置生效最低版本对应的数值
     */
    private Long effectMinVersionNum;

    /**
     * 租户id，因为下载链接不需要登录，所以请求时需要带上userAppKey
     */
    private String userAppKey;

    /**
     * 仅看应用配置
     */
    private Boolean readProjectConfig;

    /**
     * 环境标识
     */
    private String envCode;
}
