package io.shulie.takin.cloud.data.model.mysql;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.cloud.data.annocation.EnableSign;
import lombok.Data;

/**
 * @author -
 */
@Data
@TableName(value = "t_scene_big_file_slice")
public class SceneBigFileSliceEntity {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景ID
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 脚本ID
     */
    @TableField(value = "script_ref_id")
    private Long scriptRefId;

    /**
     * 文件路径
     */
    @TableField(value = "file_path")
    private String filePath;

    /**
     * 文件名
     */
    @TableField(value = "file_name")
    private String fileName;

    /**
     * 分片数量
     */
    @TableField(value = "slice_count")
    private Integer sliceCount;

    /**
     * 文件分片信息
     */
    @TableField(value = "slice_info")
    private String sliceInfo;

    /**
     * 状态：0-未分片；1-已分片；2-文件已更改
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 分片时文件最后更改时间
     */
    @TableField(value = "file_update_time")
    private Date fileUpdateTime;

    /**
     * 分片时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @TableField(value = "sign" , fill = FieldFill.INSERT)
    private String sign;
}