package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.shulie.surge.data.common.doc.annotation.Id;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.influxdb.annotation.Column;

import java.util.Date;
import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
public class PressureResourceInput extends TenantBaseEntity {
    @Id
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置名称")
    private String name;

    @ApiModelProperty("来源类型-手工/业务流程")
    private Integer type;

    @ApiModelProperty("sourceId")
    private Long sourceId;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtUpdate;

    /**
     * 详情明细
     */
    private List<PressureResourceDetailInput> detailInputs;
}
