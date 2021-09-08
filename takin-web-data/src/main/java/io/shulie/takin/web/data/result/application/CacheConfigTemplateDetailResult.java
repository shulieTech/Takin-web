package io.shulie.takin.web.data.result.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.CacheConfigTemplateEntity;

/**
 * 缓存配置模版表(CacheConfigTemplate)详情出参类
 *
 * @author 南风
 * @date 2021-08-30 10:28:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CacheConfigTemplateDetailResult extends CacheConfigTemplateEntity {

}
