package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_application_esc_center_relation")
@ToString(callSuper = true)
@EnableSign
public class ApplicationEcsCenterEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 339110573700927775L;

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * ecs中心名称
     */
    private String ecsCenterName;



}
