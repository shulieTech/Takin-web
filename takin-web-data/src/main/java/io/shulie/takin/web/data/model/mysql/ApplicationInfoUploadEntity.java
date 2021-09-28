package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 应用信息上传
 */
@Data
@TableName(value = "t_application_info_upload")
public class ApplicationInfoUploadEntity {
    /**
     * 上传应用信息id
     */
    @TableId(value = "TAIU_ID", type = IdType.INPUT)
    private Long taiuId;

    /**
     * 应用名称
     */
    @TableField(value = "APPLICATION_NAME")
    private String applicationName;

    /**
     * 1 堆栈 2 SQL 异常
     */
    @TableField(value = "INFO_TYPE")
    private Integer infoType;

    /**
     * 保存的信息
     */
    @TableField(value = "UPLOAD_INFO")
    private String uploadInfo;

    /**
     * 插入时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    private Long userId;
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
