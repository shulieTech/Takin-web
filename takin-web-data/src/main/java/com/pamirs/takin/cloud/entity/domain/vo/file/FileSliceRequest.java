package com.pamirs.takin.cloud.entity.domain.vo.file;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author moriarty
 */
@Data
public class FileSliceRequest {
    @ApiModelProperty(name = "场景ID")
    private Long sceneId;
    @ApiModelProperty(name = "启动pod数量")
    private Integer podNum;
    @ApiModelProperty(name = "文件路径")
    private String filePath;
    @ApiModelProperty(name = "文件名")
    private String fileName;
    @ApiModelProperty(name = "文件关联ID")
    private Long refId;
    @ApiModelProperty(name = "是否分片")
    private Boolean split;
    @ApiModelProperty(name = "是否按顺序分片")
    private Boolean orderSplit;
    @ApiModelProperty(name = "顺序分片排序列")
    private Integer orderColumnNum;
    @ApiModelProperty(name = "字段分隔符")
    private String columnSeparator;
    @ApiModelProperty(name = "是否强制重新分片")
    private Boolean forceSplit;
    @ApiModelProperty(name = "文件分隔符")
    private String delimiter;
    @ApiModelProperty(name = "文件MD5值")
    private String fileMd5;
}
