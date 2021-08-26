package io.shulie.takin.web.biz.agent;

import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author mubai
 * @date 2021-01-27 15:19
 */

@Data
@Accessors(chain = true)
public class CommandSendDTO {

    /**
     * agentId
     */
    private String agentId;

    /**
     * agentCommand
     */
    private AgentCommandEnum agentCommandEnum;

    /**
     * 入参
     */
    private Map<String, Object> param;

}
