package com.pamirs.takin.cloud.entity.domain.entity.scene.manage;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * -
 *
 * @author -
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneScriptRef extends SceneRef {

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

    private String fileMd5;
}
