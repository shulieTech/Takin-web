package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.NewBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 探针包表(SceneExcludedApplication)实体类
 *
 * @author liuchuan
 * @date 2021-10-28 16:21:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_scene_excluded_application")
@ToString(callSuper = true)
public class SceneExcludedApplicationEntity extends NewBaseEntity implements Serializable {
    private static final long serialVersionUID = 652689184428277878L;

    /**
     * cloud场景id
     */
    private Long sceneId;

    /**
     * 应用id
     */
    private Long applicationId;

}
