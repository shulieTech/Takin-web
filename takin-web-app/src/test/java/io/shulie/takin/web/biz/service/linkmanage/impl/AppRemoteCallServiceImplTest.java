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

import com.google.common.collect.Lists;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallConfigRequest;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

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

    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;
    @Autowired
    TransactionDefinition transactionDefinition;

    @Autowired
    private AppRemoteCallService appRemoteCallService;

    @Test
    public void batchConfigTest(){
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        AppRemoteCallConfigRequest request = new AppRemoteCallConfigRequest();
        request.setType(AppRemoteCallConfigEnum.OPEN_WHITELIST.getType().shortValue());
        request.setAppIds(Lists.newArrayList(6841250229157629952L,6846731729676275712L));
        appRemoteCallService.batchConfig(request);
        dataSourceTransactionManager.commit(transactionStatus);//提交
    }
}
