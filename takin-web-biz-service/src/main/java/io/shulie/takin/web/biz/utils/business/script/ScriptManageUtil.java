package io.shulie.takin.web.biz.utils.business.script;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.base.Joiner;
import com.pamirs.takin.common.constant.SceneManageConstant;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.vo.scenemanage.SceneBusinessActivityRefVO;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneBusinessActivityRefOpen;
import io.shulie.takin.adapter.api.model.request.scenemanage.SceneScriptRefOpen;
import io.shulie.takin.web.biz.constant.BusinessActivityRedisKeyConstant;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.PluginConfigDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.common.constant.FeaturesConstants;
import io.shulie.takin.web.common.constant.ScriptManageConstants;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * 脚本相关业务工具类
 *
 * @author liuchuan
 * @date 2021/5/11 4:24 下午
 */
public class ScriptManageUtil {

    /**
     * 根据脚本发布实例的所有文件, 获得脚本文件列表
     *
     * @param scriptManageDeployDetail 脚本实例详情
     * @return 脚本列表
     */
    public static List<SceneScriptRefOpen> buildScriptRef(ScriptManageDeployDetailResponse scriptManageDeployDetail) {
        List<FileManageResponse> fileManageResponseList = scriptManageDeployDetail.getFileManageResponseList();
        if (CollectionUtil.isNotEmpty(scriptManageDeployDetail.getAttachmentManageResponseList())) {
            fileManageResponseList.addAll(scriptManageDeployDetail.getAttachmentManageResponseList());
        }

        if (CollectionUtil.isEmpty(fileManageResponseList)) {
            return Collections.emptyList();
        }

        return fileManageResponseList.stream().map(file -> {
            SceneScriptRefOpen ref = new SceneScriptRefOpen();
            ref.setId(file.getId());
            ref.setFileType(scriptManageDeployDetail.getType());
            ref.setFileName(file.getFileName());
            ref.setFileType(file.getFileType());
            ref.setFileSize(file.getFileSize());
            ref.setUploadPath(file.getUploadPath());
            ref.setUploadedData(file.getDataCount());
            ref.setIsSplit(file.getIsSplit());
            ref.setIsOrderSplit(file.getIsOrderSplit());

            Map<String, Object> extend = new HashMap<>(5);
            extend.put(SceneManageConstant.DATA_COUNT, file.getDataCount());
            extend.put(SceneManageConstant.IS_SPLIT, file.getIsSplit());
            extend.put(SceneManageConstant.IS_ORDER_SPLIT, file.getIsOrderSplit());
            extend.put(SceneManageConstant.IS_BIG_FILE,file.getIsBigFile());
            ref.setFileExtend(JsonUtil.bean2Json(extend));

            ref.setIsDeleted(file.getIsDeleted());
            ref.setUploadTime(DateUtils.transferDateToString(file.getUploadTime()));
            return ref;
        }).collect(Collectors.toList());
    }

    /**
     * 根据 业务活动vo列表, 构造cloud需要的业务活动配置列表
     *
     * @param businessActivityConfigVOList 业务活动vo列表
     * @return 业务活动配置列表
     */
    public static List<SceneBusinessActivityRefOpen> buildCloudBusinessActivityConfigList(
        List<SceneBusinessActivityRefVO> businessActivityConfigVOList) {
        return businessActivityConfigVOList.stream().map(data -> {
            SceneBusinessActivityRefOpen sceneBusinessActivityRefOpen = new SceneBusinessActivityRefOpen();
            sceneBusinessActivityRefOpen.setBusinessActivityId(data.getBusinessActivityId());
            sceneBusinessActivityRefOpen.setBusinessActivityName(data.getBusinessActivityName());
            sceneBusinessActivityRefOpen.setApplicationIds(data.getApplicationIds());
            sceneBusinessActivityRefOpen.setBindRef(data.getBindRef());
            sceneBusinessActivityRefOpen.setTargetRT(
                BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_DEFAULT_TARGET_RT);
            sceneBusinessActivityRefOpen.setTargetTPS(
                BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_DEFAULT_TARGET_TPS);
            sceneBusinessActivityRefOpen.setTargetSA(
                new BigDecimal(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_DEFAULT_TARGET_RT));
            sceneBusinessActivityRefOpen.setTargetSuccessRate(
                new BigDecimal(BusinessActivityRedisKeyConstant.ACTIVITY_VERIFY_DEFAULT_TARGET_RT));
            return sceneBusinessActivityRefOpen;
        }).collect(Collectors.toList());
    }

    /**
     * 根据脚本发布实例的feature, 获得插件配置列表
     *
     * @param feature 脚本发布实例扩展字段
     * @return 插件配置列表
     */
    public static List<PluginConfigDetailResponse> listPluginConfigs(String feature) {
        if (StringUtils.isBlank(feature)) {
            return Collections.emptyList();
        }

        // 反序列化 feature
        Map<String, Object> featureMap = JSONUtil.toBean(feature, JSONObject.class);
        if (CollectionUtils.isEmpty(featureMap) ||
            !featureMap.containsKey(FeaturesConstants.PLUGIN_CONFIG)) {
            return Collections.emptyList();
        }

        // 插件列表
        String pluginConfigContent = JSONUtil.toJsonStr(featureMap.get(FeaturesConstants.PLUGIN_CONFIG));
        return JSONUtil.toList(pluginConfigContent, PluginConfigDetailResponse.class);
    }

    /**
     * 脚本实例下关联业务活动类型
     *
     * @param refType 关联类型
     * @return 是否关联业务活动类型
     */
    public static boolean deployRefBusinessActivityType(String refType) {
        return ScriptManageConstants.DEPLOY_REF_TYPE_BUSINESS_ACTIVITY.equals(refType);
    }

    /**
     * 脚本实例下关联业务流程类型
     *
     * @param refType 关联类型
     * @return 是否关联业务流程类型
     */
    public static boolean deployRefBusinessFlowType(String refType) {
        return ScriptManageConstants.DEPLOY_REF_TYPE_BUSINESS_FLOW.equals(refType);
    }

}
