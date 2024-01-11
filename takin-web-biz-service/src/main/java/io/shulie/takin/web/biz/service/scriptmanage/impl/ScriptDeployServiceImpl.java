package io.shulie.takin.web.biz.service.scriptmanage.impl;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.FileTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.vo.ParseScriptNodeVO;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDeployService;
import io.shulie.takin.web.data.mapper.mysql.SceneMapper;
import io.shulie.takin.web.data.mapper.mysql.ScriptFileRefMapper;
import io.shulie.takin.web.data.model.mysql.FileManageEntity;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import org.apache.commons.collections4.CollectionUtils;
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

    @Override
    public List<String> checkLeakFile(Long scriptDeployId, PressureSceneEnum pressureSceneEnum) {
        List<String> errorList = new ArrayList<>();
        SceneEntity sceneEntity = null;
        if(pressureSceneEnum == PressureSceneEnum.FLOW_DEBUG) {
            sceneEntity = sceneMapper.querySceneByScriptDeployId(scriptDeployId);
        } else {
            sceneEntity = sceneMapper.selectById(scriptDeployId);
        }
        if(sceneEntity == null || sceneEntity.getScriptJmxNode() == null) {
            return errorList;
        }
        //1. 列出所有csv文件
        //2. 判断是否有JavaRequest
        List<ScriptNode> nodeList = JSON.parseArray(sceneEntity.getScriptJmxNode(), ScriptNode.class);
        ParseScriptNodeVO nodeVO = new ParseScriptNodeVO();
        checkScriptNode(nodeList, nodeVO);
        List<FileManageEntity> fileList = scriptFileRefMapper.listFileMangerByScriptDeployId(scriptDeployId);
        if(nodeVO.getJavaRequestCount() > 0 && CollectionUtils.isEmpty(fileList.stream().filter(data -> data.getFileType() == FileTypeEnum.DATA.getCode() && data.getFileName().endsWith(".jar")).collect(Collectors.toList()))) {
            errorList.add(String.format("jar包缺失:脚本中包含%s个Java请求", nodeVO.getJavaRequestCount()));
        }
        nodeVO.getCsvFileSet().removeAll(fileList.stream().filter(data -> data.getFileType() == FileTypeEnum.DATA.getCode()).map(FileManageEntity::getFileName).collect(Collectors.toList()));
        if(CollectionUtils.isNotEmpty(nodeVO.getCsvFileSet())) {
            errorList.add(String.format("csv文件缺失:%s", String.join(",", nodeVO.getCsvFileSet())));
        }
        return errorList;
    }

    private void checkScriptNode(List<ScriptNode> nodeList, ParseScriptNodeVO nodeVO) {
        if(CollectionUtils.isEmpty(nodeList)) {
            return;
        }
        for(ScriptNode scriptNode : nodeList) {
            nodeVO.getCsvFileSet().addAll(scriptNode.getCsvSet());
            if("JavaSampler".equals(scriptNode.getName())) {
                nodeVO.setJavaRequestCount(nodeVO.getJavaRequestCount() + 1);
            }
            checkScriptNode(scriptNode.getChildren(), nodeVO);
        }
    }
}
