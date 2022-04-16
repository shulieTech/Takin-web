package io.shulie.takin.adapter.cloud.impl.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginDetailOutput;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.data.model.mysql.EnginePluginEntity;
import io.shulie.takin.adapter.api.entrypoint.engine.CloudEngineApi;
import io.shulie.takin.adapter.api.model.request.engine.EnginePluginDetailsWrapperReq;
import io.shulie.takin.adapter.api.model.request.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.adapter.api.model.response.engine.EnginePluginDetailResp;
import io.shulie.takin.adapter.api.model.response.engine.EnginePluginFileResp;
import io.shulie.takin.adapter.api.model.response.engine.EnginePluginSimpleInfoResp;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 引擎接口实现
 *
 * @author lipeng
 * @author 张天赐
 * @date 2021-01-20 3:33 下午
 */
@Service
public class CloudEngineApiImpl implements CloudEngineApi {

    @Resource(type = EnginePluginService.class)
    private EnginePluginService enginePluginService;

    /**
     * 根据插件类型获取插件列表
     *
     * @param request 请求参数
     * @return -
     */
    @Override
    public Map<String, List<EnginePluginSimpleInfoResp>> listEnginePlugins(EnginePluginFetchWrapperReq request) {
        List<String> pluginTypes = request.getPluginTypes();
        if (pluginTypes == null) {pluginTypes = new ArrayList<>(0);}
        //插件类型小写存储
        pluginTypes = pluginTypes.stream().map(String::toLowerCase).collect(Collectors.toList());
        Map<String, List<EnginePluginEntity>> dbResult = enginePluginService.findEngineAvailablePluginsByType(pluginTypes);
        Map<String, List<EnginePluginSimpleInfoResp>> result = new HashMap<>(dbResult.size());
        dbResult.forEach((k, v) ->
            result.put(k, v.stream().map(c -> new EnginePluginSimpleInfoResp() {{
                setPluginId(c.getId());
                setPluginName(c.getPluginName());
                setPluginType(c.getPluginType());
                setGmtUpdate(DateUtil.formatDateTime(c.getGmtUpdate()));
            }}).collect(Collectors.toList())));
        return result;
    }

    /**
     * 根据插件ID获取插件详情
     *
     * @param request 请求参数
     * @return -
     */
    @Override
    public EnginePluginDetailResp getEnginePluginDetails(EnginePluginDetailsWrapperReq request) {
        Long pluginId = request.getPluginId();
        if (pluginId == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.ENGINE_PLUGIN_PARAM_VERIFY_ERROR, "pluginId不能为空");
        }
        EnginePluginDetailOutput output = enginePluginService.findEnginePluginDetails(pluginId).getData();
        if (Objects.isNull(output)) {
            return null;
        }
        EnginePluginDetailResp resp = new EnginePluginDetailResp();
        BeanUtils.copyProperties(output, resp, "uploadFiles");
        List<EnginePluginFileResp> fileRespList = output.getUploadFiles()
            .stream().map(file -> BeanUtil.copyProperties(file, EnginePluginFileResp.class)
        ).collect(Collectors.toList());
        resp.setUploadFiles(fileRespList);
        return resp;
    }

}
