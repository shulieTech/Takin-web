package io.shulie.takin.web.biz.pojo.response.scriptmanage;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.common.vo.script.RequestAssertDetailVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * @author liuchuan
 * @date 2021/5/11 1:54 下午
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
     * 插入时间
     */
    @ApiModelProperty("createTime")
    private LocalDateTime createTime;

    /**
     * 变更时间
     */
    @ApiModelProperty("updateTime")
    private LocalDateTime updateTime;

}
