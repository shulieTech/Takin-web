package io.shulie.takin.web.common.vo.application;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.shulie.takin.web.ext.entity.AuthQueryResponseCommonExt;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 无涯
 * @date 2021/5/29 12:35 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AppRemoteCallVO extends AuthQueryResponseCommonExt {
    /**
     * 接口名称
     */
    @ApiModelProperty(name = "interfaceName", value = "接口名称")
    private String interfaceName;

    /**
     * 接口类型
     */
    @ApiModelProperty(name = "interfaceType", value = "接口类型")
    private Integer interfaceType;

    /**
     * 服务端应用名
     */
    @ApiModelProperty(name = "serverAppName", value = "数据库存储的服务端应用名")
    private String serverAppName;

    /**
     * 应用id
     */
    @ApiModelProperty(name = "applicationId", value = "应用id")
    private Long applicationId;

    /**
     * 应用name
     */
    @ApiModelProperty(name = "appName", value = "应用name")
    private String appName;

    /**
     * 配置类型，0：未配置，1：白名单配置;2：返回值mock;3:转发mock
     */
    @ApiModelProperty(name = "type", value = "配置类型：0：未配置，1：白名单配置;2：返回值mock;3:转发mock")
    private Integer type;

    /**
     * mock返回值
     */
    @ApiModelProperty(name = "mockReturnValue", value = "mock返回值")
    private String mockReturnValue;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(name = "gmtCreate", value = "创建时间")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(name = "gmtModified", value = "更新时间")
    private Date gmtModified;

    /**
     * 同步字段 是否从白名单同步过来的
     */
    private Boolean isSynchronize;

    /**
     * 是否是手动录入的
     */
    private Boolean isManual;

    /**
     * 接口子类型
     */
    private String interfaceChildType;

    private String remark;
    /**
     * 应用名，接口名称，接口类型，租户id,环境code求md5
     */
    private String md5;

    public AppRemoteCallVO() {}

    public AppRemoteCallVO(Date gmtCreate, Date gmtModified) {
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        this.isSynchronize = false;
    }

    public AppRemoteCallVO(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
}
