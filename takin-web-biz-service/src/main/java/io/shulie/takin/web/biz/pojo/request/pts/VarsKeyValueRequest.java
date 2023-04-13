package io.shulie.takin.web.biz.pojo.request.pts;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author junshi
 * @ClassName VarsKeyValueRequest
 * @Description
 * @createTime 2023年03月15日 15:39
 */
@Data
@ApiModel("变量key-value格式的入参")
@NoArgsConstructor
@AllArgsConstructor
public class VarsKeyValueRequest extends KeyValueRequest {
    @ApiModelProperty(value = "描述")
    private String desc;

    public VarsKeyValueRequest(String key, String value, String desc) {
        super.setKey(key);
        super.setValue(value);
        this.desc = desc;
    }
}
