package io.shulie.takin.web.biz.service.pressureresource.vo;

import io.shulie.surge.data.common.doc.annotation.Id;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.influxdb.annotation.Column;

import java.util.Date;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@ToString(callSuper = true)
public class PressureResourceVO {
    @Id
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置名称")
    private String name;

    @ApiModelProperty("来源类型-手工/业务流程")
    private int type;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtUpdate;
}
