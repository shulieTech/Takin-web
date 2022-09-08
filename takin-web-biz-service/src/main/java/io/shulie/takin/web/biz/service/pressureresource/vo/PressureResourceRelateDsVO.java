package io.shulie.takin.web.biz.service.pressureresource.vo;

import io.shulie.surge.data.common.doc.annotation.Id;
import io.shulie.takin.web.biz.pojo.request.pressureresource.ExtInfo;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@ToString(callSuper = true)
public class PressureResourceRelateDsVO extends TenantBaseEntity {
    @Id
    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("资源配置Id")
    private String resourceId;

    @ApiModelProperty("应用名称")
    private String appName;

    @ApiModelProperty("中间件类型")
    private String middlewareType;

    @ApiModelProperty("(0-未检测 1-检测失败 2-检测成功)")
    private String status;

    @ApiModelProperty("业务数据源")
    private String businessDatabase;

    @ApiModelProperty("库名")
    private String database;

    @ApiModelProperty("业务数据源用户名")
    private String businessUserName;

    @ApiModelProperty("影子数据源")
    private String shadowDatabase;

    @ApiModelProperty("影子数据源用户名")
    private String shadowUserName;

    @ApiModelProperty("影子数据源密码")
    private String shadowPassword;

    @ApiModelProperty("扩展信息")
    private ExtInfo extInfo;

    @ApiModelProperty("来源类型(0-手工,1-自动)")
    private Integer type;

    @ApiModelProperty("remark")
    private String remark;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtModified;

    private List<String> relationApps;

    @ApiModelProperty("关联应用信息")
    private List<PressureResourceRelateAppVO> appList;

    @ApiModelProperty("关联数据信息")
    private List<PressureResourceDsVO> dsList;

    private Integer size;
}



