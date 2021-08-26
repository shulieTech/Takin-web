package io.shulie.takin.web.biz.pojo.request.filemanage;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author shulie
 */
@Data
public class FileManageCreateRequest implements Serializable {
    private static final long serialVersionUID = -9076093188667619607L;

    private String uploadId;
    /**
     * 文件名称
     */
    @NotNull
    @Size(max = 64)
    private String fileName;

    /**
     * 文件大小：2MB
     */
    private String fileSize;

    /**
     * 文件类型：0-脚本文件 1-数据文件 2-脚本jar文件 3-jmeter ext jar
     */
    private Integer fileType;

    /**
     * 脚本内容，前端编辑之后的脚本，需要转化为文件存储到指定目录
     */
    private String scriptContent;

    @JsonProperty("uploadedData")
    private Long dataCount;

    private Integer isSplit;

    private Integer isOrderSplit;

    /**
     * 数据已被删除，新版本不新增进去
     */
    private Integer isDeleted;

    /**
     * 上传时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date uploadTime;

}
