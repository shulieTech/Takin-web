package com.pamirs.takin.entity.domain.vo.cloudserver;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @author mubai
 * @date 2020-05-12 14:43
 */

@Data
public class BigFileUploadVO implements Serializable {
    private static final long serialVersionUID = 3132972991439604004L;

    private String fileName;

    private String topic;

    private Long sceneId;

    private String license;

    private List<String> data;

    private Boolean generateVirtualFile = false;

    private Integer dataCount;

}
