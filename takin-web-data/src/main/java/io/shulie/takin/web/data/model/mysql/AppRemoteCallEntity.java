package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author 何仲奇
 * @date 2021/5/29 上午12:12
 */

/**
 * 远程调用表
 */
@Data
@TableName(value = "t_app_remote_call")
@EnableSign
public class AppRemoteCallEntity extends UserBaseEntity {
    /**
     * 主键
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 接口名称
     */
    @TableField(value = "interface_name")
    private String interfaceName;

    /**
     * 接口类型
     */
    @TableField(value = "interface_type")
    private Integer interfaceType;

    /**
     * 服务端应用名
     */
    @TableField(value = "server_app_name")
    private String serverAppName;

    /**
     * 应用id
     */
    @TableField(value = "APPLICATION_ID")
    private Long applicationId;

    /**
     * 应用name
     */
    @TableField(value = "APP_NAME")
    private String appName;

    /**
     * 配置类型，0：未配置，1：白名单配置;2：返回值mock;3:转发mock
     */
    @TableField(value = "type")
    private Integer type;

    /**
     * mock返回值
     */
    @TableField(value = "mock_return_value")
    private String mockReturnValue;

    /**
     * 是否有效 0:有效;1:无效
     */
    @TableField(value = "IS_DELETED")
    private Integer isDeleted;

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
     * 同步字段
     */
    @TableField(value = "is_synchronize")
    private Boolean isSynchronize;

    /**
     * 是否手动录入 0:否;1:是
     */
    @TableField(value = "manual_tag")
    private Integer manualTag;

    /**
     * 接口子类型
     */
    @TableField(value = "interface_child_type")
    private String interfaceChildType;

    @TableField(value = "remark")

    private String remark;

    /**
     * 应用名，接口名称，接口类型，租户id,envcode求md5
     */
    @TableField(value = "md5")
    private String md5;

    @TableField(value = "sign",fill = FieldFill.INSERT)
    private String sign;

}