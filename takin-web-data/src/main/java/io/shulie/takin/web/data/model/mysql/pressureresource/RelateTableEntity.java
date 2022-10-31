package io.shulie.takin.web.data.model.mysql.pressureresource;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/10/31 6:42 PM
 */
@Data
public class RelateTableEntity extends PressureResourceRelateTableEntityV2 {
    @ApiModelProperty("业务表")
    private String businessTable;

    @ApiModelProperty("影子表")
    private String shadowTable;

    @ApiModelProperty("是否加入(0-加入 1-未加入)")
    private Integer joinFlag;
}
