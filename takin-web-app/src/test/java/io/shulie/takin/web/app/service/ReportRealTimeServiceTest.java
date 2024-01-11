package io.shulie.takin.web.app.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.amdb.bean.query.trace.EntranceRuleDTO;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.util.ActivityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@RunWith(SpringRunner.class)
public class ReportRealTimeServiceTest {

    @Autowired
    ReportRealTimeService reportRealTimeService;

    @Test
    public void test(){
        List<Long> ids = Arrays.asList(456L, 468L);
        List<EntranceRuleDTO> ruleDTOS = reportRealTimeService.getEntryListByBusinessActivityIds(ids);
        System.out.println(JSON.toJSONString(ruleDTOS));
        String collect = ruleDTOS.stream().map(entrance -> {
            if (ActivityUtil.isNormalBusiness(entrance.getBusinessType())) {
                ActivityUtil.EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(entrance.getEntrance());
                return String.format("%s#%s#%s#%s", entrance.getAppName(),
                        entranceJoinEntity.getServiceName(),
                        entranceJoinEntity.getMethodName(), entranceJoinEntity.getRpcType());
            } else {
                ActivityUtil.EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertVirtualEntrance(entrance.getEntrance());
                return String.format("%s#%s#%s#%s", "", entranceJoinEntity.getVirtualEntrance(), "", entranceJoinEntity.getRpcType());
/*
                return String.format("%s#%s#%s#%s", "", "", entranceJoinEntity.getVirtualEntrance(), entranceJoinEntity.getRpcType());
*/
            }

        }).collect(Collectors.joining(AppConstants.COMMA));
        System.out.println(collect);
    }

    public static void main(String[] args) {
        String jsonString = "[{\"businessType\":1,\"entrance\":\"JMETER|Java请求1|-1\"},{\"businessType\":1,\"entrance\":\"GET|/takin-web/api/serverConfig|0\"}]\n";
        List<EntranceRuleDTO> ruleDTOS = JSON.parseArray(jsonString, EntranceRuleDTO.class);
        String collect = ruleDTOS.stream().map(entrance -> {
            if (ActivityUtil.isNormalBusiness(entrance.getBusinessType())) {
                ActivityUtil.EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertEntrance(entrance.getEntrance());
                return String.format("%s#%s#%s#%s", entrance.getAppName(),
                        entranceJoinEntity.getServiceName(),
                        entranceJoinEntity.getMethodName(), entranceJoinEntity.getRpcType());
            } else {
                ActivityUtil.EntranceJoinEntity entranceJoinEntity = ActivityUtil.covertVirtualEntrance(entrance.getEntrance());
                return String.format("%s#%s#%s#%s", "", entranceJoinEntity.getVirtualEntrance(), "", entranceJoinEntity.getRpcType());
            }

        }).collect(Collectors.joining(AppConstants.COMMA));
        System.out.println(collect);
    }
}
