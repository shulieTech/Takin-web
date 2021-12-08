package io.shulie.takin.web.data.result;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.BusinessDomainEntity;

/**
 * 业务域表(BusinessDomain)列表出参类
 *
 * @author liuchuan
 * @date 2021-12-07 15:59:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BusinessDomainListResult extends BusinessDomainEntity {
    
}
