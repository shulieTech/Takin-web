package io.shulie.takin.web.biz.service.scriptmanage.impl;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.FileTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.vo.ParseScriptNodeVO;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDeployService;
import io.shulie.takin.web.biz.utils.JarUtils;
import io.shulie.takin.web.data.mapper.mysql.SceneMapper;
import io.shulie.takin.web.data.mapper.mysql.ScriptFileRefMapper;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScriptDeployServiceImpl implements ScriptDeployService {
    @Resource
    private SceneMapper sceneMapper;
    @Resource
    private ScriptFileRefMapper scriptFileRefMapper;

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
        //1. 列出所有csv文件
        //2. 判断是否有JavaRequest|JDBCRequest
        List<ScriptNode> nodeList = JSON.parseArray(sceneEntity.getScriptJmxNode(), ScriptNode.class);
        ParseScriptNodeVO nodeVO = new ParseScriptNodeVO();
        checkScriptNode(nodeList, nodeVO);
        List<FileManageEntity> fileList = scriptFileRefMapper.listFileMangerByScriptDeployId(scriptDeployId);
        nodeVO.getCsvFileSet().removeAll(fileList.stream().filter(data -> data.getFileType() == FileTypeEnum.DATA.getCode()).map(FileManageEntity::getFileName).collect(Collectors.toList()));
        if(CollectionUtils.isNotEmpty(nodeVO.getCsvFileSet())) {
            errorList.add(String.format("csv文件缺失:%s", String.join(",", nodeVO.getCsvFileSet())));
        }
        List<String> jarList = fileList.stream().filter(data -> data.getFileType() == FileTypeEnum.DATA.getCode() && data.getFileName().endsWith(".jar")).map(FileManageEntity::getUploadPath).collect(Collectors.toList());
        //JDBC取样器
        if(nodeVO.getJdbcRequestCount() > 0) {
            for(String jdbcRequestClass : nodeVO.getJdbcRequestClass()) {
                if(!findClassFromJar(jdbcRequestClass, jarList)) {
                    errorList.add(String.format("jar包缺失@JDBC取样器:类%s找不到依赖包", jdbcRequestClass));
                }
            }
        }
        //JAVA取样器 jar包或者插件
        if(nodeVO.getJavaRequestCount() > 0) {
            for(String javaRequestClass : nodeVO.getJavaRequestClass()) {
                if(!findClassFromJar(javaRequestClass, jarList)) {
                    errorList.add(String.format("jar包缺失@Java取样器:类%s找不到依赖包", javaRequestClass));
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
                nodeVO.setJavaRequestCount(nodeVO.getJavaRequestCount() + 1);
                nodeVO.getJavaRequestClass().add(scriptNode.getProps().get("classname"));
            } else if(StringUtils.equalsAny(scriptNode.getName(), "JDBCSampler")) {
                nodeVO.setJdbcRequestCount(nodeVO.getJdbcRequestCount() + 1);
            }
            checkScriptNode(scriptNode.getChildren(), nodeVO);
        }
    }
}
