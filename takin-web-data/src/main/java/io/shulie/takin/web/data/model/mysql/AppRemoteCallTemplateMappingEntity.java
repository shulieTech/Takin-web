package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.CommonWithUserIdAndTenantIdEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 远程调用接口类型与模板映射(AppRemoteCallTemplateMapping)实体类
 *
 * @author 南风
 * @date 2021-09-09 14:44:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_app_remote_call_template_mapping")
@ToString(callSuper = true)
public class AppRemoteCallTemplateMappingEntity extends CommonWithUserIdAndTenantIdEntity implements Serializable {
    private static final long serialVersionUID = -87494347952500936L;

    /**
     * 接口类型
     */
    private String interfacetype;

    /**
     * 对应的模板
     */
    private String template;

    /**
     * 0.可用，1不可用
     */
    private Integer status;

    /**
     * 标记
     */
    private String remark;

    /**
     * 备注
     */
    private String commit;



}
