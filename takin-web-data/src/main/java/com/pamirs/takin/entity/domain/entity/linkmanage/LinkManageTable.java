package com.pamirs.takin.entity.domain.entity.linkmanage;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * t_link_manage_table
 *
 * @author
 */
@Data
public class LinkManageTable implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    private Long linkId;
    /**
     * 链路名称
     */
    private String linkName;
    /**
     * 链路入口
     */
    private String entrace;
    /**
     * 技术链路变更前
     */
    private String changeBefore;
    /**
     * 技术链路变更后
     */
    private String changeAfter;
    /**
     * 变化差异
     */
    private String changeRemark;
    /**
     * 是否有变更 0:没有变更；1:有变更
     */
    private Integer isChange;
    /**
     * 是否有效 0:有效;1:无效
     */
    private Integer isDeleted;
    /**
     * 插入时间
     */
    private Date createTime;
    /**
     * 变更时间
     */
    private Date updateTime;
    /**
     * 应用名字
     */
    private String applicationName;
    /**
     * 变更类型
     */
    private Integer changeType;
    /**
     * 是否可以删除 0:可以删除;1:不可以删除
     */
    private Integer canDelete;

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
