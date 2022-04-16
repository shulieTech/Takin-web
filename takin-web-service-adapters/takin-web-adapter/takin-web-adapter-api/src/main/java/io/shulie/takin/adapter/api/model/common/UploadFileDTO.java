package io.shulie.takin.adapter.api.model.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 上传文件的传输类
 *
 * @author liuchuan
 * @date 2021/4/26 11:00 上午
 */
@Data
@ApiModel("传输类-脚本文件")
public class UploadFileDTO {

    /**
     * web 下的 fileId
     */
    @ApiModelProperty(value = "文件id")
    private Long id;

    @ApiModelProperty(value = "上传ID")
    private String uploadId;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "上传时间")
    private String uploadTime;

    @ApiModelProperty(value = "上传路径")
    private String uploadPath;

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

    /**
     * 文件扩展
     */
    @ApiModelProperty(hidden = true)
    private String fileExtend;

    /**
     * 文件大小
     */
    @ApiModelProperty(hidden = true)
    private String fileSize;

    /**
     * 脚本id
     */
    @ApiModelProperty(hidden = true)
    private Long scriptId;

}
