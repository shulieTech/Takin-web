package com.pamirs.takin.entity.domain.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.entity.domain.query
 * @date 2020-03-17 15:47
 */
@ApiModel(value = "ShadowJobConfigQuery", description = "影子JOB配置查询实体类")
@Data
public class ShadowJobConfigQuery extends AbstractQueryPage {

    @ApiModelProperty(name = "id", value = "id")
    private Long id;

    @ApiModelProperty(name = "applicationId", value = "应用ID")
    private Long applicationId;

    @ApiModelProperty(name = "name", value = "JOB任务名称")
    private String name;

    @ApiModelProperty(name = "type", value = "JOB类型 0-quartz、1-elastic-job、2-xxl-job")
    private Integer type;

    @ApiModelProperty(name = "status", value = "0-可用 1-不可用")
    private Integer status;

    @ApiModelProperty(name = "configCode", value = "JOB配置xml")
    private String configCode;

    @ApiModelProperty(name = "orderBy", value = "排序字段")
    private String orderBy;

    @ApiModelProperty(name = "active", value = "检测影子JOB是否生效")
    private Integer active;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

}
