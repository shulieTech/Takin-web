package io.shulie.takin.web.biz.service.linkmanage.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.dao.linkguard.TLinkGuardMapper;
import com.pamirs.takin.entity.dao.linkmanage.TBusinessLinkManageTableMapper;
import com.pamirs.takin.entity.dao.linkmanage.TLinkManageTableMapper;
import com.pamirs.takin.entity.dao.linkmanage.TMiddlewareInfoMapper;
import com.pamirs.takin.entity.dao.linkmanage.TMiddlewareLinkRelateMapper;
import com.pamirs.takin.entity.dao.linkmanage.TSceneLinkRelateMapper;
import com.pamirs.takin.entity.dao.linkmanage.TSceneMapper;
import com.pamirs.takin.entity.domain.dto.EntranceSimpleDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessActiveIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessActiveViewListDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessFlowDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessFlowIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.BusinessLinkDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.ExistBusinessActiveDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.MiddleWareNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.MiddleWareVersionDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.SceneDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.SystemProcessIdAndNameDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.TechLinkDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.TopologicalGraphEntity;
import com.pamirs.takin.entity.domain.dto.linkmanage.TopologicalGraphNode;
import com.pamirs.takin.entity.domain.dto.linkmanage.TopologicalGraphRelation;
import com.pamirs.takin.entity.domain.dto.linkmanage.TopologicalGraphVo;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.ApplicationRemoteDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.BusinessCoverDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.LinkHistoryInfoDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.LinkRemarkDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.LinkRemarkmiddleWareDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.linkstatistics.SystemProcessDto;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.LinkDomainEnumMapping;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.LinkDomainEnum;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.MiddlewareTypeEnum;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.NodeClassEnum;
import com.pamirs.takin.entity.domain.entity.linkmanage.BusinessLinkManageTable;
import com.pamirs.takin.entity.domain.entity.linkmanage.LinkManageTable;
import com.pamirs.takin.entity.domain.entity.linkmanage.LinkQueryVo;
import com.pamirs.takin.entity.domain.entity.linkmanage.Scene;
import com.pamirs.takin.entity.domain.entity.linkmanage.TMiddlewareInfo;
import com.pamirs.takin.entity.domain.entity.linkmanage.statistics.StatisticsQueryVo;
import com.pamirs.takin.entity.domain.entity.linkmanage.structure.Category;
import com.pamirs.takin.entity.domain.vo.linkmanage.BusinessFlowTree;
import com.pamirs.takin.entity.domain.vo.linkmanage.BusinessFlowVo;
import com.pamirs.takin.entity.domain.vo.linkmanage.MiddleWareEntity;
import com.pamirs.takin.entity.domain.vo.linkmanage.queryparam.BusinessQueryVo;
import com.pamirs.takin.entity.domain.vo.linkmanage.queryparam.SceneQueryVo;
import io.shulie.takin.web.biz.cache.DictionaryCache;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.response.application.AgentPluginSupportResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessActivityNameResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessLinkResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.MiddleWareResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.TechLinkResponse;
import io.shulie.takin.web.biz.service.agent.AgentPluginSupportService;
import io.shulie.takin.web.biz.service.linkmanage.LinkManageService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.biz.utils.CategoryUtils;
import io.shulie.takin.web.biz.utils.PageUtils;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.linkmanage.BusinessLinkManageDAO;
import io.shulie.takin.web.data.dao.linkmanage.LinkManageDAO;
import io.shulie.takin.web.data.dao.linkmanage.SceneDAO;
import io.shulie.takin.web.data.dao.scene.SceneLinkRelateDAO;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.param.linkmanage.BusinessLinkManageQueryParam;
import io.shulie.takin.web.data.param.linkmanage.LinkManageQueryParam;
import io.shulie.takin.web.data.param.linkmanage.SceneCreateParam;
import io.shulie.takin.web.data.param.linkmanage.SceneQueryParam;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateParam;
import io.shulie.takin.web.data.param.scene.SceneLinkRelateSaveParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.application.ApplicationResult;
import io.shulie.takin.web.data.result.application.LibraryResult;
import io.shulie.takin.web.data.result.linkmange.BusinessLinkResult;
import io.shulie.takin.web.data.result.linkmange.LinkManageResult;
import io.shulie.takin.web.data.result.linkmange.SceneResult;
import io.shulie.takin.web.data.result.linkmange.TechLinkResult;
import io.shulie.takin.web.data.result.scene.SceneLinkRelateResult;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @author vernon
 * @date 2019/11/29 14:43
 */
@Slf4j
@Component
public class LinkManageServiceImpl implements LinkManageService {
    //事物
    @Resource(name = "transactionManager")
    DataSourceTransactionManager transactionManager;
    //技术链路管理表
    @Resource
    private TLinkManageTableMapper tLinkManageTableMapper;
    //业务链路管理表
    @Resource
    private TBusinessLinkManageTableMapper tBusinessLinkManageTableMapper;
    //场景链路关联表
    @Resource
    private TSceneLinkRelateMapper tSceneLinkRelateMapper;
    //场景表
    @Resource
    private TSceneMapper tSceneMapper;
    @Resource
    private TLinkGuardMapper tLinkGuardMapper;
    //中间件信息
    @Resource
    private TMiddlewareInfoMapper tMiddlewareInfoMapper;
    //中间件链路关联
    @Resource
    private TMiddlewareLinkRelateMapper tMiddlewareLinkRelateMapper;
    @Resource
    private AgentPluginSupportService agentPluginSupportService;
    @Autowired
    private BusinessLinkManageDAO businessLinkManageDAO;
    @Autowired
    private ApplicationDAO applicationDAO;
    @Autowired
    private LinkManageDAO linkManageDAO;
    @Autowired
    private SceneDAO sceneDAO;
    @Autowired
    private ActivityDAO activityDAO;
    @Autowired
    private SceneLinkRelateDAO sceneLinkRelateDAO;

    private static void iteratorChildNodes(TopologicalGraphNode parentNode,
        List<Category> childList,
        List<TopologicalGraphNode> nodes,
        List<TopologicalGraphRelation> relations) {
        if (CollectionUtils.isEmpty(childList)) {
            return;
        }
        List<Category> filterChildList = childList.stream().filter(distinctByName(Category::getApplicationName)).collect(
            Collectors.toList());
        int index = 0;
        for (Category category : filterChildList) {
            TopologicalGraphNode childNode = new TopologicalGraphNode();
            childNode.setKey(parentNode.getKey() + "." + index);
            NodeClassEnum nodeClassEnum = getNodeClassEnumByApplicationName(
                category.getApplicationName());
            MiddlewareTypeEnum middlewareTypeEnum = getMiddlewareTypeEnumByApplicationName(
                category.getApplicationName());
            childNode.setNodeType(nodeClassEnum.getCode());
            childNode.setNodeClass(nodeClassEnum.getDesc());
            if (middlewareTypeEnum != null) {
                childNode.setMiddlewareType(middlewareTypeEnum.getCode());
                childNode.setMiddlewareName(middlewareTypeEnum.getDesc());
            }
            childNode.setNodeName(category.getApplicationName());
            childNode.setNodeList(category.getNodeList());
            childNode.setUnKnowNodeList(category.getUnKnowNodeList());
            nodes.add(childNode);
            TopologicalGraphRelation relation = new TopologicalGraphRelation();
            relation.setFrom(parentNode.getKey());
            relation.setTo(childNode.getKey());
            relations.add(relation);
            if (CollectionUtils.isNotEmpty(category.getChildren())) {
                iteratorChildNodes(childNode, category.getChildren(), nodes, relations);
            }
            index++;
        }
    }

    public static NodeClassEnum getNodeClassEnumByApplicationName(String applicationName) {
        switch (applicationName) {
            case "DB中间件":
            case "MYSQL":
            case "MYSQL中间件":
            case "ORACLE":
            case "ORACLE中间件":
            case "SQLSERVER":
            case "SQLSERVER中间件":
            case "CASSANDRA":
            case "CASSANDRA中间件":
            case "HBASE":
            case "HBASE中间件":
            case "MONGODB":
            case "MONGODB中间件":
                return NodeClassEnum.DB;
            case "ELASTICSEARCH":
            case "ELASTICSEARCH中间件":
                return NodeClassEnum.ES;
            case "REDIS":
            case "REDIS中间件":
            case "MEMCACHE":
            case "MEMCACHE中间件":
                return NodeClassEnum.CACHE;
            case "ROCKETMQ":
            case "ROCKETMQ中间件":
            case "KAFKA":
            case "KAFKA中间件":
            case "ACTIVEMQ":
            case "ACTIVEMQ中间件":
            case "RABBITMQ":
            case "RABBITMQ中间件":
                return NodeClassEnum.MQ;
            case "DUBBO":
            case "DUBBO中间件":
                return NodeClassEnum.APP;
            default:
                if (applicationName.contains("未知")) {
                    return NodeClassEnum.UNKNOWN;
                }
                return NodeClassEnum.APP;
        }
    }

    public static MiddlewareTypeEnum getMiddlewareTypeEnumByApplicationName(String applicationName) {
        switch (applicationName) {
            case "MYSQL":
            case "MYSQL中间件":
                return MiddlewareTypeEnum.MySQL;
            case "ORACLE":
            case "ORACLE中间件":
                return MiddlewareTypeEnum.Oracle;
            case "SQLSERVER":
            case "SQLSERVER中间件":
                return MiddlewareTypeEnum.SQLServer;
            case "CASSANDRA":
            case "CASSANDRA中间件":
                return MiddlewareTypeEnum.Cassandra;
            case "HBASE":
            case "HBASE中间件":
                return MiddlewareTypeEnum.HBase;
            case "MONGODB":
            case "MONGODB中间件":
                return MiddlewareTypeEnum.MongoDB;
            case "ELASTICSEARCH":
            case "ELASTICSEARCH中间件":
                return MiddlewareTypeEnum.Elasticsearch;
            case "REDIS":
            case "REDIS中间件":
                return MiddlewareTypeEnum.Redis;
            case "MEMCACHE":
            case "MEMCACHE中间件":
                return MiddlewareTypeEnum.Memcache;
            case "ROCKETMQ":
            case "ROCKETMQ中间件":
                return MiddlewareTypeEnum.RocketMQ;
            case "KAFKA":
            case "KAFKA中间件":
                return MiddlewareTypeEnum.Kafka;
            case "ACTIVEMQ":
            case "ACTIVEMQ中间件":
                return MiddlewareTypeEnum.ActiveMQ;
            case "RABBITMQ":
            case "RABBITMQ中间件":
                return MiddlewareTypeEnum.RabbitMQ;
            case "DUBBO":
            case "DUBBO中间件":
                return MiddlewareTypeEnum.Dubbo;
            default:
                return null;
        }
    }

    static <T> Predicate<T> distinctByName(Function<? super T, ?> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    @Override
    public TechLinkResponse fetchLink(String applicationName, String entrance) {
        //TechLinkResponse techLinkResponse = linkManage.getApplicationLink(entrance, applicationName);
        //获取中间件
        //return techLinkResponse;
        return null;
    }

    @Override
    public TopologicalGraphVo fetchGraph(String body) {
        TopologicalGraphVo topologicalGraphVo = new TopologicalGraphVo();
        if (StringUtils.isNotBlank(body)) {
            Category category = JSON.parseObject(body, Category.class);
            List<TopologicalGraphNode> nodes = new ArrayList<>();
            List<TopologicalGraphRelation> relations = new ArrayList<>();

            TopologicalGraphNode userNode = new TopologicalGraphNode();
            userNode.setKey("0");
            userNode.setNodeType(NodeClassEnum.OTHER.getCode());
            userNode.setNodeClass(NodeClassEnum.OTHER.getDesc());
            userNode.setNodeName("user");
            nodes.add(userNode);

            TopologicalGraphNode rootNode = new TopologicalGraphNode();
            rootNode.setKey("1");
            rootNode.setNodeType(NodeClassEnum.APP.getCode());
            rootNode.setNodeClass(NodeClassEnum.APP.getDesc());
            rootNode.setNodeName(category.getApplicationName());
            nodes.add(rootNode);

            TopologicalGraphRelation rootRelation = new TopologicalGraphRelation();
            rootRelation.setFrom(userNode.getKey());
            rootRelation.setTo(rootNode.getKey());
            relations.add(rootRelation);

            List<Category> childList = category.getChildren();
            iteratorChildNodes(rootNode, childList, nodes, relations);
            topologicalGraphVo.setGraphNodes(nodes);
            topologicalGraphVo.setGraphRelations(relations);
        }
        return topologicalGraphVo;
    }

    @Override
    public Response getBussisnessLinks(BusinessQueryVo vo) {
        List<BusinessActiveViewListDto> result = Lists.newArrayList();
        LinkQueryVo queryVo = new LinkQueryVo();
        queryVo.setMiddleWareVersion(vo.getVersion());
        queryVo.setMiddleWareName(vo.getMiddleWareName());
        queryVo.setMiddleWareType(vo.getMiddleWareType());
        queryVo.setEntrance(vo.getEntrance());
        queryVo.setName(vo.getBusinessLinkName());
        queryVo.setIsChange(vo.getIschange());
        queryVo.setSystemProcessName(vo.getTechLinkName());
        queryVo.setDomain(vo.getDomain());
        List<BusinessLinkDto> queryResult = tBusinessLinkManageTableMapper.selectBussinessLinkListBySelective2(queryVo, WebPluginUtils.getQueryAllowUserIdList());
        //用户ids
        List<Long> userIds = queryResult.stream()
            .map(BusinessLinkDto::getUserId)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        //用户信息Map key:userId  value:user对象
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);

        List<BusinessLinkDto> pageData = PageUtils.getPage(true, vo.getCurrentPage(), vo.getPageSize(), queryResult);
        if (CollectionUtils.isNotEmpty(pageData) && pageData.size() > 0) {
            pageData.forEach(
                single -> {
                    BusinessActiveViewListDto dto = new BusinessActiveViewListDto();
                    dto.setBusinessActiceId(single.getId());
                    dto.setBusinessActiveName(single.getLinkName());
                    dto.setCandelete(single.getCandelete());
                    dto.setCreateTime(single.getCreateTime());
                    dto.setIschange(single.getIschange());
                    //负责人id
                    dto.setUserId(single.getUserId());
                    //负责人name
                    String userName = Optional.ofNullable(userMap.get(single.getUserId()))
                        .map(UserExt::getName)
                        .orElse("");
                    dto.setUserName(userName);
                    WebPluginUtils.fillQueryResponse(dto);
                    //新版本设置业务域
                    if (StringUtils.isNotBlank(single.getBusinessDomain())) {
                        String desc = DictionaryCache.getObjectByParam("domain",
                            Integer.parseInt(single.getBusinessDomain())).getLabel();
                        if (StringUtils.isNotBlank(desc)) {
                            dto.setBusinessDomain(desc);
                        } else {
                            //兼容历史版本
                            LinkDomainEnum domainEnum = LinkDomainEnumMapping.getByCode(single.getBusinessDomain());
                            dto.setBusinessDomain(domainEnum == null ? null : domainEnum.getDesc());
                        }
                    }
                    TechLinkDto techLinkDto = single.getTechLinkDto();

                    if (techLinkDto != null) {
                        List<TMiddlewareInfo> middlewareInfos =
                            tMiddlewareInfoMapper.selectBySystemProcessId(techLinkDto.getLinkId());
                        List<String> middleWareStrings = middlewareInfos
                            .stream()
                            .map(entity ->
                                entity.getMiddlewareName() + " " + entity.getMiddlewareVersion()
                            ).collect(Collectors.toList());
                        dto.setMiddleWareList(middleWareStrings);
                        dto.setSystemProcessName(single.getTechLinkDto().getTechLinkName());
                    }
                    result.add(dto);
                }
            );
        }
        return Response.success(result, CollectionUtils.isEmpty(queryResult) ? 0 : queryResult.size());
    }

    private void convertBusinessLinkResponse(BusinessLinkResult businessLinkResult,
        BusinessLinkResponse businessLinkResponse) {
        businessLinkResponse.setId(businessLinkResult.getId());
        businessLinkResponse.setLinkName(businessLinkResult.getLinkName());
        businessLinkResponse.setEntrance(businessLinkResult.getEntrace());
        businessLinkResponse.setIschange(businessLinkResult.getIschange());
        businessLinkResponse.setCreateTime(businessLinkResult.getCreateTime());
        businessLinkResponse.setUpdateTime(businessLinkResult.getUpdateTime());
        businessLinkResponse.setCandelete(businessLinkResult.getCandelete());
        businessLinkResponse.setIsCore(businessLinkResult.getIsCore());
        businessLinkResponse.setLinkLevel(businessLinkResult.getLinkLevel());
        businessLinkResponse.setBusinessDomain(businessLinkResult.getBusinessDomain());

        TechLinkResponse techLinkResponse = new TechLinkResponse();
        businessLinkResponse.setTechLinkResponse(techLinkResponse);
        TechLinkResult techLinkResult = businessLinkResult.getTechLinkResult();
        techLinkResponse.setLinkId(techLinkResult.getLinkId());
        techLinkResponse.setTechLinkName(techLinkResult.getTechLinkName());
        techLinkResponse.setIsChange(techLinkResult.getIsChange());
        techLinkResponse.setChange_remark(techLinkResult.getChangeRemark());
        techLinkResponse.setBody_before(techLinkResult.getBodyBefore());
        techLinkResponse.setBody_after(techLinkResult.getBodyAfter());
        techLinkResponse.setChangeType(techLinkResult.getChangeType());
    }

    @Override
    public BusinessLinkResponse getBussisnessLinkDetail(String id) {
        if (null == id) {
            throw new RuntimeException("主键不能为空");
        }
        LinkQueryVo queryVo = new LinkQueryVo();
        queryVo.setId(Long.parseLong(id));
        BusinessLinkResult businessLinkResult = businessLinkManageDAO.selectBussinessLinkById(Long.parseLong(id));

        if (Objects.nonNull(businessLinkResult)) {
            BusinessLinkResponse businessLinkResponse = new BusinessLinkResponse();
            convertBusinessLinkResponse(businessLinkResult, businessLinkResponse);
            if (businessLinkResponse.getTechLinkResponse() != null) {
                Long systemProcessId = businessLinkResponse.getTechLinkResponse().getLinkId();
                if (systemProcessId != null) {
                    LinkManageResult linkManageResult = linkManageDAO.selectLinkManageById(
                        businessLinkResult.getTechLinkResult().getLinkId());
                    businessLinkResponse.getTechLinkResponse().setMiddleWareResponses(
                        getMiddleWareResponses(linkManageResult.getApplicationName()));
                    //处理系统流程前端展示数据
                    TechLinkResponse techLinkResponse = businessLinkResponse.getTechLinkResponse();
                    String linkBody;
                    if (StringUtils.isNotBlank(techLinkResponse.getBody_after())) {
                        linkBody = techLinkResponse.getBody_after();
                    } else {
                        linkBody = techLinkResponse.getBody_before();
                    }
                    if (linkBody != null) {
                        Category category = JSON.parseObject(linkBody, Category.class);
                        CategoryUtils.assembleVo(category);
                        List<Category> list = new ArrayList<>();
                        list.add(category);
                        businessLinkResponse.getTechLinkResponse().setLinkNode(JSON.toJSONString(list));
                    }
                    TopologicalGraphEntity topologicalGraphEntity = new TopologicalGraphEntity();
                    if (StringUtils.isNotBlank(techLinkResponse.getBody_before())) {
                        TopologicalGraphVo topologicalGraphBeforeVo = fetchGraph(techLinkResponse.getBody_before());
                        topologicalGraphEntity.setTopologicalGraphBeforeVo(topologicalGraphBeforeVo);
                    }
                    if (StringUtils.isNotBlank(techLinkResponse.getBody_after())) {
                        TopologicalGraphVo topologicalGraphAfterVo = fetchGraph(techLinkResponse.getBody_after());
                        topologicalGraphEntity.setTopologicalGraphAfterVo(topologicalGraphAfterVo);
                    }
                    businessLinkResponse.getTechLinkResponse().setTopologicalGraphEntity(topologicalGraphEntity);
                }
            }
            return businessLinkResponse;
        }
        return new BusinessLinkResponse();
    }

    @Autowired
    private ScriptManageService scriptManageService;

    @Override
    public String deleteScene(String sceneId) {
        //手动控制事物,减小事物的范围
        if (null == sceneId) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "primary key cannot be null.");
        }
        SceneResult sceneDetail = sceneDAO.getSceneDetail(NumberUtils.toLong(sceneId));
        if (sceneDetail == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_VALIDATE_ERROR, "找不到对应的业务流程.");
        }

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        TransactionStatus status = transactionManager.getTransaction(def);
        try {
            if (sceneDetail.getScriptDeployId() != null) {
                scriptManageService.deleteScriptManage(sceneDetail.getScriptDeployId());
            }
            tSceneMapper.deleteByPrimaryKey(Long.parseLong(sceneId));

            //取出关联的业务活动id是否可以被删除
            List<SceneLinkRelateResult> relates = sceneLinkRelateDAO.selectBySceneId(Long.parseLong(sceneId));
            List<Long> businessLinkIds = relates.stream().map(relate -> {
                if (relate.getBusinessLinkId() != null) {
                    return Long.parseLong(relate.getBusinessLinkId());
                }
                return 0L;
            }).collect(Collectors.toList());

            //删除关联表
            sceneLinkRelateDAO.deleteBySceneId(sceneId);
            //过滤出可以设置为删除状态的业务活动id并设置为可以删除
            enableBusinessActiveCanDelte(businessLinkIds);

            transactionManager.commit(status);
            return "删除成功";
        } catch (TakinWebException e) {
            transactionManager.rollback(status);
            log.error("删除场景失败", e);
            throw e;
        } catch (Exception e) {
            transactionManager.rollback(status);
            log.error("删除场景失败", e);
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_DELETE_ERROR, "删除场景失败");
        }
    }

    private void enableBusinessActiveCanDelte(List<Long> businessLinkIds) {
        if (CollectionUtils.isEmpty(businessLinkIds)) {
            return;
        }
        List<Long> candeletedList = businessLinkIds.stream()
            .map(single -> {
                long count = sceneLinkRelateDAO.countByBusinessLinkId(single);
                if (!(count > 0)) {
                    return single;
                }
                return 0L;
            }).collect(Collectors.toList());

        tBusinessLinkManageTableMapper.cannotdelete(candeletedList, 0L);
    }

    @Override
    public Response<List<SceneDto>> getScenes(SceneQueryVo vo) {
        List<SceneDto> sceneDtos = tSceneMapper.selectByRelatedQuery(vo, WebPluginUtils.getQueryAllowUserIdList());
        List<SceneDto> pageData = PageUtils.getPage(true, vo.getCurrentPage(), vo.getPageSize(), sceneDtos);
        if (CollectionUtils.isEmpty(sceneDtos)) {
            return Response.success(pageData, 0);
        }
        //查询业务活动是否存在虚拟业务活动
        List<String> sceneIds = pageData.stream().map(SceneDto::getId).map(String::valueOf).collect(
            Collectors.toList());
        SceneLinkRelateParam relateParam = new SceneLinkRelateParam();
        relateParam.setSceneIds(sceneIds);
        List<SceneLinkRelateResult> relateResults = sceneLinkRelateDAO.getList(relateParam);
        // 流程 -> 业务活动
        Map<String, List<ActivityListResult>> map = Maps.newHashMap();
        if (relateResults.size() > 0) {
            ActivityQueryParam param = new ActivityQueryParam();
            param.setBusinessType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
            param.setActivityIds(relateResults.stream().map(SceneLinkRelateResult::getBusinessLinkId)
                .map(Long::parseLong).collect(Collectors.toList()));
            List<ActivityListResult> results = activityDAO.getActivityList(param);
            map = relateResults.stream().collect(
                Collectors.toMap(
                    SceneLinkRelateResult::getSceneId,
                    data -> results.stream()
                        .filter(activity -> data.getBusinessLinkId().equals(String.valueOf(activity.getActivityId())))
                        .collect(Collectors.toList()),
                    (List<ActivityListResult> newValueList, List<ActivityListResult> oldValueList) -> {
                        oldValueList.addAll(newValueList);
                        return oldValueList;
                    }));
        }

        Map<String, List<ActivityListResult>> finalMap = map;

        //用户ids
        List<Long> userIds = sceneDtos.stream().map(SceneDto::getUserId).filter(Objects::nonNull)
            .collect(Collectors.toList());
        Map<Long, UserExt> userExtMap = WebPluginUtils.getUserMapByIds(userIds);
        pageData = pageData.stream().map(single -> {
            long count = sceneLinkRelateDAO.countBySceneId(single.getId());
            // 填充虚拟字段
            List<ActivityListResult> activityListResults = finalMap.get(String.valueOf(single.getId()));
            if (activityListResults != null && activityListResults.size() > 0) {
                single.setBusinessType(BusinessTypeEnum.VIRTUAL_BUSINESS.getType());
            } else {
                single.setBusinessType(BusinessTypeEnum.NORMAL_BUSINESS.getType());
            }
            single.setTechLinkCount(count);
            single.setBusinessLinkCount(count);
            String userName = WebPluginUtils.getUserName(single.getUserId(), userExtMap);
            single.setUserName(userName);
            WebPluginUtils.fillQueryResponse(single);
            return single;
        }).collect(Collectors.toList());
        return Response.success(pageData, CollectionUtils.isEmpty(sceneDtos) ? 0 : sceneDtos.size());

    }

    @Override
    public Response<List<LinkRemarkmiddleWareDto>> getMiddleWareInfo(StatisticsQueryVo vo) {
        try {
            List<LinkRemarkmiddleWareDto> list = tMiddlewareInfoMapper.selectforstatistics(vo);
            List<LinkRemarkmiddleWareDto> pageData = PageUtils.getPage(true, vo.getCurrentPage(), vo.getPageSize(),
                list);

            pageData = pageData.stream().peek(
                single -> {
                    long id = single.getMiddleWareId();
                    List<String> techLinkIds = tMiddlewareLinkRelateMapper.selectTechIdsByMiddleWareIds(id);
                    single.setSystemProcessCount(String.valueOf(techLinkIds.size()));
                    //统计业务流程条数
                    if (CollectionUtils.isNotEmpty(techLinkIds)) {
                        int countBusinessProcess = sceneLinkRelateDAO.countByTechLinkIds(techLinkIds);
                        single.setBussinessProcessCount(String.valueOf(countBusinessProcess));
                    }
                }
            ).collect(Collectors.toList());

            return Response.success(pageData, CollectionUtils.isEmpty(list) ? 0 : list.size());
        } catch (Exception e) {
            return Response.fail("0", e.getMessage());
        }

    }

    @Override
    public LinkRemarkDto getstatisticsInfo() {
        //  List<LinkRemarkmiddleWareDto> middlewareInfo = middlewareInfoMapper.selectforstatistics(null);
        LinkRemarkDto dto = new LinkRemarkDto();
        /*  dto.setLinkRemarkmiddleWareDtos(middlewareInfo);*/

        long businessProcessCount = tSceneMapper.count();
        long businessActiveCount = tBusinessLinkManageTableMapper.count();
        long systemProcessCount = tLinkManageTableMapper.countTotal();
        long systemChangeCount = tLinkManageTableMapper.countChangeNum();
        long onLineApplicationCount = tLinkManageTableMapper.countApplication();
        long linkGuardCount = tLinkGuardMapper.countGuardNum();
        dto.setBusinessProcessCount(String.valueOf(businessProcessCount));
        dto.setBusinessActiveCount(String.valueOf(businessActiveCount));
        dto.setSystemProcessCount(String.valueOf(systemProcessCount));
        dto.setSystemChangeCount(String.valueOf(systemChangeCount));
        dto.setOnLineApplicationCount(String.valueOf(onLineApplicationCount));
        dto.setLinkGuardCount(String.valueOf(linkGuardCount));
        return dto;
    }

    @Override
    public LinkHistoryInfoDto getChart() {

        LinkHistoryInfoDto dto = new LinkHistoryInfoDto();

        String begin = DateUtils.preYear(new java.util.Date());
        String end = new SimpleDateFormat("yyyy-MM").format(new java.util.Date());
        //获取过去一年到现在的日期集合
        List<Date> dateRange = DateUtils.getRangeSet2(begin, end);

        List<BusinessCoverDto> businessCoverList = new ArrayList<>();
        dateRange.stream().forEach(date -> {
            BusinessCoverDto businessCoverDto = new BusinessCoverDto();
            businessCoverDto.setMonth(DateUtils.dateToString(date));
            long count = tSceneMapper.countByTime(date);
            businessCoverDto.setCover(String.valueOf(count));
            businessCoverList.add(businessCoverDto);
        });
        dto.setBusinessCover(businessCoverList);

        List<SystemProcessDto> systemProcessList = Lists.newArrayList();
        dateRange.forEach(date -> {
            SystemProcessDto systemProcessDto = new SystemProcessDto();
            systemProcessDto.setMonth(DateUtils.dateToString(date));
            long count = tLinkManageTableMapper.countSystemProcessByTime(date);
            systemProcessDto.setCover(String.valueOf(count));
            systemProcessList.add(systemProcessDto);
        });
        dto.setSystemProcess(systemProcessList);

        List<ApplicationRemoteDto> applicationRemoteList = Lists.newArrayList();
        dateRange.forEach(date -> {
            ApplicationRemoteDto applicationRemoteDto = new ApplicationRemoteDto();
            applicationRemoteDto.setMonth(DateUtils.dateToString(date));
            long count = tLinkManageTableMapper.countApplicationByTime(date);
            applicationRemoteDto.setCover(String.valueOf(count));
            applicationRemoteList.add(applicationRemoteDto);
        });
        dto.setApplicationRemote(applicationRemoteList);

        Long businessFlowTotalCountNum = tSceneMapper.count();
        String businessFlowTotalCount = String.valueOf(businessFlowTotalCountNum);
        String businessFlowPressureCount = "0";
        String businessFlowPressureRate =
            (businessFlowTotalCountNum == 0L || "0".equals(businessFlowPressureCount)) ?
                "0" : String.valueOf(businessFlowTotalCountNum / Long.parseLong(businessFlowPressureCount));
        dto.setBusinessFlowTotalCount(businessFlowTotalCount);
        dto.setBusinessFlowPressureCount(businessFlowPressureCount);
        dto.setBusinessFlowPressureRate(businessFlowPressureRate);

        // TODO: 2020/1/7 暂时统计系统流程总数
        long applicationTotalCountNum = tLinkManageTableMapper.countTotal();
        String applicationTotalCount = String.valueOf(applicationTotalCountNum);
        String applicationPressureCount = "0";
        String applicationPressureRate = (applicationTotalCountNum == 0L || "0".equals(applicationPressureCount)) ?
            "0" : String.valueOf(applicationTotalCountNum / Long.parseLong(applicationPressureCount));
        dto.setApplicationTotalCount(applicationTotalCount);
        dto.setApplicationPressureCount(applicationPressureCount);
        dto.setApplicationPressureRate(applicationPressureRate);

        return dto;
    }

    @Override
    public List<MiddleWareEntity> businessProcessMiddleWares(List<String> ids) {
        List<MiddleWareEntity> result = Lists.newArrayList();

        List<Long> businessIds =
            ids.stream().map(id -> Long.parseLong(String.valueOf(id))).collect(Collectors.toList());
        //查系统流程id集合
        List<String> techIds = tBusinessLinkManageTableMapper.selectTechIdsByBusinessIds(businessIds);
        if (CollectionUtils.isEmpty(techIds)) {
            return result;
        }
        //查中间件id集合
        List<String> middleWareIds = tMiddlewareLinkRelateMapper.selectMiddleWareIdsByTechIds(techIds);
        if (CollectionUtils.isEmpty(middleWareIds)) {
            return result;
        }
        //查中间件信息
        List<Long> midllewareIdslong = middleWareIds.stream()
            .map(Long::parseLong).collect(Collectors.toList());

        result = tMiddlewareInfoMapper.selectByIds(midllewareIdslong);

        return result;
    }

    @Override
    public List<MiddleWareEntity> getAllMiddleWareTypeList() {
        List<MiddleWareEntity> result = Lists.newArrayList();
        List<TMiddlewareInfo> infos = tMiddlewareInfoMapper
            .selectBySelective(new TMiddlewareInfo());

        //按照中间件类型去重
        infos.forEach(info -> {
            MiddleWareEntity entity = new MiddleWareEntity();
            entity.setId(info.getId());
            entity.setMiddleWareType(info.getMiddlewareType());
            entity.setVersion(info.getMiddlewareVersion());
            entity.setMiddleWareName(info.getMiddlewareName());
            result.add(entity);
        });
        List<MiddleWareEntity> distinct = result.stream()
            .collect(Collectors.collectingAndThen(Collectors.toCollection(
                    () -> new TreeSet<>(
                        Comparator.comparing(MiddleWareEntity::getMiddleWareType))),
                ArrayList::new));

        return distinct;
    }

    @Override
    public List<SystemProcessIdAndNameDto> getAllSystemProcess(String systemProcessName) {
        List<SystemProcessIdAndNameDto> result = Lists.newArrayList();
        LinkManageQueryParam queryParam = new LinkManageQueryParam();
        WebPluginUtils.fillQueryParam(queryParam);
        queryParam.setSystemProcessName(systemProcessName);
        List<LinkManageResult> linkManageResultList = linkManageDAO.selectList(queryParam);
        if (CollectionUtils.isNotEmpty(linkManageResultList)) {
            linkManageResultList.forEach(table -> {
                SystemProcessIdAndNameDto dto = new SystemProcessIdAndNameDto();
                dto.setId(String.valueOf(table.getLinkId()));
                dto.setSystemProcessName(table.getLinkName());
                result.add(dto);
            });
        }
        return result;
    }

    @Override
    public List<SystemProcessIdAndNameDto> getAllSystemProcessCanrelateBusiness(String systemProcessName) {
        List<SystemProcessIdAndNameDto> result = Lists.newArrayList();
        LinkManageTable serachTable = new LinkManageTable();
        serachTable.setLinkName(systemProcessName);
        serachTable.setCanDelete(0);

        List<LinkManageTable> tables =
            tLinkManageTableMapper.selectBySelective(serachTable);
        if (CollectionUtils.isNotEmpty(tables)) {
            tables.forEach(table -> {
                SystemProcessIdAndNameDto dto = new SystemProcessIdAndNameDto();
                dto.setId(String.valueOf(table.getLinkId()));
                dto.setSystemProcessName(table.getLinkName());
                result.add(dto);

            });
        }
        return result;
    }

    @Override
    public List<String> entranceFuzzSerach(String entrance) {
        return tLinkManageTableMapper.entranceFuzzSerach(entrance);
    }

    @Override
    public List<BusinessActiveIdAndNameDto> businessActiveNameFuzzSearch(String businessActiveName) {
        List<BusinessActiveIdAndNameDto> businessActiveIdAndNameDtoList = Lists.newArrayList();
        BusinessLinkManageQueryParam queryParam = new BusinessLinkManageQueryParam();
        WebPluginUtils.fillQueryParam(queryParam);
        queryParam.setBussinessActiveName(businessActiveName);
        queryParam.setPersistence(true);
        List<BusinessLinkResult> businessLinkResultList = businessLinkManageDAO.selectList(queryParam);
        if (CollectionUtils.isNotEmpty(businessLinkResultList)) {
            businessActiveIdAndNameDtoList = businessLinkResultList.stream().map(businessLinkResult -> {
                BusinessActiveIdAndNameDto bActive = new BusinessActiveIdAndNameDto();
                bActive.setId(businessLinkResult.getId());
                bActive.setBusinessActiveName(businessLinkResult.getLinkName());
                return bActive;
            }).collect(Collectors.toList());
        }
        return businessActiveIdAndNameDtoList;
    }

    @Transactional
    @Override
    public void addBusinessFlow(BusinessFlowVo vo) throws Exception {
        //添加业务活动主表并返回业务活动的id
        if (CollectionUtils.isEmpty(vo.getRoot())) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR, "关联业务活动不能为空");
        }
        Long sceneId = addScene(vo);
        List<SceneLinkRelateSaveParam> relates = parsingTree(vo, sceneId);
        if (CollectionUtils.isNotEmpty(relates)) {
            //补全信息
            infoCompletion(relates);
            sceneLinkRelateDAO.batchInsert(relates);
            //设置业务活动不能被删除
            diableDeleteBusinessActives(relates);
        }

    }

    /**
     * 设置业务活动不能被删除
     */
    private void diableDeleteBusinessActives(List<SceneLinkRelateSaveParam> relates) {

        List<Long> relateBusinessLinkIds =
            relates.stream().map(
                single -> {
                    if (single.getBusinessLinkId() != null) {
                        return Long.parseLong(single.getBusinessLinkId());
                    }
                    return 0L;
                }
            ).collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(relateBusinessLinkIds)) {
            tBusinessLinkManageTableMapper.cannotdelete(relateBusinessLinkIds, 1L);
        }
    }

    @Override
    public List<BusinessFlowIdAndNameDto> businessFlowIdFuzzSearch(String businessFlowName) {
        List<BusinessFlowIdAndNameDto> businessFlowIdAndNameDtoList = Lists.newArrayList();
        SceneQueryParam queryParam = new SceneQueryParam();
        WebPluginUtils.fillQueryParam(queryParam);
        queryParam.setSceneName(businessFlowName);
        List<SceneResult> sceneResultList = sceneDAO.selectList(queryParam);
        if (CollectionUtils.isNotEmpty(sceneResultList)) {
            businessFlowIdAndNameDtoList = sceneResultList.stream().map(sceneResult -> {
                BusinessFlowIdAndNameDto businessFlowIdAndNameDto = new BusinessFlowIdAndNameDto();
                businessFlowIdAndNameDto.setId(String.valueOf(sceneResult.getId()));
                businessFlowIdAndNameDto.setBusinessFlowName(sceneResult.getSceneName());
                return businessFlowIdAndNameDto;
            }).collect(Collectors.toList());
        }
        return businessFlowIdAndNameDtoList;
    }

    /**
     * 解析树并返回关联表封装集合
     *
     * @return -
     */
    private List<SceneLinkRelateSaveParam> parsingTree(BusinessFlowVo vo, Long sceneId) {

        List<SceneLinkRelateSaveParam> relates = Lists.newArrayList();
        //根节点集合
        List<BusinessFlowTree> roots = vo.getRoot();
        for (BusinessFlowTree businessFlowTree : roots) {
            String parentId = null;
            BusinessFlowTree root = businessFlowTree;
            String businessId = root.getId();
            if (StringUtils.isBlank(businessId)) {
                continue;
            }
            SceneLinkRelateSaveParam relate = new SceneLinkRelateSaveParam();
            relate.setSceneId(String.valueOf(sceneId));
            relate.setParentBusinessLinkId(parentId);
            relate.setBusinessLinkId(businessId);
            relate.setIsDeleted(0);
            //前端产生的uuid
            relate.setFrontUuidKey(root.getKey());
            relates.add(relate);
            List<BusinessFlowTree> children = root.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                parsing(children, businessId, sceneId, relates);
            }
        }

        return relates;
    }

    private Long addScene(BusinessFlowVo vo) {
        SceneCreateParam param = new SceneCreateParam();
        param.setSceneName(vo.getSceneName());
        param.setSceneLevel(vo.getSceneLevel());
        param.setIsCore(Integer.parseInt(vo.getIsCore()));
        param.setIsChanged(0);
        param.setIsDeleted(0);
        sceneDAO.insert(param);
        return param.getId();
    }

    /**
     * 对业务流程链路关联表的信息补全
     *
     * @param relates 业务流程链路关联集合
     */
    private void infoCompletion(List<SceneLinkRelateSaveParam> relates) {
        //获取出所有的业务活动ID
        List<Long> businessIds
            = relates
            .stream()
            .map(relate -> Long.parseLong(relate.getBusinessLinkId())).collect(Collectors.toList());

        List<BusinessLinkManageTable> tables =
            tBusinessLinkManageTableMapper.selectByPrimaryKeys(businessIds);

        Map<Long, List<BusinessLinkManageTable>> map
            = tables.stream()
            .collect(Collectors.groupingBy(
                BusinessLinkManageTable::getLinkId));

        relates.forEach(
            relate -> {
                Long businessLinkId = Long.parseLong(relate.getBusinessLinkId());
                List<BusinessLinkManageTable> lists = map.get(businessLinkId);
                if (CollectionUtils.isNotEmpty(lists)) {
                    BusinessLinkManageTable table = lists.get(0);
                    relate.setEntrance(table.getEntrace());
                    relate.setTechLinkId(table.getRelatedTechLink());
                }
            }
        );
    }

    /**
     * @param children 子节点集合
     * @param parentId 父亲节点
     * @param sceneId  业务流程id
     * @param result   返回结果的集合
     * @return -
     */
    private List<SceneLinkRelateSaveParam> parsing(List<BusinessFlowTree> children, String parentId, Long sceneId,
        List<SceneLinkRelateSaveParam> result) {
        for (BusinessFlowTree businessFlowTree : children) {
            SceneLinkRelateSaveParam relate = new SceneLinkRelateSaveParam();
            BusinessFlowTree child = businessFlowTree;
            String businessId = child.getId();
            if (StringUtils.isNotBlank(businessId)) {
                relate.setBusinessLinkId(child.getId());
                relate.setParentBusinessLinkId(parentId);
                relate.setIsDeleted(0);
                relate.setFrontUuidKey(child.getKey());
                relate.setSceneId(String.valueOf(sceneId));
                result.add(relate);
            }

            List<BusinessFlowTree> lowerChildren = businessFlowTree.getChildren();
            if (CollectionUtils.isNotEmpty(lowerChildren)) {
                parsing(lowerChildren, child.getId(), sceneId, result);
            }
        }
        return result;
    }

    @Override
    public BusinessFlowDto getBusinessFlowDetail(Long id) {
        BusinessFlowDto dto = new BusinessFlowDto();

        //获取业务流程基本信息
        Scene scene = tSceneMapper.selectByPrimaryKey(id);
        if (Objects.isNull(scene)) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR,
                id + "对应的业务流程不存在");
        }

        dto.setId(String.valueOf(scene.getId()));
        dto.setIsCode(String.valueOf(scene.getIsCore()));
        dto.setLevel(scene.getSceneLevel());
        dto.setBusinessProcessName(scene.getSceneName());

        List<SceneLinkRelateResult> relates = sceneLinkRelateDAO.selectBySceneId(id);
        List<ExistBusinessActiveDto> existBusinessActiveIds =
            relates.stream().map(relate ->
            {
                ExistBusinessActiveDto single = new ExistBusinessActiveDto();
                single.setKey(relate.getFrontUuidKey());
                single.setId(relate.getBusinessLinkId());
                return single;
            }).collect(Collectors.toList());

        dto.setExistBusinessActive(existBusinessActiveIds);

        List<BusinessFlowTree> roots = sceneLinkRelateDAO.listRecursion(id, WebPluginUtils.traceTenantId(),
            WebPluginUtils.traceEnvCode());
        dto.setRoots(roots);

        //中间件信息
        List<String> techLinkIds = relates.stream().map(SceneLinkRelateResult::getTechLinkId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(techLinkIds)) {
            List<String> middleWareIdStrings = tMiddlewareLinkRelateMapper.selectMiddleWareIdsByTechIds(techLinkIds);
            List<Long> middleWareIds = middleWareIdStrings.stream().map(Long::parseLong).collect(
                Collectors.toList());

            List<MiddleWareEntity> middleWareEntityList = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(middleWareIds)) {
                middleWareEntityList = tMiddlewareInfoMapper.selectByIds(middleWareIds);
            }

            dto.setMiddleWareEntities(middleWareEntityList);
        }

        return dto;
    }

    @Transactional
    @Override
    public void modifyBusinessFlow(BusinessFlowVo vo) throws Exception {
        if (CollectionUtils.isEmpty(vo.getRoot())) {
            throw new TakinWebException(TakinWebExceptionEnum.LINK_VALIDATE_ERROR, "关联业务活动不能为空");
        }
        recordBusinessFlowLog(vo);
        //修改主表
        modifyScene(vo);
        //激活可以业务活动可以删除
        enableBusinessDelete(vo);
        //删除老的关联信息
        sceneLinkRelateDAO.deleteBySceneId(vo.getId());
        //重新生成关联信息
        List<SceneLinkRelateSaveParam> relates = parsingTree(vo, Long.parseLong(vo.getId()));
        if (CollectionUtils.isNotEmpty(relates)) {
            //补全信息
            infoCompletion(relates);
            sceneLinkRelateDAO.batchInsert(relates);
        }
        //冻结业务活动可以删除
        diableDeleteBusinessActives(relates);
    }

    private void recordBusinessFlowLog(BusinessFlowVo vo) throws Exception {
        //记录变更日志
        Scene oldScene = tSceneMapper.selectByPrimaryKey(Long.parseLong(vo.getId()));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BUSINESS_PROCESS, vo.getSceneName());
        List<SceneLinkRelateResult> oldSceneLinkRelateList = sceneLinkRelateDAO.selectBySceneId(Long.parseLong(vo.getId()));

        List<Long> oldBusinessLinkIdList = oldSceneLinkRelateList.stream().map(SceneLinkRelateResult::getBusinessLinkId).map(
            Long::parseLong).collect(Collectors.toList());
        List<SceneLinkRelateSaveParam> currentSceneLinkRelateList = parsingTree(vo, Long.parseLong(vo.getId()));
        List<Long> currentBusinessLinkIdList = currentSceneLinkRelateList.stream().map(
            SceneLinkRelateSaveParam::getBusinessLinkId).map(Long::parseLong).collect(Collectors.toList());
        List<Long> toDeleteIdList = Lists.newArrayList();
        toDeleteIdList.addAll(oldBusinessLinkIdList);
        toDeleteIdList.removeAll(currentBusinessLinkIdList);
        List<Long> toAddIdList = Lists.newArrayList();
        toAddIdList.addAll(currentBusinessLinkIdList);
        toAddIdList.removeAll(oldBusinessLinkIdList);
        String selectiveContent = "";
        if (oldScene.getSceneName().equals(vo.getSceneName())
            && CollectionUtils.isEmpty(toAddIdList)
            && CollectionUtils.isEmpty(toDeleteIdList)) {
            OperationLogContextHolder.ignoreLog();
        }
        if (CollectionUtils.isNotEmpty(toAddIdList)) {
            List<BusinessLinkManageTable> businessLinkManageTableList = tBusinessLinkManageTableMapper
                .selectBussinessLinkByIdList(toAddIdList);
            if (CollectionUtils.isNotEmpty(businessLinkManageTableList)) {
                String addNodeNames = businessLinkManageTableList.stream().map(BusinessLinkManageTable::getLinkName)
                    .collect(Collectors.joining(","));
                selectiveContent = selectiveContent + "｜新增节点：" + addNodeNames;
            }
        }
        if (CollectionUtils.isNotEmpty(toDeleteIdList)) {
            List<BusinessLinkManageTable> businessLinkManageTableList = tBusinessLinkManageTableMapper
                .selectBussinessLinkByIdList(toDeleteIdList);
            if (CollectionUtils.isNotEmpty(businessLinkManageTableList)) {
                String deleteNodeNames = businessLinkManageTableList.stream().map(BusinessLinkManageTable::getLinkName)
                    .collect(Collectors.joining(","));
                selectiveContent = selectiveContent + "｜删除节点：" + deleteNodeNames;
            }
        }
        OperationLogContextHolder.addVars(BizOpConstants.Vars.BUSINESS_PROCESS_SELECTIVE_CONTENT, selectiveContent);
    }

    private void enableBusinessDelete(BusinessFlowVo vo) {
        if (vo.getId() == null) {
            return;
        }
        List<SceneLinkRelateResult> oldRelates = sceneLinkRelateDAO.selectBySceneId(Long.parseLong(vo.getId()));

        if (CollectionUtils.isEmpty(oldRelates)) {
            return;
        }

        List<Long> candeleteList = oldRelates.stream()
            .map(single ->
            {
                if (single.getBusinessLinkId() == null) {
                    return 0L;
                }
                return Long.parseLong(single.getBusinessLinkId());
            }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(candeleteList)) {
            return;
        }
        tBusinessLinkManageTableMapper.cannotdelete(candeleteList, 0L);
    }

    /**
     * 修改场景的主表
     */
    private void modifyScene(BusinessFlowVo vo) {
        String sceneId = vo.getId();
        String sceneName = vo.getSceneName();
        String isCore = vo.getIsCore();
        String sceneLevel = vo.getSceneLevel();
        Scene updateScene = new Scene();
        updateScene.setId(Long.parseLong(sceneId));
        updateScene.setSceneName(sceneName);
        updateScene.setIsCore(Integer.parseInt(isCore));
        updateScene.setSceneLevel(sceneLevel);
        tSceneMapper.updateByPrimaryKeySelective(updateScene);
    }

    @Override
    public List<MiddleWareNameDto> cascadeMiddleWareNameAndVersion(String middleWareType) throws Exception {
        List<MiddleWareNameDto> result = Lists.newArrayList();

        //拿出所有的中间件名字
        TMiddlewareInfo info = new TMiddlewareInfo();
        if (StringUtils.isNotBlank(middleWareType)) {
            info.setMiddlewareType(middleWareType);
        }
        List<TMiddlewareInfo> infos =
            tMiddlewareInfoMapper.selectBySelective(info);
        if (CollectionUtils.isNotEmpty(infos)) {
            Map<String, List<TMiddlewareInfo>> groupByMiddleWareName =
                infos.stream().collect(Collectors.groupingBy(TMiddlewareInfo::getMiddlewareName));

            for (Map.Entry<String, List<TMiddlewareInfo>> entry : groupByMiddleWareName.entrySet()) {
                MiddleWareNameDto dto = new MiddleWareNameDto();
                String middleWareName = entry.getKey();
                dto.setLabel(middleWareName);
                dto.setValue(middleWareName);
                List<TMiddlewareInfo> values = entry.getValue();
                if (CollectionUtils.isNotEmpty(values)) {
                    List<MiddleWareVersionDto> children = values.stream().map(
                        single -> {
                            MiddleWareVersionDto versionDto = new MiddleWareVersionDto();
                            String version = single.getMiddlewareVersion();
                            versionDto.setLabel(version);
                            versionDto.setValue(version);
                            return versionDto;
                        }
                    ).collect(Collectors.toList());
                    dto.setChildren(children);
                }
                result.add(dto);
            }
        }
        return result;
    }

    @Override
    public List<MiddleWareNameDto> getDistinctMiddleWareName() {
        List<MiddleWareNameDto> result = Lists.newArrayList();

        List<TMiddlewareInfo> infos = tMiddlewareInfoMapper
            .selectBySelective(new TMiddlewareInfo());

        //按照中间件类型去重
        infos.forEach(single -> {
            MiddleWareNameDto entity = new MiddleWareNameDto();
            entity.setValue(single.getMiddlewareName());
            entity.setLabel(single.getMiddlewareName());
            result.add(entity);
        });
        return result.stream()
            .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(
                Comparator.comparing(MiddleWareNameDto::getLabel))), ArrayList::new));
    }

    @Override
    public List<EntranceSimpleDto> getEntranceByAppName(String applicationName) {
        return null;
    }

    @Override
    public List<MiddleWareResponse> getMiddleWareResponses(String applicationName) {
        List<MiddleWareResponse> middleWareResponses = Lists.newArrayList();
        List<AgentPluginSupportResponse> supportList = agentPluginSupportService.queryAgentPluginSupportList();
        List<ApplicationResult> applicationResultList = applicationDAO.getApplicationByName(
            Collections.singletonList(applicationName));
        if (CollectionUtils.isEmpty(applicationResultList)) {
            return middleWareResponses;
        }
        LibraryResult[] libraryResults = applicationResultList.get(0).getLibrary();
        if (null == libraryResults || libraryResults.length == 0) {
            return middleWareResponses;
        }
        for (LibraryResult libraryResult : libraryResults) {
            MiddleWareResponse middleWareResponse = agentPluginSupportService.convertLibInfo(supportList,
                libraryResult.getLibraryName());
            if (!Objects.isNull(middleWareResponse)) {
                middleWareResponses.add(middleWareResponse);
            }
        }
        middleWareResponses.sort((a, b) -> {
            if (a.getStatusResponse().getValue() > b.getStatusResponse().getValue()) {
                return 1;
            } else if (a.getStatusResponse().getValue() < b.getStatusResponse().getValue()) {
                return -1;
            } else {
                return 0;
            }
        });
        return middleWareResponses;
    }

    @Override
    public List<BusinessActivityNameResponse> getBusinessActiveByFlowId(Long businessFlowId) {
        List<BusinessActivityNameResponse> sceneBusinessActivityRefVOS = new ArrayList<>();
        List<SceneLinkRelateResult> sceneLinkRelates = sceneLinkRelateDAO.selectBySceneId(businessFlowId);
        if (CollectionUtils.isNotEmpty(sceneLinkRelates)) {
            List<Long> businessActivityIds = sceneLinkRelates.stream().map(o -> Long.valueOf(o.getBusinessLinkId()))
                .collect(Collectors.toList());
            List<BusinessLinkManageTable> businessLinkManageTables = tBusinessLinkManageTableMapper
                .selectBussinessLinkByIdList(businessActivityIds);
            //因为businessLinkManageTables打乱了业务活动的顺序 所以使用businessActivityIds
            sceneBusinessActivityRefVOS = businessActivityIds.stream().map(activityId -> {
                BusinessActivityNameResponse businessActivityNameResponse = new BusinessActivityNameResponse();
                businessActivityNameResponse.setBusinessActivityId(activityId);
                BusinessLinkManageTable linkManageTable = businessLinkManageTables.stream().filter(
                    link -> activityId.equals(link.getLinkId())).findFirst().orElse(null);
                if (Objects.nonNull(linkManageTable)) {
                    businessActivityNameResponse.setBusinessActivityName(linkManageTable.getLinkName());
                    businessActivityNameResponse.setType(linkManageTable.getType());
                    businessActivityNameResponse.setApplicationId(linkManageTable.getApplicationId());
                    businessActivityNameResponse.setBusinessActivityName(linkManageTable.getApplicationName());
                }
                return businessActivityNameResponse;
            }).collect(Collectors.toList());
        }
        return sceneBusinessActivityRefVOS;
    }

}



