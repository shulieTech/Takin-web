package io.shulie.takin.web.data.result.application;

import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.PluginLibraryEntity;

/**
 * 插件版本库(PluginLibrary)详情出参类
 *
 * @author ocean_wll
 * @date 2021-11-09 20:47:20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PluginLibraryDetailResult extends PluginLibraryEntity {

}
