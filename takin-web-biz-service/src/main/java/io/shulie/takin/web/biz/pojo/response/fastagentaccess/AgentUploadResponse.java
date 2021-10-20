package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description agent文件上传响应
 * @Author ocean_wll
 * @Date 2021/8/12 10:22 上午
 */
@Data
@ApiModel("出参类-agent上传")
public class AgentUploadResponse {

    @ApiModelProperty("文件路径")
    private String filePath;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("上传时文件的名称, 非上传后的真名称")
    private String originalName;

    @ApiModelProperty("agent版本")
    private String version;

    @ApiModelProperty("是否已存在")
    private Boolean exist;
}
