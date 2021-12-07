package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 运维脚本文件(OpsScriptFile)实体类
 *
 * @author caijy
 * @date 2021-06-16 10:35:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_ops_script_file")
public class OpsScriptFileEntity extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 897421451853584915L;

    /**
     * 运维脚本ID
     */
    private Long opsScriptId;

    /**
     * 状态 1=主要文件 2=附件
     */
    private Integer fileType;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件大小：2MB
     */
    private String fileSize;

    /**
     * 文件后缀
     */
    private String fileExt;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件上传ID 用于删除文件
     */
    private String uploadId;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtUpdate;

}
