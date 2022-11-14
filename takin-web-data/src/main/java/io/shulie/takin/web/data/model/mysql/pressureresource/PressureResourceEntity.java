package io.shulie.takin.web.data.model.mysql.pressureresource;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@TableName(value = "t_pressure_resource")
@ToString(callSuper = true)
public class PressureResourceEntity extends TenantBaseEntity {
    @TableId(value = "ID", type = IdType.AUTO)
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置名称")
    @TableField(value = "`name`")
    private String name;

    @ApiModelProperty("来源类型(0-手工,1-自动)")
    @TableField(value = "`type`")
    private Integer type;

    @ApiModelProperty("隔离方式(0-未设置 1-影子库 2-影子库/影子表 3-影子表)")
    @TableField(value = "`isolate_type`")
    private Integer isolateType;

    @ApiModelProperty("状态(0-未开始 1-已开始)")
    @TableField(value = "`status`")
    private Integer status;

    @ApiModelProperty("检测状态(0-未检测 1-检测中 2-检测完成)")
    @TableField(value = "`check_status`")
    private Integer checkStatus;

    @ApiModelProperty("来源的Id")
    @TableField(value = "`source_id`")
    private Long sourceId;

    @ApiModelProperty("归属人Id")
    @TableField(value = "user_id")
    private Long userId;

    @TableField(value = "`check_time`")
    @ApiModelProperty("检测时间")
    private Date checkTime;

    @TableField(value = "`gmt_create`")
    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @TableField(value = "`gmt_modified`")
    @ApiModelProperty("更新时间")
    private Date gmtModified;
}
