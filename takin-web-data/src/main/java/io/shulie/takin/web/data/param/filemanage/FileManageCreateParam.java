package io.shulie.takin.web.data.param.filemanage;

import java.util.Date;

import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class FileManageCreateParam {

    private Long id;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小：2MB
     */
    private String fileSize;

    /**
     * 文件类型：0-脚本文件 1-数据文件 2-附件 3-jmeter 4 -shell ext jar
     */
    private Integer fileType;

    private String fileExtend;

    private Long customerId;

    private String uploadPath;

    /**
     * 上传时间
     */
    private Date uploadTime;

    /**
     * 是否删除
     * 以下两个, 更新cloud脚本文件用到
     */
    private Integer isDeleted;

    /**
     * 是否分隔
     */
    private Integer isSplit;

}
