package io.shulie.takin.web.biz.pojo.request.perfomanceanaly;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-16 15:54
 */

@Data
public class PressureMachineQueryRequest {

    /**
     * 压力机名称
     */
    @ApiModelProperty(value = "压力机名称")
    @NotNull
    private String name;

    /**
     * 压力机IP
     */
    @ApiModelProperty(value = "压力机IP")
    @NotNull
    private String ip;

    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    private String flag;

    /**
     * 状态 0：空闲 ；1：压测中  -1:离线
     */
    @ApiModelProperty(value = "机器状态")
    @NotNull
    private Integer status;

}
