package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_script_tag_ref")
public class ScriptTagRefEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景id
     */
    @TableField(value = "script_id")
    private Long scriptId;

    /**
     * 标签id
     */
    @TableField(value = "tag_id")
    private Long tagId;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

    @TableField(value = "gmt_update")
    private Date gmtUpdate;
}
