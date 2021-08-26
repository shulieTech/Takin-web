package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_tag_manage")
public class TagManageEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    @TableField(value = "tag_name")
    private String tagName;

    /**
     * 标签类型;0为脚本标签
     */
    @TableField(value = "tag_type")
    private Integer tagType;

    /**
     * 标签状态;0为可用
     */
    @TableField(value = "tag_status")
    private Integer tagStatus;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

    @TableField(value = "gmt_update")
    private Date gmtUpdate;
}
