package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.*;
import io.shulie.takin.utils.PathFormatForTest;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "t_file_manage")
@EnableSign
public class FileManageEntity extends TenantBaseEntity {
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
     * 文件MD5
     */
    @TableField(value = "md5")
    private String md5;

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
     * 上传时间
     */
    @TableField(value = "upload_time")
    private Date uploadTime;

    /**
     * 上传路径：相对路径
     */
    @TableField(value = "upload_path")
    private String uploadPath;

    public String getUploadPath() {
        return PathFormatForTest.format(uploadPath);
    }

    @TableField(value = "is_deleted")
    private Integer isDeleted;

    @TableField(value = "gmt_create")
    private Date gmtCreate;

    @TableField(value = "gmt_update")
    private Date gmtUpdate;

    @TableField(value = "sign",fill = FieldFill.INSERT)
    private String sign;
}
