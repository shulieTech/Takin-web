package com.pamirs.takin.entity.domain.entity.scenemanage;

import java.io.Serializable;
import java.util.Date;

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
}
