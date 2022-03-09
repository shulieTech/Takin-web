package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.annocation.SignField;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * (ApplicationPluginsConfig)实体类
 *
 * @author liuchuan
 * @date 2021-05-18 16:48:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_plugins_config")
@EnableSign
public class ApplicationPluginsConfigEntity extends UserBaseEntity implements Serializable {
    private static final long serialVersionUID = -20702119464891307L;

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 应用id
     */
    @SignField(order = 1)
    private Long applicationId;

    /**
     * 应用名称
     */
    @SignField(order = 2)
    private String applicationName;

    /**
     * 配置项
     */
    @SignField(order = 3)
    private String configItem;

    /**
     * 配置项key
     */
    @SignField(order = 4)
    private String configKey;

    /**
     * 配置说明
     */
    @SignField(order = 5)
    private String configDesc;

    /**
     * 配置值
     */
    @SignField(order = 6)
    private String configValue;


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifieTime;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 修改人ID
     */
    private Long modifierId;


    /**
     * 删除
     * 1 删除, 0 未删除
     */
    @TableLogic
    private Integer isDeleted;

    @TableField(value = "sign",fill = FieldFill.INSERT)
    private String sign;

}
