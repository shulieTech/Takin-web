package io.shulie.takin.web.data.param.opsscript;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@Valid
public class OpsScriptParam implements Serializable {
    //分页参数
    @ApiModelProperty(value = "当前页")
    private Integer current = 1;
    @ApiModelProperty(value = "页size")
    private Integer pageSize = 10;
    //
    private String id;
    /**
     * 租户ID
     */
    private Long customerId;

    /**
     * 脚本名称
     */
    @ApiModelProperty(value = "脚本名称")
    private String name;
    /**
     * 来源于字典。脚本类型：1=影子库表创建脚本 2=基础数据准备脚本 3=铺底数据脚本 4=影子库表清理脚本 5=缓存预热脚本
     */
    @ApiModelProperty(value = "脚本类型：1=影子库表创建脚本 2=基础数据准备脚本 3=铺底数据脚本 4=影子库表清理脚本 5=缓存预热脚本")
    private Integer scriptType;

    /**
     * 关联文件列表
     */
    @JsonProperty("uploadFiles")
    @ApiModelProperty(value = "关联文件列表")
    private List<OpsScriptFileParam> fileManageUpdateRequests;

    /**
     * 关联附件列表
     */
    @JsonProperty("uploadAttachments")
    @ApiModelProperty(value = "关联附件列表")
    private List<OpsScriptFileParam> attachmentManageUpdateRequests;

}
