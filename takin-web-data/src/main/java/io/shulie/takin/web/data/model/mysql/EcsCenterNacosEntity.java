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
@TableName(value = "t_ecs_nacos_info")
@ToString(callSuper = true)
@EnableSign
public class EcsCenterNacosEntity extends NewBaseEntity implements Serializable {

    private static final long serialVersionUID = 339110573810927775L;

    /**
     * ecs中心名称
     */
    private String ecsCenterName;

    /**
     * nacos服务地址
     */
    private String nacosServerAddr;

    /**
     * nacos命名空间
     */
    private String nacosNameSpace;

    /**
     * 是否是中心nacos
     */
    private Integer isCenterNacos;

}
