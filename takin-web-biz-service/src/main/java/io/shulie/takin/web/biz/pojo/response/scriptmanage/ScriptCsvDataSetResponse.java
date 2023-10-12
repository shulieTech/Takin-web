package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
* @Package io.shulie.takin.web.biz.pojo.response.scriptmanage
* @ClassName: ScriptCsvDataSetResponse
* @author hezhongqi
* @description:
* @date 2023/9/21 14:23
*/
@Data
@ApiModel("出参类-csv数据")
public class ScriptCsvDataSetResponse {

    /**
     * 主键id
     */
    @ApiModelProperty("id")
    private Long id;

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

    /**
     * 关联的文件ID
     */
    @ApiModelProperty("fileManageId")
    private Long fileManageId;

    /**
     * 生成类型
     */
    @ApiModelProperty(value = "生成类型")
    private Integer createType;

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



    @ApiModelProperty(value = "csv文件列表")
    private List<FileManageResponse> files;


}
