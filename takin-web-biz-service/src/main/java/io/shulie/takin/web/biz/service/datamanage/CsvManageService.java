package io.shulie.takin.web.biz.service.datamanage;

import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.adapter.api.model.response.file.UploadResponse;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileV2Request;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptCssManageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptCsvAliasNameUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptCsvCreateTaskRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptCsvDataTemplateRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvCreateDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvDataSetResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvDataTemplateResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptCsvManageResponse;
import io.shulie.takin.web.data.model.mysql.ScriptCsvDataSetEntity;
import org.springframework.web.multipart.MultipartFile;

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
     * csv列表
     * @param businessFlowId
     * @return
     */
    List<ScriptCsvDataSetResponse> listCsvByBusinessFlowId(Long businessFlowId);

    /**
     * 附件列表
     * @param businessFlowId
     * @return
     */
    List<FileManageResponse> listAnnexByBusinessFlowId(Long businessFlowId);


    /**
     * 点击生成csv，调用接口，获取相关数据
     * @param businessFlowId
     * @return
     */
    List<ScriptCsvCreateDetailResponse> createDetail(Long businessFlowId);

    /**
     * 拉取模板数据
     * @param request
     * @return
     */
    ScriptCsvDataTemplateResponse getCsvTemplate(ScriptCsvDataTemplateRequest request);

    /**
     * 点击生成csv,并生成任务
     * @param request
     */
    void createTask(List<ScriptCsvCreateTaskRequest> request);

    /**
     * 取消任务
     * @param request
     */
    void cancelTask(ScriptCsvCreateTaskRequest request);


    /**
     * 获取csv文件列表
     * @param businessFlowId
     * @param scriptCsvDataSetId
     * @return
     */
    ScriptCsvDataSetResponse listFileCsvById(Long businessFlowId, Long scriptCsvDataSetId);

    void spiltOrIsOrderSplit(BusinessFlowDataFileV2Request request);

    /**
     * 删除文件
     * @param fileManageId
     */
    void deleteFile(Long fileManageId);

    /**
     * 选择csv文件
     * @param scriptCsvDataSetId
     * @param fileManageId
     */
    void selectCsv(Long scriptCsvDataSetId, Long fileManageId);

    /**
     * css管理页面
     * @param request
     * @return
     */
    PagingList<ScriptCsvManageResponse> csvManage(PageScriptCssManageQueryRequest request);


    /**
     * 上传文件
     * @param sceneId
     * @param scriptCsvDataSetId
     * @param file
     * @return
     */
    List<UploadResponse> upload(Long sceneId,Long scriptCsvDataSetId, List<MultipartFile> file);

    /**
     * 保存上传文件
     * @param businessFlowDataFileRequest
     * @return
     */
    BusinessFlowDetailResponse uploadDataFile(BusinessFlowDataFileRequest businessFlowDataFileRequest);

    /**
     * 更新备注
     * @param request
     */
    void updateAliasName(ScriptCsvAliasNameUpdateRequest request);

    void runJob();
}
