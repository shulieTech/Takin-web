package io.shulie.takin.web.biz.service.statistics;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.constant.ScriptEnum;
import com.pamirs.takin.common.util.DateUtils;
import io.shulie.takin.cloud.sdk.model.request.statistics.PressureTotalReq;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressureListTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.sdk.model.response.statistics.ReportTotalResp;
import io.shulie.takin.web.biz.convert.statistics.StatisticsConvert;
import io.shulie.takin.web.biz.pojo.input.statistics.PressureTotalInput;
import io.shulie.takin.web.biz.pojo.output.statistics.PressureListTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.PressurePieTotalOutput.PressurePieTotal;
import io.shulie.takin.web.biz.pojo.output.statistics.ReportTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.ScriptLabelListTotalOutput;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.data.dao.statistics.StatisticsManageDao;
import io.shulie.takin.web.data.result.statistics.PressureListTotalResult;
import io.shulie.takin.web.data.result.statistics.PressurePieTotalResult;
import io.shulie.takin.web.data.result.statistics.ScriptLabelListTotalResult;
import io.shulie.takin.web.diff.api.statistics.PressureStatisticsApi;
import io.shulie.takin.web.ext.entity.UserExt;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @date 2020/11/30 9:36 下午
 */
@Service
public class PressureStatisticsServiceImpl implements PressureStatisticsService {
    @Resource
    private PressureStatisticsApi pressureStatisticsApi;

    @Resource
    private StatisticsManageDao statisticsManageDao;

    @Override
    public PressurePieTotalOutput getPressurePieTotal(PressureTotalInput input) {
        PressurePieTotalOutput output = new PressurePieTotalOutput();

        switch (input.getType()) {
            case "0":
                // 场景状态统计 需要cloud统计
                PressureTotalReq req = new PressureTotalReq();
                BeanUtils.copyProperties(input, req);
                PressurePieTotalResp resp = pressureStatisticsApi.getPressurePieTotal(req);
                output = StatisticsConvert.ofCloud(resp);
                break;
            case "1":
                //  脚本统计
                List<PressurePieTotalResult> list = statisticsManageDao.getPressureScenePieTotal(input.getStartTime(),
                    input.getEndTime());
                Map<String, Integer> totalMap = Maps.newHashMap();
                for (ScriptEnum scriptEnum : ScriptEnum.values()) {
                    totalMap.put(scriptEnum.getName(), 0);
                }
                list.forEach(data -> {
                    totalMap.put(ScriptEnum.getName(data.getType()), data.getCount());
                });
                List<PressurePieTotal> totals = totalMap.entrySet()
                    .stream().map(e -> new PressurePieTotal(e.getKey(), e.getValue())).collect(
                        Collectors.toList());
                Integer count = list.stream().mapToInt(PressurePieTotalResult::getCount).sum();
                output.setData(totals);
                output.setTotal(count);
                break;
            default: {}
        }
        return output;
    }

    @Override
    public ReportTotalOutput getReportTotal(PressureTotalInput input) {
        PressureTotalReq req = new PressureTotalReq();
        BeanUtils.copyProperties(input, req);
        ReportTotalResp resp = pressureStatisticsApi.getReportTotal(req);
        return StatisticsConvert.ofCloud(resp);
    }

    @Override
    public List<PressureListTotalOutput> getPressureListTotal(PressureTotalInput input) {
        List<PressureListTotalOutput> outputs = Lists.newArrayList();
        PressureTotalReq req = new PressureTotalReq();
        BeanUtils.copyProperties(input, req);
        switch (input.getType()) {
            case "0":
                //  压测场景次数统计   获取标签信息
                List<PressureListTotalResp> resp = pressureStatisticsApi.getPressureListTotal(req);
                outputs = StatisticsConvert.ofListCloud(resp);
                List<Long> ids = outputs.stream().map(PressureListTotalOutput::getId).collect(Collectors.toList());
                // 获取标签
                List<PressureListTotalResult> tags = Lists.newArrayList();
                if (ids != null && ids.size() > 0) {
                    tags = statisticsManageDao.getSceneTag(ids);
                }
                Map<Long, PressureListTotalResult> map = tags.stream().collect(
                    Collectors.toMap(PressureListTotalResult::getId, totalResult -> totalResult));
                List<Long> useIds = outputs.stream().map(PressureListTotalOutput::getCreateName)
                    .filter(StringUtils::isNotBlank).map(Long::valueOf).collect(Collectors.toList());
                Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(useIds);
                outputs.forEach(output -> {
                    PressureListTotalResult result = map.get(output.getId());
                    if (result != null) {
                        output.setLabel(result.getTags());
                    }
                    // 创建人根据id查
                    if (StringUtils.isNotBlank(output.getCreateName())) {
                        UserExt userExt = userMap.get(NumberUtils.toLong(output.getCreateName(),0));
                        if (userExt != null) {
                            output.setCreateName(userExt.getName());
                        }
                    }
                });

                break;
            case "1":
                //  压测脚本次数统计
                //  获取 脚本id
                req.setScriptIds(statisticsManageDao.selectScriptManageDeployIds());
                List<PressureListTotalResp> totalResps = pressureStatisticsApi.getPressureListTotal(req);
                outputs = StatisticsConvert.ofListCloud(totalResps);
                // 获取ids
                List<Long> scriptIds = totalResps.stream().map(PressureListTotalResp::getId).collect(
                    Collectors.toList());
                List<PressureListTotalResult> results = Lists.newArrayList();
                if (scriptIds != null && scriptIds.size() > 0) {
                    results = statisticsManageDao.getScriptTag(scriptIds);
                } else {
                    // 脚本必须要有
                    return outputs;
                }
                Map<Long, PressureListTotalResult> totalResultMap = results.stream().collect(
                    Collectors.toMap(PressureListTotalResult::getId, totalResult -> totalResult));
                // 先时间，再count
                outputs.sort(Comparator.comparing(sort -> totalResultMap.get(sort.getId()).getGmtCreate(), Comparator.reverseOrder()));
                outputs.sort(Comparator.comparing(PressureListTotalOutput::getCount, Comparator.reverseOrder()));
                outputs.forEach(output -> {
                    PressureListTotalResult result = totalResultMap.get(output.getId());
                    if (result != null) {
                        output.setName(result.getName() + " 版本" + result.getScriptVersion());
                        output.setLabel(result.getTags());
                        output.setGmtCreate(DateUtils.dateToString(result.getGmtCreate(), DateUtils.FORMATE_YMDHMS));
                        output.setCreateName(result.getCreateName());
                    }
                });
                break;
            default: {}
        }
        return outputs;
    }

    @Override
    public List<ScriptLabelListTotalOutput> getScriptLabelListTotal(PressureTotalInput input) {
        List<ScriptLabelListTotalResult> results = statisticsManageDao.getScriptLabelListTotal(input.getStartTime(),
            input.getEndTime());
        return StatisticsConvert.ofListResult(results);
    }
}
