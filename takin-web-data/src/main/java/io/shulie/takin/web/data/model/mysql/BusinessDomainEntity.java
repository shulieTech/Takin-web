package io.shulie.takin.web.data.model.mysql;

import java.sql.Date;
import java.time.LocalDateTime;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.BaseEntity;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 业务域表(BusinessDomain)实体类
 *
 * @author liuchuan
 * @date 2021-12-07 15:59:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_business_domain")
@ToString(callSuper = true)
public class BusinessDomainEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -92138334469783918L;
    
   /**
   * 业务域名称
   */  
    private String name;
    
   /**
   * 业务域类型:0默认 1自定义
   */  
    private Integer type;
    
   /**
   * 业务域顺序
   */  
    private Integer domainOrder;
    
   /**
   * 业务域编码
   */  
    private Integer domainCode;
    
   /**
   * 业务域状态;0为可用,1为不可用
   */  
    private Integer status;
    
   /**
   * 创建时间
   */  
    private Date createTime;
    
   /**
   * 修改时间
   */  
    private Date updateTime;
    
   /**
   * 租户id
   */  
    private Long tenantId;
    
   /**
   * 环境变量
   */  
    private String envCode;
    
}
