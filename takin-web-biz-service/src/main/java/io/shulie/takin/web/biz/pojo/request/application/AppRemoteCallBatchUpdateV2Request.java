package io.shulie.takin.web.biz.pojo.request.application;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/9/9 8:19 下午
 */
@Data
@NoArgsConstructor
public class AppRemoteCallBatchUpdateV2Request {

    @NotEmpty
    List<AppRemoteCallUpdateV2Request> updateInfos;

    @ApiModelProperty(name = "updateType", value = "配置类型，0：未配置，1：白名单配置;2：返回值mock;3:转发mock")
    @NotNull
    private Integer updateType;
}
