package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.UserBaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_global_scene_manage")
public class GlobalSceneManageEntity extends UserBaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableId(value = "scene_manage_id")
    private Long sceneManageId;

    @TableId(value = "scene_name")
    private String sceneName;

    @TableId(value = "scene_detail")
    private String sceneDetail;

    @TableId(value = "file_content")
    private String fileContent;
    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    /**
     * 添加逻辑删除注解
     */
    @TableField(value = "is_deleted")
    private Integer isDeleted;
}
