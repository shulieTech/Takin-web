package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.CommonEntityWithoutTenantIdAndEnvCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 第三方登录服务表(AnnualReport)实体类
 *
 * @author liuchuan
 * @date 2021-12-30 10:54:00
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_annual_report")
@ToString(callSuper = true)
public class AnnualReportEntity extends CommonEntityWithoutTenantIdAndEnvCode implements Serializable {
    private static final long serialVersionUID = 820643127897475427L;

    /**
     * 租户logo
     */
    private String tenantLogo;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 数据内容
     */
    private String content;

}
