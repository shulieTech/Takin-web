package io.shulie.takin.web.data.result.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginUpgradeEntity;

/**
 * 应用升级单(ApplicationPluginUpgrade)列表出参类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:45:03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApplicationPluginUpgradeListResult extends ApplicationPluginUpgradeEntity {

}
