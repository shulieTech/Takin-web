package io.shulie.takin.web.biz.pojo.response.filemanage;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class FileManageResponse implements Serializable {
    private static final long serialVersionUID = -6001101141616189318L;

    @ApiModelProperty(value = "文件id")
    private Long id;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件大小：2MB
     */
    @ApiModelProperty(value = "文件大小：")
    private String fileSize;

    /**
     * 文件类型：0-脚本文件 1-数据文件 2-脚本jar文件 3-jmeter ext jar
     */
    @ApiModelProperty(value = "文件类型：0-脚本文件 1-数据文件 2-脚本jar文件 3-jmeter ext jar")
    private Integer fileType;

    @ApiModelProperty(value = "文件数据量")
    @JsonProperty("uploadedData")
    private Long dataCount;

    @ApiModelProperty(value = "是否拆分")
    private Integer isSplit;

    @ApiModelProperty(value = "是否按照顺序拆分")
    private Integer isOrderSplit;

    /**
     * 文件MD5值
     */
    @ApiModelProperty(value = "md5")
    private String md5;

    /**
     * 上传时间
     */
    @ApiModelProperty(value = "上传时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /**
     * 上传路径：返回给前端下载路径
     */
    @ApiModelProperty(value = "上传路径：返回给前端下载路径")
    @JsonProperty("downloadUrl")
    private String uploadPath;

    /**
     * 数据已被删除，新版本不新增进去
     */
    @ApiModelProperty(value = "数据已被删除，新版本不新增进去")
    private Integer isDeleted;

    @ApiModelProperty(value = "是否是大文件")
    private Integer isBigFile;

    @ApiModelProperty("jmx校验结果")
    private Boolean jmxCheckSuccess = true;

    @ApiModelProperty("jmx校验异常信息")
    private String jmxCheckErrorMsg;
}
