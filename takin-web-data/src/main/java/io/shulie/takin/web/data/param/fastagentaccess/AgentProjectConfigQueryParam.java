package io.shulie.takin.web.data.param.fastagentaccess;

import lombok.Data;

/**
 * @Description 应用配置查询对象
 * @Author ocean_wll
 * @Date 2021/8/16 2:15 下午
 */
@Data
public class AgentProjectConfigQueryParam {

    /**
     * 英文配置key
     */
    private String enKey;

    /**
     * 应用名称（应用配置时才生效）
     */
    private String projectName;

    /**
     * 用户信息（应用配置时才生效）
     */
    private String userAppKey;
}
