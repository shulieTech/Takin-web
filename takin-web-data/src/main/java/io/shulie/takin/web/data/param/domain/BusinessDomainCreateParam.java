package io.shulie.takin.web.data.param.domain;
import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.BusinessDomainEntity;

/**
 * 业务域表(BusinessDomain)创建入参类
 *
 * @author liuchuan
 * @date 2021-12-07 15:59:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BusinessDomainCreateParam extends BusinessDomainEntity {
    
}
