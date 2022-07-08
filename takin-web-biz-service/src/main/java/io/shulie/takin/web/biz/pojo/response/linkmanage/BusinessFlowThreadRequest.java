package io.shulie.takin.web.biz.pojo.response.linkmanage;

import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import io.shulie.takin.web.common.util.JsonUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("出参类-业务流程线程组保存入参")
public class BusinessFlowThreadRequest {

    @ApiModelProperty(name = "id", value = "流程ID")
    private Long id;

    @ApiModelProperty(name = "xpathMd5", value = "xpathMd5")
    private String xpathMd5;

    @ApiModelProperty(name = "threadScriptJmxNodes", value = "线程组节点数据")
    private List<ScriptJmxNode> threadScriptJmxNodes;

    public String getJsonText(){
        return JsonUtil.bean2Json(threadScriptJmxNodes);
    }
}
