package io.shulie.takin.web.biz.pojo.bo.agentupgradeonline;

import lombok.Data;

/**
 * @Description agent命令业务数据
 * @Author ocean_wll
 * @Date 2021/11/17 10:29 上午
 */
@Data
public class AgentCommandBO {

    /**
     * 指令id
     */
    private Long id;

    /**
     * 指令对应的参数
     */
    private String extras;
}
