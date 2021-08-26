package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "pradar_app_group")
public class PradarAppGroupEntity {
    /**
     * 组ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 关联应用ID
     */
    @TableField(value = "app_info_id")
    private Long appInfoId;

    /**
     * 组名称
     */
    @TableField(value = "group_name")
    private String groupName;

    /**
     * 域名
     */
    @TableField(value = "domain_name")
    private String domainName;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "modify_time")
    private LocalDateTime modifyTime;

    /**
     * 删除标识
     */
    @TableField(value = "deleted")
    private Integer deleted;
}
