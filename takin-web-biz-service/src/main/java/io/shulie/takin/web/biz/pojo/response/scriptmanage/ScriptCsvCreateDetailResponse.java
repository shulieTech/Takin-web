package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @Package io.shulie.takin.web.biz.pojo.response.scriptmanage
* @ClassName: ScriptCsvCreateDetailResponse
* @author hezhongqi
* @description:
* @date 2023/9/21 14:20
*/
@Data
public class ScriptCsvCreateDetailResponse {

    /**
     * 组件id
     */
    @ApiModelProperty("scriptCsvDataSetId")
    private Long scriptCsvDataSetId;

    /**
     * 脚本csv组件名
     */
    @ApiModelProperty("scriptCsvDataSetName")
    private String scriptCsvDataSetName;

    /**
     * 脚本csv文件名
     */
    @ApiModelProperty("scriptCsvFileName")
    private String scriptCsvFileName;

    /**
     * 脚本csv变量名
     */
    @ApiModelProperty("scriptCsvVariableName")
    private String scriptCsvVariableName;

    /**
     * 是否忽略首行(0:否；1:是)
     */
    @ApiModelProperty("ignoreFirstLine")
    private Boolean ignoreFirstLine;

    /**
     * 脚本实例ID
     */
    @ApiModelProperty("scriptDeployId")
    private Long scriptDeployId;

    /**
     * 业务流程ID
     */
    @ApiModelProperty("businessFlowId")
    private Long businessFlowId;


    @ApiModelProperty(value = "aliasName")
    private String aliasName;


    /**
     * 任务id
     */
    @ApiModelProperty("taskId")
    private Long taskId;

    /**
     * 脚本csv变量的jsonPath映射
     */
    @ApiModelProperty("脚本csv变量的jsonPath映射")
    private Map<String,String> scriptCsvVariableJsonPath;
    @ApiModelProperty("脚本csv变量的jsonPath映射 --- 前端使用")
    private List<ScriptCsvVariableJsonPathDTO> scriptCsvVariableJsonPathList;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;


    /**
     * 部门ID（模块ID）
     */
    @ApiModelProperty("deptId")
    private Long deptId;


    @ApiModelProperty("业务活动ID")
    private Long linkId;


    private ScriptCsvDataTemplateResponse template;

    public void setScriptCsvVariableJsonPath(Map<String, String> scriptCsvVariableJsonPath) {
        this.scriptCsvVariableJsonPath = scriptCsvVariableJsonPath;
        this.scriptCsvVariableJsonPathList = this.getScriptCsvVariableJsonPathList();
    }

    public List<ScriptCsvVariableJsonPathDTO> getScriptCsvVariableJsonPathList() {
        return  this.scriptCsvVariableJsonPath.keySet().stream().map(t -> {
            ScriptCsvVariableJsonPathDTO jsonPathDTO = new ScriptCsvVariableJsonPathDTO();
            jsonPathDTO.setVariable(t);
            jsonPathDTO.setJsonPathKey(this.scriptCsvVariableJsonPath.get(t));
            return jsonPathDTO;
        }).collect(Collectors.toList());
    }
}



