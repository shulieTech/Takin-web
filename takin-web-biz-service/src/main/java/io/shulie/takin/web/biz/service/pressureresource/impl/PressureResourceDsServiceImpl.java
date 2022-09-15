package io.shulie.takin.web.biz.service.pressureresource.impl;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.MD5Util;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.ExtInfo;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateDsInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateDsRequest;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateTableInput;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceDsService;
import io.shulie.takin.web.biz.service.pressureresource.common.*;
import io.shulie.takin.web.biz.service.pressureresource.vo.*;
import io.shulie.takin.web.biz.utils.xlsx.ExcelUtils;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.vo.excel.ExcelSheetVO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateAppDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateDsDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateTableDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelateDsMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateAppEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceAppQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDsQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/31 7:37 PM
 */
@Service
public class PressureResourceDsServiceImpl implements PressureResourceDsService {
    private static Logger logger = LoggerFactory.getLogger(PressureResourceDsServiceImpl.class);

    @Resource
    private PressureResourceRelateDsDAO pressureResourceRelateDsDAO;

    @Resource
    private PressureResourceRelateTableDAO pressureResourceRelateTableDAO;

    @Resource
    private PressureResourceRelateDsMapper pressureResourceRelateDsMapper;

    @Resource
    private PressureResourceMapper pressureResourceMapper;

    @Resource
    private PressureResourceRelateAppDAO pressureResourceRelateAppDAO;

    /**
     * 新增
     *
     * @param input
     */
    @Override
    public void add(PressureResourceRelateDsInput input) {
        // 批量给拆分到不同应用里面去，应用视角需要分页查询
        List<String> appNames = input.getRelationApps();
        if (CollectionUtils.isEmpty(appNames)) {
            return;
        }
        // 判断数据源是否已存在
        PressureResourceDsQueryParam param = new PressureResourceDsQueryParam();
        param.setBussinessDatabase(input.getBusinessDatabase());
        param.setResourceId(input.getResourceId());
        List<PressureResourceRelateDsEntity> list = pressureResourceRelateDsDAO.queryByParam(param);
        if (CollectionUtils.isNotEmpty(list)) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "数据源已存在");
        }
        List<PressureResourceRelateDsEntity> dsEntitys = appNames.stream().map(appName -> {
            PressureResourceRelateDsEntity tmpEntity = new PressureResourceRelateDsEntity();

            BeanUtils.copyProperties(input, tmpEntity);
            tmpEntity.setAppName(appName);
            tmpEntity.setGmtCreate(new Date());

            ExtInfo extInfo = input.getExtInfo();
            tmpEntity.setExtInfo(JSON.toString(extInfo));
            return tmpEntity;
        }).collect(Collectors.toList());
        pressureResourceRelateDsDAO.add(dsEntitys);
    }

    @Override
    public String getDsKey(Long dsId) {
        PressureResourceRelateDsEntity entity = pressureResourceRelateDsMapper.selectById(dsId);
        return entity.getUniqueKey();
    }

    @Override
    public void update(PressureResourceRelateDsInput input) {
        if (input.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "参数Id未指定");
        }
        // 判断是否存在
        PressureResourceRelateDsEntity entity = pressureResourceRelateDsMapper.selectById(input.getId());
        if (entity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "未查询到指定数据");
        }

        PressureResourceRelateDsEntity updateEntity = new PressureResourceRelateDsEntity();
        updateEntity.setId(input.getId());
        updateEntity.setStatus(input.getStatus());
        updateEntity.setBusinessDatabase(input.getBusinessDatabase());
        updateEntity.setBusinessUserName(input.getBusinessUserName());
        updateEntity.setShadowDatabase(input.getShadowDatabase());
        updateEntity.setShadowUserName(input.getShadowUserName());
        updateEntity.setShadowPassword(input.getShadowPassword());
        //updateEntity.set
    }

    /**
     * 删除
     *
     * @param dsId
     */
    @Override
    public void del(Long dsId) {
        if (dsId == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "参数ID未传递!");
        }
        pressureResourceRelateDsMapper.deleteById(dsId);
    }

    /**
     * 数据源视图页面,内存分页
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateDsVO> listByDs(PressureResourceRelateDsRequest request) {
        // 查询所有的数据源信息
        PressureResourceDsQueryParam param = new PressureResourceDsQueryParam();
        param.setResourceId(request.getResourceId());
        param.setQueryBussinessDatabase(request.getQueryBusinessDataBase());
        param.setStatus(request.getStatus());

        List<PressureResourceRelateDsEntity> dsList = pressureResourceRelateDsDAO.queryByParam(param);
        // 相同数据源合并
        List<PressureResourceRelateDsVO> listVO = Lists.newArrayList();
        Map<String, List<PressureResourceRelateDsEntity>> dsMap = dsList.stream().collect(Collectors.groupingBy(ds -> ds.getBusinessDatabase()));
        for (Map.Entry<String, List<PressureResourceRelateDsEntity>> entry : dsMap.entrySet()) {
            List<PressureResourceRelateDsEntity> tmpList = entry.getValue();
            List<String> appNames = tmpList.stream().map(ds -> ds.getAppName()).collect(Collectors.toList());
            PressureResourceRelateDsVO tmpVO = new PressureResourceRelateDsVO();
            BeanUtils.copyProperties(tmpList.get(0), tmpVO);
            tmpVO.setDatabase(DbNameUtil.getDbName(tmpList.get(0).getBusinessDatabase()));

            tmpVO.setId(String.valueOf(tmpList.get(0).getId()));
            tmpVO.setResourceId(String.valueOf(tmpList.get(0).getResourceId()));
            if (StringUtils.isNotBlank(tmpVO.getBusinessDatabase())) {
                String bussinessDatabase = tmpVO.getBusinessDatabase();
                tmpVO.setDatabase(DbNameUtil.getDbName(bussinessDatabase));
            }
            // 通过应用获取是否加入压测范围
            PressureResourceAppQueryParam appQueryParam = new PressureResourceAppQueryParam();
            appQueryParam.setAppNames(appNames);
            appQueryParam.setResourceId(request.getResourceId());
            List<PressureResourceRelateAppEntity> appEntitys = pressureResourceRelateAppDAO.queryList(appQueryParam);
            if (CollectionUtils.isNotEmpty(appEntitys)) {
                Map<String, List<PressureResourceRelateAppEntity>> appMap = appEntitys.stream().collect(Collectors.groupingBy(app -> app.getAppName()));
                List<PressureResourceRelateAppVO> appVOList = appNames.stream().map(app -> {
                    PressureResourceRelateAppVO appVO = new PressureResourceRelateAppVO();
                    appVO.setAppName(app);
                    appVO.setJoinPressure(JoinFlagEnum.NO.getCode());
                    if (appMap.containsKey(app)) {
                        appVO.setJoinPressure(appMap.get(app).get(0).getJoinPressure());
                    }
                    return appVO;
                }).collect(Collectors.toList());
                tmpVO.setAppList(appVOList);
            }
            tmpVO.setSize(tmpList.size());
            listVO.add(tmpVO);
        }
        List<PressureResourceRelateDsVO> pageList = ListUtil.page(request.getCurrentPage(), request.getPageSize(), listVO);
        return PagingList.of(pageList, listVO.size());
    }

    /**
     * 应用视图
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelateDsVO> listByApp(PressureResourceRelateDsRequest request) {
        // 查询所有的数据源信息
        PressureResourceDsQueryParam param = new PressureResourceDsQueryParam();
        param.setResourceId(request.getResourceId());
        param.setQueryAppName(request.getQueryAppName());
        List<PressureResourceRelateDsEntity> dsList = pressureResourceRelateDsDAO.queryByParam(param);
        // 相同数据源合并
        List<PressureResourceRelateDsVO> listVO = Lists.newArrayList();
        Map<String, List<PressureResourceRelateDsEntity>> appMap = dsList.stream().collect(Collectors.groupingBy(ds -> ds.getAppName()));
        for (Map.Entry<String, List<PressureResourceRelateDsEntity>> entry : appMap.entrySet()) {
            List<PressureResourceRelateDsEntity> tmpList = entry.getValue();
            PressureResourceRelateDsVO tmpVO = new PressureResourceRelateDsVO();
            BeanUtils.copyProperties(tmpList.get(0), tmpVO);
            tmpVO.setId(String.valueOf(tmpList.get(0).getId()));
            tmpVO.setResourceId(String.valueOf(tmpList.get(0).getResourceId()));
            List<PressureResourceDsVO> dsVOList = tmpList.stream().map(ds -> {
                PressureResourceDsVO tmpDs = new PressureResourceDsVO();
                tmpDs.setBusinessDataBase(ds.getBusinessDatabase());
                tmpDs.setStatus(ds.getStatus());
                return tmpDs;
            }).collect(Collectors.toList());

            tmpVO.setDsList(dsVOList);
            tmpVO.setSize(tmpList.size());
            listVO.add(tmpVO);
        }
        List<PressureResourceRelateDsVO> pageList = ListUtil.page(request.getCurrentPage(), request.getPageSize(), listVO);
        return PagingList.of(pageList, listVO.size());
    }

    /**
     * 导入数据源配置
     *
     * @param file
     * @param resourceId
     */
    @Override
    public void importDsConfig(MultipartFile file, Long resourceId) {
        String originalFilename = file.getOriginalFilename();
        if (StringUtils.isBlank(originalFilename)) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_CONFIG_FILE_VALIDATE_ERROR, "文件不存在!");
        }
        if (!originalFilename.endsWith(".xlsx")) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR,
                    "文件格式不正确，必须为xlsx格式文件，请重新导出配置文件！");
        }
        // 简单校验
        PressureResourceEntity resourceEntity = pressureResourceMapper.selectById(resourceId);
        if (resourceEntity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "资源配置未获取到");
        }
        // 读取xlsx文件
        Map<String, ArrayList<ArrayList<String>>> stringArrayListHashMap = ExcelUtils.readExcelForXlsx(file, 0);
        if (stringArrayListHashMap.isEmpty()) {
            return;
        }
        // 数据校验
        // 保存到DB
        int isolateType = resourceEntity.getIsolateType();
        // 影子表单独处理
        if (isolateType == IsolateTypeEnum.SHADOW_TABLE.getCode()) {
            processShadowTable(resourceId, stringArrayListHashMap);
        } else {
            // 影子库处理
            processShadowDB(resourceId, stringArrayListHashMap);
        }
    }

    /**
     * 导出
     *
     * @param response
     * @param resourceId
     */
    @Override
    public void export(HttpServletResponse response, Long resourceId) {
        PressureResourceEntity resourceEntity = pressureResourceMapper.queryByIdNoTenant(resourceId);
        if (resourceEntity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "资源配置未获取到");
        }
        TenantInfoExt tenantInfo = WebPluginUtils.getTenantInfo(resourceEntity.getTenantId());
        String tenantCode = "";
        String tenantAppKey = "";
        if (tenantInfo != null) {
            tenantCode = tenantInfo.getTenantCode();
            tenantAppKey = tenantInfo.getTenantAppKey();
        }
        // 复制上下文
        WebPluginUtils.setTraceTenantContext(resourceEntity.getTenantId(),
                tenantAppKey, resourceEntity.getEnvCode(), tenantCode, ContextSourceEnum.HREF.getCode());

        // 判断隔离方式
        if (resourceEntity.getIsolateType() == IsolateTypeEnum.SHADOW_TABLE.getCode()) {
            // 影子表导出
            exportShadowTable(response, resourceEntity);
        } else {
            // 影子库导出
            exportShadowDB(response, resourceEntity);
        }
    }

    /**
     * 导出影子库
     *
     * @param response
     * @param resource
     */
    private void exportShadowDB(HttpServletResponse response, PressureResourceEntity resource) {
        // 查询所有影子库
        PressureResourceDsQueryParam dsQueryParam = new PressureResourceDsQueryParam();
        dsQueryParam.setResourceId(resource.getId());
        List<PressureResourceRelateDsEntity> dsEntityList = pressureResourceRelateDsDAO.queryByParam(dsQueryParam);
        List<ShadowDbExcelVO> shadowDbExcelVOList = Lists.newArrayList();
        List<ExcelSheetVO<?>> sheets = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dsEntityList)) {
            shadowDbExcelVOList = dsEntityList.stream().map(entity -> {
                ShadowDbExcelVO excelVO = new ShadowDbExcelVO();
                excelVO.setBusinessDatabase(entity.getBusinessDatabase());
                excelVO.setShadowDatabase(entity.getShadowDatabase());
                excelVO.setIsolateType(IsolateTypeEnum.getName(resource.getType()));
                excelVO.setShadowUsername(entity.getShadowUserName());
                excelVO.setShadowPassword(entity.getShadowPassword());
                return excelVO;
            }).collect(Collectors.toList());
        }
        ExcelSheetVO<ShadowDbExcelVO> shadowDbSheet = new ExcelSheetVO<>();
        shadowDbSheet.setData(shadowDbExcelVOList);
        shadowDbSheet.setExcelModelClass(ShadowDbExcelVO.class);
        shadowDbSheet.setSheetName("隔离方案-" + IsolateTypeEnum.getName(resource.getIsolateType()));
        shadowDbSheet.setSheetNum(1);
        sheets.add(shadowDbSheet);
        try {
            ExcelUtils.exportExcelManySheet(response, resource.getName() + "_隔离配置", sheets);
        } catch (Exception e) {
            logger.error("配置导出错误: {}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 导出影子表
     *
     * @param response
     * @param resource
     */
    private void exportShadowTable(HttpServletResponse response, PressureResourceEntity resource) {
        // 查询当前配置下的所有数据源信息
        PressureResourceDsQueryParam dsQueryParam = new PressureResourceDsQueryParam();
        dsQueryParam.setResourceId(resource.getId());
        List<PressureResourceRelateDsEntity> dsEntityList = pressureResourceRelateDsDAO.queryByParam(dsQueryParam);
        List<ExcelSheetVO<?>> sheets = new ArrayList<>();
        List<ShadowTableExcelVO> shadowTableExcelVOList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dsEntityList)) {
            // 查询影子表
            PressureResourceTableQueryParam tableQueryParam = new PressureResourceTableQueryParam();
            tableQueryParam.setResourceId(resource.getId());
            List<PressureResourceRelateTableEntity> tableEntityList = pressureResourceRelateTableDAO.queryList(tableQueryParam);
            // 按照数据源分组下
            Map<String, List<PressureResourceRelateDsEntity>> dsMap = dsEntityList.stream().collect(Collectors.groupingBy(item -> String.valueOf(item.getUniqueKey())));
            if (CollectionUtils.isNotEmpty(tableEntityList)) {
                Map<String, List<PressureResourceRelateTableEntity>> tableEntityMap = tableEntityList.stream().collect(Collectors.groupingBy(item -> item.getDsKey()));
                for (Map.Entry<String, List<PressureResourceRelateTableEntity>> entry : tableEntityMap.entrySet()) {
                    String dsId = entry.getKey();
                    PressureResourceRelateDsEntity tmpDs = dsMap.get(dsId).stream().findFirst().orElse(new PressureResourceRelateDsEntity());
                    if (tmpDs != null && tmpDs.getId() != null) {
                        List<ShadowTableExcelVO> list = entry.getValue().stream().map(table -> {
                            ShadowTableExcelVO excelVO = new ShadowTableExcelVO();
                            excelVO.setBusinessDatabase(tmpDs.getBusinessDatabase());
                            excelVO.setDatabase(DbNameUtil.getDbName(tmpDs.getBusinessDatabase()));
                            excelVO.setIsolateType(IsolateTypeEnum.getName(resource.getIsolateType()));
                            excelVO.setShadowTable(table.getShadowTable());
                            excelVO.setBusinessTable(table.getBusinessTable());
                            return excelVO;
                        }).collect(Collectors.toList());
                        shadowTableExcelVOList.addAll(list);
                    }
                }
            } else {
                for (Map.Entry<String, List<PressureResourceRelateDsEntity>> entry : dsMap.entrySet()) {
                    ShadowTableExcelVO excelVO = new ShadowTableExcelVO();
                    excelVO.setBusinessDatabase(entry.getValue().get(0).getBusinessDatabase());
                    excelVO.setDatabase(DbNameUtil.getDbName(excelVO.getBusinessDatabase()));
                    excelVO.setIsolateType(IsolateTypeEnum.getName(resource.getIsolateType()));
                    shadowTableExcelVOList.add(excelVO);
                }
            }
        }
        ExcelSheetVO<ShadowTableExcelVO> shadowTableSheet = new ExcelSheetVO<>();
        shadowTableSheet.setData(shadowTableExcelVOList);
        shadowTableSheet.setExcelModelClass(ShadowTableExcelVO.class);
        shadowTableSheet.setSheetName("隔离方案-" + IsolateTypeEnum.getName(resource.getIsolateType()));
        shadowTableSheet.setSheetNum(1);
        sheets.add(shadowTableSheet);
        try {
            ExcelUtils.exportExcelManySheet(response, resource.getName(), sheets);
        } catch (Exception e) {
            logger.error("配置导出错误: {}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 处理影子表
     *
     * @param resourceId
     * @param stringArrayListHashMap
     */
    private void processShadowTable(Long resourceId, Map<String, ArrayList<ArrayList<String>>> stringArrayListHashMap) {
        ArrayList<ArrayList<String>> isolateType_ShadowTable = stringArrayListHashMap.get("隔离方案-影子表");
        if (CollectionUtils.isEmpty(isolateType_ShadowTable)) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "隔离方案为空");
        }
        // 解析数据
        List<PressureResourceRelateTableInput> tableList = Lists.newArrayList();
        for (int i = 0; i < isolateType_ShadowTable.size(); i++) {
            ArrayList<String> mess = isolateType_ShadowTable.get(i);
            // 业务数据源地址
            String bussinessDatabase = mess.get(0);
            // 业务库
            String shadowDatabase = mess.get(2);
            // 业务表
            String businessTable = mess.get(3);
            // 影子表
            String shadowTable = mess.get(4);

            PressureResourceRelateTableInput input = new PressureResourceRelateTableInput();
            input.setDatabase(bussinessDatabase);
            input.setBusinessTable(businessTable);
            input.setShadowTable(shadowTable);

            // 导入数据
            tableList.add(input);
        }
        // 按照URL分组
        Map<String, List<PressureResourceRelateTableInput>> tableMap = tableList.stream().collect(Collectors.groupingBy(PressureResourceRelateTableInput::getDatabase));
        List<PressureResourceRelateTableEntity> tableEntityList = Lists.newArrayList();
        for (Map.Entry<String, List<PressureResourceRelateTableInput>> entry : tableMap.entrySet()) {
            // 数据源
            String database = entry.getKey();
            // 判断数据源是否存在
            PressureResourceDsQueryParam queryParam = new PressureResourceDsQueryParam();
            queryParam.setResourceId(resourceId);
            queryParam.setBussinessDatabase(database);
            List<PressureResourceRelateDsEntity> dsEntityList = pressureResourceRelateDsDAO.queryByParam(queryParam);
            if (CollectionUtils.isEmpty(dsEntityList)) {
                // TODO,新增数据源
                continue;
            }
            // 根据dsKey去更新 dsKey = resource_id + business_database
            String dsKey = DataSourceUtil.generateDsKey(resourceId, database, WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode());

            List<PressureResourceRelateTableInput> inputs = entry.getValue();
            for (int i = 0; i < inputs.size(); i++) {
                PressureResourceRelateTableInput input = inputs.get(i);
                PressureResourceRelateTableEntity entity = new PressureResourceRelateTableEntity();
                entity.setResourceId(resourceId);
                entity.setDsKey(dsKey);
                entity.setBusinessTable(input.getBusinessTable());
                entity.setShadowTable(input.getShadowTable());
                entity.setJoinFlag(JoinFlagEnum.NO.getCode());
                entity.setType(SourceTypeEnum.MANUAL.getCode());
                entity.setStatus(StatusEnum.NO.getCode());
                entity.setGmtCreate(new Date());
                entity.setTenantId(WebPluginUtils.traceTenantId());
                entity.setEnvCode(WebPluginUtils.traceEnvCode());
                tableEntityList.add(entity);
            }
        }
        // 批量保存
        pressureResourceRelateTableDAO.saveOrUpdate(tableEntityList);
    }

    /**
     * 处理影子库
     *
     * @param resourceId
     * @param stringArrayListHashMap
     */
    private void processShadowDB(Long resourceId, Map<String, ArrayList<ArrayList<String>>> stringArrayListHashMap) {
        ArrayList<ArrayList<String>> isolateType_Shadowdb = stringArrayListHashMap.get("隔离方案-影子库");
        if (CollectionUtils.isEmpty(isolateType_Shadowdb)) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "隔离方案");
        }
        // 解析列表值
        for (int i = 1; i < isolateType_Shadowdb.size(); i++) {
            ArrayList<String> mess = isolateType_Shadowdb.get(i);
            // 获取每列值
            String bussinessDatabase = mess.get(0);
            String shadowDatabase = mess.get(2);
            String userName = mess.get(3);
            String password = mess.get(4);

            // 按业务数据源查询，是否存在
            PressureResourceDsQueryParam queryParam = new PressureResourceDsQueryParam();
            queryParam.setResourceId(resourceId);
            queryParam.setBussinessDatabase(bussinessDatabase);
            List<PressureResourceRelateDsEntity> dsEntityList = pressureResourceRelateDsDAO.queryByParam(queryParam);
            if (CollectionUtils.isEmpty(dsEntityList)) {
                // 新增
                PressureResourceRelateDsEntity dsInput = new PressureResourceRelateDsEntity();
                dsInput.setResourceId(resourceId);
                dsInput.setBusinessDatabase(bussinessDatabase);
                dsInput.setShadowDatabase(shadowDatabase);
                dsInput.setShadowUserName(userName);
                dsInput.setShadowPassword(password);
                // 手工新增
                dsInput.setType(SourceTypeEnum.MANUAL.getCode());

                pressureResourceRelateDsMapper.insert(dsInput);
            } else {
                // update
                PressureResourceRelateDsEntity updateEntity = new PressureResourceRelateDsEntity();
                updateEntity.setShadowDatabase(shadowDatabase);
                updateEntity.setShadowUserName(userName);
                updateEntity.setShadowPassword(password);
                updateEntity.setGmtModified(new Date());

                QueryWrapper<PressureResourceRelateDsEntity> whereWrapper = new QueryWrapper<>();
                whereWrapper.eq("resource_id", resourceId);
                whereWrapper.eq("business_database", bussinessDatabase);
                pressureResourceRelateDsMapper.update(updateEntity, whereWrapper);
            }
        }
    }
}
