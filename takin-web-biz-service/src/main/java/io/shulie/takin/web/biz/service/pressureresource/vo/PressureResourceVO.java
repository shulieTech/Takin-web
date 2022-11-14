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
    private String id;

    @ApiModelProperty("资源配置名称")
    private String name;

    @ApiModelProperty("来源类型-手工/业务流程")
    private int type;

    @ApiModelProperty("隔离方式(0-无 1-影子库 2-影子库/影子表 3-影子表)")
    private int isolateType;

    @ApiModelProperty("状态(0-未开始 1-已开始)")
    private int status;

    @ApiModelProperty("链路类型")
    private Integer configType;

    @ApiModelProperty("明细条数")
    private Integer detailCount;

    @ApiModelProperty("检测状态(0-未检测 1-检测中 2-检测完成)")
    private Integer checkStatus;

    @ApiModelProperty("检测时间")
    private Date checkTime;

    @ApiModelProperty("创建时间")
    private Date gmtCreate;

    @ApiModelProperty("更新时间")
    private Date gmtUpdate;
}
