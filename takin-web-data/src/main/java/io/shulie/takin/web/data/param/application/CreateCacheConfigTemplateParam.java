package io.shulie.takin.web.data.param.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.CacheConfigTemplateEntity;

/**
 * 缓存配置模版表(CacheConfigTemplate)创建入参类
 *
 * @author 南风
 * @date 2021-08-30 10:28:57
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateCacheConfigTemplateParam extends CacheConfigTemplateEntity {

}
