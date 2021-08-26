package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 探针包表(Probe)实体类
 *
 * @author liuchuan
 * @date 2021-06-03 12:11:44
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_probe")
public class ProbeEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = -72722253832357824L;

    /**
     * 版本号
     */
    private String version;

    /**
     * 上传地址
     */
    private String path;

    /**
     * 租户id
     */
    @TableField(value = "customer_id", fill = FieldFill.INSERT)
    private Long customerId;

}
