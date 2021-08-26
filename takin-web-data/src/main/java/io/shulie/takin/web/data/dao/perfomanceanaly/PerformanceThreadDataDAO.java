package io.shulie.takin.web.data.dao.perfomanceanaly;

import java.util.List;

import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceThreadQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PerformanceThreadCountResult;
import io.shulie.takin.web.data.result.perfomanceanaly.PerformanceThreadDataResult;

/**
* @author qianshui
 * @date 2020/11/4 下午2:39
 */
public interface PerformanceThreadDataDAO {

    /**
     * 图表：时间 统计线程数
     * @param baseIds
     * @return
     */
    List<PerformanceThreadCountResult> getPerformanceThreadCountList(List<String> baseIds);

    /**
     * 线程列表
     * @param param
     * @return
     */
    List<PerformanceThreadDataResult> getPerformanceThreadDataList(PerformanceThreadQueryParam param);

    /**
     * 获取线程栈数据
     * @param link
     * @return
     */
    String getThreadStackInfo(String link);

    void clearData(String time);
}
