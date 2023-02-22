package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhaoyong
 * @since 2023-02-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_application_ds_warn")
@ApiModel(value = "ApplicationDsWarn对象", description = "")
public class ApplicationDsWarnEntity extends TenantBaseEntity implements Serializable {


    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "应用名称")
    @TableField("app_name")
    private String appName;

    @ApiModelProperty(value = "agentId")
    @TableField("agent_id")
    private String agentId;

    @ApiModelProperty(value = "校验时间间隔")
    @TableField("check_interval")
    private Integer checkInterval;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "校验时间")
    @TableField("check_time")
    private Long checkTime;

    @ApiModelProperty(value = "校验URL")
    @TableField("check_url")
    private String checkUrl;

    @ApiModelProperty(value = "校验用户")
    @TableField("check_user")
    private String checkUser;

    @ApiModelProperty(value = "校验错误信息")
    @TableField("error_msg")
    private String errorMsg;

    @ApiModelProperty(value = "校验错误信息")
    @TableField("host_ip")
    private String hostIp;

    @ApiModelProperty(value = "是否删除：0-否 1-是")
    @TableField("is_deleted")
    private Integer isDeleted;

    @ApiModelProperty(value = "创建时间")
    @TableField("create_time")
    private LocalDateTime createTime;

    @ApiModelProperty(value = "最后修改时间")
    @TableField("update_time")
    private LocalDateTime updateTime;

}
