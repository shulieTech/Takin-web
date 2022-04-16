package io.shulie.takin.cloud.data.param.scenemanage;

import java.util.Date;

import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class SceneBigFileSliceParam {
    private Long id;
    private Long sceneId;
    private Long fileRefId;
    private Integer sliceCount;
    private String fileName;
    private String filePath;
    private String sliceInfo;
    private Integer status;
    private Date fileUploadTime;
    private Integer isOrderSplit;
    private Integer isSplit;
    private String fileMd5;
}
