package io.shulie.takin.web.data.param.application;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ApplicationMiddlewareEntity;
import lombok.ToString;

/**
 * 应用中间件(ApplicationMiddleware)更新入参类
 *
 * @author liuchuan
 * @date 2021-06-30 16:09:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateApplicationMiddlewareParam extends ApplicationMiddlewareEntity {

}
