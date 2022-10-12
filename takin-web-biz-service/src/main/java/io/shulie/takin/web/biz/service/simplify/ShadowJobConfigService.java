package io.shulie.takin.web.biz.service.simplify;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.common.constant.JobEnum;
import com.pamirs.takin.entity.dao.simplify.TShadowJobConfigMapper;
import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import com.pamirs.takin.entity.domain.query.ShadowJobConfigQuery;
import com.pamirs.takin.entity.domain.vo.ShadowJobConfigVo;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.init.sync.ConfigSyncService;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.utils.Estimate;
import io.shulie.takin.web.biz.utils.XmlUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.ApplicationShadowJobDAO;
import io.shulie.takin.web.data.param.application.ShadowJobCreateParam;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 影子JOB配置
 *
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @package: com.pamirs.takin.web.api.service.simplify
 * @date 2020-03-17 15:44
 */
@Slf4j
@Service
public class ShadowJobConfigService {

    @Resource
    private TShadowJobConfigMapper tShadowJobConfigMapper;
    @Resource
    private ApplicationDAO applicationDAO;

    @Autowired
    private ConfigSyncService configSyncService;
    @Autowired
    private ApplicationShadowJobDAO applicationShadowJobDAO;
    @Autowired
    private AgentConfigCacheManager agentConfigCacheManager;
    @Autowired
    private ApplicationService applicationService;

    public static void main(String[] args) throws DocumentException {
        String text = "<xml>\n" +
            "    <className>com.pradar.xx.TestJob</className>\n" +
            "    <cron>0 * * * * *</cron>\n" +
            "    <jobType>elastic-job</jobType>\n" +
            "    <jobDataType>simple</jobDataType>\n" +
            "    <listener>com.pradar.listener.TestListener</listener>\n" +
            "</xml>";
        Map<String, String> stringStringMap = XmlUtil.readStringXml(text);
        System.out.println(stringStringMap);
    }

    public Response insert(TShadowJobConfig tShadowJobConfig) throws DocumentException {
        Map<String, String> xmlMap = XmlUtil.readStringXml(tShadowJobConfig.getConfigCode());
        String className = xmlMap.get("className");
        String jobType = xmlMap.get("jobType");
        Estimate.notBlank(className, "className不能为空");
        Estimate.notBlank(jobType, "job类型不能为空");

        tShadowJobConfig.setName(className);
        tShadowJobConfig.setType(JobEnum.getJobByText(jobType).ordinal());
        if (null != tShadowJobConfig.getId()) {
            return Response.fail("ID必须为空");
        }
        ShadowJobCreateParam shadowJobCreateParam = new ShadowJobCreateParam();
        BeanUtils.copyProperties(tShadowJobConfig, shadowJobCreateParam);
        WebPluginUtils.fillUserData(shadowJobCreateParam);
        // 重复判断
        if (applicationShadowJobDAO.exist(shadowJobCreateParam)) {
            return Response.fail(shadowJobCreateParam.getName() + ",类型为"+
                JobEnum.getJobByIndex(shadowJobCreateParam.getType()).getText() + "已存在");
        }

        applicationShadowJobDAO.insert(shadowJobCreateParam);
        configSyncService.syncShadowJob(WebPluginUtils.traceTenantCommonExt(), tShadowJobConfig.getApplicationId(),
            null);
        ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(tShadowJobConfig.getApplicationId());

        if (null == tApplicationMnt) {
            return Response.fail("未查询到相关应用信息");
        }
        agentConfigCacheManager.evictShadowJobs(tApplicationMnt.getApplicationName());
        return Response.success();
    }

    public Response queryDetail(Long id) {
        ShadowJobConfigQuery query = new ShadowJobConfigQuery();
        query.setId(id);
        query.setTenantId(WebPluginUtils.traceTenantId());
        query.setEnvCode(WebPluginUtils.traceEnvCode());
        TShadowJobConfig shadowJobConfig = tShadowJobConfigMapper.selectOneById(query);
        if (shadowJobConfig == null) {
            return Response.fail("未查询到相关数据");
        }
        ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(shadowJobConfig.getApplicationId());

        if (null == tApplicationMnt) {
            return Response.fail("未查询到相关应用信息");
        }
        return Response.success(new ShadowJobConfigVo(shadowJobConfig, tApplicationMnt.getApplicationName()));
    }

    public Response update(ShadowJobConfigQuery query) throws DocumentException {
        TShadowJobConfig updateShadowJobConfig = new TShadowJobConfig();
        TShadowJobConfig shadowJobConfig = tShadowJobConfigMapper.selectOneById(query);
        if (null == shadowJobConfig) {
            return Response.fail("根据ID未查询到相关数据");
        }
        if (StringUtils.isNotBlank(query.getConfigCode())) {

            if (null != query.getConfigCode() && !query.getConfigCode().equals(shadowJobConfig.getConfigCode())) {
                updateShadowJobConfig.setConfigCode(query.getConfigCode());
                Map<String, String> xmlMap = XmlUtil.readStringXml(query.getConfigCode());
                String className = xmlMap.get("className");
                String jobType = xmlMap.get("jobType");
                JobEnum jobText = JobEnum.getJobByText(jobType);

                //是否有重复
                // 重复判断
                ShadowJobCreateParam shadowJobCreateParam = new ShadowJobCreateParam();
                shadowJobCreateParam.setName(className);
                shadowJobCreateParam.setApplicationId(shadowJobConfig.getApplicationId());
                shadowJobCreateParam.setType(jobText.ordinal());
                shadowJobCreateParam.setId(shadowJobConfig.getId());
                if (applicationShadowJobDAO.exist(shadowJobCreateParam)) {
                    return Response.fail(shadowJobCreateParam.getName() + ",类型为"+
                        JobEnum.getJobByIndex(shadowJobCreateParam.getType()).getText() + "已存在");
                }

                if (StringUtils.isNotBlank(className) && !className.equals(shadowJobConfig.getName())) {
                    updateShadowJobConfig.setName(className);
                }

                if (null != jobText && !Integer.valueOf(jobText.ordinal()).equals(shadowJobConfig.getType())) {
                    updateShadowJobConfig.setType(jobText.ordinal());
                }
            }
        }
        updateShadowJobConfig.setId(query.getId());
        if (null != query.getStatus() && !query.getStatus().equals(shadowJobConfig.getStatus())) {
            updateShadowJobConfig.setStatus(query.getStatus());
        }
        if (null != query.getActive() && !query.getActive().equals(shadowJobConfig.getActive())) {
            updateShadowJobConfig.setActive(query.getActive());
        }

        if (StringUtils.isNotBlank(updateShadowJobConfig.getConfigCode()) || null != query.getStatus()
            || null != query.getActive()) {
            tShadowJobConfigMapper.update(updateShadowJobConfig);
        }
        // 仅仅更新备注字段
        if (query.getRemark() != null) {
            TShadowJobConfig updateRemark = new TShadowJobConfig();
            updateRemark.setId(query.getId());
            updateRemark.setRemark(query.getRemark());
            tShadowJobConfigMapper.update(updateRemark);
        }
        ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(shadowJobConfig.getApplicationId());

        if (null == tApplicationMnt) {
            return Response.fail("未查询到相关应用信息");
        }
        configSyncService.syncShadowJob(WebPluginUtils.traceTenantCommonExt(), shadowJobConfig.getApplicationId(),
            null);
        agentConfigCacheManager.evictShadowJobs(tApplicationMnt.getApplicationName());
        return Response.success();
    }

    public Response delete(Long id) {
        ShadowJobConfigQuery query = new ShadowJobConfigQuery();
        query.setId(id);
        query.setTenantId(WebPluginUtils.traceTenantId());
        query.setEnvCode(WebPluginUtils.traceEnvCode());
        TShadowJobConfig shadowJobConfig = tShadowJobConfigMapper.selectOneById(query);
        if (null == shadowJobConfig) {
            return Response.fail("根据ID未查询到相关信息");
        }
        ApplicationDetailResult tApplicationMnt = applicationDAO.getApplicationById(shadowJobConfig.getApplicationId());
        ;
        if (null == tApplicationMnt) {
            return Response.fail("未查询到相关应用信息");
        }
        tShadowJobConfigMapper.delete(id);
        configSyncService.syncShadowJob(WebPluginUtils.traceTenantCommonExt(), shadowJobConfig.getApplicationId(),
            null);
        agentConfigCacheManager.evictShadowJobs(tApplicationMnt.getApplicationName());
        return Response.success();
    }

    public Response queryByPage(ShadowJobConfigQuery query) {
        //PageHelper.startPage(query.getPageNum(), query.getPageSize());
        if (StringUtils.isBlank(query.getOrderBy())) {
            query.setOrderBy("id desc");
        }
        List<TShadowJobConfig> tShadowJobConfigs = tShadowJobConfigMapper.selectList(query);
        PageInfo<TShadowJobConfig> pageInfo = new PageInfo<>(tShadowJobConfigs);
        List<ShadowJobConfigVo> configVos = new ArrayList<>();
        pageInfo.getList().forEach(tShadowJobConfig -> {
            ShadowJobConfigVo vo = new ShadowJobConfigVo();
            vo.setId(String.valueOf(tShadowJobConfig.getId()));
            vo.setApplicationId(String.valueOf(tShadowJobConfig.getApplicationId()));
            vo.setName(tShadowJobConfig.getName());
            vo.setType(tShadowJobConfig.getType());
            vo.setStatus(tShadowJobConfig.getStatus());
            vo.setConfigCode(tShadowJobConfig.getConfigCode());
            vo.setTypeName(JobEnum.getJobByIndex(tShadowJobConfig.getType()).getText());
            vo.setActive(tShadowJobConfig.getActive());
            vo.setActive(tShadowJobConfig.getActive());
            vo.setUpdateTime(tShadowJobConfig.getUpdateTime());
            vo.setRemark(tShadowJobConfig.getRemark());

            vo.setUserId(tShadowJobConfig.getUserId());
            WebPluginUtils.fillQueryResponse(vo);
            configVos.add(vo);
        });
        return Response.success(configVos, pageInfo.getTotal());
    }

    public List<TShadowJobConfig> queryByAppName(String appName) {
        Estimate.notBlank(appName, "应用名称不能为空");
        ApplicationDetailResult tApplicationMnt = applicationDAO.getByName(appName);
        if (null == tApplicationMnt) {
            log.warn("未查询到应用相关数据 appName:{}", appName);
            return new ArrayList<>();
        }
        ShadowJobConfigQuery query = new ShadowJobConfigQuery();
        if (StringUtils.isBlank(query.getOrderBy())) {
            query.setOrderBy("id desc");
        }
        query.setApplicationId(tApplicationMnt.getApplicationId());
        List<TShadowJobConfig> tShadowJobConfigs = tShadowJobConfigMapper.selectList(query);
        try {
            for (TShadowJobConfig tShadowJobConfig : tShadowJobConfigs) {
                Map<String, String> stringStringMap = XmlUtil.readStringXml(tShadowJobConfig.getConfigCode());
                tShadowJobConfig.setConfigCode(JSONObject.toJSONString(stringStringMap));
            }
        } catch (Exception e) {
            throw new RuntimeException("dom 解析发生异常");
        }
        return tShadowJobConfigs;
    }

    public List<TShadowJobConfig> getAllEnableShadowJobs(long applicationId) {
        return tShadowJobConfigMapper.getAllEnableShadowJobs(applicationId);
    }
}
