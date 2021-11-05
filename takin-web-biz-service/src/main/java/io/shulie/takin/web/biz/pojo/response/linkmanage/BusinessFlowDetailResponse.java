package io.shulie.takin.web.biz.pojo.response.linkmanage;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessLinkDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import com.pamirs.takin.entity.domain.vo.linkmanage.MiddleWareEntity;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.PluginConfigDetailResponse;
import io.shulie.takin.web.common.vo.WebOptionEntity;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhaoyong
 */
@Data
@ApiModel("出参类-业务流程详情出参")
public class BusinessFlowDetailResponse extends AuthQueryResponseCommonExt implements Serializable {

    @ApiModelProperty(name = "id", value = "业务流程id")
    private Long id;

    @ApiModelProperty(name = "businessProcessName", value = "业务流程名字")
    private String businessProcessName;

    @ApiModelProperty(name = "isCode", value = "是否核心流程")
    private Integer isCode;

    @ApiModelProperty(name = "sceneLevel", value = "业务流程等级")
    private String sceneLevel;

    @ApiModelProperty(name = "scriptJmxNodeList", value = "脚本的节点信息")
    private List<WebOptionEntity> scriptJmxNodeList;

    @ApiModelProperty(name = "threadGroupNum", value = "线程组数量")
    private Integer threadGroupNum;

    @ApiModelProperty(name = "linkRelateNum", value = "关联节点数")
    private Integer linkRelateNum;

    @ApiModelProperty(name = "totalNodeNum", value = "脚本总节点数")
    private Integer totalNodeNum;

    @ApiModelProperty(name = "scriptType", value = "脚本类型;0为jmeter脚本")
    private Integer scriptType;

    @ApiModelProperty(name = "updateTime", value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updateTime")
    private Date gmtUpdate;

    @ApiModelProperty(name = "scriptDeployId", value = "脚本实例id")
    private Long scriptDeployId;

    @ApiModelProperty(name = "scriptVersion", value = "脚本版本")
    private Integer scriptVersion;

    @ApiModelProperty(name = "scriptFile", value = "脚本文件")
    private FileManageResponse scriptFile;

    @ApiModelProperty(name = "relatedFiles", value = "文件列表")
    @JsonProperty("relatedFiles")
    private List<FileManageResponse> fileManageResponseList;

    @ApiModelProperty(name = "fileNum", value = "文件数量")
    private Integer fileNum;

    @ApiModelProperty(name = "pluginConfigs", value = "引擎插件列表")
    @JsonProperty("pluginConfigs")
    private List<PluginConfigDetailResponse> pluginConfigDetailResponseList;
}
