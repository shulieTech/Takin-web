package io.shulie.takin.web.biz.service.datamanage.impl;

import io.shulie.takin.adapter.api.model.request.scenemanage.ScriptAnalyzeRequest;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.JmxUtil;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.service.datamanage.CsvManageService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.mapper.mysql.ScriptCsvDataSetMapper;
import io.shulie.takin.web.data.model.mysql.ScriptCsvDataSetEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CsvManageServiceImpl implements CsvManageService {

    @Autowired
    private ScriptCsvDataSetMapper scriptCsvDataSetMapper;

    @Override
    public List<ScriptCsvDataSetEntity> transformFromJmeter(ScriptAnalyzeRequest request) {
        if (StringUtils.isBlank(request.getScriptFile())) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_ANALYZE_PARAMS_ERROR, "请提供脚本文件完整的路径和名称");
        }
        File file = new File(request.getScriptFile());
        if (!file.exists() || !file.isFile()) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_FILE_NOT_EXISTS, "请检测脚本文件是否存在");
        }
        List<ScriptNode> scriptNodes = JmxUtil.buildCsvNode(request.getScriptFile());
        if(CollectionUtils.isEmpty(scriptNodes)) {
            return Lists.newArrayList();
        }
        List<ScriptCsvDataSetEntity> tempCsvDataSets = scriptNodes.stream().map(t -> {
            ScriptCsvDataSetEntity setEntity = new ScriptCsvDataSetEntity();
            setEntity.setScriptCsvDataSetName(t.getTestName());
            Map<String, String> props = t.getProps();
            // 文件名
            Path path = Paths.get(props.getOrDefault("filename", ""));
            setEntity.setScriptCsvFileName(path.getFileName().toString());
            setEntity.setScriptCsvVariableName(props.getOrDefault("variableNames", ""));
            setEntity.setIgnoreFirstLine("true".equals(props.getOrDefault("ignoreFirstLine", "false")));
            setEntity.setCreateTime(LocalDateTime.now());
            setEntity.setUpdateTime(LocalDateTime.now());
            return setEntity;
        }).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(tempCsvDataSets)) {
            // 2.2 先校验 csv的文件名不重复
            List<ScriptCsvDataSetEntity> distinct = this.distinct(tempCsvDataSets);
            if(tempCsvDataSets.size() !=  distinct.size()) {
                throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_VALIDATE_ERROR,"jmeter脚本存在相同文件名的csv组件，请修改csv文件名！");
            }
           return distinct;
        }
        return Lists.newArrayList();
    }




    @Override
    public void save(List<ScriptCsvDataSetEntity> setEntityList, BusinessFlowParseRequest businessFlowParseRequest) {
        // 1. 获取原有的csv
        List<ScriptCsvDataSetEntity> oldCsv = scriptCsvDataSetMapper.listByBusinessFlowId(businessFlowParseRequest.getId());
        Map<String, ScriptCsvDataSetEntity> oldDataMap = oldCsv.stream().collect(Collectors.toMap(this::getIndex, t -> t));
        Set<String> indexKeys = oldDataMap.keySet();
        // 2. 对比现有的，用文件名来对比，分成3种情况
        List<ScriptCsvDataSetEntity> addList = Lists.newArrayList();
        List<ScriptCsvDataSetEntity> unChangeList = Lists.newArrayList();
        for (ScriptCsvDataSetEntity setEntity : setEntityList) {
            if (indexKeys.contains(this.getIndex(setEntity))) {
                unChangeList.add(setEntity);
                continue;
            }
            // 2.1 新增csv的，则直接添加进去
            setEntity.setBusinessFlowId(businessFlowParseRequest.getId());
            setEntity.setScriptDeployId(businessFlowParseRequest.getScriptDeployId());
            addList.add(setEntity);
        }

        //得出需要删除  // 2.2 原有的csv，但组件名修改或者变量名称修改了  将该文件历史全部删除，如果有任务，则自动取消任务
        List<String> unchangeList = unChangeList.stream().map(this::getIndex).collect(Collectors.toList());
        List<ScriptCsvDataSetEntity> deleteList = oldCsv.stream().filter(t -> !unchangeList.contains(this.getIndex(t))).collect(Collectors.toList());

        if(!CollectionUtils.isEmpty(addList)) {
            scriptCsvDataSetMapper.batchInsert(addList);

        }
        if(!CollectionUtils.isEmpty(deleteList)) {
            for(ScriptCsvDataSetEntity deleteModel : deleteList) {
                // 删除文件 todo

            }
        }

    }


    @Override
    public List<ScriptCsvDataSetEntity> listByBusinessFlowId(Long businessFlowId) {
        return scriptCsvDataSetMapper.listByBusinessFlowId(businessFlowId);
    }

    private String getIndex(ScriptCsvDataSetEntity scriptCsvDataSetEntity) {
        return scriptCsvDataSetEntity.getScriptCsvDataSetName() + "#" + scriptCsvDataSetEntity.getScriptCsvFileName() + "#" + scriptCsvDataSetEntity.getScriptCsvVariableName();
    }

    private List<ScriptCsvDataSetEntity> distinct(List<ScriptCsvDataSetEntity> setEntityList) {
        // 去重
        return setEntityList.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(
                                () -> new TreeSet<>(
                                        Comparator.comparing(ScriptCsvDataSetEntity::getScriptCsvFileName))), ArrayList::new));
    }

}
