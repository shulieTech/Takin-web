package io.shulie.takin.web.biz.pojo.bo.agentupgradeonline;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description agent命令业务数据
 * @Author ocean_wll
 * @Date 2021/11/17 10:29 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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
