package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;

/**
 * 黑名单管理
 */
@Data
@TableName(value = "t_black_list")
@EnableSign
public class BlackListEntity extends UserBaseEntity {
    /**
     * 主键id
     */
    @TableId(value = "BLIST_ID", type = IdType.AUTO)
    private Long blistId;


    /**
     * 黑名单类型
     */
    @TableField(value = "type")
    private Integer type ;

    /**
     * 应用id
     */
    @TableField(value = "APPLICATION_ID")
    private Long applicationId ;

    /**
     * 插入时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 变更时间
     */
    @TableField(value = "gmt_modified")
    private Date gmtModified;


    /**
     * redis的键
     */
    @TableField(value = "REDIS_KEY")
    private String redisKey;

    /**
     * 是否可用(0表示未启动,1表示启动,2表示启用未校验)
     */
    @TableField(value = "USE_YN")
    private Integer useYn;

    /**
     * 是否删除 0-未删除、1-已删除
     */
    @TableLogic
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    @TableField(value = "sign" , fill = FieldFill.INSERT)
    private String sign;

    @TableField(value = "PRINCIPAL_NO")
    private String principalNo;

    private String value;

    @TableField(value = "dept_id")
    private Long deptId;
}
