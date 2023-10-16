package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
* @Package io.shulie.takin.web.biz.pojo.response.scriptmanage
* @ClassName: ScriptCsvDataSetResponse
* @author hezhongqi
* @description:
* @date 2023/9/21 14:23
*/
@Data
@ApiModel("出参类-csv数据")
public class ScriptCsvManageResponse  extends AuthQueryResponseCommonExt {
    /**
     * 关联的文件ID
     */
    @ApiModelProperty("fileManageId")
    private Long fileManageId;


    private Long taskId;

    /**
     * 业务流程ID
     */
    @ApiModelProperty("businessFlowId")
    private Long businessFlowId;

    /**
     * 业务流程Name
     */
    @ApiModelProperty("scene")
    private String businessFlowName;


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
     * 生成类型
     */
    @ApiModelProperty("createType")
    private Integer createType;

    /**
     * 任务进度
     */
    @ApiModelProperty(value = "任务进度")
    private String currentCreateSchedule;

    // 模板名称

    @ApiModelProperty(value = "是否选择该csv")
    private Boolean isSelect;

    /**
     * 变更时间
     */
    @ApiModelProperty("fileCreateTime")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fileCreateTime;

    /**
     * 主键id
     */
    @ApiModelProperty("id")
    private Long id;


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
     * 生成状态(0：生成中，1：排队中，2：已生成，3已取消)
     */
    @ApiModelProperty("任务状态")
    private Integer createStatus;


    /**
     * 插入时间
     */
    @ApiModelProperty("createTime")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 变更时间
     */
    @ApiModelProperty("updateTime")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


    @ApiModelProperty(value = "aliasName")
    private String aliasName;

    @ApiModelProperty(value = "是否拆分")
    private Integer isSplit;

    @ApiModelProperty(value = "是否按照顺序拆分")
    private Integer isOrderSplit;

    @ApiModelProperty(value = "上传路径：返回给前端下载路径")
    @JsonProperty("downloadUrl")
    private String uploadPath;

    private String currentCreateScheduleString;




}
