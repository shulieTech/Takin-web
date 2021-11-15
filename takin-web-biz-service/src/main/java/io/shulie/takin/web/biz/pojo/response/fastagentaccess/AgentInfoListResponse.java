package io.shulie.takin.web.biz.pojo.response.fastagentaccess;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * agent版本管理(AgentVersion)controller 应用探针信息
 *
 * @author 南风
 * @date 2021-11-15 10:43:22
 */
@ApiModel("出参类-信息出参")
@Data
public class AgentInfoListResponse  {

    @ApiModelProperty("应用id")
    private Long applicationId;

    @ApiModelProperty("应用名称")
    private String applicationName;

    @ApiModelProperty("节点数量")
    private String mntNodeNum;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("更新时间")
    private Date gmtUpdate;

    /**
     * 负责人
     */
    @ApiModelProperty("负责人")
    private String owner;


    /**
     * 版本特性
     */
    @ApiModelProperty("标签")
    private List<String> tags;

    @ApiModelProperty("探针状态")
    private AgentStateInfo agentState;

    /**
     * 是否可升级
     *
     */
    @ApiModelProperty("是否可升级")
    private Boolean canUpgrade = true;

    /**
     * 是否可分配
     */
    @ApiModelProperty("是否可分配")
    private Boolean canAssign = true;


    @Data
    public static class AgentStateInfo {

        @ApiModelProperty("应用id")
        private Long applicationId;

        @ApiModelProperty(name = "accessStatus", value = "接入状态； 0：正常 ； 1；异常 ；2：升级中")
        private Integer accessStatus;

        @ApiModelProperty(name = "nodeNum", value = "应用实际总节点数量")
        private Integer nodeNum;

        @ApiModelProperty(name = "errorNum", value = "异常节点数量")
        private Integer errorNum;

        @ApiModelProperty(name = "waitRestartNum", value = "待重启节点数量")
        private Integer waitRestartNum;

        @ApiModelProperty(name = "waitRestartNum", value = "休眠节点数量")
        private Integer sleepNum;

        @ApiModelProperty(name = "runningNum", value = "运行中节点数量")
        private Integer runningNum;

        @ApiModelProperty(name = "version", value = "探针主版本")
        private String version;

    }

}
