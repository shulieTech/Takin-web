package io.shulie.takin.web.biz.agent;

import io.shulie.takin.channel.bean.CommandPacket;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/1/25 2:28 下午
 */
@Data
public class TakinWebCommandPacket extends CommandPacket {
    private String agentId;
    /**
     * 响应等待时间
     */
    private Long timeoutMillis;
    /**
     * 是否运行执行多次，true:运行，false:不允许
     */
    private Boolean isAllowMultipleExecute;

}
