package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageCreateRequest;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScriptManageDeployCreateRequest implements Serializable {

    private static final long serialVersionUID = -492660081130014355L;
    /**
     * 名称
     */
    @JsonProperty("scriptName")
    private String name;

    /**
     * 关联类型(业务活动)
     */
    @JsonProperty("relatedType")
    private String refType;

    /**
     * 关联值(活动id)
     */
    @JsonProperty("relatedId")
    private String refValue;

    /**
     * 脚本类型;0为jmeter脚本
     */
    @JsonProperty("scriptType")
    private Integer type;

    /**
     * 关联文件列表
     */
    @JsonProperty("uploadFiles")
    private List<FileManageCreateRequest> fileManageCreateRequests;

    /**
     * 关联附件列表
     */
    @JsonProperty("uploadAttachments")
    private List<FileManageCreateRequest> attachmentManageCreateRequests;

    /**
     * 引擎插件列表
     */
    @JsonProperty("pluginConfigs")
    private List<PluginConfigCreateRequest> pluginConfigCreateRequests;

}
