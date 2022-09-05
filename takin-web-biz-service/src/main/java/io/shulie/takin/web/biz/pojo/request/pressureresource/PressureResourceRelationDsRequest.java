package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.shulie.surge.data.common.doc.annotation.Id;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
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
public class PressureResourceRelationDsRequest extends PageBaseDTO {
    @Id
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    private Long resourceId;

    @ApiModelProperty("应用名称,模糊查询")
    private String queryAppName;

    @ApiModelProperty("业务数据源,模糊查询")
    private String queryBusinessDataBase;

    @ApiModelProperty("状态(0-未检测 1-检测失败 2-检测成功)")
    private String status;

    @ApiModelProperty("业务数据源")
    private String businessDataBase;

    @ApiModelProperty("业务数据源用户名")
    private String businessUserName;
}

