package io.shulie.takin.web.data.param;

import io.shulie.takin.web.data.model.mysql.SceneExcludedApplicationEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 探针包表(SceneExcludedApplication)创建入参类
 *
 * @author liuchuan
 * @date 2021-10-28 16:21:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CreateSceneExcludedApplicationParam extends SceneExcludedApplicationEntity {

}
