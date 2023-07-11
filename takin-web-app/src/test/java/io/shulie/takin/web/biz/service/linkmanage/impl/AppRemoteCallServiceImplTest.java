/*
 * Copyright (c) 2021. Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.shulie.takin.web.biz.service.linkmanage.impl;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallConfigRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceQueryRequest;
import io.shulie.takin.web.biz.pojo.response.datasource.DatasourceListResponse;
import io.shulie.takin.web.biz.service.DataSourceService;
import io.shulie.takin.web.biz.service.async.AsyncService;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseDataParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceThreadDataParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明:
 *
 * @author liuxuewu
 * @2021/9/24
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = Application.class)
public class AppRemoteCallServiceImplTest {

    @Value("${data.path}")
    protected String uploadPath;

    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;
    @Autowired
    TransactionDefinition transactionDefinition;

    @Autowired
    private AppRemoteCallService appRemoteCallService;

    //@Resource
    //private PluginLibraryDAO pluginLibraryDAO;

    @Autowired
    DataSourceService dataSourceService;

    @Autowired
    AsyncService asyncService;

    @Test
    public void zzTest(){
        PerformanceBaseDataParam param = new PerformanceBaseDataParam();
        param.setAgentId("11");
        param.setTimestamp(System.currentTimeMillis());
        List<PerformanceThreadDataParam> list = new ArrayList<>();
        for(int i=0;i<50;i++){
//            int i=9;
            PerformanceThreadDataParam p1 = new PerformanceThreadDataParam();
            p1.setThreadStackLink(1L);
            p1.setThreadId(i+"1");
            p1.setThreadName(i+"t1");
            p1.setGroupName(i+"g1");
            p1.setThreadCpuUsage(1D);
            list.add(p1);
        }
        param.setThreadDataList(list);
        asyncService.savePerformanceBaseData(param);
    }

    @Test
    public void batchConfigTest(){
//        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
//        AppRemoteCallConfigRequest request = new AppRemoteCallConfigRequest();
//        request.setType(AppRemoteCallConfigEnum.OPEN_WHITELIST.getType().shortValue());
//        request.setAppIds(Lists.newArrayList(6841250229157629952L,6846731729676275712L));
//        appRemoteCallService.batchConfig(request);
//        dataSourceTransactionManager.commit(transactionStatus);//提交
    }

    @Test
    public void agentTest(){
        //// 查询当前最高版本的agent、simulator、middleware的包
        //PluginLibraryDetailResult agent = pluginLibraryDAO.queryMaxVersionPlugin(PluginTypeEnum.AGENT.getCode()).get(0);
        //PluginLibraryDetailResult simulator = pluginLibraryDAO.queryMaxVersionPlugin(PluginTypeEnum.SIMULATOR.getCode())
        //    .get(0);
        //List<PluginLibraryDetailResult> middlewareList = pluginLibraryDAO.queryMaxVersionPlugin(
        //    PluginTypeEnum.MIDDLEWARE.getCode());
        //
        //// 聚合包
        //String completeAgentPath = AgentPkgUtil.aggregation(agent, simulator, middlewareList, uploadPath);
        //String completeSimulatorPath = AgentPkgUtil.aggregation(null, simulator, middlewareList, uploadPath);
        //System.out.println(completeAgentPath);
        //System.out.println(completeSimulatorPath);
    }

//    @Test
//    public void like(){
//        final DataSourceQueryRequest request = new DataSourceQueryRequest();
//        request.setCurrent(0);
//        request.setPageSize(10);
//        //request.setDatasourceName("%");
//        request.setJdbcUrl("_0");
//        final PagingList<DatasourceListResponse> list = dataSourceService.listDatasource(
//            request);
//        System.out.println(JSON.toJSONString(list));
//    }
}
