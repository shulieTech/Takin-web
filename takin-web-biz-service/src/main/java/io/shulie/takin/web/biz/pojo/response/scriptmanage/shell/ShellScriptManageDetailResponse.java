package io.shulie.takin.web.biz.pojo.response.scriptmanage.shell;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author shulie
 */
@Data
public class ShellScriptManageDetailResponse implements Serializable {
    private static final long serialVersionUID = 573256048970054542L;

    /**
     * 脚本id
     */
    private Long id;

    /**
     * 脚本实例id
     */
    private Long scriptDeployId;

    /**
     * 名称
     */
    @JsonProperty("scriptName")
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 脚本类型;0为jmeter脚本,1为shell脚本
     */
    @JsonProperty("scriptType")
    private Integer type;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updateTime")
    private Date gmtUpdate;

    /**
     * 脚本版本
     */
    private Integer scriptVersion;


    /**
     * shell脚本内容
     */
    private String content;

    /**
     * 文件类型
     */
    private Integer fileType;

    /**
     * 版本列表
     */
    private List<Map<String,Object>> versions;

}
