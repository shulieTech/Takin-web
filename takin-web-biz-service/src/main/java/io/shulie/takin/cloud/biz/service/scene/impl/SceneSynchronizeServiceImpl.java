package io.shulie.takin.cloud.biz.service.scene.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneService;
import io.shulie.takin.cloud.biz.service.scene.SceneSynchronizeService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.data.mapper.mysql.SceneBusinessActivityRefMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneManageMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneSlaRefMapper;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneSlaRefEntity;
import io.shulie.takin.cloud.ext.content.enginecall.PtConfigExt;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneRequest;
import io.shulie.takin.adapter.api.model.response.scenemanage.SynchronizeRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

/**
 * 场景同步服务 - 新 - 实现
 *
 * @author 张天赐
 */
@Slf4j
@Service
public class SceneSynchronizeServiceImpl implements SceneSynchronizeService {
    @Resource
    CloudSceneService cloudSceneService;
    @Resource
    SceneManageMapper sceneManageMapper;
    @Resource
    SceneSlaRefMapper sceneSlaRefMapper;
    @Resource
    SceneBusinessActivityRefMapper sceneBusinessActivityRefMapper;

    // 事务控制

    private final ThreadLocal<String> transactionIdentifier = new ThreadLocal<>();
    @Resource
    private TransactionDefinition transactionDefinition;
    @Resource
    private PlatformTransactionManager platformTransactionManager;

    /**
     * 同步
     *
     * @param request 入参信息
     *                <ul>
     *                <li>脚本主键</li>
     *                <li>脚本解析结果</li>
     *                <li>脚本节点和业务活动对应关系</li>
     *                </ul>
     * @return 同步事务标识
     */
    @Override
    public String synchronize(SynchronizeRequest request) {
        // 事务标识
        transactionIdentifier.set(UUID.randomUUID().toString());
        // 脚本主键
        long scriptId = request.getScriptId();
        // 脚本解析结果
        final List<ScriptNode> analysisResult = request.getAnalysisResult();
        final String analysisResultString = JSONObject.toJSONString(analysisResult);
        // 业务活动所有节点
        Set<String> businessKey = request.getBusinessActivityInfo().keySet();
        // 业务活动信息 - MD5&业务活动主键 对应关系
        final Map<String, Long> businessActivityRef = businessKey.stream()
            .collect(Collectors.toMap(t -> t, t -> request.getBusinessActivityInfo().get(t).getId()));
        // 业务活动信息 - MD5&业务活动关联应用主键 对应关系
        final Map<String, List<String>> businessActivityApplicationRef = businessKey.stream()
            .collect(Collectors.toMap(t -> t, t -> request.getBusinessActivityInfo().get(t).getApplicationIdList()));
        // 脚本解析结果分组
        final Map<NodeTypeEnum, List<ScriptNode>> analysisGroupResult = new HashMap<>(4);
        groupByAnalysisResult(analysisResult, analysisGroupResult);
        // 同步模块
        {
            // 采样器节点MD5
            final Set<String> samplerMd5 = analysisGroupResult.get(NodeTypeEnum.SAMPLER).stream()
                .map(ScriptNode::getXpathMd5).collect(Collectors.toSet());
            // 线程组节点MD5
            final Set<String> threadGroupMd5 = analysisGroupResult.get(NodeTypeEnum.THREAD_GROUP).stream()
                .map(ScriptNode::getXpathMd5).collect(Collectors.toSet());
            // 校验参数是否合法
            {
                // 关联关系的所有Key       - Copy
                Set<String> businessInfoKey = new HashSet<>(request.getBusinessActivityInfo().keySet());
                // 采样器的所有xPath MD5  - Copy
                Set<String> tempSamplerMd5 = new HashSet<>(samplerMd5);
                // 合并
                tempSamplerMd5.addAll(businessInfoKey);
                // 移除
                tempSamplerMd5.removeAll(businessInfoKey);
                // 如果还有值，则代表未传入全部的参数(采样器数量>传入业务信息数量)
                if (tempSamplerMd5.size() != 0) {
                    throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_UPDATE_ERROR, "采样器与业务活动关联关系不匹配");
                }
            }
            // 遍历&计数
            AtomicInteger successNumber = new AtomicInteger();
            List<SceneManageEntity> sceneList = getSceneListByScriptId(scriptId);
            sceneList.forEach(t -> {
                boolean itemSynchronizeResult = synchronize(t.getId(),
                    threadGroupMd5, samplerMd5, businessActivityRef, businessActivityApplicationRef,
                    analysisResultString);
                if (itemSynchronizeResult) {
                    successNumber.getAndIncrement();
                }
            });
            String onceTransactionIdentifier = transactionIdentifier.get();
            transactionIdentifier.remove();
            log.info("同步场景信息:{},同步结果:{}/{}.", onceTransactionIdentifier, successNumber, sceneList.size());
            return onceTransactionIdentifier;
        }

    }

    /**
     * 同步某个场景
     *
     * @param sceneId                        场景主键
     * @param threadGroupMd5                 线程组MD5集合
     * @param samplerMd5                     采样器MD5集合
     * @param businessActivityRef            业务活动信息 - MD5&业务活动主键 对应关系
     * @param businessActivityApplicationRef 业务活动信息 - MD5&业务活动关联应用主键 对应关系
     * @param analysisResultString           脚本解析结果
     * @return 同步是否成功
     * <p>失败会在拓展字段中标识场景不可压测，成功会更新场景以及关联数据</p>
     */
    private boolean synchronize(long sceneId,
        Set<String> threadGroupMd5, Set<String> samplerMd5,
        Map<String, Long> businessActivityRef, Map<String, List<String>> businessActivityApplicationRef,
        String analysisResultString) {
        // 手动事务控制
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        try {
            // 同步线程组配置
            if (synchronizeThreadGroupConfig(sceneId, threadGroupMd5)) {
                // 同步场景节点
                if (synchronizeSceneNode(sceneId, samplerMd5, businessActivityRef, businessActivityApplicationRef)) {
                    // 更新脚本解析结果
                    if (synchronizeAnalysisResult(sceneId, analysisResultString)) {
                        // 在拓展字段中标识场景不可压测
                        setSceneDisabledInFeature(sceneId, false);
                        platformTransactionManager.commit(transactionStatus);
                        return true;
                    }
                }
            }
            // 上述模块全部同步成功后才提交数据，否则回滚数据
            platformTransactionManager.rollback(transactionStatus);
            // 在拓展字段中标识场景不可压测
            setSceneDisabledInFeature(sceneId, true);
            return false;
        } catch (Exception e) {
            // 发生异常则回滚数据
            platformTransactionManager.rollback(transactionStatus);
            // 在拓展字段中标识场景不可压测
            setSceneDisabledInFeature(sceneId, true);
            return false;
        }
    }

    /**
     * 同步压测场景节点
     *
     * @param sceneId                        场景主键
     * @param samplerNode                    采样器节点
     * @param businessActivityRef            业务活动信息 - MD5&业务活动主键 对应关系
     * @param businessActivityApplicationRef 业务活动信息 - MD5&业务活动关联应用主键 对应关系
     * @return 匹配结果
     */
    private boolean synchronizeSceneNode(long sceneId, Set<String> samplerNode,
        Map<String, Long> businessActivityRef, Map<String, List<String>> businessActivityApplicationRef) {
        // 同步场景节点、压测目标
        boolean goalSynchronizeResult = synchronizeGoal(sceneId, new HashSet<>(samplerNode), businessActivityRef, businessActivityApplicationRef);
        log.info("同步节点、压测目标.事务{}.场景主键:{}.{}.", transactionIdentifier.get(), sceneId, goalSynchronizeResult);
        if (!goalSynchronizeResult) {return false;}
        // 同步SLA
        boolean monitoringGoalSynchronizeResult = synchronizeMonitoringGoal(sceneId, new HashSet<>(samplerNode));
        log.info("同步SLA.事务{}.场景主键:{}.{}.", transactionIdentifier.get(), sceneId, monitoringGoalSynchronizeResult);
        // 返回结果
        return monitoringGoalSynchronizeResult;
    }

    /**
     * 匹配压测目标及场景节点
     *
     * @param sceneId                        场景主键
     * @param allNodeMd5                     全部节点的MD5值
     * @param businessActivityRef            业务活动信息 - MD5&业务活动主键 对应关系
     * @param businessActivityApplicationRef 业务活动信息 - MD5&业务活动关联应用主键 对应关系
     * @return 匹配结果
     */
    private boolean synchronizeGoal(long sceneId, Set<String> allNodeMd5,
        Map<String, Long> businessActivityRef, Map<String, List<String>> businessActivityApplicationRef) {
        // 开始匹配
        //  1. 获取历史数据
        Map<String, SceneRequest.Goal> sceneGoal = cloudSceneService.getGoal(sceneId);
        Map<String, SceneRequest.Content> sceneContent = cloudSceneService.getContent(sceneId);
        // 2.组装历史的节点信息
        Set<String> currentNodeMd5 = new HashSet<>(sceneGoal.keySet());
        currentNodeMd5.addAll(sceneContent.keySet());
        // 拷贝临时变量
        Set<String> allNodeMd5Copy = new HashSet<>(allNodeMd5);
        // 3. 判断是否是可以同步
        //      新配置少于或等于旧配置数量，就可以同步
        currentNodeMd5.forEach(allNodeMd5Copy::remove);
        if (allNodeMd5Copy.size() > 0) {
            log.info("事务:{}.场景{}.同步失败.压测目标匹配失败.", transactionIdentifier.get(), sceneId);
            return false;
        }
        // 开始同步
        //  1. 删除本次未匹配上的
        new HashSet<>(sceneGoal.keySet()).forEach(t -> {
            if (!allNodeMd5.contains(t)) {
                sceneGoal.remove(t);
                sceneContent.remove(t);
            }
        });
        //  2. 更新节点匹配的应用信息
        sceneContent.forEach((k, v) -> {
            v.setBusinessActivityId(businessActivityRef.get(k));
            v.setApplicationId(businessActivityApplicationRef.get(k));
        });
        //  3. 更新配置信息
        List<SceneRequest.Content> contentList = new ArrayList<>(sceneContent.size());
        sceneContent.forEach((k, v) -> {
            v.setPathMd5(k);
            contentList.add(v);
        });
        // 重新填充数据
        int activityClearRows = sceneBusinessActivityRefMapper.delete(Wrappers.lambdaUpdate(SceneBusinessActivityRefEntity.class)
            .eq(SceneBusinessActivityRefEntity::getSceneId, sceneId));
        log.info("事务:{}.场景{}.更新管理业务活动信息。\n清理业务活动数据:{}。", transactionIdentifier.get(), sceneId, activityClearRows);
        cloudSceneService.buildBusinessActivity(sceneId, contentList, sceneGoal);
        return true;
    }

    /**
     * 匹配SLA
     *
     * @param sceneId    场景主键
     * @param allNodeMd5 全部节点的MD5值
     * @return 匹配结果
     */
    private boolean synchronizeMonitoringGoal(long sceneId, HashSet<String> allNodeMd5) {
        // 兼容[全部]选项
        {
            allNodeMd5.add("all");
            allNodeMd5.add("0f1a197a2040e645dcdb4dfff8a3f960");
        }
        Map<Long, List<String>> readyUpdateSla = new HashMap<>(allNodeMd5.size());
        List<SceneRequest.MonitoringGoal> monitoringGoal = cloudSceneService.getMonitoringGoal(sceneId);
        // 遍历SLA
        for (SceneRequest.MonitoringGoal goal : monitoringGoal) {
            HashSet<String> itemTarget = new HashSet<>(goal.getTarget());
            List<String> newItemTarget = new ArrayList<>(itemTarget.size());
            // 遍历目标
            for (String target : itemTarget) {
                // 筛选匹配
                if (allNodeMd5.contains(target)) {newItemTarget.add(target);}
            }
            // 填充入待更新项
            if (itemTarget.size() != newItemTarget.size()) {readyUpdateSla.put(goal.getId(), newItemTarget);}
        }
        readyUpdateSla.forEach((k, v) -> {
            // 如果没有匹配的目标，则删除SLA
            if (v.size() == 0) {sceneSlaRefMapper.deleteById(k);}
            // 否则更新目标值
            sceneSlaRefMapper.updateById(new SceneSlaRefEntity() {{
                setSceneId(k);
                setBusinessActivityIds(String.join(",", v));
            }});
        });
        return true;
    }

    /**
     * 更新脚本解析结果
     *
     * @param sceneId              场景主键
     * @param analysisResultString 脚本解析结果
     * @return 更新结果
     */
    private boolean synchronizeAnalysisResult(long sceneId, String analysisResultString) {
        return sceneManageMapper.updateById(new SceneManageEntity() {{
            setId(sceneId);
            setScriptAnalysisResult(analysisResultString);
        }}) == 1;
    }

    /**
     * 在拓展字段中设置场景是否要禁止发起压测
     *
     * @param sceneId  场景主键
     * @param disabled 是否禁止
     */
    private void setSceneDisabledInFeature(long sceneId, boolean disabled) {
        String disabledKey = "DISABLED";
        // 获取旧的features字段并解析为Map
        SceneManageEntity scene = cloudSceneService.getScene(sceneId);
        String featureString = scene.getFeatures();
        Map<String, Object> feature = JSONObject.parseObject(featureString, new TypeReference<Map<String, Object>>() {});
        // 按需填充（如果需要更新数据库，则补充这个变量，会根据这个变量去判断是否更新数据库）
        final String needUpdateFeature;
        // 需要禁用则增加字段
        if (disabled && !feature.containsKey(disabledKey)) {
            feature.put(disabledKey, "");
            needUpdateFeature = JSONObject.toJSONString(feature);
        }
        // 选用启用则移除字段
        else if (!disabled && feature.containsKey(disabledKey)) {
            feature.remove(disabledKey);
            needUpdateFeature = JSONObject.toJSONString(feature);
        }
        // 其它情况不更新
        else {needUpdateFeature = null;}
        // 如果需要更新数据库
        if (StrUtil.isNotBlank(needUpdateFeature)) {
            sceneManageMapper.updateById(new SceneManageEntity() {{
                setId(sceneId);
                setFeatures(needUpdateFeature);
            }});
        }
    }

    /**
     * 同步线程组施压配置
     *
     * @param sceneId        场景主键
     * @param threadGroupMd5 新的线程组
     * @return 同步结果
     */
    private boolean synchronizeThreadGroupConfig(long sceneId, Set<String> threadGroupMd5) {
        SceneManageEntity scene = sceneManageMapper.selectById(sceneId);
        String ptConfigString = scene.getPtConfig();
        if (StrUtil.isBlank(ptConfigString)) {return false;}
        try {
            // 反序列化施压配置信息
            PtConfigExt ptConfig = JSONObject.parseObject(ptConfigString, new TypeReference<PtConfigExt>() {});
            // 获取线程组配置
            Map<String, ThreadGroupConfigExt> threadGroupConfigMap = ptConfig.getThreadGroupConfigMap();
            // 备份新参数
            Set<String> threadGroupMd5Copy = new HashSet<>(threadGroupMd5);
            // 判断是否是可以同步
            //      新配置少于或等于旧配置数量，就可以同步
            threadGroupConfigMap.keySet().forEach(threadGroupMd5Copy::remove);
            if (threadGroupMd5Copy.size() > 0) {
                log.info("同步线程组.事务:{}.场景主键:{}.同步失败.线程组施压配置匹配失败", transactionIdentifier.get(), sceneId);
                return false;
            }
            // 开始同步
            //  1. 删除本次未匹配上的
            threadGroupMd5.forEach(t -> {
                if (!threadGroupConfigMap.containsKey(t)) {threadGroupConfigMap.remove(t);}
            });
            //  2. 更新配置信息
            sceneManageMapper.updateById(new SceneManageEntity() {{
                setId(sceneId);
                setPtConfig(JSONObject.toJSONString(ptConfig));
            }});
            log.info("同步线程组.事务{}.场景主键:{}.成功.", transactionIdentifier.get(), sceneId);
            return true;
        } catch (Exception e) {
            log.error("同步线程组.事务{}.场景主键:{}.同步线程组施压配置失败.", transactionIdentifier.get(), sceneId, e);
            return false;
        }
    }

    /**
     * 对脚本解析结果进行分组
     *
     * @param analysisResult 脚本解析结果
     * @param container      分组容器  &lt;节点类型,对应类型的节点集合&gt;
     */
    private void groupByAnalysisResult(List<ScriptNode> analysisResult, Map<NodeTypeEnum, List<ScriptNode>> container) {
        analysisResult.forEach(t -> {
            // 根据类型分类
            NodeTypeEnum groupKey = t.getType();
            // 类别初始化
            if (!container.containsKey(groupKey)) {container.put(groupKey, new LinkedList<>());}
            // 数据入桶
            container.get(groupKey).add(t);
            if (t.getChildren() != null) {groupByAnalysisResult(t.getChildren(), container);}
        });
    }

    /**
     * 根据脚本主键获取压测场景集合
     *
     * @param scriptId 脚本主键
     * @return 压测场景集合
     */
    private List<SceneManageEntity> getSceneListByScriptId(long scriptId) {
        // 从数据库获取全部的场景
        // TODO 租户隔离
        List<SceneManageEntity> sceneList = sceneManageMapper.selectList(Wrappers.lambdaQuery(SceneManageEntity.class)
            .eq(SceneManageEntity::getIsDeleted, false)
            .isNotNull(SceneManageEntity::getScriptAnalysisResult)
            .ne(SceneManageEntity::getScriptAnalysisResult, ""));
        // 根据脚本主键过滤
        return sceneList.stream().filter(t -> {
            String featureString = t.getFeatures();
            if (StrUtil.isBlank(featureString)) {return false;}
            try {
                Map<String, Object> features = JSONObject.parseObject(featureString, new TypeReference<Map<String, Object>>() {});
                if (features.containsKey("scriptId") && features.get("scriptId") != null) {
                    return String.valueOf(scriptId).equals(features.get("scriptId").toString());
                } else {return false;}
            } catch (Exception e) {
                return false;
            }
        }).collect(Collectors.toList());
    }

}
