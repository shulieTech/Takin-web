package io.shulie.takin.web.data.param.application;

import io.shulie.takin.web.data.model.mysql.AppAgentConfigReportEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * agent配置上报详情(AppAgentConfigReport)创建入参类
 *
 * @author 南风
 * @date 2021-09-28 17:27:23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateAppAgentConfigReportParam extends AppAgentConfigReportEntity {

}
