package io.shulie.takin.web.amdb.api;

import com.pamirs.pradar.log.parser.trace.RpcBased;
import com.pamirs.pradar.log.parser.trace.RpcStack;
import io.shulie.surge.data.deploy.pradar.link.model.TTrackClickhouseModel;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.query.script.QueryLinkDetailDTO;
import io.shulie.takin.web.amdb.bean.query.trace.DataCalibrationDTO;
import io.shulie.takin.web.amdb.bean.query.trace.TraceInfoQueryDTO;
import io.shulie.takin.web.amdb.bean.query.trace.TraceLogQueryDTO;
import io.shulie.takin.web.amdb.bean.result.trace.EntryTraceInfoDTO;

import java.util.List;

/**
 * @author shiyajian
 * create: 2020-10-12
 */
public interface TraceClient {

    /**
     * 获得请求流量明细
     *
     * @param dto 请求参数
     * @return 分页数据
     */
    PagingList<EntryTraceInfoDTO> listEntryTraceByTaskIdV2(QueryLinkDetailDTO dto);

    /**
     * 查询入口的trace请求流量信息
     * 用于压测实况，还有压测报告中的请求流量明细功能
     */
    PagingList<EntryTraceInfoDTO> listEntryTraceInfo(TraceInfoQueryDTO query);


    /**
     * webIde使用
     * 根据业务活动获取对应的最新的trace
     * @param query
     * @return
     */
    EntryTraceInfoDTO getEntryTraceInfo(TraceInfoQueryDTO query);

    /**
     * 根据 traceId 查询Trace的调用栈
     * times[0]=startTime yyyy-MM-dd HH:mm:ss
     * times[1]=endTime yyyy-MM-dd HH:mm:ss
     */
    RpcStack getTraceDetailById(String traceId, String... times);

    /**
     * 根据traceID 查询base
     * @param traceId
     * @return
     */
    List<RpcBased> getTraceBaseById(String traceId);

    /**
     * 查询trace日志
     * @param query
     * @return
     */
    PagingList<TTrackClickhouseModel> listTraceLog(TraceLogQueryDTO query);

    String dataCalibration(DataCalibrationDTO dataCalibration);
}
