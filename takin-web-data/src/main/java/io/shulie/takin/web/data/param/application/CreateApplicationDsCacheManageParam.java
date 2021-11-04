package io.shulie.takin.web.data.param.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ApplicationDsCacheManageEntity;

/**
 * 缓存影子库表配置表(ApplicationDsCacheManage)创建入参类
 *
 * @author 南风
 * @date 2021-08-30 14:40:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateApplicationDsCacheManageParam extends ApplicationDsCacheManageEntity {

}
