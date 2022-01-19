package io.shulie.takin.web.biz.pojo.response.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author liuchuan
 * @date 2021/5/11 1:54 下午
 */
@ApiModel("出参类-压测详情出参")
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class SceneDetailResponse extends SceneManageWrapperResp {

    @ApiModelProperty("排除的应用id列表")
    private List<String> excludedApplicationIds;

}
