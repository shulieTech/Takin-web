package io.shulie.takin.web.data.param.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbTableEntity;

/**
 * 业务数据库表(ApplicationDsDbTable)更新入参类
 *
 * @author 南风
 * @date 2021-09-15 17:21:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateApplicationDsDbTableParam extends ApplicationDsDbTableEntity {

}
