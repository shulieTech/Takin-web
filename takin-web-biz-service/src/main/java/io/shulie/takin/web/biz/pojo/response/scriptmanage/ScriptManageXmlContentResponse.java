package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhaoyong
 */
@Data
public class ScriptManageXmlContentResponse implements Serializable {
    private static final long serialVersionUID = -3681184638943613401L;

    @ApiParam(value = "脚本内容")
    private String content;

    @ApiParam(value = "脚本实例id")
    private Long scriptManageDeployId;
}
