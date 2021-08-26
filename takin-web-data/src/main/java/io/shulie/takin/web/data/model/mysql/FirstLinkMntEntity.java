package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 一级链路管理表
 */
@Data
@TableName(value = "t_first_link_mnt")
public class FirstLinkMntEntity {
    /**
     * 主键id
     */
    @TableId(value = "LINK_ID", type = IdType.INPUT)
    private String linkId;

    /**
     * 链路名称
     */
    @TableField(value = "LINK_NAME")
    private String linkName;

    /**
     * 二级业务链路id串
     */
    @TableField(value = "SECOND_LINKS")
    private String secondLinks;

    /**
     * 链路TPS
     */
    @TableField(value = "LINK_TPS")
    private Long linkTps;

    /**
     * 一级链路TPS计算规则
     */
    @TableField(value = "LINK_TPS_RULE")
    private String linkTpsRule;

    /**
     * 是否可用(0表示未启用,1表示启用)
     */
    @TableField(value = "USE_YN")
    private Integer useYn;

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

    /**
     * 备注(预留)
     */
    @TableField(value = "remark")
    private String remark;
}
