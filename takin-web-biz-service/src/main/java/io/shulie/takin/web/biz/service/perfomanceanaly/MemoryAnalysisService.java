package io.shulie.takin.web.biz.service.perfomanceanaly;

import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.MemoryAnalysisRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.DownloadDumpResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.MemoryAnalysisResponse;

/**
 * @author mubai
 * @date 2020-11-09 11:26
 */
public interface MemoryAnalysisService {

    MemoryAnalysisResponse queryMemoryDump(MemoryAnalysisRequest request);

    /**
     * 上传dump文件目录
     *
     * @param agentId agent主键
     * @return -
     */
    DownloadDumpResponse downloadDump(String agentId) throws Throwable;
}
