package io.shulie.takin.web.entrypoint.controller.perfomanceanaly;

import java.util.concurrent.atomic.AtomicInteger;

import io.shulie.takin.web.biz.convert.performace.PerformanceBaseReqConvert;
import io.shulie.takin.web.biz.pojo.input.PerformanceBaseDataCreateInput;
import io.shulie.takin.web.biz.pojo.perfomanceanaly.PerformanceBaseDataReq;
import io.shulie.takin.web.biz.service.perfomanceanaly.PerformanceBaseDataService;
import io.shulie.takin.web.common.constant.AgentUrls;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/11/4 下午2:49
 */
@RestController
@RequestMapping(value = AgentUrls.PREFIX_URL)
@Api(tags = "性能分析数据")
public class PerformanceBaseDataController {

    private static final AtomicInteger INTEGER = new AtomicInteger();

    @Autowired
    private PerformanceBaseDataService performanceBaseDataService;


    @PostMapping(value = AgentUrls.PERFORMANCE_BASE_URL)
    @ApiOperation(value = "接收agent写入数据")
    public void receivePerformanceBaseData(@RequestBody PerformanceBaseDataReq req) {
        if (INTEGER.get() > 100000000) {
            INTEGER.set(0);
        }

        int frequency = ConfigServerHelper.getIntegerValueByKey(ConfigServerKeyEnum.PERFORMANCE_BASE_AGENT_FREQUENCY);
        if (INTEGER.getAndIncrement() % frequency == 0) {
            PerformanceBaseDataCreateInput input = PerformanceBaseReqConvert.INSTANCE.reqToInput(req);
            performanceBaseDataService.cache(input);
        }
    }

}
