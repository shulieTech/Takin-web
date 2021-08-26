package io.shulie.takin.web.biz.pojo.response.activity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2021-01-11
 */
@Data
@ApiModel
public class ActivityListResponse extends AuthQueryResponseCommonExt {

    /**
     * 主键
     */
    private Long activityId;

    /**
     * 链路名称
     */
    private String activityName;

    /**
     * 是否有变更 0:正常；1:已变更
     */
    private Integer isChange;

    /**
     * 业务链路是否否核心链路 0:不是;1:是
     */
    @ApiModelProperty(name = "isCore", value = "业务活动链路是否核心链路 0:不是;1:是")
    private String isCore;

    @ApiModelProperty(name = "link_level", value = "业务活动等级")
    @JsonProperty("link_level")
    private String activityLevel;

    /**
     * 是否有效 0:有效;1:无效
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 业务域: 0:订单域", "1:运单域", "2:结算域
     */
    @ApiModelProperty(name = "businessDomain", value = "业务域")
    private String businessDomain;

    /**
     * 是否可以删除 0:可以删除;1:不可以删除
     */
    private Integer canDelete;

    /**
     * 业务活动类型
     */
    private Integer businessType;

}
