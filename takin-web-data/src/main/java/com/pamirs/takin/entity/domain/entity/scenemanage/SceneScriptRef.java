package com.pamirs.takin.entity.domain.entity.scenemanage;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SceneScriptRef extends SceneRef implements Serializable {

    private static final long serialVersionUID = -5988460182923288110L;

    private Long id;

    private Integer scriptType;

    private String fileName;

    private String fileSize;

    private Integer fileType;

    private String fileExtend;

    private Date uploadTime;

    private String uploadPath;

    private Integer isDeleted;

    private Date createTime;

    private String createName;

    private Date updateTime;

    private String updateName;

    /**
     * 上传文件ID
     */
    private String uploadId;
    /**
     * 租户id
     */
    private long tenantId;
    /**
     * 用户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
