package io.shulie.takin.web.biz.pojo.response.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/6/4 11:28 上午
 */
@Data
@ApiModel("出参类-文件上传")
public class FileUploadResponse {

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("上传时文件的名称, 非上传后的真名称")
    private String originalName;

}
