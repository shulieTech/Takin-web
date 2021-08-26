package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_cloud_account")
public class CloudAccountEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 云平台id
     */
    @TableField(value = "platform_id")
    private Long platformId;

    /**
     * 云平台名称
     */
    @TableField(value = "platform_name")
    private String platformName;

    /**
     * 账号
     */
    @TableField(value = "account")
    private String account;

    /**
     * 平台授权数据
     */
    @TableField(value = "authorize_param")
    private String authorizeParam;

    /**
     * 状态 0:启用  1： 冻结
     */
    @TableField(value = "status")
    private Boolean status;

    /**
     * 状态 0: 正常 1： 删除
     */
    @TableField(value = "is_delete")
    private Boolean isDelete;

    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 修改时间
     */
    @TableField(value = "gmt_update")
    private LocalDateTime gmtUpdate;
}
