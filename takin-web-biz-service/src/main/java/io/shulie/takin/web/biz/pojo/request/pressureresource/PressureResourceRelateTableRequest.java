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
public class PressureResourceRelateTableRequest extends PageBaseDTO {
    @Id
    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("资源配置Id")
    private Long resourceId;

    @ApiModelProperty("数据源ID")
    private Long dsId;

    @ApiModelProperty("数据源Key")
    private String dsKey;

    @ApiModelProperty("查询的业务表名")
    private String queryBusinessTableName;

    @ApiModelProperty("业务表名")
    private String businessTableName;

    @ApiModelProperty("配置状态")
    private Integer status;
}

