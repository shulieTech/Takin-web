package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 线程列表
 *
 * @author qianshui
 * @date 2020/11/4 上午11:30
 */
@Data
@ApiModel("线程列表入参")
public class ThreadListRequest implements Serializable {
    private static final long serialVersionUID = 8497435887724133144L;

    @ApiModelProperty(value = "基础信息id")
    private String baseId;
}
