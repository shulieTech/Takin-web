package io.shulie.takin.web.data.param.fastagentaccess;

import lombok.Data;

/**
 * @Description agent配置dao层查询对象
 * @Author ocean_wll
 * @Date 2021/8/16 11:12 上午
 */
@Data
public class AgentConfigQueryParam {

    /**
     * 应用名
     */
    private String projectName;

    /**
     * 用户唯一key
     */
    private String userAppKey;

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
}
