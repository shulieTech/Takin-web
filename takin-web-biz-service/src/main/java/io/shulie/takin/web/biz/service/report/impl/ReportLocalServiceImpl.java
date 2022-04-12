package io.shulie.takin.web.biz.service.report.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.dto.report.ApplicationDTO;
import com.pamirs.takin.entity.domain.dto.report.BottleneckInterfaceDTO;
import com.pamirs.takin.entity.domain.dto.report.MachineDetailDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportCountDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportTraceQueryDTO;
import com.pamirs.takin.entity.domain.dto.report.RiskApplicationCountDTO;
import com.pamirs.takin.entity.domain.dto.report.RiskMacheineDTO;
import com.pamirs.takin.entity.domain.entity.report.TpsTargetArray;
import io.shulie.takin.web.amdb.enums.LinkRequestResultTypeEnum;
import io.shulie.takin.web.biz.service.report.ReportLocalService;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.common.constant.ReportConfigConstant;
import io.shulie.takin.web.data.dao.report.ReportApplicationSummaryDAO;
import io.shulie.takin.web.data.dao.report.ReportBottleneckInterfaceDAO;
import io.shulie.takin.web.data.dao.report.ReportMachineDAO;
import io.shulie.takin.web.data.dao.report.ReportSummaryDAO;
import io.shulie.takin.web.data.param.report.ReportApplicationSummaryQueryParam;
import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;
import io.shulie.takin.web.data.result.report.ReportApplicationSummaryResult;
import io.shulie.takin.web.data.result.report.ReportBottleneckInterfaceResult;
import io.shulie.takin.web.data.result.report.ReportMachineResult;
import io.shulie.takin.web.data.result.report.ReportSummaryResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/7/27 下午8:39
 */
@Service
@Slf4j
public class ReportLocalServiceImpl implements ReportLocalService {

    public static final BigDecimal ZERO = new BigDecimal("0");
    @Autowired
    private ReportSummaryDAO reportSummaryDAO;
    @Resource
    private ReportBottleneckInterfaceDAO reportBottleneckInterfaceDAO;
    @Resource
    private ReportApplicationSummaryDAO reportApplicationSummaryDAO;
    @Resource
    private ReportMachineDAO reportMachineDAO;

    @Autowired
    private ReportRealTimeService reportRealTimeService;

    public static void main(String[] args) {
        String data1
                = "{\"cpu\":[10,11,12],\"io\":[40,30,35],\"loading\":[75,55,70],\"memory\":[40,43,45],\"network\":[20,40,"
                + "24],\"tps\":[100,110,120]}";
        String data2
                = "{\"cpu\":[30,31,32],\"io\":[42,32,37],\"loading\":[77,57,72],\"memory\":[42,45,47],\"network\":[22,42,"
                + "26],\"tps\":[110,120,130]}";
        MachineDetailDTO dto = new MachineDetailDTO();
        ReportLocalServiceImpl reportLocal = new ReportLocalServiceImpl();
        reportLocal.parseTpsConfig(dto, Arrays.asList(data1, data2));
        System.out.println(dto);
    }

    @Override
    public ReportCountDTO getReportCount(Long reportId) {
        ReportSummaryResult data = reportSummaryDAO.selectOneByReportId(reportId);
        if (data == null) {
            return new ReportCountDTO();
        }
        return convert2ReportCountDTO(data);
    }

    @Override
    public PageInfo<BottleneckInterfaceDTO> listBottleneckInterface(ReportLocalQueryParam queryParam) {
        Page page = PageHelper.startPage(queryParam.getCurrentPage() + 1, queryParam.getPageSize());
        List<ReportBottleneckInterfaceResult> dataList = reportBottleneckInterfaceDAO.selectByExample(queryParam);
        if (CollectionUtils.isEmpty(dataList)) {
            return new PageInfo<>(Lists.newArrayList());
        }
        List<BottleneckInterfaceDTO> resultList = convert2BottleneckInterfaceDTO(dataList);
        PageInfo pageInfo = new PageInfo<>(resultList);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    @Override
    public RiskApplicationCountDTO listRiskApplication(Long reportId) {
        ReportApplicationSummaryQueryParam param = new ReportApplicationSummaryQueryParam();
        param.setReportId(reportId);
        List<ReportApplicationSummaryResult> dataList = reportApplicationSummaryDAO.selectByParam(param);
        if (CollectionUtils.isEmpty(dataList)) {
            return new RiskApplicationCountDTO();
        }
        List<ReportApplicationSummaryResult> resultList = dataList.stream().filter(
                data -> data.getMachineRiskCount() != null && data.getMachineRiskCount() > 0).sorted((o1, o2) -> {
            if (o1.getMachineRiskCount() > o2.getMachineRiskCount()) {
                return -1;
            } else if (o1.getMachineRiskCount() < o2.getMachineRiskCount()) {
                return 1;
            }
            return 0;
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(resultList)) {
            return new RiskApplicationCountDTO();
        }
        //风险机器数从汇总表里取
        int riskMachineCount = 0;
        ReportSummaryResult data = reportSummaryDAO.selectOneByReportId(reportId);
        if (data != null) {
            riskMachineCount = data.getRiskMachineCount();
        }
        return convert2RiskApplicationCountDTO(resultList, riskMachineCount);
    }

    @Override
    public PageInfo<RiskMacheineDTO> listRiskMachine(ReportLocalQueryParam queryParam) {
        Page page = PageHelper.startPage(queryParam.getCurrentPage() + 1, queryParam.getPageSize());
        queryParam.setRiskFlag(1);
        List<ReportMachineResult> dataList = reportMachineDAO.selectSimpleByExample(queryParam);
        if (CollectionUtils.isEmpty(dataList)) {
            return new PageInfo<>(Lists.newArrayList());
        }
        List<RiskMacheineDTO> resultList = convert2RiskMacheineDTO(dataList);
        PageInfo pageInfo = new PageInfo<>(resultList);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    @Override
    public MachineDetailDTO getMachineDetail(Long reportId, String applicationName, String machineIp) {
        ReportLocalQueryParam queryParam = new ReportLocalQueryParam();
        queryParam.setReportId(reportId);
        queryParam.setApplicationName(applicationName);
        queryParam.setMachineIp(machineIp);
        if ("全部".equals(machineIp) || "0".equals(machineIp) || "all".equalsIgnoreCase(machineIp)) {
            //只展示图表
            queryParam.setMachineIp(null);
            List<ReportMachineResult> dataList = reportMachineDAO.selectListByParam(queryParam);
            if (CollectionUtils.isEmpty(dataList)) {
                return new MachineDetailDTO();
            }
            MachineDetailDTO dto = new MachineDetailDTO();
            parseTpsConfig(dto, dataList.stream().filter(data -> data.getMachineTpsTargetConfig() != null)
                    .map(ReportMachineResult::getMachineTpsTargetConfig).collect(Collectors.toList()));
            return dto;
        } else {
            ReportMachineResult data = reportMachineDAO.selectOneByParam(queryParam);
            if (data == null) {
                return new MachineDetailDTO();
            }
            return convert2MachineDetailDTO(data);
        }
    }

    @Override
    public List<ApplicationDTO> listApplication(Long reportId, String applicationName) {
        ReportApplicationSummaryQueryParam param = new ReportApplicationSummaryQueryParam();
        param.setReportId(reportId);
        param.setApplicationName(applicationName);
        List<ReportApplicationSummaryResult> dataList = reportApplicationSummaryDAO.selectByParam(param);
        if (CollectionUtils.isEmpty(dataList)) {
            return Lists.newArrayList();
        }
        return convert2ApplicationDTO(dataList);
    }

    @Override
    public PageInfo<MachineDetailDTO> listMachineDetail(ReportLocalQueryParam queryParam) {
        Page page = PageHelper.startPage(queryParam.getCurrentPage() + 1, queryParam.getPageSize());
        List<ReportMachineResult> dataList = reportMachineDAO.selectSimpleByExample(queryParam);
        if (CollectionUtils.isEmpty(dataList)) {
            return new PageInfo<>(Lists.newArrayList());
        }
        List<MachineDetailDTO> resultList = convert2MachineDetailDTO(dataList);
        PageInfo pageInfo = new PageInfo<>(resultList);
        pageInfo.setTotal(page.getTotal());
        return pageInfo;
    }

    //@Override
    //public Long getTraceFailedCount(Long reportId) {
    //    WebResponse reportByReportId = null;
    //    try {
    //        reportByReportId = reportService.getReportByReportId(reportId);
    //    } catch (Exception e) {
    //        return 0L;
    //    }
    //    if (!reportByReportId.getSuccess()) {
    //        return 0L;
    //    }
    //    LinkedHashMap map = (LinkedHashMap)reportByReportId.getData();
    //    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//注意月份是MM
    //    Long endTime = (Long)map.get("endTime");
    //    Long startTime;
    //    try {
    //        startTime = simpleDateFormat.parse((String)map.get("startTime")).getTime();
    //    } catch (ParseException e) {
    //        // 如果 startTime 有问题，那么最多取10分钟
    //        startTime = endTime - 10 * 60 * 1000;
    //    }
    //    return TReportMachineMapper.getTraceFailedCount(startTime, endTime);
    //}

    @Override
    public Long getTraceFailedCount(Long reportId) {
        ReportTraceQueryDTO queryDTO = new ReportTraceQueryDTO();
        queryDTO.setReportId(reportId);
        queryDTO.setPageSize(1);
        queryDTO.setResultType(LinkRequestResultTypeEnum.FAILED.getCode());
        PageInfo<ReportTraceDTO> failed = reportRealTimeService.getReportLinkListByReportId(queryDTO);
        queryDTO.setResultType(LinkRequestResultTypeEnum.FAILED_ASSERT.getCode());
        PageInfo<ReportTraceDTO> failedAssert = reportRealTimeService.getReportLinkListByReportId(queryDTO);

        long failedTotal = 0;
        long failedAssertTotal = 0;
        if (failed != null) {
            failedTotal = failed.getTotal();
        }

        if (failedAssert != null) {
            failedAssertTotal = failedAssert.getTotal();
        }

        return failedTotal + failedAssertTotal;
    }

    private ReportCountDTO convert2ReportCountDTO(ReportSummaryResult result) {
        ReportCountDTO dto = new ReportCountDTO();
        dto.setBottleneckInterfaceCount(result.getBottleneckInterfaceCount());
        dto.setRiskMachineCount(result.getRiskMachineCount());
        dto.setBusinessActivityCount(result.getBusinessActivityCount());
        dto.setNotpassBusinessActivityCount(result.getUnachieveBusinessActivityCount());
        dto.setApplicationCount(result.getApplicationCount());
        dto.setMachineCount(result.getMachineCount());
        dto.setWarnCount(result.getWarnCount());
        return dto;
    }

    private List<BottleneckInterfaceDTO> convert2BottleneckInterfaceDTO(List<ReportBottleneckInterfaceResult> results) {
        List<BottleneckInterfaceDTO> dataList = Lists.newArrayList();
        results.forEach(data -> {
            BottleneckInterfaceDTO dto = new BottleneckInterfaceDTO();
            dto.setRank(data.getSortNo());
            dto.setApplicationName(data.getApplicationName());
            dto.setInterfaceName(data.getInterfaceName());
            dto.setTps(data.getTps());
            dto.setRt(data.getRt());
            dto.setSuccessRate(new BigDecimal("100"));
            dataList.add(dto);
        });
        return dataList;
    }

    private RiskApplicationCountDTO convert2RiskApplicationCountDTO(List<ReportApplicationSummaryResult> paramList,
                                                                    int riskMachineCount) {
        RiskApplicationCountDTO dto = new RiskApplicationCountDTO();
        List<ApplicationDTO> apps = Lists.newArrayList();
        for (ReportApplicationSummaryResult param : paramList) {
            ApplicationDTO app = new ApplicationDTO();
            app.setApplicationName(param.getApplicationName());
            app.setRiskCount(param.getMachineRiskCount());
            app.setTotalCount(param.getMachineTotalCount());
            apps.add(app);
        }
        dto.setApplicationCount(paramList.size());
        dto.setMachineCount(riskMachineCount);
        dto.setApplicationList(apps);
        return dto;
    }

    private List<RiskMacheineDTO> convert2RiskMacheineDTO(List<ReportMachineResult> paramList) {
        List<RiskMacheineDTO> dataList = Lists.newArrayList();
        paramList.forEach(data -> {
            RiskMacheineDTO dto = new RiskMacheineDTO();
            dto.setId(data.getId());
            dto.setMachineIp(data.getMachineIp());
            dto.setRiskContent(data.getRiskContent());
            dto.setAgentId(data.getAgentId());
            dataList.add(dto);
        });
        return dataList;
    }

    private MachineDetailDTO convert2MachineDetailDTO(ReportMachineResult param) {
        MachineDetailDTO dto = new MachineDetailDTO();
        dto.setId(param.getId());
        dto.setMachineIp(param.getMachineIp());
        dto.setApplicationName(param.getApplicationName());
        dto.setAgentId(param.getAgentId());
        dto.setRiskFlag(checkRisk(param.getRiskFlag()));
        parseBaseConfig(dto, param.getMachineBaseConfig());
        parseTpsConfig(dto, Arrays.asList(param.getMachineTpsTargetConfig()));
        return dto;
    }

    private List<ApplicationDTO> convert2ApplicationDTO(List<ReportApplicationSummaryResult> paramList) {
        List<ApplicationDTO> dataList = Lists.newArrayList();
        paramList.forEach(data -> {
            ApplicationDTO dto = new ApplicationDTO();
            dto.setApplicationName(data.getApplicationName());
            dto.setRiskCount(data.getMachineRiskCount());
            dto.setTotalCount(data.getMachineTotalCount());
            dataList.add(dto);
        });
        return dataList;
    }

    private List<MachineDetailDTO> convert2MachineDetailDTO(List<ReportMachineResult> paramList) {
        List<MachineDetailDTO> dataList = Lists.newArrayList();
        paramList.forEach(data -> {
            MachineDetailDTO dto = new MachineDetailDTO();
            dto.setId(data.getId());
            dto.setMachineIp(data.getMachineIp());
            dto.setRiskFlag(checkRisk(data.getRiskFlag()));
            dto.setAgentId(data.getAgentId());
            dto.setApplicationName(data.getApplicationName());
            dto.setProcessName(dto.getMachineIp() + "|" + dto.getAgentId());
            dataList.add(dto);
        });
        return dataList;
    }

    private Boolean checkRisk(Integer riskFlag) {
        return (riskFlag != null && riskFlag == 1) ? true : false;
    }

    private void parseBaseConfig(MachineDetailDTO dto, String baseConfig) {
        try {
            if (StringUtils.isBlank(baseConfig)) {
                return;
            }
            JSONObject jsonObject = JSON.parseObject(baseConfig);
            dto.setCpuNum(jsonObject.getInteger(ReportConfigConstant.BASE_CPU_KEY));
            dto.setMemorySize(jsonObject.getBigDecimal(ReportConfigConstant.BASE_MEMORY_KEY));
            dto.setDiskSize(jsonObject.getBigDecimal(ReportConfigConstant.BASE_DISK_KEY));
            dto.setMbps(convertByte2Mb(jsonObject.getBigDecimal(ReportConfigConstant.BASE_MBPS_KEY)));
        } catch (Exception e) {
            log.error("Parse BaseConfig Error: config={}, error={}", baseConfig, e.getMessage());
        }
    }

    private void parseTpsConfig(MachineDetailDTO dto, List<String> configs) {
        try {
            if (CollectionUtils.isEmpty(configs)) {
                return;
            }
            //获取横坐标长度
            String[] time = getTpsConfigLength(configs);
            if (time == null || time.length == 0) {
                return;
            }
            int count = 0;
            Integer[] tps = new Integer[time.length];
            BigDecimal[] cpu = new BigDecimal[time.length];
            BigDecimal[] loading = new BigDecimal[time.length];
            BigDecimal[] memory = new BigDecimal[time.length];
            BigDecimal[] io = new BigDecimal[time.length];
            BigDecimal[] network = new BigDecimal[time.length];
            for (String config : configs) {
                if (StringUtils.isBlank(config)) {
                    continue;
                }
                TpsTargetArray array = JSON.parseObject(config, TpsTargetArray.class);
                if (array == null) {
                    continue;
                }
                //计算累计值
                for (int i = 0; i < time.length; i++) {
                    tps[i] = (tps[i] != null ? tps[i] : 0) + array.getTps()[i];
                    cpu[i] = (cpu[i] != null ? cpu[i] : ZERO).add(array.getCpu()[i]);
                    loading[i] = ((loading[i] != null) ? loading[i] : ZERO).add(array.getLoading()[i]);
                    memory[i] = (memory[i] != null ? memory[i] : ZERO).add(array.getMemory()[i]);
                    io[i] = (io[i] != null ? io[i] : ZERO).add(array.getIo()[i]);
                    network[i] = (network[i] != null ? network[i] : ZERO).add(array.getNetwork()[i]);
                }
                count++;
            }
            //tps计算累加值， 其他计算平均值
            for (int i = 0; i < time.length; i++) {
                cpu[i] = avg(cpu[i], count);
                loading[i] = avg(loading[i], count);
                memory[i] = avg(memory[i], count);
                io[i] = avg(io[i], count);
                network[i] = avg(network[i], count);
            }
            MachineDetailDTO.MachineTPSTargetDTO tpsTargetDTO = new MachineDetailDTO().new MachineTPSTargetDTO();
            tpsTargetDTO.setTime(time);
            tpsTargetDTO.setTps(tps);
            tpsTargetDTO.setCpu(cpu);
            tpsTargetDTO.setLoad(loading);
            tpsTargetDTO.setMemory(memory);
            tpsTargetDTO.setIo(io);
            tpsTargetDTO.setMbps(network);

            dto.setTpsTarget(tpsTargetDTO);
        } catch (Exception e) {
            log.error("Parse PtsConfig Error:, error={}", e.getMessage());
        }
    }

    private String[] getTpsConfigLength(List<String> configs) {
        String[] min = null;
        for (String config : configs) {
            if (StringUtils.isBlank(config)) {
                continue;
            }
            TpsTargetArray array = JSON.parseObject(config, TpsTargetArray.class);
            if (array == null) {
                continue;
            }

            if(min == null || min.length > array.getTime().length) {
                min = array.getTime();
            }
        }
        return min;
    }

    private BigDecimal avg(BigDecimal num1, Integer count) {
        return num1.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);
    }

    /**
     * 网络带宽单位转换 Byte转Mb
     *
     * @return
     */
    private BigDecimal convertByte2Mb(BigDecimal num) {
        if (num == null) {
            return ZERO;
        }
        return num.divide(new BigDecimal(1024 * 1024), 0, RoundingMode.HALF_UP);
    }
}
