package io.shulie.takin.web.biz.service.application.impl;

import cn.hutool.core.convert.Convert;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationPluginDownloadPathInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationPluginDownloadPathUpdateInput;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationPluginPathDetailResponse;
import io.shulie.takin.web.biz.service.application.ApplicationAgentPathTypeService;
import io.shulie.takin.web.biz.service.application.IPluginDownLoadPathProcess;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.enums.application.ApplicationAgentPathValidStatusEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.ApplicationPluginDownloadPathDAO;
import io.shulie.takin.web.data.param.application.CreateApplicationPluginDownloadPathParam;
import io.shulie.takin.web.data.param.application.UpdateApplicationPluginDownloadPathParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginDownloadPathDetailResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: 南风
 * @Date: 2021/11/10 4:26 下午
 */
@Service
public class ApplicationAgentPathTypeServiceImpl implements ApplicationAgentPathTypeService {

    @Autowired
    private ApplicationPluginDownloadPathDAO pathDAO;



    private final Map<Integer, IPluginDownLoadPathProcess> pluginDownLoadPathProcessHashMap = new HashMap<>();


    public ApplicationAgentPathTypeServiceImpl(List<IPluginDownLoadPathProcess> list) {
        list.forEach(process -> pluginDownLoadPathProcessHashMap.put(process.getType().getVal(),process));
    }


    @Override
    public Response<ApplicationPluginPathDetailResponse> queryConfigDetail() {
        ApplicationPluginDownloadPathDetailResult result = pathDAO.queryDetailByTenant();
        if(Objects.isNull(result)){
            return  Response.success();
        }
        ApplicationPluginPathDetailResponse response = Convert.convert(ApplicationPluginPathDetailResponse.class, result );
        response.setPathType(result.getPathType().toString());
        response.setEditable(ApplicationAgentPathValidStatusEnum.CHECK_FAILED.getVal().equals(result.getValidStatus())?0:1);
        return  Response.success(response);
    }

    @Override
    public Response createConfig(ApplicationPluginDownloadPathInput createInput) {
        CreateApplicationPluginDownloadPathParam createParam = Convert.convert(CreateApplicationPluginDownloadPathParam.class, createInput);
        createParam = pluginDownLoadPathProcessHashMap.get(Integer.valueOf(createInput.getPathType())).encrypt(createParam);
        pathDAO.createConfig(createParam);
        return Response.success();
    }

    @Override
    public Response updateConfig(ApplicationPluginDownloadPathUpdateInput updateInput) {
        ApplicationPluginDownloadPathDetailResult detail = pathDAO.queryById(updateInput.getId());
        if(!ApplicationAgentPathValidStatusEnum.CHECK_FAILED.getVal().equals(detail.getValidStatus())){
            throw new TakinWebException(TakinWebExceptionEnum.PLUGIN_PATH_VALID_ERROR,"不允许修改");
        }

        UpdateApplicationPluginDownloadPathParam updateParam = Convert.convert(UpdateApplicationPluginDownloadPathParam.class, updateInput);
        updateParam = pluginDownLoadPathProcessHashMap.get(updateParam.getPathType()).encrypt(updateParam);
        //重置到待检测状态，等待检测
        updateParam.setValidStatus(ApplicationAgentPathValidStatusEnum.TO_BE_CHECKED.getVal());
        pathDAO.updateConfig(updateParam);
        return Response.success();
    }

    @Override
    public Response validEfficient() {
        ApplicationPluginDownloadPathDetailResult result = pathDAO.queryDetailByTenant();
        return Response.success(ApplicationAgentPathValidStatusEnum.CHECK_PASSED.getVal().equals(result.getValidStatus()));
    }
}
