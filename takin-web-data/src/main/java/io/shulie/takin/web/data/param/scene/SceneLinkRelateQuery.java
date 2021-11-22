package io.shulie.takin.web.data.param.scene;

import io.shulie.takin.ext.content.AbstractEntry;
import lombok.Data;


/**
 * @Author: liyuanba
 * @Date: 2021/10/27 10:57 上午
 */
@Data
public class SceneLinkRelateQuery extends AbstractEntry {
    /**
     * 租户id
     */
    private Long tenantId;
    /**
     * 环境
     */
    private String envCode;
    private String xpathMd5;
    private String entrance;

    /**
     * 业务流程id
     */
    private Long sceneId;

    private String scriptIdentification;

}
