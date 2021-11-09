package io.shulie.takin.web.biz.pojo.response.linkmanage;

import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("出参类-业务流程线程组查询出参")
public class BusinessFlowThreadResponse {

    @ApiModelProperty(name = "linkRelateNum", value = "关联节点数")
    private Integer linkRelateNum;

    @ApiModelProperty(name = "totalNodeNum", value = "脚本总节点数")
    private Integer totalNodeNum;

    @ApiModelProperty(name = "threadScriptJmxNodes", value = "线程组节点数据")
    private List<ScriptJmxNode> threadScriptJmxNodes;
}
