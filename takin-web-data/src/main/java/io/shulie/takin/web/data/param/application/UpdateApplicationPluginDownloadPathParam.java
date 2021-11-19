package io.shulie.takin.web.data.param.application;
import lombok.ToString;
import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.web.data.model.mysql.ApplicationPluginDownloadPathEntity;

/**
 * 探针根目录(ApplicationPluginDownloadPath)更新入参类
 *
 * @author 南风
 * @date 2021-11-10 16:12:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UpdateApplicationPluginDownloadPathParam extends CreateApplicationPluginDownloadPathParam {
    
}
