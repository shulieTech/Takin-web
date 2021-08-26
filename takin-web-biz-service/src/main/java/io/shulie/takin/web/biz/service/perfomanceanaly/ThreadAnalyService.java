package io.shulie.takin.web.biz.service.perfomanceanaly;

import java.util.List;

import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PerformanceAnalyzeRequest;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.ThreadCpuUseRateRequest;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.ThreadListRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ProcessBaseDataResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ThreadCpuChartResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ThreadCpuUseRateChartResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.ThreadListResponse;

/**
 * @author qianshui
 * @date 2020/11/4 下午2:43
 */
public interface ThreadAnalyService {

    ProcessBaseDataResponse getBaseData(PerformanceAnalyzeRequest request);

    List<ThreadCpuChartResponse> getThreadAnalyze(PerformanceAnalyzeRequest request);

    ThreadListResponse getThreadList(ThreadListRequest threadList);

    List<ThreadCpuUseRateChartResponse> getThreadCpuUseRate(ThreadCpuUseRateRequest request);

    /**
     * 获取线程栈数据
     *
     * @return
     */
    String getThreadStackInfo(String link);

    void clearData(Integer time);
}
