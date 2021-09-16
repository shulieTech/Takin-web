package io.shulie.takin.web.biz.service.impl;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Objects;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.Comparator;
import java.text.MessageFormat;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.google.gson.Gson;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.google.common.base.Joiner;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.pamirs.takin.common.util.NumberUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import com.pamirs.takin.entity.domain.vo.TScenario;
import com.pamirs.takin.entity.domain.query.Result;
import com.pamirs.takin.entity.domain.entity.TAlarm;
import io.shulie.takin.web.biz.common.CommonService;
import com.pamirs.takin.entity.domain.entity.TReport;
import com.pamirs.takin.entity.domain.vo.TLinkBasicVO;
import io.shulie.takin.web.biz.service.TReportService;
import com.pamirs.takin.entity.domain.query.ResultList;
import com.pamirs.takin.entity.domain.vo.TReportDetail;
import com.pamirs.takin.entity.domain.vo.TReportResult;
import org.apache.commons.collections4.CollectionUtils;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.entity.domain.query.TAlarmQuery;
import com.pamirs.takin.entity.domain.query.TReportQuery;
import com.pamirs.takin.entity.domain.vo.TLinkServiceMntVo;
import com.pamirs.takin.entity.domain.vo.TReportAppIpDetail;

/**
 * 说明: 压测报告相关服务实接口实现
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月17日
 */
@Service
public class TReportServiceImpl extends CommonService implements TReportService {

    /**
     * 添加压测报告
     *
     * @param tReport 压测报告
     * @return 添加结果
     */
    @Override
    public Result<Void> add(TReport tReport) {
        Result<Void> result = new Result<>();
        try {
            int count = tReportDao.insert(tReport);
            if (count != 1) {
                result.setSuccess(Boolean.FALSE);
                result.setErrorMessage(TakinErrorEnum.MONITOR_DB_ADD_EXCEPTION.getErrorMessage());
            }
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("error:{0},model:{1}",
                TakinErrorEnum.MONITOR_DB_ADD_EXCEPTION.getErrorMessage(),
                JSON.toJSONString(tReport)), e);
            result.setSuccess(Boolean.FALSE);
            result.setErrorMessage(TakinErrorEnum.MONITOR_DB_ADD_EXCEPTION.getErrorMessage());
        }
        return result;
    }

    /**
     * 按id删除压测报告
     *
     * @param id 报告id
     * @return 删除结果
     */
    @Override
    public Result<Void> deleteById(Long id) {
        Result<Void> result = new Result<>();
        if (null == id) {
            result.setSuccess(Boolean.FALSE);
            return result;
        }
        try {
            int count = tReportDao.delete(id);
            if (count != 1) {
                result.setSuccess(Boolean.FALSE);
                result.setErrorMessage(TakinErrorEnum.MONITOR_DB_DELETE_EXCEPTION.getErrorMessage());
            }
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("error:{0},id:{1}",
                TakinErrorEnum.MONITOR_DB_DELETE_EXCEPTION.getErrorMessage(), id), e);
            result.setSuccess(Boolean.FALSE);
            result.setErrorMessage(TakinErrorEnum.MONITOR_DB_DELETE_EXCEPTION.getErrorMessage());
        }
        return result;
    }

    /**
     * 查询压测报告列表
     *
     * @param query 报告查询实体
     * @return 查询的报告列表
     */
    @Override
    public ResultList<TReport> queryListByQuery(TReportQuery query) {
        ResultList<TReport> resultList = new ResultList<>();
        try {
            query.setOrderBy("start_time desc");
            List<TReport> tReportList = tReportDao.selectList(query);
            long count = tReportDao.selectListCount(query);
            resultList = new ResultList<>(query.getStart(), count, query.getPageSize(), tReportList);

            return resultList;
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("error:{0},query:{1}",
                TakinErrorEnum.MONITOR_DB_QUERYLIST_EXCEPTION.getErrorMessage(), JSON.toJSONString(query)), e);
            resultList.setSuccess(Boolean.FALSE);
            resultList.setErrorMessage(TakinErrorEnum.MONITOR_DB_QUERYLIST_EXCEPTION.getErrorMessage());

        }
        return resultList;
    }

    /**
     * 按id查询压测报告
     *
     * @param id 报告id
     * @return 压测报告
     */
    @Override
    public Result<TReport> queryOneById(Long id) {
        Result<TReport> result = new Result<>();
        if (null == id) {
            result.setSuccess(Boolean.FALSE);
            return result;
        }
        try {
            TReport tReport = tReportDao.selectOneById(id);
            result.setData(tReport);
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("error:{0},id:{1}",
                TakinErrorEnum.MONITOR_DB_QUERY_EXCEPTION.getErrorMessage(), id), e);
            result.setSuccess(Boolean.FALSE);
            result.setErrorMessage(TakinErrorEnum.MONITOR_DB_QUERY_EXCEPTION.getErrorMessage());
        }
        return result;
    }

    @Override
    public Result<TReport> queryOneByScenarioId(Long id, Integer status) {
        Result<TReport> result = new Result<>();
        if (null == id || null == status) {
            result.setSuccess(Boolean.FALSE);
            return result;
        }
        try {
            TReport tReport = tReportDao.selectScenarioId(id, status);
            result.setData(tReport);
        } catch (Exception e) {
            LOGGER.error(MessageFormat.format("error:{0},id:{1}",
                TakinErrorEnum.MONITOR_DB_QUERY_EXCEPTION.getErrorMessage(), id), e);
            result.setSuccess(Boolean.FALSE);
            result.setErrorMessage(TakinErrorEnum.MONITOR_DB_QUERY_EXCEPTION.getErrorMessage());
        }
        return result;
    }

    @Override
    public List<TReportAppIpDetail> queryMachineDetail(TReport tReport) {
        return tReportAppIpDetailDao.queryMachineDetail(tReport.getId() + "");
    }

    @Override
    public List<TReport> queryBySecondLinkIdAndStatus(String secondLinkId, String status) {
        return tReportDao.queryBySecondLinkIdAndStatus(secondLinkId, status);
    }

    @Override
    public List<TReport> queryStatus(String status) {
        return tReportDao.queryByStatus(status);
    }

    @Override
    public TReportDetail queryReportDetail(Long reportId) {

        Result<TReport> reportResult = queryOneById(reportId);
        TReportDetail tReportDetail = new TReportDetail();

        if (reportResult.isSuccess() && null != reportResult.getData()) {
            TReport tReport = reportResult.getData();
            //将报告名称去重并逗号分隔,
            List<List<Map<String, Object>>> list = new Gson().fromJson(tReport.getLinkBasicName().trim(),
                new TypeToken<List<List<Map<String, Object>>>>() {
                }.getType());
            String basicLinkName = Joiner.on(",").join(list.stream().flatMap(Collection::stream).map(
                map -> MapUtils.getString(map, "label")).distinct().collect(Collectors.toList()));
            tReport.setLinkBasicName(basicLinkName);

            tReportDetail.setPass(true);
            tReportDetail.settReport(tReport);

            List<TReportResult> tReportResults = new ArrayList<>();
            String linkBasic = tReport.getLinkBasic();
            List<TLinkBasicVO> tLinkBasicVoList = JSON.parseArray(linkBasic, TLinkBasicVO.class);
            if (CollectionUtils.isEmpty(tLinkBasicVoList)) {
                tReportDetail.setPass(false);
            }

            for (TLinkBasicVO tLinkBasicVO : tLinkBasicVoList) {
                TScenario statistics = tLinkBasicVO.getStatistics();
                TReportResult tReportResult = new TReportResult();
                tReportResult.setPass(true);

                TLinkServiceMntVo tLinkServiceMntVo = new TLinkServiceMntVo();
                //复制部分属性值给前端使用, 目标值
                tLinkServiceMntVo.setLinkId(Long.parseLong(tLinkBasicVO.getLinkId()));
                tLinkServiceMntVo.setLinkName(tLinkBasicVO.getLinkName());
                tLinkServiceMntVo.setRtSa(tLinkBasicVO.getRtSa());
                tLinkServiceMntVo.setRt(tLinkBasicVO.getRt());
                tLinkServiceMntVo.setTps(tLinkBasicVO.getTps());
                tLinkServiceMntVo.setTargetSuccessRate(tLinkBasicVO.getTargetSuccessRate());
                tLinkServiceMntVo.setAswanId(tLinkBasicVO.getAswanId());
                tReportResult.settLinkServiceMntVo(tLinkServiceMntVo);
                //通过统计的压测信息和基础链路的目标值比对，如果小于目标就将测试报告状态设置为false
                if (statistics != null) {
                    tReportResult.settScenario(statistics);

                    int actualTps = Objects.isNull(statistics.getTps()) ? 0 : statistics.getTps();
                    Integer targetTps = NumberUtils.createInteger(
                        StringUtils.isEmpty(tLinkBasicVO.getTps()) ? "0" : tLinkBasicVO.getTps());

                    BigDecimal actualRt = Objects.isNull(statistics.getRt()) ? BigDecimal.ZERO : statistics.getRt();
                    BigDecimal targetRt = NumberUtils.createBigDecimal(
                        StringUtils.isEmpty(tLinkBasicVO.getRt()) ? "0" : tLinkBasicVO.getRt());

                    BigDecimal actualSuccessRate = Objects.isNull(statistics.getSuccessRate()) ? BigDecimal.ZERO
                        : statistics.getSuccessRate();
                    BigDecimal targetSuccessRate = NumberUtils.createBigDecimal(
                        StringUtils.isEmpty(tLinkBasicVO.getTargetSuccessRate()) ? "0"
                            : tLinkBasicVO.getTargetSuccessRate());

                    BigDecimal actualRtRate = Objects.isNull(statistics.getRtRate()) ? BigDecimal.ZERO
                        : statistics.getRtRate();
                    BigDecimal targetRtSa = NumberUtils.createBigDecimal(
                        StringUtils.isEmpty(tLinkBasicVO.getRtSa()) ? "0" : tLinkBasicVO.getRtSa());

                    boolean flag = (actualTps < targetTps ||
                        actualRt.compareTo(targetRt) > 0 ||
                        actualSuccessRate.compareTo(targetSuccessRate) < 0) ||
                        actualRtRate.compareTo(targetRtSa) < 0;
                    if (flag) {
                        tReportDetail.setPass(false);
                        tReportResult.setPass(false);
                    }
                } else {
                    tReportDetail.setPass(false);
                    tReportResult.setPass(false);
                }

                tReportResult.setDuration(
                    DateUtil.between(tLinkBasicVO.getStartTime(), tLinkBasicVO.getEndTime(), DateUnit.MINUTE));

                //查询应用ip(服务器)资源详情
                List<TReportAppIpDetail> tReportAppIpDetails = queryApplicationIpByIpList(String.valueOf(reportId),
                    String.valueOf(tLinkBasicVO.getLinkId()));
                tReportResult.settReportAppIpDetails(tReportAppIpDetails);

                //对服务器信息进行分租，应用服务器为web，数据库服务器为db，其他为中间件服务器
                Map<String, List<TReportAppIpDetail>> serverGroup = new HashMap<>(10);
                for (TReportAppIpDetail tReportAppIpDetail : tReportAppIpDetails) {
                    String type = tReportAppIpDetail.getType();
                    if ("web".equals(type)) {
                        List<TReportAppIpDetail> webServerArray = serverGroup.get("web");
                        if (CollectionUtils.isEmpty(webServerArray)) {
                            webServerArray = new ArrayList<>();
                        }
                        webServerArray.add(tReportAppIpDetail);
                        serverGroup.put("web", webServerArray);
                    } else if ("db".equals(type)) {
                        List<TReportAppIpDetail> dbServerArray = serverGroup.get("db");
                        if (CollectionUtils.isEmpty(dbServerArray)) {
                            dbServerArray = new ArrayList<>();
                        }
                        dbServerArray.add(tReportAppIpDetail);
                        serverGroup.put("db", dbServerArray);
                    } else {
                        List<TReportAppIpDetail> middlewareServerArray = serverGroup.get("other");
                        if (CollectionUtils.isEmpty(middlewareServerArray)) {
                            middlewareServerArray = new ArrayList<>();
                        }
                        middlewareServerArray.add(tReportAppIpDetail);
                        serverGroup.put("other", middlewareServerArray);
                    }
                }

                if (serverGroup.size() > 0) {
                    List<TReportAppIpDetail> webServer = serverGroup.get("web");
                    Map<String, Float> webServerInfo = new HashMap<>();
                    webServerInfo.put("maxCpuUsageRate", webServer == null ? 0f : webServer.stream()
                        .map(tReportAppIpDetail -> NumberUtil.getFloat(tReportAppIpDetail.getCpu()))
                        .max(Comparator.comparing(u -> u)).orElse(0f));
                    webServerInfo.put("maxMemoryUsageRate", webServer == null ? 0f : webServer.stream()
                        .map(tReportAppIpDetail -> NumberUtil.getFloat(tReportAppIpDetail.getMemory()))
                        .max(Comparator.comparing(u -> u)).orElse(0F));
                    tReportResult.setWebServer(webServerInfo);

                    List<TReportAppIpDetail> dbServerInfo = serverGroup.get("db");
                    Map<String, Float> dbServer = new HashMap<>();
                    dbServer.put("maxCpuUsageRate", dbServerInfo == null ? 0f : dbServerInfo.stream()
                        .map(tReportAppIpDetail -> NumberUtil.getFloat(tReportAppIpDetail.getCpu()))
                        .max(Comparator.comparing(u -> u)).orElse(0f));
                    dbServer.put("maxMemoryUsageRate", dbServerInfo == null ? 0f : dbServerInfo.stream()
                        .map(tReportAppIpDetail -> NumberUtil.getFloat(tReportAppIpDetail.getMemory()))
                        .max(Comparator.comparing(u -> u)).orElse(0F));
                    tReportResult.setDbServer(dbServer);

                    List<TReportAppIpDetail> otherServerInfo = serverGroup.get("other");

                    Map<String, Float> otherServer = new HashMap<>();
                    otherServer.put("maxCpuUsageRate", dbServerInfo == null ? 0f : otherServerInfo.stream()
                        .map(tReportAppIpDetail -> NumberUtil.getFloat(tReportAppIpDetail.getCpu()))
                        .max(Comparator.comparing(u -> u)).orElse(0f));
                    otherServer.put("maxMemoryUsageRate", dbServerInfo == null ? 0f : otherServerInfo.stream()
                        .map(tReportAppIpDetail -> NumberUtil.getFloat(tReportAppIpDetail.getMemory()))
                        .max(Comparator.comparing(u -> u)).orElse(0F));
                    tReportResult.setMiddlewareServer(otherServer);
                }
                tReportResult.settScenarioList(tLinkBasicVO.getStatisticsPerMinute());
                tReportResults.add(tReportResult);
            }

            tReportDetail.settReportResults(tReportResults);

            //查询报告中压测的基础链路相关资源的告警
            List<TReportAppIpDetail> reportAppIpDetailList = queryMachineDetail(tReport);

            TAlarmQuery query = new TAlarmQuery();
            TAlarm tAlarm = new TAlarm();
            query.setQuery(tAlarm);
            query.setBeginAlarmDate(DateUtil.formatDateTime(tReport.getStartTime()));
            query.setEndAlarmDate(DateUtil.formatDateTime(tReport.getEndTime()));
            query.setWarNames(
                reportAppIpDetailList.stream().map(TReportAppIpDetail::getApplicationName)
                    .collect(Collectors.toList()));
            //            ResultList<TAlarm> alarmResultList = tAlarmService.queryListByQuery(query);
            //            tReportDetail.settAlarms((List<TAlarm>) alarmResultList.getDatalist());
            //            if (CollectionUtils.isNotEmpty(tReportDetail.gettAlarms())) {
            //                tReportDetail.setPass(false);
            //            }
            tReportDetail.setPass(true);

        }
        return tReportDetail;
    }

    @Override
    public List<TReportAppIpDetail> queryApplicationIpByIpList(String reportId, String linkId) {
        return tReportAppIpDetailDao.queryReportAppIpListByReportIdAndLinkId(reportId, linkId);
    }
}
