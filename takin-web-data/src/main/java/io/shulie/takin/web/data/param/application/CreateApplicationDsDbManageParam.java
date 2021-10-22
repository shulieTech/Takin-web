package io.shulie.takin.web.data.param.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbManageEntity;

/**
 * db连接池影子库表配置表(ApplicationDsDbManage)创建入参类
 *
 * @author 南风
 * @date 2021-08-30 14:41:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateApplicationDsDbManageParam extends ApplicationDsDbManageEntity {

}
