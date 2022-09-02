package io.shulie.takin.web.biz.pojo.request.pressureresource;

import io.shulie.surge.data.common.doc.annotation.Id;
import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 10:24 AM
 */
@Data
@ToString(callSuper = true)
public class PressureResourceRelationTableRequest extends PageBaseDTO {
    @Id
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    private Long resourceId;

    @ApiModelProperty("数据源ID")
    private Long dsId;
}

