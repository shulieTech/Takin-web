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

    @ApiModelProperty(name = "status", value = "状态：0未匹配完成，1匹配完成")
    private Integer status;

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

    @ApiModelProperty(name = "scriptDeployId", value = "脚本实例id")
    private Long scriptDeployId;

    private String features;
}
