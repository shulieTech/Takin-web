package io.shulie.takin.web.data.result;

import io.shulie.takin.web.data.model.mysql.SceneMonitorEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 第三方登录服务表(SceneMonitor)详情出参类
 *
 * @author liuchuan
 * @date 2021-12-29 10:20:13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SceneMonitorListResult extends SceneMonitorEntity {

}
