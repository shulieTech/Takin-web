package com.pamirs.takin.entity.domain.vo.application;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.Data;

/**
 * @Description 应用节点数对象
 * @Author ocean_wll
 * @Date 2021/10/14 10:53 上午
 */
@Data
public class NodeNumParam {

    /**
     * 应用名
     */
    @NotBlank(message = "appName不能为空")
    private String appName;

    /**
     * 节点数
     */
    @Min(value = 1, message = "节点数应该大于0")
    private Integer nodeNum;
}
