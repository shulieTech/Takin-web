package com.pamirs.takin.entity.domain.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/6/9 下午5:22
 */
@Data
@ApiModel(value = "TBListDelete", description = "黑名单实体类")
public class TBListDelete {
    @ApiModelProperty(name = "blistIds", value = "黑名单编号列表")
    private List<Long> blistIds;
    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;

    /**
     * 用户id
     */
    private long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
