package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * csv组件表
 */
@Data
@TableName(value = "t_script_csv_data_set")
public class ScriptCsvDataSetEntity {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 脚本csv组件名
     */
    @TableField(value = "script_csv_data_set_name")
    private String scriptCsvDataSetName;

    /**
     * 脚本csv文件名
     */
    @TableField(value = "script_csv_file_name")
    private String scriptCsvFileName;

    /**
     * 脚本csv变量名
     */
    @TableField(value = "script_csv_variable_name")
    private String scriptCsvVariableName;

    /**
     * 是否忽略首行(0:否；1:是)
     */
    @TableField(value = "ignore_first_line")
    private Boolean ignoreFirstLine;

    /**
     * 脚本实例ID
     */
    @TableField(value = "script_deploy_id")
    private Long scriptDeployId;

    /**
     * 业务流程ID
     */
    @TableField(value = "business_flow_id")
    private Long businessFlowId;

    /**
     * 关联的文件ID
     */
    @TableField(value = "file_manage_id")
    private Long fileManageId;

    /**
     * 是否拆分
     */
    @TableField(value = "is_split")
    private Boolean isSplit;

    /**
     * 是否按照顺序拆分
     */
    @TableField(value = "is_order_split")
    private Boolean isOrderSplit;

    /**
     * 插入时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 变更时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;
}