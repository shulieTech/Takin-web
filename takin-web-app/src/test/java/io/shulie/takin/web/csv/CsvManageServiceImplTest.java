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

package io.shulie.takin.web.csv;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallConfigRequest;
import io.shulie.takin.web.biz.pojo.request.datasource.DataSourceQueryRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.response.datasource.DatasourceListResponse;
import io.shulie.takin.web.biz.service.DataSourceService;
import io.shulie.takin.web.biz.service.datamanage.CsvManageService;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.data.mapper.mysql.AppRemoteCallMapper;
import io.shulie.takin.web.data.model.mysql.AppRemoteCallEntity;
import io.shulie.takin.web.data.model.mysql.ScriptCsvDataSetEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
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
public class CsvManageServiceImplTest {

   @Autowired
   private CsvManageService csvManageService;

    @Test
    public void transformFromJmeter(){
        ScriptAnalyzeRequest analyzeRequest = new ScriptAnalyzeRequest();
        analyzeRequest.setScriptFile("/Users/hezhongqi/shulie/东方甄选/全链路-查看商品/全链路-查看商品.jmx");
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = csvManageService.transformFromJmeter(analyzeRequest);
        System.out.println(JSON.toJSONString(csvDataSetEntityList));
    }
    @Test
    public void save(){
        ScriptAnalyzeRequest analyzeRequest = new ScriptAnalyzeRequest();
        analyzeRequest.setScriptFile("/Users/hezhongqi/shulie/东方甄选/全链路-查看商品/全链路-查看商品.jmx");
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = csvManageService.transformFromJmeter(analyzeRequest);
        BusinessFlowParseRequest businessFlowParseRequest = new BusinessFlowParseRequest();
        businessFlowParseRequest.setId(58L);
        businessFlowParseRequest.setScriptDeployId(115L);
        csvManageService.save(csvDataSetEntityList,businessFlowParseRequest);
        System.out.println(JSON.toJSONString(csvDataSetEntityList));
    }

    @Test
    public void listByBusinessFlowId(){
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = csvManageService.listByBusinessFlowId(58L);
        System.out.println(JSON.toJSONString(csvDataSetEntityList));
    }

}
