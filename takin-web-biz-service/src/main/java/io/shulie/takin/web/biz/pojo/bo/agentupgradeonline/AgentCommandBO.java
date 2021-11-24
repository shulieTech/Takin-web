package io.shulie.takin.web.biz.pojo.bo.agentupgradeonline;

import java.util.UUID;

import lombok.Data;

/**
 * @Description agent命令业务数据
 * @Author ocean_wll
 * @Date 2021/11/17 10:29 上午
 */
@Data
public class AgentCommandBO {

    /**
     * 任务的uuid
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * 指令id
     */
    private Long id;

    /**
     * 指令对应的参数
     */
    private String extrasString;

    public AgentCommandBO() {
    }

    public AgentCommandBO(Long id, String extras) {
        this.id = id;
        this.extrasString = extras;
    }
}
