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
public class BusinessFlowMatchResponse extends AuthQueryResponseCommonExt implements Serializable {

    @ApiModelProperty(name = "id", value = "业务流程id")
    private Long id;

    @ApiModelProperty(name = "businessProcessName", value = "业务流程名字")
    private String businessProcessName;

    @ApiModelProperty(name = "matchNum", value = "匹配条数")
    private Integer matchNum;

    @ApiModelProperty(name = "unMatchNum", value = "未匹配条数")
    private Integer unMatchNum;

    @ApiModelProperty(name = "finishDate", value = "完成时间")
    private Date finishDate;
}
