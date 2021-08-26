package io.shulie.takin.web.biz.convert.statistics;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.cloud.open.resp.statistics.ReportTotalResp;
import io.shulie.takin.web.biz.pojo.output.statistics.ReportTotalOutput;
import io.shulie.takin.cloud.open.resp.statistics.PressurePieTotalResp;
import io.shulie.takin.cloud.open.resp.statistics.PressureListTotalResp;
import io.shulie.takin.web.biz.pojo.output.statistics.PressurePieTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.PressureListTotalOutput;
import io.shulie.takin.web.biz.pojo.output.statistics.ScriptLabelListTotalOutput;
import io.shulie.takin.web.data.result.statistics.ScriptLabelListTotalResult;

/**
 * @author 无涯
 * @date 2020/12/1 7:14 下午
 */
public class StatisticsConvert {
    public static PressurePieTotalOutput of(PressurePieTotalOutput output) {
        return JsonHelper.json2Bean(JsonHelper.bean2Json(output), PressurePieTotalOutput.class);
    }

    public static PressurePieTotalOutput ofCloud(PressurePieTotalResp resp) {
        return JsonHelper.json2Bean(JsonHelper.bean2Json(resp), PressurePieTotalOutput.class);
    }

    public static ReportTotalOutput of(ReportTotalOutput output) {
        ReportTotalOutput response = new ReportTotalOutput();
        BeanUtils.copyProperties(output, response);
        return response;
    }

    public static ReportTotalOutput ofCloud(ReportTotalResp resp) {
        ReportTotalOutput output = new ReportTotalOutput();
        BeanUtils.copyProperties(resp, output);
        return output;
    }

    public static List<PressureListTotalOutput> ofList(List<PressureListTotalOutput> output) {
        List<PressureListTotalOutput> responses = output.stream().map(out -> {
            PressureListTotalOutput response = new PressureListTotalOutput();
            BeanUtils.copyProperties(out, response);
            return response;
        }).collect(Collectors.toList());
        return responses;
    }

    public static List<PressureListTotalOutput> ofListCloud(List<PressureListTotalResp> resps) {
        List<PressureListTotalOutput> outputs = resps.stream().map(resp -> {
            PressureListTotalOutput output = new PressureListTotalOutput();
            BeanUtils.copyProperties(resp, output);
            return output;
        }).collect(Collectors.toList());
        return outputs;
    }

    public static List<ScriptLabelListTotalOutput> ofListOutput(List<ScriptLabelListTotalOutput> output) {
        List<ScriptLabelListTotalOutput> resps = output.stream().map(out -> {
            ScriptLabelListTotalOutput resp = new ScriptLabelListTotalOutput();
            BeanUtils.copyProperties(out, resp);
            return resp;
        }).collect(Collectors.toList());
        return resps;
    }

    public static List<ScriptLabelListTotalOutput> ofListResult(List<ScriptLabelListTotalResult> output) {
        List<ScriptLabelListTotalOutput> resps = output.stream().map(out -> {
            ScriptLabelListTotalOutput resp = new ScriptLabelListTotalOutput();
            BeanUtils.copyProperties(out, resp);
            return resp;
        }).collect(Collectors.toList());
        return resps;
    }

}
