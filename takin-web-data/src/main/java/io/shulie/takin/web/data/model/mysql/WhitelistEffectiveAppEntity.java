package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;

/**
* @author 何仲奇
* @date 2021/4/14 上午10:13
*/
@Data
@TableName(value = "t_whitelist_effective_app")
public class WhitelistEffectiveAppEntity extends UserBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 接口名
     */
    @TableField(value = "wlist_id")
    private Long wlistId;

    /**
     * 接口名
     */
    @TableField(value = "interface_name")
    private String interfaceName;

    /**
     * 类型
     */
    @TableField(value = "type")
    private String type;

    /**
     * 生效应用
     */
    @TableField(value = "EFFECTIVE_APP_NAME")
    private String effectiveAppName;


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