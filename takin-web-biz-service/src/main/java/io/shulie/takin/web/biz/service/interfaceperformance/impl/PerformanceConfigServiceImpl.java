package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.amdb.common.dto.trace.EntryTraceInfoDTO;
import io.shulie.takin.adapter.api.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.adapter.api.model.response.scenetask.SceneActionResp;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.*;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.scene.NewSceneRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceConfigService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformancePressureService;
import io.shulie.takin.web.biz.service.interfaceperformance.aspect.Action;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.biz.service.interfaceperformance.vo.PerformanceConfigVO;
import io.shulie.takin.web.biz.service.interfaceperformance.vo.RelationAppNameVO;
import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceConfigDto;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.SceneEntity;
import io.shulie.takin.web.data.param.interfaceperformance.PerformanceConfigQueryParam;
import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 1:46 下午
 */
@Slf4j
@Service
public class PerformanceConfigServiceImpl implements PerformanceConfigService {
    @Resource
    private PerformanceConfigDAO performanceConfigDAO;

    @Resource
    private InterfacePerformanceConfigMapper interfacePerformanceConfigMapper;

    @Autowired
    PerformancePressureService performancePressureService;

    @Autowired
    private AmdbClientProperties properties;

    private String APP_REQ_URL = "/amdb/trace/getAppAndReqByUrl";

    /**
     * 新增
     *
     * @param input
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long add(PerformanceConfigCreateInput input) {
        // 名称是否重复
        InterfacePerformanceConfigEntity configEntity = performanceConfigDAO.queryConfigByName(input.getName());
        if (configEntity != null && configEntity.getId() != null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_CREATE_ERROR, input.getName() + " 已存在");
        }
        InterfacePerformanceConfigEntity insertConfigEntity = PerformanceConvert.convertConfigEntity(input);
        insertConfigEntity.setGmtCreate(new Date());
        insertConfigEntity.setGmtModified(new Date());
        // 设置归属人未当前创建人
        insertConfigEntity.setUserId(WebPluginUtils.traceUserId());
        // 新增配置信息
        Long returnObj = performanceConfigDAO.add(insertConfigEntity);
        /**
         * id回填给后续操作
         */
        input.setId(returnObj);
        doAction(input, returnObj, Action.ActionEnum.create);
        return returnObj;
    }

    /**
     * update
     *
     * @param input
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(PerformanceConfigCreateInput input) {
        // 校验数据是否存在
        Long configId = input.getId();
        if (configId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        InterfacePerformanceConfigEntity entity = interfacePerformanceConfigMapper.selectById(configId);
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "未查询到指定数据");
        }
        // 名字不能重复
        InterfacePerformanceConfigEntity nameEntity = performanceConfigDAO.queryConfigByName(input.getName());
        if (nameEntity != null) {
            // 判断两个是否为同一条数据
            if (nameEntity.getId() != null && !nameEntity.getId().equals(input.getId())) {
                throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, input.getName() + "已存在");
            }
        }
        // 更新数据
        InterfacePerformanceConfigEntity updateEntity = PerformanceConvert.convertConfigEntity(input);
        updateEntity.setName(input.getName());
        updateEntity.setGmtModified(new Date());
        updateEntity.setId(input.getId());
        Integer returnObj = interfacePerformanceConfigMapper.updateById(updateEntity);
        doAction(input, returnObj, Action.ActionEnum.update);
    }

    /**
     * 删除
     *
     * @param configId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long configId) {
        if (configId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "参数未设置!");
        }
        // 判断数据是否存在
        InterfacePerformanceConfigEntity entity = interfacePerformanceConfigMapper.selectById(configId);
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "数据不存在!");
        }
        performanceConfigDAO.delete(configId);
        doAction(configId, null, Action.ActionEnum.delete);
    }

    @Resource
    private SceneManageService sceneManageService;

    /**
     * 查询单接口压测场景数据
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PerformanceConfigVO> query(PerformanceConfigQueryRequest request) {
        PerformanceConfigQueryParam param = new PerformanceConfigQueryParam();
        BeanUtils.copyProperties(request, param);
        AuthQueryParamCommonExt ext = new AuthQueryParamCommonExt();
        WebPluginUtils.fillQueryParam(ext);
        // TODO 菜单配置的时候，设置权限处理
        //param.setUserIdList(ext.getUserIdList());
        PagingList<PerformanceConfigDto> dtoPagingList = performanceConfigDAO.pageList(param);
        //转换下
        List<PerformanceConfigDto> source = dtoPagingList.getList();
        long total = dtoPagingList.getTotal();
        List<PerformanceConfigVO> returnList = source.stream().map(configDto -> {
            PerformanceConfigVO vo = new PerformanceConfigVO();
            BeanUtils.copyProperties(configDto, vo);
            try {
                //查绑定的场景id
                Long configId = vo.getId();
                Long sceneId = performancePressureService.querySceneId(configId);
                vo.setBindSceneId(sceneId);
                //查压测状态
                ResponseResult<SceneManageWrapperResp> result = sceneManageService.detailScene(sceneId);
                if (Objects.nonNull(result.getData())) {
                    vo.setPressureStatus(result.getData().getStatus());
                }
            } catch (Throwable t) {
                logger.error("数据异常:{}", t.getMessage());
            }
            return vo;
        }).collect(Collectors.toList());
        return PagingList.of(returnList, total);
    }

    /**
     * 获取详情
     *
     * @param configId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public PerformanceConfigVO detail(Long configId) {
        if (configId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "参数未设置");
        }
        InterfacePerformanceConfigEntity queryEntity = interfacePerformanceConfigMapper.selectById(configId);
        if (queryEntity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "数据不存在!");
        }
        PerformanceConfigVO result = new PerformanceConfigVO();
        BeanUtils.copyProperties(queryEntity, result);
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(Arrays.asList(queryEntity.getUserId()));
        result.setUserName(WebPluginUtils.getUserName(queryEntity.getUserId(), userMap));
        result.setContentTypeVo(JsonHelper.json2Bean(queryEntity.getContentType(), ContentTypeVO.class));

        doAction(configId, result, Action.ActionEnum.detail);

        Long sceneId = result.getBindSceneId();
        ResponseResult<SceneManageWrapperResp> responseResult = sceneManageService.detailScene(sceneId);
        if (Objects.nonNull(responseResult.getData())) {
            result.setPressureStatus(responseResult.getData().getStatus());
        }
        return result;
    }

    public SceneEntity bizFlowDetail(Long apiId) {
        return performancePressureService.bizFlowDetailByApiId(apiId);
    }

    @Override
    public Long querySceneId(Long apiId) {
        return performancePressureService.querySceneId(apiId);
    }

    @Override
    public ResponseResult<SceneActionResp> start(SceneActionParam param) {
        return performancePressureService.start(param);
    }

    public ResponseResult uploadDataFile(BusinessFlowDataFileRequest request) {
        return performancePressureService.uploadDataFile(request);
    }

    /**
     * 关联入口应用
     *
     * @param param
     * @return
     */
    @Override
    public List<RelationAppNameVO> relationName(PerformanceConfigQueryRequest param) {
        List<RelationAppNameVO> relationAppNameVOList = Lists.newArrayList();
        String requestUrl = param.getRequestUrl();
        String path = "";
        if (StringUtils.isNotBlank(requestUrl)) {
            try {
                path = URLUtil.getPath(requestUrl);
            } catch (Throwable e) {
                log.error("格式化URL错误,{}}", requestUrl);
            }
        }
        if (StringUtils.isNotBlank(path)) {
            String url = properties.getUrl().getAmdb() + APP_REQ_URL;
            Map<String, Object> queryMap = Maps.newHashMap();
            queryMap.put("serviceName", path);
            queryMap.put("methodName", param.getHttpMethod());
            queryMap.put("tenantAppKey", WebPluginUtils.traceTenantAppKey());
            queryMap.put("envCode", WebPluginUtils.traceEnvCode());

            AmdbResult<List<EntryTraceInfoDTO>> amdbResponse = AmdbHelper.builder().url(url)
                    .httpMethod(HttpMethod.GET)
                    .param(queryMap)
                    .eventName("查询入口请求参数")
                    .exception(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_PARAM_ERROR)
                    .list(EntryTraceInfoDTO.class);
            List<EntryTraceInfoDTO> infos = amdbResponse.getData();
            if (CollectionUtils.isNotEmpty(infos)) {
                final String tmpPath = path;
                relationAppNameVOList = infos.stream().map(info -> {
                    RelationAppNameVO tmpVo = new RelationAppNameVO();
                    tmpVo.setEntranceAppName(info.getAppName() + "|" + tmpPath);
                    tmpVo.setParam(info.getRequest());
                    // TODO 暂时设置为空，目前没有header字段
                    tmpVo.setHeader("");
                    return tmpVo;
                }).collect(Collectors.toList());
            }
        }
        return relationAppNameVOList;
    }

    private void _doAction(Object arg, Object response, Action.ActionEnum action) throws Throwable {
        switch (action) {
            case create:
                PerformanceConfigCreateInput createIn = (PerformanceConfigCreateInput) arg;
                performancePressureService.add(createIn);
                break;
            case delete:
                Long deleteIn = (Long) arg;
                performancePressureService.delete(deleteIn);
                break;
            case update:
                PerformanceConfigCreateInput updateIn = (PerformanceConfigCreateInput) arg;
                performancePressureService.update(updateIn);
                break;
            case detail:
                Long detailIn = (Long) arg;
                ResponseResult<SceneDetailResponse> result = performancePressureService.query(detailIn);
                SceneDetailResponse source = result.getData();
                if (response.getClass().isAssignableFrom(PerformanceConfigVO.class)) {
                    NewSceneRequest requestDetail = performancePressureService.convert(source, detailIn);
                    ((PerformanceConfigVO) response).setPressureConfigRequest(requestDetail);
                    ((PerformanceConfigVO) response).setBindSceneId(source.getId());
                }
                break;
            case select:
                break;
        }
    }

    Logger logger = LoggerFactory.getLogger(getClass());

    private void doAction(Object arg, Object response, Action.ActionEnum action) {
        try {
            _doAction(arg, response, action);
        } catch (Throwable t) {
            logger.error("do pressure config action error:{}", t.getMessage());
            throw new RuntimeException(t.getMessage());
        }
    }

}
