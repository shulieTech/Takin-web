package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @Package io.shulie.takin.web.biz.pojo.response.scriptmanage
* @ClassName: ScriptCsvCreateTaskResponse
* @author hezhongqi
* @description:
* @date 2023/9/21 14:18
*/
@Data
@ApiModel("出参类-csv数据")
public class ScriptCsvCreateTaskResponse {
    /**
     * 主键id
     */
    @ApiModelProperty("id")
    private Long id;

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
     * 模板变量(存储起止时间，应用名，接口，接口类型)
     */
    @ApiModelProperty("模板变量(存储起止时间，应用名，接口，接口类型)")
    private String templateVariable;

    /**
     * 模板内容，存对应变量生成的模板内容
     */
    @ApiModelProperty("模板内容，存对应变量生成的模板内容")
    private String templateContent;

    /**
     * 脚本csv变量的jsonPath映射
     */
    @ApiModelProperty("脚本csv变量的jsonPath映射")
    private Map<String,String> scriptCsvVariableJsonPath;
    @ApiModelProperty("脚本csv变量的jsonPath映射 --- 前端使用")
    private List<ScriptCsvVariableJsonPathDTO> scriptCsvVariableJsonPathList;

    /**
     * 当前生成进度
     */
    @TableField(value = "当前生成进度")
    private String currentCreateSchedule;

    /**
     * 生成状态(0：生成中，1：排队中，2：已生成，3已取消)
     */
    @ApiModelProperty("生成状态(0：生成中，1：排队中，2：已生成，3已取消)")
    private Integer createStatus;

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
     * 部门ID（模块ID）
     */
    @ApiModelProperty("deptId")
    private Long deptId;

    /**
     * 业务流程ID
     */
    @ApiModelProperty("业务流程ID")
    private Long businessFlowId;

    @ApiModelProperty("业务活动ID")
    private Long linkId;

    /**
     * csv组件表Id
     */
    @ApiModelProperty( "csv组件表Id")
    private Long scriptCsvDataSetId;

    /**
     * 插入时间
     */
    @TableField(value = "createTime")
    private LocalDateTime createTime;

    /**
     * 变更时间
     */
    @ApiModelProperty("updateTime")
    private LocalDateTime updateTime;


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
