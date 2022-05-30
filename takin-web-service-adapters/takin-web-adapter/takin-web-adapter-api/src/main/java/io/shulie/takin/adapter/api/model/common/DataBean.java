package io.shulie.takin.adapter.api.model.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 无涯
 * @date 2021/2/3 3:36 下午
 */
@Data
@NoArgsConstructor
public class DataBean {
    @ApiModelProperty(value = "实际")
    private Object result;

    @ApiModelProperty(value = "目标")
    private Object value;

    public DataBean(Object result, Object value) {
        this.result = result;
        this.value = value;
    }
}
