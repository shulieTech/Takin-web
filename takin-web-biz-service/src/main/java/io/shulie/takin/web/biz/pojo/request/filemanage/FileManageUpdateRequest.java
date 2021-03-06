package io.shulie.takin.web.biz.pojo.request.filemanage;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class FileManageUpdateRequest implements Serializable {
    private static final long serialVersionUID = -4697425775712998397L;

    private Long id;

    private String uploadId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小：2MB
     */
    private String fileSize;

    /**
     * 文件类型：0-脚本文件 1-数据文件 2-附件
     */
    private Integer fileType;

    /**
     * 文件MD5值
     */
    private String md5;

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

    /**
     * 大文件标识
     */
    private Integer isBigFile;

    /**
     * 新上传的文件的下载路径
     */
    private String downloadUrl;
}
