package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.annocation.SignField;
import io.shulie.takin.web.data.model.mysql.base.CommonWithUserIdAndTenantIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 业务数据库表(ApplicationDsDbTable)实体类
 *
 * @author 南风
 * @date 2021-09-15 17:21:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_ds_db_table")
@ToString(callSuper = true)
@EnableSign
public class ApplicationDsDbTableEntity extends CommonWithUserIdAndTenantIdEntity implements Serializable {
    private static final long serialVersionUID = -96340134222281342L;

    @SignField(order = 1)
    private Long appId;

    @SignField(order = 2)
    private String url;

    private String userName;

    /**
     * 业务库
     */
    private String bizDataBase;

    /**
     * 业务表
     */
    @SignField(order = 3)
    private String bizTable;

    @SignField(order = 4)
    private String shadowTable;

    /**
     * 是否手动录入 0:否;1:是
     */
    @SignField(order = 5)
    private Integer manualTag;

    @SignField(order = 6)
    private Integer isCheck;

    @TableField(value = "sign", fill = FieldFill.INSERT)
    private String sign;

}
