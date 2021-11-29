package io.shulie.takin.web.biz.pojo.bo.agentupgradeonline;

import lombok.Data;

/**
 * @Description agent命令上报对象
 * @Author ocean_wll
 * @Date 2021/11/29 4:26 下午
 */
@Data
public class AgentCommandReqBO {

    /**
     * 指令id
     */
    private Long id;

    /**
     * 指令对应的参数
     */
    private String extrasString;

    /**
     * 任务id，对应下发的uuid
     */
    private String taskId;

    /**
     * 指令下发是否成功
     */
    private Boolean success;

    /**
     * 指令返回的数据、如果有，没有则空
     */
    private String result;

    /**
     * 指令执行结果
     */
    private String executeStatus;

    /**
     * 失败或进行中的表述信息
     */
    private String msg;
}
