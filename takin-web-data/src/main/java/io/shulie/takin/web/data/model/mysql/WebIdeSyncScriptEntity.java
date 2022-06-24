package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: 南风
 * @Date: 2022/6/24 1:58 下午
 */
@Data
@TableName("t_webide_sync_script")
public class WebIdeSyncScriptEntity extends TenantBaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long workRecordId;

    private String request;

    private Integer isError;

    private String callbackContext;

    private Long businessFlowId;

    private Long scriptDeployId;

    private Long scriptDebugId;

    private String errorMsg;

    private String errorStage;

    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
