package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_activity_category")
public class ActivityCategoryEntity extends TenantBaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "parent_id")
    private Long parentId;

    @TableField(value = "title")
    private String title;

    /**
     * 维护从根节点 -> 该节点 的关系
     */
    @TableField("relation_path")
    private String relationPath;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

    @TableField(value = "gmt_update")
    private Date gmtUpdate;

    public ActivityCategoryEntity() {
    }

    public ActivityCategoryEntity(Long id, Long parentId, String title, String relationPath) {
        this.id = id;
        this.parentId = parentId;
        this.title = title;
        this.relationPath = relationPath;
    }
}
