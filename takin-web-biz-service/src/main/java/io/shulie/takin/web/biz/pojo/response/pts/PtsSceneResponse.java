package io.shulie.takin.web.biz.pojo.response.pts;

import io.shulie.takin.web.biz.pojo.request.pts.PtsSceneRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel("jmx解析返回|查询返回")
public class PtsSceneResponse extends PtsSceneRequest implements Serializable {

    @ApiModelProperty(value = "解析jmx异常信息")
    private List<String> message = new ArrayList<>();

    @ApiModelProperty(value = "是否存在错误信息，true-是 false-否")
    private Boolean existError = false;

    public Boolean getExistError() {
        return CollectionUtils.isNotEmpty(message);
    }
}
