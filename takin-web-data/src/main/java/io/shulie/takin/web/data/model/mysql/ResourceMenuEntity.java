package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/3 7:34 下午
 */
@Data
public class ResourceMenuEntity {

    private Long id;

    private Long parentId;

    private String code;

    private String name;

    private String value;

    private String sequence;

    private String action;

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
