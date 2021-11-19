package io.shulie.takin.web.biz.pojo.response.application;

import io.shulie.takin.common.beans.component.SelectVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: 南风
 * @Date: 2021/11/10 4:34 下午
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class ApplicationPluginPathDetailResponse implements Serializable {

    private Long id;

    @ApiModelProperty("类型")
    private String pathType;

    @ApiModelProperty("配置内容")
    private String context;


}
