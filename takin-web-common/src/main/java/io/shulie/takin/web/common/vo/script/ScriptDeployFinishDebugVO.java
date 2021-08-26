package io.shulie.takin.web.common.vo.script;

import io.shulie.takin.web.common.constant.AppConstants;
import lombok.Data;

/**
 * 脚本发布id, 对应的调试是否都结束类
 *
 * @author liuchuan
 * @date 2021/5/13 10:31 上午
 */
@Data
public class ScriptDeployFinishDebugVO {

    /**
     * 脚本发布id
     */
    private Long scriptDeployId;

    /**
     * 未完成的数量
     */
    private Integer unfinishedCount;

    /**
     * 是否完成调试
     * 条件: 未完成的数量等于0
     * 相当于 是否可以 debug, 1 为可
     * 0, 不可
     *
     * @return 是否
     */
    public Integer isFinished() {
        return unfinishedCount == 0 ? AppConstants.YES : AppConstants.NO;
    }

}
