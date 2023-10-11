package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import com.google.common.collect.Maps;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvDataTemplateResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvVariableJsonPathDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
* @Package io.shulie.takin.web.biz.pojo.request.scriptmanage
* @ClassName: ScriptCsvCreateTaskRequest
* @author hezhongqi
* @description:
* @date 2023/9/21 14:20
*/
@Data
@ApiModel("入参类-csv 创建task")
public class ScriptCsvCreateTaskRequest {

    /**
     * 任务开始时间
     */
    @ApiModelProperty("任务id")
    private Long taskId;


    /**
     * 任务开始时间
     */
    @ApiModelProperty("任务开始时间")
    private LocalDateTime taskStartTime;

    /**
     * 任务完成时间
     */
    @ApiModelProperty("任务完成时间")
    private LocalDateTime taskEndTime;

    /**
     * 模板内容，存对应变量生成的模板内容
     */
    @ApiModelProperty("模板内容，存对应变量生成的模板内容")
    private ScriptCsvDataTemplateResponse template;

    /**
     * 脚本csv变量的jsonPath映射
     */
    @ApiModelProperty("脚本csv变量的jsonPath映射")
    private Map<String,String> scriptCsvVariableJsonPath;

    @ApiModelProperty("脚本csv变量的jsonPath映射")
    private List<ScriptCsvVariableJsonPathDTO> scriptCsvVariableJsonPathList;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 别名
     */
    @ApiModelProperty("aliasName")
    private String aliasName;
    /**
     * 业务流程ID
     */
    @ApiModelProperty("businessFlowId")
    private Long businessFlowId;

    @ApiModelProperty("linkId")
    private Long linkId;

    @ApiModelProperty("scriptCsvDataSetId")
    private Long scriptCsvDataSetId;


    public Map<String, String> getScriptCsvVariableJsonPath() {
        Map<String,String> scriptCsvVariableJsonPathMap = Maps.newHashMap();
        if(CollectionUtils.isEmpty(this.scriptCsvVariableJsonPathList)) {
            return scriptCsvVariableJsonPathMap;
        }
        for(ScriptCsvVariableJsonPathDTO jsonPathDTO : this.scriptCsvVariableJsonPathList) {
            scriptCsvVariableJsonPathMap.put(jsonPathDTO.getVariable(), jsonPathDTO.getJsonPathKey());
        }
        return scriptCsvVariableJsonPathMap;
    }

}
