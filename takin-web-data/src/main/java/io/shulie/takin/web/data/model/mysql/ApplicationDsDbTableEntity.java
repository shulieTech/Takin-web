package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

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
public class ApplicationDsDbTableEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = -96340134222281342L;

    private Long appId;

    private String url;

    private String userName;

    /**
     * 业务库
     */
    private String bizDataBase;

    /**
     * 业务表
     */
    private String bizTable;


    private String shadowTable;

    /**
     * 是否手动录入 0:否;1:是
     */
    private Integer manualTag;

    /**
     * 租户id
     */
    private Long customerId;

    /**
     * 用户id
     */
    private Long userId;

    private Integer isCheck;

}
