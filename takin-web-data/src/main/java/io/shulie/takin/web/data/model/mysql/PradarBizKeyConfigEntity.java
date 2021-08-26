package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 业务字段采集配置表
 */
@Data
@TableName(value = "pradar_biz_key_config")
public class PradarBizKeyConfigEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务key,不区分大小写
     */
    @TableField(value = "biz_key")
    private String bizKey;

    /**
     * 插入时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @TableField(value = "modify_time")
    private LocalDateTime modifyTime;

    /**
     * 1，有效，0，删除
     */
    @TableField(value = "deleted")
    private Boolean deleted;
}
