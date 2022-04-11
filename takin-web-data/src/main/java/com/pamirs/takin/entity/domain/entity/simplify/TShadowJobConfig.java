package com.pamirs.takin.entity.domain.entity.simplify;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import io.shulie.takin.web.data.annocation.EnableSign;
import io.shulie.takin.web.ext.entity.UserCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.entity.domain.entity.simplify
 * @date 2020-03-17 15:39
 */
@ApiModel(value = "ShadowJobConfigQuery", description = "影子JOB配置实体类")
@Data
@EnableSign
public class TShadowJobConfig extends UserCommonExt {

    @ApiModelProperty(name = "id", value = "id")
    @TableId(value = "id")
    private Long id;

    @ApiModelProperty(name = "applicationId", value = "应用ID")
    private Long applicationId;

    @ApiModelProperty(name = "name", value = "JOB任务名称")
    private String name;

    @ApiModelProperty(name = "type", value = "JOB类型 0-quartz、1-elastic-job、2-xxl-job")
    private Integer type;

    @ApiModelProperty(name = "status", value = "0-可用 1-不可用")
    private Integer status;

    @ApiModelProperty(name = "active", value = "检测是否可用 0-可用 1-不可用")
    private Integer active;

    @ApiModelProperty(name = "createTime", value = "创建时间")
    private Date createTime;

    @ApiModelProperty(name = "updateTime", value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(name = "configCode", value = "JOB配置xml")
    private String configCode;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    private Integer isDeleted;

    private Long customerId;

    private String sign;

}
