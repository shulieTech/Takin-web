package io.shulie.takin.web.data.result.application;

import io.shulie.takin.web.data.model.mysql.ApplicationMiddlewareEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 应用中间件(ApplicationMiddleware)列表出参类
 *
 * @author liuchuan
 * @date 2021-06-30 16:09:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApplicationMiddlewareListResult extends ApplicationMiddlewareEntity {

}
