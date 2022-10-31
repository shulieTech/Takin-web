package io.shulie.takin.web.biz.service.pressureresource.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateTableInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateTableRequest;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceDsService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceTableService;
import io.shulie.takin.web.biz.service.pressureresource.common.DataSourceUtil;
import io.shulie.takin.web.biz.service.pressureresource.common.DbNameUtil;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelateTableVO;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbTableDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateDsDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateTableDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationDsDbTableMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateDsMapperV2;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateTableMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateTableMapperV2;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbTableEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntityV2;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDsQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;
import io.shulie.takin.web.data.result.application.ApplicationDsDbTableDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/1 8:36 PM
 */
@Service
public class PressureResourceTableServiceImpl implements PressureResourceTableService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceTableServiceImpl.class);

    @Resource
    private PressureResourceRelateTableDAO pressureResourceRelateTableDAO;

    @Resource
    private PressureResourceRelateTableMapper pressureResourceRelateTableMapper;

    @Resource
    private PressureResourceRelateTableMapperV2 pressureResourceRelateTableMapperV2;

    @Resource
    private PressureResourceDsService pressureResourceDsService;

    @Resource
    private PressureResourceRelateDsDAO pressureResourceRelateDsDAO;

    @Autowired
    private ApplicationDsDbTableDAO applicationDsDbTableDAO;

    @Resource
    private ApplicationService applicationService;

    @Autowired
    private ApplicationDsDbTableDAO dsDbTableDAO;

    @Autowired
    private ApplicationDsDbTableMapper applicationDsDbTableMapper;

    /**
     * 新增
     *
     * @param input
     */
    @Override
    public void save(PressureResourceRelateTableInput input) {
        // 判断业务表是否存在
        PressureResourceTableQueryParam queryParam = new PressureResourceTableQueryParam();
        queryParam.setBusinessTableName(input.getBusinessTable());
        // 通过数据源ID的主键是由应用名+业务数据源+resouceId生成,这里需要转换下
        String dsKey = pressureResourceDsService.getDsKey(input.getDsId());
        queryParam.setDsKey(dsKey);
        List<PressureResourceRelateTableEntity> tableList = pressureResourceRelateTableDAO.queryList(queryParam);
        if (CollectionUtils.isNotEmpty(tableList)) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "业务表已存在");
        }
        // 新增
        PressureResourceRelateTableEntity tableEntity = new PressureResourceRelateTableEntity();
        tableEntity.setResourceId(input.getResourceId());
        tableEntity.setBusinessTable(input.getBusinessTable());
        tableEntity.setShadowTable(input.getShadowTable());
        tableEntity.setJoinFlag(input.getJoinFlag());
        tableEntity.setType(input.getType());
        tableEntity.setGmtCreate(new Date());
        tableEntity.setDsKey(dsKey);
        pressureResourceRelateTableDAO.add(Arrays.asList(tableEntity));
    }


    /**
     * 新增
     *
     * @param input
     */
    @Override
    public void save_v2(PressureResourceRelateTableInput input) {
        // 判断数据源是否存在
        PressureResourceDsQueryParam dsQueryParam = new PressureResourceDsQueryParam();
        dsQueryParam.setResourceId(input.getResourceId());
        dsQueryParam.setId(input.getDsId());
        List<PressureResourceRelateDsEntity> dsEntityList = pressureResourceRelateDsDAO.queryByParam_v2(dsQueryParam);
        if (CollectionUtils.isEmpty(dsEntityList)) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "关联数据源信息不存在");
        }
        // 只有有一个
        PressureResourceRelateDsEntity dsEntity = dsEntityList.get(0);
        // 判断业务表是否存在,找到当前数据源下关联的表信息
        PressureResourceTableQueryParam queryParam = new PressureResourceTableQueryParam();
        queryParam.setResourceId(input.getResourceId());
        queryParam.setDsKey(DataSourceUtil.generateDsKey_ext(dsEntity.getBusinessDatabase(), dsEntity.getShadowUserName()));
        List<PressureResourceRelateTableEntity> v2 = pressureResourceRelateTableDAO.queryList_v2(queryParam);

        Long appId = applicationService.queryApplicationIdByAppName(dsEntity.getAppName());
        if (CollectionUtils.isNotEmpty(v2)) {
            // 判断表是否存在
            List vList = v2.stream().filter(v -> input.getBusinessTable().equals(v.getBusinessTable())).collect(Collectors.toList());
            if (vList.size() > 0) {
                throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "业务表已存在");
            }
        }
        // 新增
        ApplicationDsDbTableDetailResult detailResult = new ApplicationDsDbTableDetailResult();
        detailResult.setBizTable(input.getBusinessTable());
        detailResult.setShadowTable(input.getShadowTable());
        detailResult.setUrl(dsEntity.getBusinessDatabase());
        detailResult.setUserName(dsEntity.getBusinessUserName());
        detailResult.setBizDataBase(DbNameUtil.getDbName(dsEntity.getBusinessDatabase()));
        detailResult.setAppId(appId);
        detailResult.setManualTag(input.getType());
        // 加入就是选中
        detailResult.setIsCheck(input.getJoinFlag() == 0 ? 1 : 0);
        List<ApplicationDsDbTableEntity> dsdbList = applicationDsDbTableDAO.batchSave_ext(Arrays.asList(detailResult));

        // 保存关联关系
        List<PressureResourceRelateTableEntityV2> relateTableList = dsdbList.stream().map(table -> {
            PressureResourceRelateTableEntityV2 tableEntityV2 = new PressureResourceRelateTableEntityV2();
            tableEntityV2.setResourceId(input.getResourceId());
            tableEntityV2.setRelateId(table.getId());
            tableEntityV2.setDsKey(input.getDsKey());
            tableEntityV2.setType(1);
            tableEntityV2.setTenantId(WebPluginUtils.traceTenantId());
            tableEntityV2.setEnvCode(WebPluginUtils.traceEnvCode());
            tableEntityV2.setGmtCreate(new Date());
            return tableEntityV2;
        }).collect(Collectors.toList());
        pressureResourceRelateTableDAO.add_V2(relateTableList);
    }

    /**
     * update
     *
     * @param updateInput
     */
    @Override
    public void update_v2(PressureResourceRelateTableInput updateInput) {
        if (updateInput.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        // 判断是否存在
        PressureResourceRelateTableEntityV2 entity = pressureResourceRelateTableMapperV2.selectById(updateInput.getId());
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "未查询到指定数据");
        }
        // 找到关联的Id
        Long relateId = entity.getRelateId();
        ApplicationDsDbTableEntity updateEntity = new ApplicationDsDbTableEntity();
        updateEntity.setId(relateId);
        updateEntity.setBizTable(updateInput.getBusinessTable());
        updateEntity.setShadowTable(updateInput.getShadowTable());
        // 是否选中,加入就选中
        updateEntity.setIsCheck(updateInput.getJoinFlag() == 0 ? 1 : 0);
        updateEntity.setGmtUpdate(new Date());
        applicationDsDbTableMapper.updateById(updateEntity);
    }

    /**
     * update
     *
     * @param updateInput
     */
    @Override
    public void update(PressureResourceRelateTableInput updateInput) {
        if (updateInput.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        // 判断是否存在
        PressureResourceRelateTableEntity entity = pressureResourceRelateTableMapper.selectById(updateInput.getId());
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "未查询到指定数据");
        }
        PressureResourceRelateTableRequest tableRequest = new PressureResourceRelateTableRequest();
        tableRequest.setBusinessTableName(updateInput.getBusinessTable());
        tableRequest.setDsId(updateInput.getDsId());
        PagingList<PressureResourceRelateTableVO> pageList = this.pageList(tableRequest);
        if (!pageList.isEmpty()) {
            List<PressureResourceRelateTableVO> list = pageList.getList();
            if (CollectionUtils.isNotEmpty(list)) {
                PressureResourceRelateTableVO vo = list.get(0);
                if (!vo.getId().equals(String.valueOf(updateInput.getId()))) {
                    throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "业务表名已存在");
                }
            }
        }
        PressureResourceRelateTableEntity updateEntity = new PressureResourceRelateTableEntity();
        updateEntity.setId(updateInput.getId());
        updateEntity.setJoinFlag(updateInput.getJoinFlag());
        updateEntity.setBusinessTable(updateInput.getBusinessTable());
        updateEntity.setShadowTable(updateInput.getShadowTable());
        updateEntity.setRemark(updateEntity.getRemark());
        updateEntity.setStatus(updateEntity.getStatus());
        updateEntity.setExtInfo(updateEntity.getExtInfo());
        pressureResourceRelateTableMapper.updateById(updateEntity);
    }

    /**
     * 批量加入或取消
     *
     * @param updateInput
     */
    @Override
    public void batchUpdate(PressureResourceRelateTableInput updateInput) {
        if (CollectionUtils.isEmpty(updateInput.getIds())) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "参数未传递");
        }
        // 查询所有
        List<PressureResourceRelateTableEntityV2> relateTableEntityV2List = pressureResourceRelateTableMapperV2.selectBatchIds(updateInput.getIds());
        List<Long> ids = relateTableEntityV2List.stream().map(relate -> relate.getRelateId()).collect(Collectors.toList());

        ApplicationDsDbTableEntity updateEntity = new ApplicationDsDbTableEntity();
        // 是否选中,加入就选中
        updateEntity.setIsCheck(updateInput.getJoinFlag() == 0 ? 1 : 0);
        updateEntity.setGmtUpdate(new Date());

        QueryWrapper<ApplicationDsDbTableEntity> updateWrapper = new QueryWrapper<>();
        updateWrapper.in("id", ids);
        applicationDsDbTableMapper.update(updateEntity, updateWrapper);
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        pressureResourceRelateTableMapper.deleteById(id);
    }

    @Override
    public void delete_v2(Long id) {
        if (id == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        // 判断是否存在
        PressureResourceRelateTableEntityV2 entity = pressureResourceRelateTableMapperV2.selectById(id);
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "未查询到指定数据");
        }
        // 找到关联的Id
        Long relateId = entity.getRelateId();
        applicationDsDbTableMapper.deleteById(relateId);

        // 关联关系也删除
        pressureResourceRelateTableMapperV2.deleteById(id);
    }

    /**
     * 分页
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateTableVO> pageList_v2(PressureResourceRelateTableRequest request) {
        PressureResourceTableQueryParam param = new PressureResourceTableQueryParam();
        param.setResourceId(request.getResourceId());
        param.setDsKey(request.getDsKey());

        PagingList<PressureResourceRelateTableEntity> pageList = pressureResourceRelateTableDAO.pageList_v2(param);

        if (pageList.isEmpty()) {
            return PagingList.of(Collections.emptyList(), pageList.getTotal());
        }
        //转换下
        List<PressureResourceRelateTableEntity> source = pageList.getList();
        List<PressureResourceRelateTableVO> returnList = source.stream().map(configDto -> {
            PressureResourceRelateTableVO vo = new PressureResourceRelateTableVO();
            BeanUtils.copyProperties(configDto, vo);
            vo.setId(String.valueOf(configDto.getId()));
            return vo;
        }).collect(Collectors.toList());
        return PagingList.of(returnList, pageList.getTotal());
    }

    /**
     * 分页
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateTableVO> pageList(PressureResourceRelateTableRequest request) {
        PressureResourceTableQueryParam param = new PressureResourceTableQueryParam();
        BeanUtils.copyProperties(request, param);
        if (request.getDsId() != null) {
            String dsKey = pressureResourceDsService.getDsKey(request.getDsId());
            param.setDsKey(dsKey);
        }
        PagingList<PressureResourceRelateTableEntity> pageList = pressureResourceRelateTableDAO.pageList(param);

        if (pageList.isEmpty()) {
            return PagingList.of(Collections.emptyList(), pageList.getTotal());
        }
        //转换下
        List<PressureResourceRelateTableEntity> source = pageList.getList();
        List<PressureResourceRelateTableVO> returnList = source.stream().map(configDto -> {
            PressureResourceRelateTableVO vo = new PressureResourceRelateTableVO();
            BeanUtils.copyProperties(configDto, vo);
            vo.setId(String.valueOf(configDto.getId()));
            return vo;
        }).collect(Collectors.toList());
        return PagingList.of(returnList, pageList.getTotal());
    }
}
