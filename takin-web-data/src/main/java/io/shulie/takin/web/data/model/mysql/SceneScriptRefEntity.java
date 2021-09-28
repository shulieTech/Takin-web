package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_scene_script_ref")
public class SceneScriptRefEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景ID
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * 脚本类型
     */
    @TableField(value = "script_type")
    private Integer scriptType;

    /**
     * 文件名称
     */
    @TableField(value = "file_name")
    private String fileName;

    /**
     * 文件大小：2MB
     */
    @TableField(value = "file_size")
    private String fileSize;

    /**
     * 文件类型：0-脚本文件 1-数据文件
     */
    @TableField(value = "file_type")
    private Integer fileType;

    /**
     * {
     * “dataCount”:”数据量”,
     * “isSplit”:”是否拆分”,
     * “topic”:”MQ主题”,
     * “nameServer”:”mq nameServer”,
     * }
     */
    @TableField(value = "file_extend")
    private String fileExtend;

    /**
     * 上传时间
     */
    @TableField(value = "upload_time")
    private LocalDateTime uploadTime;

    /**
     * 上传路径：相对路径
     */
    @TableField(value = "upload_path")
    private String uploadPath;

    @TableField(value = "is_deleted")
    private Integer isDeleted;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "create_name")
    private String createName;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @TableField(value = "update_name")
    private String updateName;

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
