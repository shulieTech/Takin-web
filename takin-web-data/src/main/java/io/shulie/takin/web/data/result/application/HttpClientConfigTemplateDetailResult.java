package io.shulie.takin.web.data.result.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.HttpClientConfigTemplateEntity;

/**
 * http-client配置模版表(HttpClientConfigTemplate)详情出参类
 *
 * @author 南风
 * @date 2021-08-25 14:56:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class HttpClientConfigTemplateDetailResult extends HttpClientConfigTemplateEntity {

}
