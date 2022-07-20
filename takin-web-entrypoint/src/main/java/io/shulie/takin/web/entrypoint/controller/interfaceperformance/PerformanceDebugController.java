package io.shulie.takin.web.entrypoint.controller.interfaceperformance;

import com.google.common.collect.Maps;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.*;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceConfigService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceDebugService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceResultService;
import io.shulie.takin.web.biz.service.interfaceperformance.impl.PerformanceDebugUtil;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 1:33 下午
 */
@RestController
@RequestMapping(value = ApiUrls.TAKIN_API_URL + "/interfaceperformance/debug")
@Api(tags = "接口: 接口压测")
@Slf4j
public class PerformanceDebugController {
    @Resource
    private PerformanceConfigService performanceConfigService;

    @Resource
    private PerformanceDebugService performanceDebugService;

    @Resource
    private PerformanceResultService performanceResultService;

    @Resource
    private RedisClientUtil redisClientUtil;

    @Resource
    private PerformanceDebugUtil performanceDebugUtil;

    @ApiOperation("调试,如果未设置调试Id,则默认是新增以后开启调试")
    @RequestMapping(value = "/startAndSave", method = RequestMethod.POST)
    public ResponseResult startAndSave(@RequestBody PerformanceDebugRequest request) {
        // 配置Id为空,则需要保存以后，再做调试功能
        if (request.getId() == null) {
            PerformanceConfigCreateInput input = new PerformanceConfigCreateInput();
            BeanUtils.copyProperties(request, input);
            Long configId = performanceConfigService.add(input);
            request.setId(configId);
        } else {
            // ConfigId不为空,保存下当前配置信息
            PerformanceConfigCreateInput input = new PerformanceConfigCreateInput();
            BeanUtils.copyProperties(request, input);
            performanceConfigService.update(input);
        }
        String uuid = performanceDebugService.debug(request);
        // 返回接口压测Id到前端
        return ResponseResult.success(uuid);
    }

    @ApiOperation("简单调试,不保存数据，也不更新数据")
    @RequestMapping(value = "/start", method = RequestMethod.POST)
    public ResponseResult simpleDebug(@RequestBody PerformanceDebugRequest request) {
        return ResponseResult.success(performanceDebugService.start(request));
    }

    @ApiOperation("获取调试结果")
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    public ResponseResult result(PerformanceResultCreateInput input) {
        PagingList<PerformanceResultResponse> pageResult = performanceResultService.pageResult(input);
        // 封装失败数据
        long failCount = 0;
        if (!pageResult.isEmpty()) {
            failCount = pageResult.getList().stream().filter(result -> !result.getStatus().equals(200)).count();
        }
        ResponseResult responseResult = ResponseResult.success(pageResult);
        Map<String, Object> extData = Maps.newHashMap();
        // 失败条数
        extData.put("failCount", failCount);
        extData.put("requestFinished", true);
        // 轮训标记,给前端使用
        String resultId = input.getResultId();
        if (StringUtils.isNotBlank(resultId)) {
            // 获取下状态标记,有值就代表没处理完成
            Object obj = redisClientUtil.get(performanceDebugUtil.formatResultKey(resultId));
            if (!Objects.isNull(obj)) {
                extData.put("requestFinished", false);
            }
        }
        responseResult.setExtData(extData);
        return responseResult;
    }

    @ApiOperation("清理结果")
    @RequestMapping(value = "/flush", method = RequestMethod.DELETE)
    public ResponseResult flush(@RequestBody PerformanceResultCreateInput input) {
        performanceResultService.flushAll(input);
        return ResponseResult.success();
    }
}
