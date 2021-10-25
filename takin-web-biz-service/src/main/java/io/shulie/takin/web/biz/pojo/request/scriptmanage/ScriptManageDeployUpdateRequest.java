package io.shulie.takin.web.biz.pojo.request.scriptmanage;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import lombok.Data;

/**
 * @author zhaoyong
 */
@Data
public class ScriptManageDeployUpdateRequest implements Serializable {
    private static final long serialVersionUID = 459472161282660909L;

    /**
     * 脚本发布id(看使用情况, 是表 script_manage_deploy 的 id)
     */
    @JsonProperty("scriptId")
    private Long id;
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
    private List<FileManageUpdateRequest> fileManageUpdateRequests;

    /**
     * 关联附件列表
     */
    @JsonProperty("uploadAttachments")
    private List<FileManageUpdateRequest> attachmentManageUpdateRequests;

    /**
     * 引擎插件列表
     */
    @JsonProperty("pluginConfigs")
    private List<PluginConfigUpdateRequest> pluginConfigUpdateRequests;

    /**
     * 是否覆盖大文件
     */
    private Integer ifCoverBigFile;
}
