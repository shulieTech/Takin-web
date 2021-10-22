package io.shulie.takin.web.data.result.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbTableEntity;

/**
 * 业务数据库表(ApplicationDsDbTable)详情出参类
 *
 * @author 南风
 * @date 2021-09-15 17:21:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApplicationDsDbTableDetailResult extends ApplicationDsDbTableEntity {

}
