package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/11 2:54 下午
 */
@Data
@TableName(value = "t_script_execute_result")
public class ScriptExecuteResultEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 实例id
     */
    @TableField(value = "script_deploy_id")
    private Long scripDeployId;

    /**
     * 脚本id
     */
    @TableField(value = "script_id")
    private Long scriptId;

    /**
     * 脚本id
     */
    @TableField(value = "script_version")
    private Integer scriptVersion;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 执行人
     */
    @TableField(value = "executor")
    private String executor;

    /**
     * 执行结果
     */
    @TableField(value = "success")
    private Boolean success;

    /**
     * 执行结果
     */
    @TableField(value = "result")
    private String result;

}
