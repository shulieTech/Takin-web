package io.shulie.takin.web.data.param.opsscript;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Valid
public class OpsScriptFileParam implements Serializable {

    private static final long serialVersionUID = -4697425775712998397L;

    private String id;

    /**
     * 运维脚本ID
     */
    @ApiModelProperty(value = "运维脚本ID")
    private String opsScriptId;

    /**
     * 状态 1=主要文件 2=附件
     */
    @ApiModelProperty(value = "状态 1=主要文件 2=附件")
    private Integer fileType;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件大小：2MB
     */
    @ApiModelProperty(value = "文件大小")
    private String fileSize;

    @ApiModelProperty(value = "uploadId 上传ID")
    private String uploadId;

    @ApiModelProperty(value = "下载路径")
    @JsonProperty("downloadUrl")
    private String filePath;

    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date uploadTime;

    @ApiModelProperty(value = "文件内容")
    @JsonProperty("content")
    private String content;

}
