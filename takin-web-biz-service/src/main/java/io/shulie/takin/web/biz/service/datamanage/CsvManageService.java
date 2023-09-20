package io.shulie.takin.web.biz.service.datamanage;

import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.data.model.mysql.ScriptCsvDataSetEntity;

import java.util.List;

public interface CsvManageService {


    /**
     * @param analyzeRequest
     * @return
     */
    List<ScriptCsvDataSetEntity> transformFromJmeter(ScriptAnalyzeRequest analyzeRequest);




    /**
     * 保存css 数据
     * @param setEntityList
     * @param businessFlowParseRequest
     */
    void save(List<ScriptCsvDataSetEntity> setEntityList, BusinessFlowParseRequest businessFlowParseRequest);


    /**
     * @param businessFlowId
     * @return
     */
    List<ScriptCsvDataSetEntity> listByBusinessFlowId(Long businessFlowId);



}
