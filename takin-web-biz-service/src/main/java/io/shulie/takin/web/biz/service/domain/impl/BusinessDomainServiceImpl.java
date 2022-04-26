package io.shulie.takin.web.biz.service.domain.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.dao.dict.TDictionaryDataMapper;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.takin.cloud.common.utils.redis.RedisClientUtils;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainCreateRequest;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainQueryRequest;
import io.shulie.takin.web.biz.pojo.request.domain.BusinessDomainUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.domain.BusinessDomainListResponse;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.domain.BusinessDomainService;
import io.shulie.takin.web.common.constant.BusinessDomainConstant;
import io.shulie.takin.web.common.enums.domain.DomainType;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.domain.BusinessDomainDAO;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.data.model.mysql.BusinessDomainEntity;
import io.shulie.takin.web.data.model.mysql.BusinessLinkManageTableEntity;
import io.shulie.takin.web.data.param.domain.BusinessDomainCreateParam;
import io.shulie.takin.web.data.param.domain.BusinessDomainDeleteParam;
import io.shulie.takin.web.data.param.domain.BusinessDomainQueryParam;
import io.shulie.takin.web.data.param.domain.BusinessDomainUpdateParam;
import io.shulie.takin.web.data.result.BusinessDomainDetailResult;
import io.shulie.takin.web.data.result.BusinessDomainListResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 业务域表(BusinessDomain)service
 *
 * @author liuchuan
 * @date 2021-12-07 16:06:57
 */
@Service
public class BusinessDomainServiceImpl implements BusinessDomainService {
    private static final Logger log = LoggerFactory.getLogger(BusinessDomainServiceImpl.class);

    @Autowired
    private BusinessDomainDAO businessDomainDAO;

    @Autowired
    private TDictionaryDataMapper tDictionaryDataMapper;

    @Autowired
    private BusinessLinkManageDAO businessLinkManageDAO;

    @Autowired
    private RedisClientUtils redisClientUtils;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public void create(BusinessDomainCreateRequest createRequest) {
        this.preCheckBeforeCreate(createRequest);
        BusinessDomainCreateParam createParam = new BusinessDomainCreateParam();
        setDomainCode(createParam);
        createParam.setName(createRequest.getName());
        createParam.setType(DomainType.USER_DEFINE.getType());
        businessDomainDAO.insert(createParam);
    }

    @Override
    public void update(BusinessDomainUpdateRequest updateRequest) {
        this.preCheckBeforeUpdate(updateRequest);
        BusinessDomainUpdateParam updateParam = new BusinessDomainUpdateParam();
        updateParam.setId(updateRequest.getId());
        updateParam.setName(updateRequest.getName());
        businessDomainDAO.update(updateParam);
    }

    @Override
    public void delete(BusinessDomainDeleteRequest deleteRequest) {
        this.preCheckBeforeDelete(deleteRequest.getIds());
        BusinessDomainDeleteParam deleteParam = new BusinessDomainDeleteParam();
        deleteParam.setIds(deleteRequest.getIds());
        businessDomainDAO.delete(deleteParam);
    }

    @Override
    public void deleteById(Long id) {
        this.preCheckBeforeDelete(Collections.singletonList(id));
        BusinessDomainDeleteParam deleteParam = new BusinessDomainDeleteParam();
        deleteParam.setIds(Collections.singletonList(id));
        businessDomainDAO.delete(deleteParam);
    }

    @Override
    public PagingList<BusinessDomainListResponse> list(BusinessDomainQueryRequest queryRequest) {
        this.initBaseBusinessDomainIfNeeded();
        BusinessDomainQueryParam queryParam = new BusinessDomainQueryParam();
        queryParam.setCurrent(queryRequest.getCurrent());
        queryParam.setPageSize(queryRequest.getPageSize());
        PagingList<BusinessDomainListResult> pagingList = businessDomainDAO.selectPage(queryParam);
        if (pagingList.isEmpty()) {
            PagingList.of(Lists.newArrayList(), pagingList.getTotal());
        }
        List<BusinessDomainListResponse> responses = pagingList.getList().stream().map(result -> {
            BusinessDomainListResponse response = new BusinessDomainListResponse();
            BeanUtils.copyProperties(result, response);
            return response;
        }).collect(Collectors.toList());
        return PagingList.of(responses, pagingList.getTotal());
    }

    @Override
    public List<BusinessDomainListResponse> listNoPage() {
        this.initBaseBusinessDomainIfNeeded();
        List<BusinessDomainListResult> results = businessDomainDAO.selectList(new BusinessDomainQueryParam());
        if (CollectionUtils.isEmpty(results)) {
            return Collections.emptyList();
        }
        return results.stream().sorted(Comparator.comparing(BusinessDomainEntity::getDomainOrder)).map(result -> {
            BusinessDomainListResponse response = new BusinessDomainListResponse();
            BeanUtils.copyProperties(result, response);
            return response;
        }).collect(Collectors.toList());
    }

    private void preCheckBeforeCreate(BusinessDomainCreateRequest createRequest) {
        if (this.isExistName(createRequest.getName())) {
            throw new TakinWebException(TakinWebExceptionEnum.BUSINESS_DOMAIN_ADD_ERROR, "业务域名称已存在");
        }
    }

    private void preCheckBeforeUpdate(BusinessDomainUpdateRequest updateRequest) {
        BusinessDomainDetailResult detailResult = businessDomainDAO.selectById(updateRequest.getId());
        if (Objects.isNull(detailResult)) {
            throw new TakinWebException(TakinWebExceptionEnum.BUSINESS_DOMAIN_UPDATE_ERROR, "业务域不存在");
        }
        if (DomainType.DEFAULT.getType() == detailResult.getType()) {
            throw new TakinWebException(TakinWebExceptionEnum.BUSINESS_DOMAIN_UPDATE_ERROR, "系统业务域禁止编辑");
        }
        if (StringUtils.isNotBlank(updateRequest.getName()) && !updateRequest.getName().equals(
            detailResult.getName())) {
            if (this.isExistName(updateRequest.getName())) {
                throw new TakinWebException(TakinWebExceptionEnum.BUSINESS_DOMAIN_UPDATE_ERROR, "业务域名称已存在");
            }
        }
    }

    private void preCheckBeforeDelete(List<Long> ids) {
        BusinessDomainQueryParam queryParam = new BusinessDomainQueryParam();
        queryParam.setIds(ids);
        List<BusinessDomainListResult> results = businessDomainDAO.selectList(queryParam);
        if (CollectionUtils.isEmpty(results)) {
            throw new TakinWebException(TakinWebExceptionEnum.BUSINESS_DOMAIN_DELETE_ERROR, "业务域不存在");
        }
        long systemBusinessDomainCount = results.stream().filter(
            result -> DomainType.DEFAULT.getType() == result.getType()).count();
        if (systemBusinessDomainCount > 0) {
            throw new TakinWebException(TakinWebExceptionEnum.BUSINESS_DOMAIN_DELETE_ERROR, "系统业务域禁止删除");
        }
        List<String> codes = results.stream().map(BusinessDomainEntity::getDomainCode).map(String::valueOf).collect(
            Collectors.toList());
        List<BusinessLinkManageTableEntity> activities = businessLinkManageDAO.listByBusinessDomain(codes);
        if (CollectionUtils.isNotEmpty(activities)) {
            throw new TakinWebException(TakinWebExceptionEnum.BUSINESS_DOMAIN_DELETE_ERROR, "业务域已被业务活动引用，删除失败");
        }
    }

    private boolean isExistName(String name) {
        BusinessDomainQueryParam queryParam = new BusinessDomainQueryParam();
        queryParam.setName(name);
        List<BusinessDomainListResult> domainListResults = businessDomainDAO.selectList(queryParam);
        if (CollectionUtils.isNotEmpty(domainListResults)) {
            return true;
        }
        return false;
    }

    private void setDomainCode(BusinessDomainCreateParam createParam) {
        int maxCode = businessDomainDAO.selectMaxDomainCode();
        if (maxCode == 0) {
            //表中业务域数据为空，设置默认的业务域编码100，0-99为系统定义，100+为用户自定义
            createParam.setDomainCode(100);
        } else {
            int currentCode = ++maxCode;
            createParam.setDomainCode(currentCode);
        }
    }

    private void initBaseBusinessDomainIfNeeded() {
        List<TDictionaryVo> voList = getDefaultDomain();
        if (CollectionUtils.isEmpty(voList)) {
            return;
        }
        if (!isExistDefaultDomain(voList)) {
            doInit(voList);
        }
    }

    private boolean isExistDefaultDomain(List<TDictionaryVo> voList) {
        String tenantSuffix = WebPluginUtils.traceTenantId() + ":" + WebPluginUtils.traceEnvCode();
        // 先查缓存
        String initFlag = redisClientUtils.getString(
            BusinessDomainConstant.BUSINESS_DOMAIN_INIT_FLAG_KEY + tenantSuffix);
        if (StringUtils.isNotBlank(initFlag)) {
            return true;
        }
        String lockKey = tenantSuffix + BusinessDomainConstant.BUSINESS_DOMAIN_INIT_LOCK_KEY;
        if (distributedLock.checkLock(lockKey)) {
            // 有锁，说明已经有web节点在初始化
            return true;
        }
        // 锁60秒后自动失效
        boolean tryLock = distributedLock.tryLock(lockKey, 0L, 60L, TimeUnit.SECONDS);
        if (!tryLock) {
            // 没有获取到锁，说明已经有web节点在初始化
            return true;
        }
        // 再查数据库
        BusinessDomainQueryParam queryParam = new BusinessDomainQueryParam();
        queryParam.setDomainCodes(voList.stream().map(TDictionaryVo::getValueCode).map(Integer::parseInt).collect(Collectors.toList()));
        List<BusinessDomainListResult> results = businessDomainDAO.selectList(queryParam);
        if (CollectionUtils.isNotEmpty(results)) {
            redisClientUtils.setString(BusinessDomainConstant.BUSINESS_DOMAIN_INIT_FLAG_KEY + tenantSuffix,
                "INITIALIZED");
            return true;
        }
        return false;
    }

    private void doInit(List<TDictionaryVo> voList) {
        voList.stream().sorted(Comparator.comparing(TDictionaryVo::getValueOrder)).forEach(tDictionaryVo -> {
            BusinessDomainCreateParam createParam = new BusinessDomainCreateParam();
            createParam.setName(tDictionaryVo.getValueName());
            createParam.setType(DomainType.DEFAULT.getType());
            createParam.setDomainOrder(Integer.parseInt(tDictionaryVo.getValueOrder()));
            createParam.setDomainCode(Integer.parseInt(tDictionaryVo.getValueCode()));
            businessDomainDAO.insert(createParam);
        });
        // 设置初始化标志为：已初始化
        String tenantSuffix = WebPluginUtils.traceTenantId() + ":" + WebPluginUtils.traceEnvCode();
        redisClientUtils.setString(BusinessDomainConstant.BUSINESS_DOMAIN_INIT_FLAG_KEY + tenantSuffix,
            "INITIALIZED");
    }

    private List<TDictionaryVo> getDefaultDomain() {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("valueActive", "Y");
        paramMap.put("tenantId", WebPluginUtils.traceTenantId());
        paramMap.put("envCode", WebPluginUtils.traceEnvCode());
        paramMap.put("typeAlias", "domain");
        List<TDictionaryVo> voList = tDictionaryDataMapper.queryDictionaryList(paramMap);
        if (CollectionUtils.isEmpty(voList)) {
            log.error(
                "业务域基础配置数据为空，tenantId:" + WebPluginUtils.traceTenantId() + "，envCode:" + WebPluginUtils.traceEnvCode());
            return Collections.emptyList();
        }
        return voList;
    }
}
