package io.shulie.takin.web.data.result.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallTemplateMappingEntity;

/**
 * 远程调用接口类型与模板映射(AppRemoteCallTemplateMapping)详情出参类
 *
 * @author 南风
 * @date 2021-09-09 14:44:42
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppRemoteCallTemplateMappingDetailResult extends AppRemoteCallTemplateMappingEntity {

}
