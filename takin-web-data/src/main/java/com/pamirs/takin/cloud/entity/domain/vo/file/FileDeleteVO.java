package com.pamirs.takin.cloud.entity.domain.vo.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qianshui
 * @date 2020/4/20 上午11:19
 */
@Data
@ApiModel(description = "文件删除入参")
public class FileDeleteVO {

    @ApiModelProperty(value = "上传文件ID")
    private String uploadId;

    @ApiModelProperty(value = "Topic")
    private String topic;
}
