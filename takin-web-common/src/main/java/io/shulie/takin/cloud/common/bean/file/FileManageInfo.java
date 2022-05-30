package io.shulie.takin.cloud.common.bean.file;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文件管理请求
 *
 * @author lipeng
 * @date 2021-01-13 5:41 下午
 */
@Data
@ApiModel("文件请求信息")
public class FileManageInfo {

    /**
     * 数据库字表id
     */
    @ApiModelProperty(value = "文件ID", dataType = "long")
    private Long fileId;

    @ApiModelProperty(value = "上传文件ID", dataType = "string")
    private String uploadId;

    @ApiModelProperty(value = "文件名称", dataType = "string")
    private String fileName;

    @ApiModelProperty(value = "文件是否删除")
    private Boolean isDeleted;
}
