package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 运维脚本批次号表(OpsScriptBatchNo)实体类
 *
 * @author caijy
 * @date 2021-06-16 10:35:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_ops_script_batch_no")
public class OpsScriptBatchNoEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 786491316776991383L;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 运维脚本ID
     */
    private Long opsScriptId;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtUpdate;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;

}
