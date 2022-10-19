package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.web.biz.service.pressureresource.vo.CommandTaskVo;
import io.shulie.takin.web.biz.service.pressureresource.vo.agent.command.*;

/**
 * @author guann1n9
 * @date 2022/9/14 10:57 AM
 */
public interface PressureResourceCommandService {

    /**
     * 下发命令并更新数据库
     * @param resourceId
     * @return
     */
    void pushCommand(CommandTaskVo commandTaskVo);

    void processAck(TakinAck pressureResourceAck);
}
