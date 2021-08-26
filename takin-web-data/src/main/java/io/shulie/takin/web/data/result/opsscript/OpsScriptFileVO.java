package io.shulie.takin.web.data.result.opsscript;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * (OpsScriptVO)
 *
 * @author caijy
 * @date 2021-05-18 16:48:12
 */
@Data
public class OpsScriptFileVO implements Serializable {
    private static final long serialVersionUID = -4697425775712998397L;

    private String id;

    @ApiModelProperty(value = "脚本ID")
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
    @ApiModelProperty(value = "上传ID")
    private String uploadId;
    @ApiModelProperty(value = "下载路径")
    private String downloadUrl;

    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date uploadTime;
}
