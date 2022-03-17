package io.shulie.takin.web.data.model.mysql;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
@TableName("t_tro_version")
public class VersionEntity implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @JsonIgnore
    private Long id;
    /**
     * 版本号
     */
    private String version;
    /**
     * 描述
     */
    private String description;
    /**
     * 发布人
     */
    private String publisher;
    /**
     * 升级文档
     */
    private String url;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
