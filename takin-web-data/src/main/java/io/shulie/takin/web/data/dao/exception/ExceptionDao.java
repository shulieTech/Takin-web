package io.shulie.takin.web.data.dao.exception;

import java.util.List;

import io.shulie.takin.web.data.param.exception.ExceptionParam;
import io.shulie.takin.web.data.result.exception.ExceptionResult;

/**
 * @author 无涯
 * @date 2021/1/4 7:38 下午
 */
public interface ExceptionDao {
    /**
     * 插入
     * @param param
     */
    void insert(ExceptionParam param);

    /**
     * 获取
     * @return
     */
    List<ExceptionResult> getList();

    /**
     * 通过code 异常数据
     * @param agentCode
     * @return
     */
    ExceptionResult getByAgentCode(String agentCode);

}
