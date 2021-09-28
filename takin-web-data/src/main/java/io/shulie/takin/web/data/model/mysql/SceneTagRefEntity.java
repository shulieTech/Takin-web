package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-30 11:49
 */

@Data
@TableName(value = "t_scene_tag_ref")
public class SceneTagRefEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景id
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 标签id
     */
    @TableField(value = "tag_id")
    private Long tagId;

    @TableField(value = "gmt_create")
    private String gmtCreate;

    @TableField(value = "gmt_update")
    private String gmtUpdate;
    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
