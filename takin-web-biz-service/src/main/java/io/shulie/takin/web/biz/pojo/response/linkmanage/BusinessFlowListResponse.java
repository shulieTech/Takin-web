package io.shulie.takin.web.biz.pojo.response.linkmanage;


import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhaoyong
 */
@Data
@ApiModel("出参类-业务流程自动匹配出参")
public class BusinessFlowListResponse  extends AuthQueryResponseCommonExt implements Serializable {

    @ApiModelProperty(name = "id", value = "业务流程id")
    private Long id;

    @ApiModelProperty(name = "sceneName", value = "业务流程名称")
    private String sceneName;

    @ApiModelProperty(name = "sceneLevel", value = "场景等级 :p0/p1/02/03")
    private String sceneLevel;

    @ApiModelProperty(name = "isCore", value = "是否核心场景 0:不是;1:是")
    private Integer isCore;

    @ApiModelProperty(name = "isChanged", value = "是否有变更 0:没有变更，1有变更")
    private Integer isChanged;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private Date createTime;

    @ApiModelProperty(name = "updateTime", value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(name = "type", value = "场景类型，标识1为jmeter上传，默认0")
    private Integer type;

    @ApiModelProperty(name = "linkRelateNum", value = "关联节点数")
    private Integer linkRelateNum;

    @ApiModelProperty(name = "totalNodeNum", value = "脚本总节点数")
    private Integer totalNodeNum;
}
