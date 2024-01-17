package io.shulie.takin.web.biz.service.scriptmanage.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.FileTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PluginConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.vo.ParseScriptNodeVO;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDeployService;
import io.shulie.takin.web.biz.utils.JarUtils;
import io.shulie.takin.web.common.constant.FeaturesConstants;
import io.shulie.takin.web.data.mapper.mysql.SceneMapper;
import io.shulie.takin.web.data.mapper.mysql.ScriptFileRefMapper;
import io.shulie.takin.web.data.mapper.mysql.ScriptManageDeployMapper;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import io.shulie.takin.web.data.model.mysql.ScriptManageDeployEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ScriptDeployServiceImpl implements ScriptDeployService {
    @Resource
    private SceneMapper sceneMapper;
    @Resource
    private ScriptFileRefMapper scriptFileRefMapper;
    @Resource
    private ScriptManageDeployMapper scriptManageDeployMapper;

    private static Map<String, String> skipPluginsMap = new HashMap<>();

    static {
        /**
         * key 对应JmxUtils中的自定义采样器
         * value 对应t_engine_plugin_info表的plugin_type字段
         */
        skipPluginsMap.put("io.github.ningyu.jmeter.plugin.dubbo.sample.DubboSample", "dubbo");
        skipPluginsMap.put("io.shulie.jmeter.plugins.rabbit.RabbitPublisherSampler", "rabbitmq");
        skipPluginsMap.put("ShulieKafkaDataSetSampler", "kafka-data_set");
        skipPluginsMap.put("io.shulie.jmeter.plugins.kafka.dataset.Sampler", "kafka");
    }

    /**
     * 调试传scriptDeployId
     * 压测传业务流程id
     * @param masterId
     * @param pressureSceneEnum
     * @return
     */
    @Override
    public List<String> checkLeakFile(Long masterId, PressureSceneEnum pressureSceneEnum) {
        List<String> errorList = new ArrayList<>();
        SceneEntity sceneEntity = null;
        Long scriptDeployId;
        if(pressureSceneEnum == PressureSceneEnum.FLOW_DEBUG) {
            sceneEntity = sceneMapper.querySceneByScriptDeployId(masterId);
            scriptDeployId = masterId;
        } else {
            sceneEntity = sceneMapper.selectById(masterId);
            if(sceneEntity == null || sceneEntity.getScriptJmxNode() == null) {
                return errorList;
            }
            scriptDeployId = sceneEntity.getScriptDeployId();
        }
        ScriptManageDeployEntity scriptManageDeployEntity = scriptManageDeployMapper.selectById(scriptDeployId);
        if(scriptManageDeployEntity == null) {
            return errorList;
        }
        JSONObject jsonObject = JSON.parseObject(scriptManageDeployEntity.getFeature());
        List<String> pluginTypeList = new ArrayList<>();
        if(jsonObject != null && jsonObject.containsKey(FeaturesConstants.PLUGIN_CONFIG)) {
            List<PluginConfigCreateRequest> pluginList = JSON.parseArray(jsonObject.getString(FeaturesConstants.PLUGIN_CONFIG), PluginConfigCreateRequest.class);
            pluginTypeList.addAll(pluginList.stream().map(PluginConfigCreateRequest::getType).collect(Collectors.toList()));
        }
        //1. 列出所有csv文件
        //2. 判断是否有JavaRequest|JDBCRequest
        List<ScriptNode> nodeList = JSON.parseArray(sceneEntity.getScriptJmxNode(), ScriptNode.class);
        ParseScriptNodeVO nodeVO = new ParseScriptNodeVO();
        checkScriptNode(nodeList, nodeVO);
        //java取样器的类排除同插件名一致的
        nodeVO.getJavaRequestClass().removeAll(skipPluginsMap.keySet());
        List<FileManageEntity> fileList = scriptFileRefMapper.listFileMangerByScriptDeployId(scriptDeployId);
        nodeVO.getCsvFileSet().removeAll(fileList.stream().filter(data -> data.getFileType() == FileTypeEnum.DATA.getCode()).map(FileManageEntity::getFileName).collect(Collectors.toList()));
        if(CollectionUtils.isNotEmpty(nodeVO.getCsvFileSet())) {
            errorList.add(String.format("csv文件缺失:%s", String.join(",", nodeVO.getCsvFileSet())));
        }
        List<String> jarList = fileList.stream().filter(data -> data.getFileType() == FileTypeEnum.DATA.getCode() && data.getFileName().endsWith(".jar")).map(FileManageEntity::getUploadPath).collect(Collectors.toList());
        //JDBC取样器
        if(nodeVO.getJdbcRequestClass().size() > 0) {
            for(String jdbcRequestClass : nodeVO.getJdbcRequestClass()) {
                if(!findClassFromJar(jdbcRequestClass, jarList)) {
                    errorList.add(String.format("jar包缺失@JDBC取样器:类%s找不到依赖包", jdbcRequestClass));
                }
            }
        }
        //JAVA取样器 jar包
        if(nodeVO.getJavaRequestClass().size() > 0) {
            for(String javaRequestClass : nodeVO.getJavaRequestClass()) {
                if(!findClassFromJar(javaRequestClass, jarList)) {
                    errorList.add(String.format("jar包缺失@Java取样器:类%s找不到依赖包", javaRequestClass));
                }
            }
        }
        //插件取样器
        if(nodeVO.getPluginRequestClass().size() > 0) {
            for(String pluginRequestClass : nodeVO.getPluginRequestClass()) {
                if(!pluginTypeList.contains(skipPluginsMap.get(pluginRequestClass))) {
                    errorList.add(String.format("插件包缺失:%s找不到插件包", pluginRequestClass));
                }
            }
        }
        return errorList;
    }

    private Boolean findClassFromJar(String className, List<String> jarList) {
        if(CollectionUtils.isEmpty(jarList)) {
            return false;
        }
        for(String jar : jarList) {
            if(JarUtils.checkClassExist(jar, className)) {
                return true;
            }
        }
        return false;
    }

    private void checkScriptNode(List<ScriptNode> nodeList, ParseScriptNodeVO nodeVO) {
        if(CollectionUtils.isEmpty(nodeList)) {
            return;
        }
        for(ScriptNode scriptNode : nodeList) {
            nodeVO.getCsvFileSet().addAll(scriptNode.getCsvSet());
            nodeVO.getJdbcRequestClass().addAll(scriptNode.getDriverSet());
            if(StringUtils.equalsAny(scriptNode.getName(), "JavaSampler")) {
                nodeVO.getJavaRequestClass().add(scriptNode.getProps().get("classname"));
            } else if(skipPluginsMap.containsKey(scriptNode.getName())) {
                nodeVO.getPluginRequestClass().add(scriptNode.getName());
            }
            checkScriptNode(scriptNode.getChildren(), nodeVO);
        }
    }
}
