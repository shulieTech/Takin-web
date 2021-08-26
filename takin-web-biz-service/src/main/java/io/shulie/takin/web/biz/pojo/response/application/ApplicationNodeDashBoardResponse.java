package io.shulie.takin.web.biz.pojo.response.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/16 8:35 下午
 */
@Data
@ApiModel("出参类-应用下的节点统计信息出参类")
public class ApplicationNodeDashBoardResponse {

    @ApiModelProperty("节点总数(应用新增/编辑时，填写的应用节点总数)")
    @JsonProperty("allNodeNum")
    private Integer nodeTotalCount = 0;

    @ApiModelProperty("在线节点数(agent上报上来的应用节点，即为在线节点)")
    @JsonProperty("onlineNodeNum")
    private Long nodeOnlineCount = 0L;

    @ApiModelProperty("安装节点数(已经安装了探针的在线应用节点总数)")
    private Long probeInstalledNodeNum = 0L;

    @ApiModelProperty("错误信息")
    private String errorMsg = "";


}
