package io.shulie.takin.web.data.model.mysql;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_file_manage")
public class FileManageEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
     * 文件后缀
     */
    @TableField(value = "file_ext")
    private String fileExt;

    /**
     * 文件类型：0-脚本文件 1-数据文件 2-脚本jar文件 3-jmeter ext jar
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
     * 客户id（当前登录用户对应的admin的id，数据隔离使用）
     */
    @TableField(value = "customer_id" ,fill = FieldFill.INSERT)
    private Long customerId;

    /**
     * 上传时间
     */
    @TableField(value = "upload_time")
    private Date uploadTime;

    /**
     * 上传路径：相对路径
     */
    @TableField(value = "upload_path")
    private String uploadPath;

    @TableField(value = "is_deleted")
    private Integer isDeleted;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

    @TableField(value = "gmt_update")
    private Date gmtUpdate;

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
