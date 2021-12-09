package io.shulie.takin.web.data.dao.perfomanceanaly;

import java.util.List;

import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PerformanceBaseDataResult;

/**
 * @author qianshui
 * @date 2020/11/4 下午2:39
 */
public interface PerformanceBaseDataDAO {

    /**
     * 插入db base and thread
     *
     * @param param
     */
    void insert(PerformanceBaseDataParam param);

    /**
     * 根据appName 获取进程名称列表
     *
     * @param param
     * @return
     */
    List<String> getProcessNameList(PerformanceBaseQueryParam param);

    /**
     * base基础信息 单条数据
     *
     * @param param
     * @return
     */
    PerformanceBaseDataResult getOnePerformanceBaseData(PerformanceBaseQueryParam param);

    /**
     * base基础信息 列表
     *
     * @param param
     * @return
     */
    List<PerformanceBaseDataResult> getPerformanceBaseDataList(PerformanceBaseQueryParam param);

}
