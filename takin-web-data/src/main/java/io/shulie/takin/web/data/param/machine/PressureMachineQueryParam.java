package io.shulie.takin.web.data.param.machine;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @author mubai
 * @date 2020-11-13 10:07
 */

@Data
public class PressureMachineQueryParam extends PagingDevice {

    /**
     * id
     */
    private Long id;

    /**
     * 机器水位排序； 1：正序 ； -1：倒叙
     */
    private Integer machineUsageOrder;

    /**
     * 压力机名称
     */
    private String name;

    /**
     * 压力机IP
     */
    private String ip;

    /**
     * 标签
     */
    private String flag;

    /**
     * 状态 0：空闲 ；1：压测中  -1:离线
     */
    private Integer status;

}
