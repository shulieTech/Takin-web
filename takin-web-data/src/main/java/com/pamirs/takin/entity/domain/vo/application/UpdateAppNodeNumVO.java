package com.pamirs.takin.entity.domain.vo.application;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 修改应用节点接受对象
 *
 * @author ocean_wll
 * @date 2021/10/14 10:55 上午
 */
@Data
@ApiModel(value = "UpdateAppNodeNumVO", description = "修改应用节点入参")
public class UpdateAppNodeNumVO {

    /**
     * 应用节点数集合
     */
    @Valid
    @NotEmpty(message = "data不能为空")
    private List<NodeNumParam> data;

    /**
     * JSONObject 扩展字段
     */
    private String extra;
}
