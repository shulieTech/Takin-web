package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import cn.hutool.core.util.URLUtil;
import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConvert;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceConfigService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformancePressureService;
import io.shulie.takin.web.biz.service.interfaceperformance.aspect.Action;
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
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
            if (nameEntity.getId() != null && nameEntity.getId() != input.getId()) {
                throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "名称重复");
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
        PagingList<PerformanceConfigDto> dtoPagingList = performanceConfigDAO.pageList(param);
        //转换下
        List<PerformanceConfigDto> source = dtoPagingList.getList();
        long total = dtoPagingList.getTotal();
        List<Long> userIds = source.stream().map(dto -> dto.getCreatorId()).collect(Collectors.toList());
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);
        List<PerformanceConfigVO> returnList = source.stream().map(configDto -> {
            PerformanceConfigVO vo = new PerformanceConfigVO();
            BeanUtils.copyProperties(configDto, vo);
            vo.setCreatorName(WebPluginUtils.getUserName(configDto.getCreatorId(), userMap));
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
        doAction(configId, result, Action.ActionEnum.detail);
        return result;
    }

    public SceneEntity bizFlowDetail(Long apiId) {
        return performancePressureService.bizFlowDetailByApiId(apiId);
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
        String requestUrl = param.getRequestUrl();
        // 格式化一下
        String path = "";
        if (StringUtils.isNotBlank(requestUrl)) {
            try {
                path = URLUtil.getPath(requestUrl);
            } catch (Throwable e) {
                log.error("格式化URL错误,{}}", requestUrl);
            }
        }
        if (StringUtils.isNotBlank(path)) {
            // TODO 调用AMDB
        }
        return null;
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
                    ((PerformanceConfigVO) response).setPressureConfigDetail(source);
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
            logger.error("do pressure config action error:{}", t.getCause());
            throw new RuntimeException(t.getCause());
        }
    }

}
