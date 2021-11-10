package io.shulie.takin.web.data.result.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginUpgradeRefEntity;

/**
 * 应用升级批次明细(ApplicationPluginUpgradeRef)列表出参类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:41:55
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ApplicationPluginUpgradeRefListResult extends ApplicationPluginUpgradeRefEntity {

}
