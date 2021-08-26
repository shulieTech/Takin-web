package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 无涯
 * @date 2020/12/2 8:18 下午
 */
@Data
public class ThreadStackRequest implements Serializable {
    private static final long serialVersionUID = -7182637518514949578L;

    @ApiModelProperty(value = "link")
    private String link;
}
