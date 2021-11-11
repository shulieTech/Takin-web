package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

/**
* @author 何仲奇
* @date 2021/1/4 7:30 下午
*/
@Data
@TableName(value = "t_exception_info")
public class ExceptionInfoEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 异常类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 异常编码
     */
    @TableField(value = "code")
    private String code;
    /**
     * agent异常编码
     */
    @TableField(value = "agent_code")
    private String agentCode;

    /**
     * 异常描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 处理建议
     */
    @TableField(value = "suggestion")
    private String suggestion;

    /**
     * 发生次数
     */
    @TableField(value = "count")
    private Long count;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @TableField(value = "gmt_modified")
    private Date gmtModified;

    /**
     * 软删
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

}