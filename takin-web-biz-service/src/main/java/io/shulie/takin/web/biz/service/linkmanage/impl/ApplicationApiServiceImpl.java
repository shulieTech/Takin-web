package io.shulie.takin.web.biz.service.linkmanage.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.collection.CollStreamUtil;
import cn.hutool.core.convert.Convert;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import com.pamirs.takin.entity.domain.query.ApplicationApiParam;
import com.pamirs.takin.entity.domain.vo.entracemanage.ApiCreateVo;
import com.pamirs.takin.entity.domain.vo.entracemanage.ApiUpdateVo;
import com.pamirs.takin.entity.domain.vo.entracemanage.EntranceApiVo;
import io.shulie.takin.web.biz.cache.DictionaryCache;
import io.shulie.takin.web.biz.cache.agentimpl.ApplicationApiManageAmdbCache;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.service.linkmanage.ApplicationApiService;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.vo.application.ApplicationApiManageVO;
import io.shulie.takin.web.data.dao.application.ApplicationApiDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.param.application.ApplicationApiCreateParam;
import io.shulie.takin.web.data.param.application.ApplicationApiQueryParam;
import io.shulie.takin.web.data.result.application.ApplicationApiManageResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author vernon
 * @date 2020/4/2 13:11
 */
@Slf4j
@Component
public class ApplicationApiServiceImpl implements ApplicationApiService {
    private static final String EMPTY = " ";
    private static final String HTTP_METHOD_TYPE = "HTTP_METHOD_TYPE";

    @Resource
    private ApplicationDAO applicationDAO;
    @Resource
    private ApplicationApiDAO applicationApiDAO;
    @Resource
    private ApplicationApiManageAmdbCache applicationApiManageAmdbCache;

    @Override
    public Response registerApi(Map<String, List<String>> register) {
        List<ApplicationApiCreateParam> batch = Lists.newArrayList();
        try {
            for (Map.Entry entry : register.entrySet()) {

                String appName = String.valueOf(entry.getKey());

                List<String> apis = (List<String>)entry.getValue();

                if (StringUtils.isBlank(appName) || CollectionUtils.isEmpty(apis)) {
                    continue;
                }

                ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationByTenantIdAndName(appName);
                if (applicationDetailResult == null) {
                    throw new TakinWebException(TakinWebExceptionEnum.AGENT_REGISTER_API,
                        String.format("应用不存在, 应用名称: %s", appName));
                }

                apis.forEach(api -> {

                    String[] str = api.split("#");
                    String requestMethod = str[1];
                    if ("[]".equals(requestMethod)) {
                        //
                        requestMethod = EMPTY;
                    } else {
                        requestMethod = requestMethod.substring(1, requestMethod.length() - 1);
                    }
                    api = str[0];
                    if (requestMethod.contains("||")) {
                        String[] splits = requestMethod.split("\\|\\|");
                        for (String split : splits) {
                            ApplicationApiCreateParam manage = new ApplicationApiCreateParam();
                            manage.setApi(api);
                            manage.setApplicationName(appName);
                            manage.setIsDeleted((byte)0);
                            manage.setCreateTime(new Date());
                            manage.setUpdateTime(new Date());
                            manage.setMethod(split);
                            manage.setApplicationId(applicationDetailResult.getApplicationId());
                            manage.setUserId(applicationDetailResult.getUserId());
                            manage.setIsAgentRegiste(1);
                            manage.setTenantId(WebPluginUtils.traceTenantId());
                            manage.setEnvCode(WebPluginUtils.traceEnvCode());
                            batch.add(manage);
                        }

                    } else {
                        ApplicationApiCreateParam manage = new ApplicationApiCreateParam();
                        manage.setApi(api.trim());
                        manage.setApplicationName(appName);
                        manage.setIsDeleted((byte)0);
                        manage.setCreateTime(new Date());
                        manage.setMethod(requestMethod);
                        manage.setUpdateTime(new Date());
                        manage.setApplicationId(applicationDetailResult.getApplicationId());
                        manage.setUserId(applicationDetailResult.getUserId());
                        manage.setIsAgentRegiste(1);
                        manage.setTenantId(WebPluginUtils.traceTenantId());
                        manage.setEnvCode(WebPluginUtils.traceEnvCode());
                        batch.add(manage);
                    }

                });

                // 把旧的记录删除了，新的再添加
                applicationApiDAO.deleteByAppName(appName);

                applicationApiDAO.insertBatch(batch);
            }
        } catch (Exception e) {
            batch.forEach(single -> {
                try {
                    ApplicationApiCreateParam manage = new ApplicationApiCreateParam();
                    manage.setMethod(single.getMethod());
                    manage.setApi(single.getApi());
                    manage.setApplicationName(single.getApplicationName());
                    manage.setIsDeleted(single.getIsDeleted());
                    manage.setUpdateTime(single.getUpdateTime());
                    manage.setCreateTime(single.getCreateTime());
                    manage.setApplicationId(single.getApplicationId());
                    manage.setTenantId(single.getTenantId());
                    manage.setUserId(single.getUserId());
                    manage.setIsAgentRegiste(single.getIsAgentRegiste());
                    applicationApiDAO.insertSelective(manage);
                } catch (TakinWebException e1) {
                    log.error(e1.getMessage(), e1);
                    throw new TakinWebException(e1.getEx(), e1.getMessage(), e1);
                } catch (Exception e3) {
                    log.error(e3.getMessage(), e3);
                    throw new TakinWebException(TakinWebExceptionEnum.AGENT_REGISTER_API_ERROR,
                        "agent 注册 api 异常, 联系技术人员定位", e3);
                }
            });
        }
        return Response.success();
    }

    @Override
    public Response pullApi(String appName) {
        ApplicationApiParam apiParam = new ApplicationApiParam();
        apiParam.setAppName(appName);
        List<ApplicationApiManageResult> all = applicationApiDAO.querySimple(apiParam);
        if (org.apache.commons.collections4.CollectionUtils.isEmpty(all)) {
            return Response.success(new HashMap<>());
        }
        Map<String, List<String>> res = new HashMap<>();
        for (ApplicationApiManageResult applicationApiManage : all) {
            res.computeIfAbsent(applicationApiManage.getApplicationName(), k -> new ArrayList<>()).add(
                applicationApiManage.getApi()
                    + "#" + applicationApiManage.getMethod());
        }
        return Response.success(res);
    }

    @Override
    public Map<String, List<String>> pullApiV1(String appName) {
        ApplicationApiParam apiParam = new ApplicationApiParam();
        apiParam.setAppName(appName);
        List<ApplicationApiManageResult> all = applicationApiDAO.querySimpleWithTenant(apiParam);
        if (CollectionUtils.isEmpty(all)) {
            return null;
        }
        Map<String, List<String>> res = new HashMap<>();
        for (ApplicationApiManageResult applicationApiManage : all) {
            res.computeIfAbsent(applicationApiManage.getApplicationName(), k -> new ArrayList<>()).add(
                applicationApiManage.getApi()
                    + "#" + applicationApiManage.getMethod());
        }
        return res;

    }

    @Override
    public Response delete(String id) {
        ApplicationApiManageResult result = applicationApiDAO.selectByPrimaryKey(Long.parseLong(id));
        if (result == null) {
            return Response.fail("0", "该规则不存在");
        }
        applicationApiDAO.deleteByPrimaryKey(Long.parseLong(id));
        this.reseting(result.getApplicationName());
        return Response.success();
    }

    @Override
    public Response query(EntranceApiVo vo) {
        ApplicationApiQueryParam manage = new ApplicationApiQueryParam();
        manage.setApplicationName(vo.getApplicationName());
        manage.setApi(vo.getApi());

        List<ApplicationApiManageResult> dataList = applicationApiDAO.selectBySelective(manage, WebPluginUtils.getQueryAllowUserIdList());

        dataList.sort(Comparator.comparing(ApplicationApiManageResult::getCreateTime).reversed());
        List<ApplicationApiManageResult> pageData =
            PageUtils.getPage(true, vo.getCurrentPage(), vo.getPageSize(), dataList);
        List<ApplicationApiManageVO> dtoList = new ArrayList<>();
        pageData.forEach(data -> {
            ApplicationApiManageVO dto = new ApplicationApiManageVO();
            BeanUtils.copyProperties(data, dto);
            dto.setRequestMethod(data.getMethod());
            List<Long> allowUpdateUserIdList = WebPluginUtils.getUpdateAllowUserIdList();
            if (CollectionUtils.isNotEmpty(allowUpdateUserIdList)) {
                dto.setCanEdit(allowUpdateUserIdList.contains(data.getUserId()));
            }
            List<Long> allowDeleteUserIdList = WebPluginUtils.getDeleteAllowUserIdList();
            if (CollectionUtils.isNotEmpty(allowDeleteUserIdList)) {
                dto.setCanRemove(allowDeleteUserIdList.contains(data.getUserId()));
            }
            dtoList.add(dto);
        });
        /*  PageInfo<ApplicationApiManage> pageInfo = new PageInfo<>(pageData);*/
        return Response.success(dtoList, dataList.size());

    }

    @Override
    public Response update(ApiUpdateVo vo) {
        if (Objects.isNull(vo.getId())) {
            return Response.fail("0", "主键为空");
        }
        ApplicationApiManageResult applicationApiManage = applicationApiDAO.selectByPrimaryKey(Long.parseLong(vo.getId()));
        if (null == applicationApiManage) {
            return Response.fail("0", "该规则不存在");
        }
        String applicationName = Optional.ofNullable(vo.getApplicationName()).orElse(
            applicationApiManage.getApplicationName());
        if (StringUtils.isNotBlank(vo.getApi()) && !vo.getApi().equals(applicationApiManage.getApi())) {
            OperationLogContextHolder.addVars(BizOpConstants.Vars.APPLICATION_NAME,
                applicationName + "，入口地址：" + vo.getApi());
        } else {
            OperationLogContextHolder.addVars(BizOpConstants.Vars.APPLICATION_NAME, applicationName);
        }
        ApplicationApiCreateParam manage = new ApplicationApiCreateParam();
        manage.setId(Long.parseLong(vo.getId()));
        manage.setApplicationName(vo.getApplicationName());
        manage.setApi(vo.getApi());
        manage.setMethod(
            DictionaryCache.getObjectByParam(HTTP_METHOD_TYPE, Integer.parseInt(vo.getMethod())).getLabel());
        manage.setUpdateTime(new Date());
        applicationApiDAO.updateByPrimaryKeySelective(manage);

        this.reseting(vo.getApplicationName());
        return Response.success();
    }

    @Override
    public Response create(ApiCreateVo vo) {
        ApplicationApiCreateParam createParam = new ApplicationApiCreateParam();
        //前端给的是字典中的枚举数据
        createParam.setMethod(
            DictionaryCache.getObjectByParam(HTTP_METHOD_TYPE, Integer.parseInt(vo.getMethod())).getLabel());
        createParam.setApi(vo.getApi());
        createParam.setApplicationName(vo.getApplicationName());
        createParam.setIsDeleted((byte)0);
        createParam.setUpdateTime(new Date());
        createParam.setCreateTime(new Date());
        //ApplicationDetailResult applicationDetailResult = applicationDAO.getApplicationByTenantIdAndName(
        //    vo.getApplicationName());
        ////4.8.0.4以后入口规则的所属用户跟着应用走
        //createParam.setTenantId(applicationDetailResult.getTenantId());
        //createParam.setUserId(applicationDetailResult.getUserId());
        createParam.setIsDeleted((byte)0);
        createParam.setEnvCode(WebPluginUtils.traceEnvCode());
        createParam.setTenantId(WebPluginUtils.traceTenantId());
        applicationApiDAO.insert(createParam);
        this.reseting(vo.getApplicationName());
        return Response.success();
    }

    @Override
    public Response queryDetail(String id) {
        if (Objects.isNull(id)) {
            return Response.fail("0", "id 不能为空");
        }
        ApplicationApiManageVO dto = new ApplicationApiManageVO();
        ApplicationApiManageResult manage = applicationApiDAO.selectByPrimaryKey(Long.parseLong(id));
        if (Objects.nonNull(manage)) {
            BeanUtils.copyProperties(manage, dto);
        }
        if (manage != null && StringUtils.isNotBlank(manage.getMethod())) {
            EnumResult objectByParam = DictionaryCache.getObjectByParamByLabel(HTTP_METHOD_TYPE,
                manage.getMethod());
            if (objectByParam != null) {
                dto.setRequestMethod(objectByParam.getValue());
            }
        }
        return Response.success(dto);
    }

    @Override
    public Map<Long, List<ApplicationApiManageVO>> selectListGroupByAppId() {
        List<ApplicationApiManageResult> apiManageList = applicationApiDAO.query();
        if (CollectionUtils.isEmpty(apiManageList)) {
            return new HashMap<>(0);
        }
        List<ApplicationApiManageVO> voList = apiManageList.stream()
            .filter(Objects::nonNull)
            .filter(d -> Objects.nonNull(d.getApplicationId()))
            .map(apiManage -> Convert.convert(ApplicationApiManageVO.class, apiManage))
            .collect(Collectors.toList());
        return CollStreamUtil.groupByKey(voList, ApplicationApiManageVO::getApplicationId);
    }

    /**
     * 失效
     *
     * @param applicationName
     */
    private void reseting(String applicationName) {
        applicationApiManageAmdbCache.evict(applicationName);
    }

}
