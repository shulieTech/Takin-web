package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * agent版本管理(AgentVersion)实体类
 *
 * @author liuchuan
 * @date 2021-08-11 19:44:46
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_agent_version")
@ToString(callSuper = true)
public class AgentVersionEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 931570071748090483L;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 大版本号
     */
    private String firstVersion;

    /**
     * 版本号
     */
    private String version;

    /**
     * 版本号对应的数值
     */
    private Long versionNum;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 版本特性
     */
    private String versionFeatures;

    /**
     * 删除
     * 1 删除, 0 未删除
     */
    private Integer isDeleted;

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    private Long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
