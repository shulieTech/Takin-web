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
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.cloud.data.mapper.mysql.SceneScriptRefMapper;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.app.Application;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileV2Request;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptCssManageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptCsvAliasNameUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptCsvCreateTaskRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessActivityInfoResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvCreateDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvDataSetResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvDataTemplateResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvManageResponse;
import io.shulie.takin.web.biz.service.datamanage.CsvManageService;
import io.shulie.takin.web.biz.service.webide.WebIDESyncService;
import io.shulie.takin.web.data.mapper.mysql.FileManageMapper;
import io.shulie.takin.web.data.mapper.mysql.ScriptCsvCreateTaskMapper;
import io.shulie.takin.web.data.mapper.mysql.ScriptCsvDataSetMapper;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import io.shulie.takin.web.data.model.mysql.ScriptCsvCreateTaskEntity;
import io.shulie.takin.web.data.model.mysql.ScriptCsvDataSetEntity;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private FileManageMapper fileManageMapper;
    @Autowired
    private SceneScriptRefMapper sceneScriptRefMapper;
    @Autowired
    private ScriptCsvDataSetMapper scriptCsvDataSetMapper;

    @Autowired
    private ScriptCsvCreateTaskMapper scriptCsvCreateTaskMapper;

    @Resource
    private WebIDESyncService webIDESyncService;


    /**
     * 测试 1. csv组件列表
     * path:GET http://localhost:8080/api/script/data/csv/list？businessFlowId=？？？
     */
    @Test
    public void listCsvByBusinessFlowId() {
        List<ScriptCsvDataSetResponse> scriptCsvDataSetResponses = csvManageService.listCsvByBusinessFlowId(58L);
        for (ScriptCsvDataSetResponse response : scriptCsvDataSetResponses) {
            System.out.println(JSON.toJSONString(response));
        }
    }

    /**
     * 测试 2. 附件列表
     * path:GET http://localhost:8080/api/script/data/annex/list
     */
    @Test
    public void listAnnexByBusinessFlowId() {
        List<FileManageResponse> responseList = csvManageService.listAnnexByBusinessFlowId(58L);
        for (FileManageResponse response : responseList) {
            System.out.println(JSON.toJSONString(response));
        }
    }

    /**
     * 测试 3. 拆分接口 & 是否按分区排序接口
     * path ：PUT http://localhost:8080/api/script/data/csv/splitOrOrderSplit
     */
    @Test
    public void deleteFile() {

        Long fileManageId = 197L;
        FileManageEntity fileManageEntity = fileManageMapper.selectById(fileManageId);

        Long sceneId = 59L;
        csvManageService.deleteFile(fileManageId);
        // 查看组件有没有变化
        FileManageEntity fileManageEntity_temp = fileManageMapper.selectById(fileManageId);
        if(fileManageEntity_temp == null) {
            System.out.println("文件已删除:查看下文件是否删除" + fileManageEntity.getUploadPath());
        }
        // 查看场景有没有变化
        List<SceneScriptRefEntity> sceneScriptRefEntities = sceneScriptRefMapper.selectList(Wrappers.lambdaUpdate(SceneScriptRefEntity.class)
                .eq(SceneScriptRefEntity::getSceneId, sceneId));
        for (SceneScriptRefEntity response : sceneScriptRefEntities) {
            System.out.println("场景:" + JSON.toJSONString(response));
        }


    }

    /**
     * 测试 4. 拆分接口 & 是否按分区排序接口
     * path ：PUT http://localhost:8080/api/script/data/csv/splitOrOrderSplit
     */
    @Test
    public void spiltOrIsOrderSplit() {
        Long scriptCsvDataSetId = 30L;
        Long sceneId = 59L;
        BusinessFlowDataFileV2Request request = new BusinessFlowDataFileV2Request();
        request.setScriptCsvDataSetId(scriptCsvDataSetId);
        request.setIsSplit(1);
        request.setIsOrderSplit(1);
        csvManageService.spiltOrIsOrderSplit(request);
        // 查看组件有没有变化
        ScriptCsvDataSetEntity setEntity = scriptCsvDataSetMapper.selectById(scriptCsvDataSetId);
        System.out.println("组件:" + JSON.toJSONString(setEntity));
        //查看 文件有没有变化
        LambdaQueryWrapper<FileManageEntity> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileManageEntity::getScriptCsvDataSetId, scriptCsvDataSetId);
        List<FileManageEntity> fileManageEntityList = fileManageMapper.selectList(queryWrapper);
        for (FileManageEntity fileManageEntity : fileManageEntityList) {
            System.out.println("文件:" + JSON.toJSONString(fileManageEntity));
        }
        // 查看场景有没有变化
        List<SceneScriptRefEntity> sceneScriptRefEntities = sceneScriptRefMapper.selectList(Wrappers.lambdaUpdate(SceneScriptRefEntity.class)
                .eq(SceneScriptRefEntity::getSceneId, sceneId));
        for (SceneScriptRefEntity response : sceneScriptRefEntities) {
            System.out.println("场景:" + JSON.toJSONString(response));
        }

    }


    /**
     * 测试 5. csv文件列表
     * path GET http://localhost:8080/api/script/data/csv/file/list?businessFlowId=&scriptCsvDataSetId=
     */
    @Test
    public void listFileCsvById() {
        Long scriptCsvDataSetId = 30L;
        Long sceneId = 59L;
        ScriptCsvDataSetResponse responses = csvManageService.listFileCsvById(sceneId, scriptCsvDataSetId);
        System.out.println(JSON.toJSONString(responses));
    }


    /**
     * 测试：6：csv选择接口
     * GET http://localhost:8080/api/script/data/csv/file/select?scriptCsvDataSetId=&fileManageId=?
     *
     */
    @Test
    public void selectCsv() {
        Long scriptCsvDataSetId = 30L;
        Long fileManageId = 218L;
        Long sceneId = 59L;
        csvManageService.selectCsv(scriptCsvDataSetId, fileManageId);
        // 查看
        ScriptCsvDataSetEntity setEntity = scriptCsvDataSetMapper.selectById(scriptCsvDataSetId);
        System.out.println(JSON.toJSONString(setEntity));
        // 查看场景有没有变化
        List<SceneScriptRefEntity> sceneScriptRefEntities = sceneScriptRefMapper.selectList(Wrappers.lambdaUpdate(SceneScriptRefEntity.class)
                .eq(SceneScriptRefEntity::getSceneId, sceneId));
        for (SceneScriptRefEntity response : sceneScriptRefEntities) {
            System.out.println("场景:" + JSON.toJSONString(response));
        }
    }

    /**
     * 测试7： csv管理列表  任务列表  同一个接口
     * POST http://localhost:8080/api/script/data/csv/manage
     */
    @Test
    public void csvManage () {
        PageScriptCssManageQueryRequest request = new PageScriptCssManageQueryRequest();
        request.setType(1);
        PagingList<ScriptCsvManageResponse> scriptCsvManageResponsePagingList = csvManageService.csvManage(request);
        for (ScriptCsvManageResponse response : scriptCsvManageResponsePagingList.getList()) {
            System.out.println("场景:" + JSON.toJSONString(response));
        }

    }

    /**
     * 测试8：  上传文件  新开接口
     * POST http://localhost:8080/api/script/data/upload?scriptCsvDataSetId=
     * 用postman 测试
     */

    /**
     * 测试9： 保存文件
     * POST http://localhost:8080/api/script/data/uploadDataFile
     * 用postman 测试
     */

    /**
     * 测试10：点击csv生成后获取数据，并展示页面
     * GET http://localhost:8080/api/script/data/csv/create/detail?businessFlowId=业务流程id
     */
    @Test
    public void createDetail () {
        Long businessFlowId = 58L;
        List<ScriptCsvCreateDetailResponse> detail = csvManageService.createDetail(businessFlowId);
        System.out.println(JSON.toJSONString(detail));
    }

    /**
     * 测试11： 获取模板接口
     * POST http://localhost:8080/api/script/data/csv/template/pull
     * postman
     */

    /**
     * 测试12： 创建生成Task任务
     * POST http://localhost:8080/api/script/data/csv/template/pull
     * postman
     */
    @Test
    public void createTask () {
        ScriptCsvCreateTaskRequest request = new ScriptCsvCreateTaskRequest();
        //request.setTemplateVariable("referer,aaa");
        String template = "{\"templateDTO\":[{\"title\":\"header参数\",\"key\":\"header参数\",\"children\":[{\"title\":\"content-length\",\"key\":\"header#$.content-length\",\"children\":null},{\"title\":\"pinpoint-traceid\",\"key\":\"header#$.pinpoint-traceid\",\"children\":null},{\"title\":\"pinpoint-papptype\",\"key\":\"header#$.pinpoint-papptype\",\"children\":null},{\"title\":\"x-forwarded-port\",\"key\":\"header#$.x-forwarded-port\",\"children\":null},{\"title\":\"x-forwarded-for\",\"key\":\"header#$.x-forwarded-for\",\"children\":null},{\"title\":\"accept\",\"key\":\"header#$.accept\",\"children\":null},{\"title\":\"pinpoint-host\",\"key\":\"header#$.pinpoint-host\",\"children\":null},{\"title\":\"pinpoint-spanid\",\"key\":\"header#$.pinpoint-spanid\",\"children\":null},{\"title\":\"pinpoint-flags\",\"key\":\"header#$.pinpoint-flags\",\"children\":null},{\"title\":\"host\",\"key\":\"header#$.host\",\"children\":null},{\"title\":\"content-type\",\"key\":\"header#$.content-type\",\"children\":null},{\"title\":\"connection\",\"key\":\"header#$.connection\",\"children\":null},{\"title\":\"pinpoint-pspanid\",\"key\":\"header#$.pinpoint-pspanid\",\"children\":null},{\"title\":\"accept-encoding\",\"key\":\"header#$.accept-encoding\",\"children\":null},{\"title\":\"user-agent\",\"key\":\"header#$.user-agent\",\"children\":null},{\"title\":\"pinpoint-pappname\",\"key\":\"header#$.pinpoint-pappname\",\"children\":null}]},{\"title\":\"requestBody参数\",\"key\":\"requestBody参数\",\"children\":[{\"title\":\"bus\",\"key\":\"requestBody#$.bus\",\"children\":[]},{\"title\":\"act\",\"key\":\"requestBody#$.act\",\"children\":[]},{\"title\":\"data\",\"key\":\"requestBody#$.data\",\"children\":[{\"title\":\"bus\",\"key\":\"requestBody#$.data[0].bus\",\"children\":[]},{\"title\":\"unionId\",\"key\":\"requestBody#$.data[0].unionId\",\"children\":[]},{\"title\":\"act\",\"key\":\"requestBody#$.data[0].act\",\"children\":[]},{\"title\":\"version\",\"key\":\"requestBody#$.data[0].version\",\"children\":[]}]},{\"title\":\"obj\",\"key\":\"requestBody#$.obj\",\"children\":[{\"title\":\"id\",\"key\":\"requestBody#$.obj.id\",\"children\":[]},{\"title\":\"visitType\",\"key\":\"requestBody#$.obj.visitType\",\"children\":[]}]},{\"title\":\"type\",\"key\":\"requestBody#$.type\",\"children\":[]},{\"title\":\"templateId\",\"key\":\"requestBody#$.templateId\",\"children\":[]},{\"title\":\"objs\",\"key\":\"requestBody#$.objs\",\"children\":[]}]},{\"title\":\"Url参数\",\"key\":\"Url参数\",\"children\":null}],\"count\":100,\"total\":6,\"appName\":\"fm_ac\",\"requestUrl\":\"/ac/auth\",\"requestMethod\":\"\",\"startTime\":\"2023-10-09 00:00:38\",\"endTime\":\"2023-10-10 17:42:38\"}\n";
        request.setTemplate(JSON.parseObject(template,ScriptCsvDataTemplateResponse.class));
        Map<String,String> map = Maps.newHashMap();
        map.put("visitType","requestBody#$.obj.visitType");
        map.put("content-type","requestBody#$.obj.visitType");
        request.setScriptCsvVariableJsonPath(map);
        request.setRemark("测试");
        request.setAliasName("AA");
        request.setBusinessFlowId(58L);
        request.setLinkId(11L);
        request.setScriptCsvDataSetId(31L);


        ScriptCsvCreateTaskRequest request1 = new ScriptCsvCreateTaskRequest();
        //request.setTemplateVariable("referer,aaa");
        String template1 = "{\"templateDTO\":[{\"title\":\"header参数\",\"key\":\"header参数\",\"children\":[{\"title\":\"sec-fetch-mode\",\"key\":\"header#$.sec-fetch-mode\",\"children\":null},{\"title\":\"referer\",\"key\":\"header#$.referer\",\"children\":null},{\"title\":\"content-length\",\"key\":\"header#$.content-length\",\"children\":null},{\"title\":\"sec-fetch-site\",\"key\":\"header#$.sec-fetch-site\",\"children\":null},{\"title\":\"accept-language\",\"key\":\"header#$.accept-language\",\"children\":null},{\"title\":\"x-forwarded-proto\",\"key\":\"header#$.x-forwarded-proto\",\"children\":null},{\"title\":\"cookie\",\"key\":\"header#$.cookie\",\"children\":null},{\"title\":\"x-forwarded-port\",\"key\":\"header#$.x-forwarded-port\",\"children\":null},{\"title\":\"x-forwarded-for\",\"key\":\"header#$.x-forwarded-for\",\"children\":null},{\"title\":\"dnt\",\"key\":\"header#$.dnt\",\"children\":null},{\"title\":\"forwarded\",\"key\":\"header#$.forwarded\",\"children\":null},{\"title\":\"accept\",\"key\":\"header#$.accept\",\"children\":null},{\"title\":\"x-real-ip\",\"key\":\"header#$.x-real-ip\",\"children\":null},{\"title\":\"authorization\",\"key\":\"header#$.authorization\",\"children\":null},{\"title\":\"sec-ch-ua\",\"key\":\"header#$.sec-ch-ua\",\"children\":null},{\"title\":\"sec-ch-ua-mobile\",\"key\":\"header#$.sec-ch-ua-mobile\",\"children\":null},{\"title\":\"x-forwarded-host\",\"key\":\"header#$.x-forwarded-host\",\"children\":null},{\"title\":\"sec-ch-ua-platform\",\"key\":\"header#$.sec-ch-ua-platform\",\"children\":null},{\"title\":\"host\",\"key\":\"header#$.host\",\"children\":null},{\"title\":\"user\",\"key\":\"header#$.user\",\"children\":null},{\"title\":\"accept-encoding\",\"key\":\"header#$.accept-encoding\",\"children\":null},{\"title\":\"user-agent\",\"key\":\"header#$.user-agent\",\"children\":null},{\"title\":\"sec-fetch-dest\",\"key\":\"header#$.sec-fetch-dest\",\"children\":null}]},{\"title\":\"requestBody参数\",\"key\":\"requestBody参数\",\"children\":[{\"title\":\"n\",\"key\":\"requestBody#$.n\",\"children\":[]}]},{\"title\":\"Url参数\",\"key\":\"Url参数\",\"children\":[{\"title\":\"value-0\",\"key\":\"url#value-0\",\"children\":null},{\"title\":\"value-1\",\"key\":\"url#value-1\",\"children\":null},{\"title\":\"value-2\",\"key\":\"url#value-2\",\"children\":null}]}],\"count\":100,\"total\":279,\"appName\":\"msasso_a\",\"requestUrl\":\"/bit-msa-sso/common/v2/auth/send2FACode/token/{value}/account/{value}/cipher/{value}/AK/BIT-MSA\",\"requestMethod\":\"GET\",\"startTime\":\"2023-10-09 00:00:38\",\"endTime\":\"2023-10-10 17:42:38\"}\n";
        request1.setTemplate(JSON.parseObject(template1,ScriptCsvDataTemplateResponse.class));
        Map<String,String> map1 = Maps.newHashMap();
        map1.put("value0","url#value-0");
        map1.put("value1","url#value-1");
        map1.put("value2","url#value-2");
        map1.put("n","requestBody#$.n");
        map1.put("authorization","header#$.authorization");
        request1.setScriptCsvVariableJsonPath(map1);
        request1.setRemark("测试");
        request1.setAliasName("AA");
        request1.setBusinessFlowId(58L);
        request1.setLinkId(11L);
        request1.setScriptCsvDataSetId(32L);

        List<ScriptCsvCreateTaskRequest> requestList = Lists.newArrayList();
        requestList.add(request);
        requestList.add(request1);
        csvManageService.createTask(requestList);
        // 检查job是否存在
        List<ScriptCsvCreateTaskEntity> scriptCsvCreateTaskEntities = scriptCsvCreateTaskMapper.selectList(new LambdaQueryWrapper<>());
        for (ScriptCsvCreateTaskEntity response : scriptCsvCreateTaskEntities) {
            System.out.println("任务:" + JSON.toJSONString(response));
        }
    }

    /**
     * 测试13： 取消任务
     * PUT http://localhost:8080/api/script/data/csv/task/cancel
     *
     */
    @Test
    public void cancelTask () {
        ScriptCsvCreateTaskRequest request = new ScriptCsvCreateTaskRequest();
        request.setTaskId(1L);
        csvManageService.cancelTask(request);
        // 检查job状态
        List<ScriptCsvCreateTaskEntity> scriptCsvCreateTaskEntities = scriptCsvCreateTaskMapper.selectList(new LambdaQueryWrapper<>());
        for (ScriptCsvCreateTaskEntity response : scriptCsvCreateTaskEntities) {
            System.out.println("任务:" + JSON.toJSONString(response));
        }
    }

    /**
     * 测试14： 更新备注
     * PUT POST http://localhost:8080/api/script/data/updateAliasName
     */
    @Test
    public void updateAliasName () {
        ScriptCsvAliasNameUpdateRequest request = new ScriptCsvAliasNameUpdateRequest();
        Long fileManageId =  111L;
        request.setAliasName("aaa");
        request.setFileManageId(fileManageId);
        csvManageService.updateAliasName(request);
        FileManageEntity fileManageEntity = fileManageMapper.selectById(fileManageId);
        System.out.println(JSON.toJSONString(fileManageEntity));

    }


    /**
     * 测试15  业务活动获取接口 复用原有的
     * GET http://localhost:8080/api/webide/script/businessFlow/activityList?businessFlowId=业务流程id
     */
    @Test
    public void activityList () {
        List<BusinessActivityInfoResponse> businessActivityInfoResponses = webIDESyncService.activityList(41L);
        System.out.println(JSON.toJSONString(businessActivityInfoResponses));
    }


    @Test
    public void transformFromJmeter() {
        ScriptAnalyzeRequest analyzeRequest = new ScriptAnalyzeRequest();
        analyzeRequest.setScriptFile("/Users/hezhongqi/shulie/东方甄选/全链路-查看商品/全链路-查看商品.jmx");
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = csvManageService.transformFromJmeter(analyzeRequest);
        System.out.println(JSON.toJSONString(csvDataSetEntityList));
    }

    @Test
    public void save() {
        ScriptAnalyzeRequest analyzeRequest = new ScriptAnalyzeRequest();
        analyzeRequest.setScriptFile("/Users/hezhongqi/shulie/东方甄选/全链路-查看商品/全链路-查看商品.jmx");
        List<ScriptCsvDataSetEntity> csvDataSetEntityList = csvManageService.transformFromJmeter(analyzeRequest);
        BusinessFlowParseRequest businessFlowParseRequest = new BusinessFlowParseRequest();
        businessFlowParseRequest.setId(58L);
        businessFlowParseRequest.setScriptDeployId(115L);
        csvManageService.save(csvDataSetEntityList, businessFlowParseRequest);
        System.out.println(JSON.toJSONString(csvDataSetEntityList));
    }

    @Test
    public void listByBusinessFlowId() {
        List<ScriptCsvDataSetResponse> csvDataSetEntityList = csvManageService.listCsvByBusinessFlowId(58L);
        System.out.println(JSON.toJSONString(csvDataSetEntityList));
    }

}
