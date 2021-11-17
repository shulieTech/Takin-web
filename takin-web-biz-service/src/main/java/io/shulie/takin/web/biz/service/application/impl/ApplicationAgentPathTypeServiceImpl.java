package io.shulie.takin.web.biz.service.application.impl;

import cn.hutool.core.convert.Convert;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationPluginDownloadPathInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationPluginDownloadPathUpdateInput;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationPluginPathDetailResponse;
import io.shulie.takin.web.biz.service.application.ApplicationAgentPathTypeService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.enums.application.ApplicationAgentPathTypeEnum;
import io.shulie.takin.web.common.enums.application.ApplicationAgentPathValidStatusEnum;
import io.shulie.takin.web.data.dao.application.ApplicationPluginDownloadPathDAO;
import io.shulie.takin.web.data.param.application.CreateApplicationPluginDownloadPathParam;
import io.shulie.takin.web.data.param.application.UpdateApplicationPluginDownloadPathParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginDownloadPathDetailResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @Author: 南风
 * @Date: 2021/11/10 4:26 下午
 */
@Service
public class ApplicationAgentPathTypeServiceImpl implements ApplicationAgentPathTypeService {

    @Autowired
    private ApplicationPluginDownloadPathDAO pathDAO;

//    @Override
//    public List<SelectVO> supportType() {
//        List<SelectVO> list = new ArrayList<>();
//         Arrays.stream(ApplicationAgentPathTypeEnum.values()).forEach(typeEnum ->
//                 list.add(new SelectVO(typeEnum.getDesc(), String.valueOf(typeEnum.getVal()))));
//        return list;
//    }

    @Override
    public Response<ApplicationPluginPathDetailResponse> queryConfigDetail() {
        ApplicationPluginDownloadPathDetailResult result = pathDAO.queryDetailByCustomerId();
        if(Objects.isNull(result)){
            return  Response.success();
        }
        ApplicationPluginPathDetailResponse response = Convert.convert(ApplicationPluginPathDetailResponse.class, result );
        response.setPathType(new SelectVO(ApplicationAgentPathTypeEnum.getEnumByVal(result.getPathType()).getDesc(),
                String.valueOf(result.getPathType())));
        return  Response.success(response);
    }

    @Override
    public Response createConfig(ApplicationPluginDownloadPathInput createInput) {
        CreateApplicationPluginDownloadPathParam createParam = Convert.convert(CreateApplicationPluginDownloadPathParam.class, createInput);
        pathDAO.createConfig(createParam);
        return Response.success();
    }

    @Override
    public Response updateConfig(ApplicationPluginDownloadPathUpdateInput updateInput) {
        UpdateApplicationPluginDownloadPathParam updateParam = Convert.convert(UpdateApplicationPluginDownloadPathParam.class, updateInput);
        //重置到待检测状态，等待检测
        updateParam.setValidStatus(ApplicationAgentPathValidStatusEnum.TO_BE_CHECKED.getVal());
        pathDAO.updateConfig(updateParam);
        return Response.success();
    }

    @Override
    public Response validEfficient() {
        ApplicationPluginDownloadPathDetailResult result = pathDAO.queryDetailByCustomerId();
        return Response.success(ApplicationAgentPathValidStatusEnum.CHECK_PASSED.getVal().equals(result.getValidStatus()));
    }
}
