package com.pamirs.takin.cloud.entity.domain.vo.scenemanage;

import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class FileSplitResultVO {
    private Long sceneId;
    private String fileName;
    private String sliceInfo;
    private Integer sliceCount;
    private Integer isSplit;
    private Integer isOrderSplit;
}
