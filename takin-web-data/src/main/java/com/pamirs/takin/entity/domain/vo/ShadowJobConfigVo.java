package com.pamirs.takin.entity.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pamirs.takin.common.constant.JobEnum;
import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.entity.domain.vo
 * @date 2020-03-17 16:02
 */
@ApiModel(value = "ShadowJobConfigQuery", description = "影子JOB配置视图")
@Data
public class ShadowJobConfigVo extends AuthQueryResponseCommonExt {

    @ApiModelProperty(name = "id", value = "ID")
    private String id;

    @ApiModelProperty(name = "applicationId", value = "应用ID")
    private String applicationId;

    @ApiModelProperty(name = "applicationName", value = "应用名称")
    private String applicationName;

    @ApiModelProperty(name = "name", value = "JOB任务名称")
    private String name;

    @ApiModelProperty(name = "typeName", value = "JOB类型名称")
    private String typeName;

    @ApiModelProperty(name = "type", value = "JOB类型 0-quartz、1-elastic-job、2-xxl-job")
    private Integer type;

    @ApiModelProperty(name = "status", value = "0-可用 1-不可用")
    private Integer status;

    @ApiModelProperty(name = "configCode", value = "JOB配置xml")
    private String configCode;

    @ApiModelProperty(name = "active", value = "检测是否可用 0-可用 1-不可用")
    private Integer active;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(name = "updateTime", value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    public ShadowJobConfigVo() {
    }

    public ShadowJobConfigVo(TShadowJobConfig config) {
        this.id = String.valueOf(config.getId());
        this.applicationId = String.valueOf(config.getApplicationId());
        this.name = config.getName();
        this.type = config.getType();
        this.status = config.getStatus();
        this.configCode = config.getConfigCode();
        this.typeName = JobEnum.getJobByIndex(type).getText();
        this.active = config.getActive();
        this.updateTime = config.getUpdateTime();
        this.remark = config.getRemark();
    }

    public ShadowJobConfigVo(TShadowJobConfig config, String applicationName) {
        this.id = String.valueOf(config.getId());
        this.applicationId = String.valueOf(config.getApplicationId());
        this.name = config.getName();
        this.type = config.getType();
        this.status = config.getStatus();
        this.configCode = config.getConfigCode();
        this.typeName = JobEnum.getJobByIndex(type).getText();
        this.updateTime = config.getUpdateTime();
        this.applicationName = applicationName;
        this.active = config.getActive();
        this.remark = config.getRemark();
    }
}
