package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import com.pamirs.takin.entity.domain.vo.report.SceneActionParam;
import io.shulie.takin.cloud.sdk.model.response.scenetask.SceneActionResp;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConvert;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceConfigService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformancePressureService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.vo.interfaceperformance.PerformanceConfigVO;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.param.interfaceperformance.PerformanceConfigQueryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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
        return performanceConfigDAO.add(insertConfigEntity);
    }

    /**
     * update
     *
     * @param input
     */
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
        interfacePerformanceConfigMapper.updateById(updateEntity);
    }

    /**
     * 删除
     *
     * @param configId
     */
    @Override
    public void delete(Long configId) {
        // 判断数据是否存在
        InterfacePerformanceConfigEntity entity = interfacePerformanceConfigMapper.selectById(configId);
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "数据不存在!");
        }
        performanceConfigDAO.delete(configId);
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
        PagingList<PerformanceConfigVO> resultList = performanceConfigDAO.pageList(param);
        return resultList;
    }

    /**
     * 获取详情
     *
     * @param configId
     * @return
     */
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
        return result;
    }
	
	@Override
    public ResponseResult<SceneActionResp> start(SceneActionParam param) {
        return performancePressureService.start(param);
    }
}
