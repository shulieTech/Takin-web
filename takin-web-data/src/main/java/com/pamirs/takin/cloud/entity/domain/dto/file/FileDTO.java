package com.pamirs.takin.cloud.entity.domain.dto.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/17 下午9:03
 */
@Data
@ApiModel(description = "上传结果")
public class FileDTO {
    @ApiModelProperty(value = "上传文件ID")
    private String uploadId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件MD5值")
    private String md5;

    @ApiModelProperty(value = "上传时间")
    private String uploadTime;

    @ApiModelProperty(value = "上传结果")
    private Boolean uploadResult;

    @ApiModelProperty(value = "失败原因")
    private String errorMsg;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;

    @ApiModelProperty(value = "上传数据量")
    private Long uploadedData;

    @ApiModelProperty(value = "是否拆分")
    private Integer isSplit;

    @ApiModelProperty(value = "Topic")
    private String topic;

    @ApiModelProperty(value = "文件类型")
    private Integer fileType;

    @ApiModelProperty(value = "文件访问路径")
    private String downloadUrl;
}
