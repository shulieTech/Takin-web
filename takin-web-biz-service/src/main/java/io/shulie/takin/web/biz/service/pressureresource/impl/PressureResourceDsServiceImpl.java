package io.shulie.takin.web.biz.service.pressureresource.impl;

import cn.hutool.core.collection.ListUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.ExtInfo;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationDsInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationDsRequest;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationTableInput;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceDsService;
import io.shulie.takin.web.biz.service.pressureresource.common.DbNameUtil;
import io.shulie.takin.web.biz.service.pressureresource.common.IsolateTypeEnum;
import io.shulie.takin.web.biz.service.pressureresource.vo.*;
import io.shulie.takin.web.biz.utils.xlsx.ExcelUtils;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.vo.excel.ExcelSheetVO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationAppDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationDsDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelationTableDAO;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceMapper;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceRelationDsMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationAppEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationDsEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationTableEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceAppQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDsQueryParam;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;
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
    private PressureResourceRelationDsDAO pressureResourceRelationDsDAO;

    @Resource
    private PressureResourceRelationTableDAO pressureResourceRelationTableDAO;

    @Resource
    private PressureResourceRelationDsMapper pressureResourceRelationDsMapper;

    @Resource
    private PressureResourceMapper pressureResourceMapper;

    @Resource
    private PressureResourceRelationAppDAO pressureResourceRelationAppDAO;

    /**
     * 新增
     *
     * @param input
     */
    @Override
    public void add(PressureResourceRelationDsInput input) {
        // 批量给拆分到不同应用里面去，应用视角需要分页查询
        List<String> appNames = input.getRelationApps();
        if (CollectionUtils.isEmpty(appNames)) {
            return;
        }
        // 判断数据源是否已存在
        PressureResourceDsQueryParam param = new PressureResourceDsQueryParam();
        param.setBussinessDatabase(input.getBusinessDatabase());
        param.setResourceId(input.getResourceId());
        List<PressureResourceRelationDsEntity> list = pressureResourceRelationDsDAO.queryByParam(param);
        if (CollectionUtils.isNotEmpty(list)) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "数据源已存在");
        }
        List<PressureResourceRelationDsEntity> dsEntitys = appNames.stream().map(appName -> {
            PressureResourceRelationDsEntity tmpEntity = new PressureResourceRelationDsEntity();

            BeanUtils.copyProperties(input, tmpEntity);
            tmpEntity.setAppName(appName);
            tmpEntity.setGmtCreate(new Date());

            ExtInfo extInfo = input.getExtInfo();
            tmpEntity.setExtInfo(JSON.toString(extInfo));
            return tmpEntity;
        }).collect(Collectors.toList());
        pressureResourceRelationDsDAO.add(dsEntitys);
    }

    /**
     * 数据源视图页面,内存分页
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelationDsVO> listByDs(PressureResourceRelationDsRequest request) {
        // 查询所有的数据源信息
        PressureResourceDsQueryParam param = new PressureResourceDsQueryParam();
        param.setResourceId(request.getResourceId());
        param.setQueryBussinessDatabase(request.getQueryBusinessDataBase());

        List<PressureResourceRelationDsEntity> dsList = pressureResourceRelationDsDAO.queryByParam(param);
        // 相同数据源合并
        List<PressureResourceRelationDsVO> listVO = Lists.newArrayList();
        Map<String, List<PressureResourceRelationDsEntity>> dsMap = dsList.stream().collect(Collectors.groupingBy(ds -> ds.getBusinessDatabase()));
        for (Map.Entry<String, List<PressureResourceRelationDsEntity>> entry : dsMap.entrySet()) {
            List<PressureResourceRelationDsEntity> tmpList = entry.getValue();
            List<String> appNames = tmpList.stream().map(ds -> ds.getAppName()).collect(Collectors.toList());
            PressureResourceRelationDsVO tmpVO = new PressureResourceRelationDsVO();
            BeanUtils.copyProperties(tmpList.get(0), tmpVO);
            tmpVO.setDatabase(DbNameUtil.getDbName(tmpList.get(0).getBusinessDatabase()));

            tmpVO.setId(String.valueOf(tmpList.get(0).getId()));
            tmpVO.setResourceId(String.valueOf(tmpList.get(0).getResourceId()));
            if (StringUtils.isNotBlank(tmpVO.getBusinessDatabase())) {
                String bussinessDatabase = tmpVO.getBusinessDatabase();
                if (bussinessDatabase.indexOf("/") > 0) {
                    tmpVO.setDatabase(bussinessDatabase.substring(bussinessDatabase.indexOf("/") + 1));
                }
            }
            // 通过应用获取是否加入压测范围
            PressureResourceAppQueryParam appQueryParam = new PressureResourceAppQueryParam();
            appQueryParam.setAppNames(appNames);
            appQueryParam.setResourceId(request.getResourceId());
            List<PressureResourceRelationAppEntity> appEntitys = pressureResourceRelationAppDAO.queryList(appQueryParam);
            if (CollectionUtils.isNotEmpty(appEntitys)) {
                Map<String, List<PressureResourceRelationAppEntity>> appMap = appEntitys.stream().collect(Collectors.groupingBy(app -> app.getAppName()));
                List<PressureResourceRelationAppVO> appVOList = appNames.stream().map(app -> {
                    PressureResourceRelationAppVO appVO = new PressureResourceRelationAppVO();
                    appVO.setAppName(app);
                    appVO.setJoinPressure(appMap.get(app).get(0).getJoinPressure());
                    return appVO;
                }).collect(Collectors.toList());
                tmpVO.setAppList(appVOList);
            }
            tmpVO.setSize(tmpList.size());
            listVO.add(tmpVO);
        }
        List<PressureResourceRelationDsVO> pageList = ListUtil.page(request.getCurrentPage(), request.getPageSize(), listVO);
        return PagingList.of(pageList, listVO.size());
    }

    /**
     * 应用视图
     *
     * @param request
     * @return
     */
    @Override
    public PagingList<PressureResourceRelationDsVO> listByApp(PressureResourceRelationDsRequest request) {
        // 查询所有的数据源信息
        PressureResourceDsQueryParam param = new PressureResourceDsQueryParam();
        param.setResourceId(request.getResourceId());
        param.setQueryAppName(request.getQueryAppName());
        List<PressureResourceRelationDsEntity> dsList = pressureResourceRelationDsDAO.queryByParam(param);
        // 相同数据源合并
        List<PressureResourceRelationDsVO> listVO = Lists.newArrayList();
        Map<String, List<PressureResourceRelationDsEntity>> appMap = dsList.stream().collect(Collectors.groupingBy(ds -> ds.getAppName()));
        for (Map.Entry<String, List<PressureResourceRelationDsEntity>> entry : appMap.entrySet()) {
            List<PressureResourceRelationDsEntity> tmpList = entry.getValue();
            PressureResourceRelationDsVO tmpVO = new PressureResourceRelationDsVO();
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
        List<PressureResourceRelationDsVO> pageList = ListUtil.page(request.getCurrentPage(), request.getPageSize(), listVO);
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
        PressureResourceEntity resourceEntity = pressureResourceMapper.selectById(resourceId);
        if (resourceEntity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_QUERY_ERROR, "资源配置未获取到");
        }
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
        List<PressureResourceRelationDsEntity> dsEntityList = pressureResourceRelationDsDAO.queryByParam(dsQueryParam);
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
        List<PressureResourceRelationDsEntity> dsEntityList = pressureResourceRelationDsDAO.queryByParam(dsQueryParam);
        List<ExcelSheetVO<?>> sheets = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dsEntityList)) {
            // 查询影子表
            PressureResourceTableQueryParam tableQueryParam = new PressureResourceTableQueryParam();
            tableQueryParam.setResourceId(resource.getId());
            List<PressureResourceRelationTableEntity> tableEntityList = pressureResourceRelationTableDAO.queryList(tableQueryParam);
            // 按照数据源分组下
            if (CollectionUtils.isNotEmpty(tableEntityList)) {
                Map<String, List<PressureResourceRelationTableEntity>> tableEntityMap = tableEntityList.stream().collect(Collectors.groupingBy(item -> String.valueOf(item.getDsId())));
                Map<String, List<PressureResourceRelationDsEntity>> dsMap = dsEntityList.stream().collect(Collectors.groupingBy(item -> String.valueOf(item.getId())));
                List<ShadowTableExcelVO> shadowTableExcelVOList = Lists.newArrayList();
                for (Map.Entry<String, List<PressureResourceRelationTableEntity>> entry : tableEntityMap.entrySet()) {
                    String dsId = entry.getKey();
                    PressureResourceRelationDsEntity tmpDs = dsMap.get(dsId).stream().findFirst().orElse(new PressureResourceRelationDsEntity());
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
                ExcelSheetVO<ShadowTableExcelVO> shadowTableSheet = new ExcelSheetVO<>();
                shadowTableSheet.setData(shadowTableExcelVOList);
                shadowTableSheet.setExcelModelClass(ShadowTableExcelVO.class);
                shadowTableSheet.setSheetName("隔离方案-" + IsolateTypeEnum.getName(resource.getIsolateType()));
                shadowTableSheet.setSheetNum(1);
                sheets.add(shadowTableSheet);
            }
        }
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
        List<PressureResourceRelationTableInput> tableList = Lists.newArrayList();
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

            PressureResourceRelationTableInput input = new PressureResourceRelationTableInput();
            input.setDatabase(bussinessDatabase);
            input.setBusinessTable(businessTable);
            input.setShadowTable(shadowTable);

            tableList.add(input);
        }
        // 按照URL分组
        Map<String, List<PressureResourceRelationTableInput>> tableMap = tableList.stream().collect(Collectors.groupingBy(PressureResourceRelationTableInput::getDatabase));
        List<PressureResourceRelationTableEntity> tableEntityList = Lists.newArrayList();
        for (Map.Entry<String, List<PressureResourceRelationTableInput>> entry : tableMap.entrySet()) {
            // 数据源
            String database = entry.getKey();
            // 判断数据源是否存在
            PressureResourceDsQueryParam queryParam = new PressureResourceDsQueryParam();
            queryParam.setResourceId(resourceId);
            queryParam.setBussinessDatabase(database);
            List<PressureResourceRelationDsEntity> dsEntityList = pressureResourceRelationDsDAO.queryByParam(queryParam);
            if (CollectionUtils.isEmpty(dsEntityList)) {
                throw new TakinWebException(TakinWebExceptionEnum.PRESSURE_RESOURCE_OP_ERROR, "数据源不存在");
            }
            // 获取数据源Id
            Long dsId = dsEntityList.get(0).getId();
            List<PressureResourceRelationTableInput> inputs = entry.getValue();
            for (int i = 0; i < inputs.size(); i++) {
                PressureResourceRelationTableInput input = inputs.get(i);
                PressureResourceRelationTableEntity entity = new PressureResourceRelationTableEntity();
                entity.setResourceId(resourceId);
                entity.setDsId(dsId);
                entity.setBusinessTable(input.getBusinessTable());
                entity.setShadowTable(input.getShadowTable());
                entity.setJoinFlag(0);
                entity.setGmtCreate(new Date());

                tableEntityList.add(entity);
            }
        }
        // 批量保存
        pressureResourceRelationTableDAO.saveOrUpdate(tableEntityList);
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
            List<PressureResourceRelationDsEntity> dsEntityList = pressureResourceRelationDsDAO.queryByParam(queryParam);
            if (CollectionUtils.isEmpty(dsEntityList)) {
                // 新增
                PressureResourceRelationDsEntity dsInput = new PressureResourceRelationDsEntity();
                dsInput.setResourceId(resourceId);
                dsInput.setBusinessDatabase(bussinessDatabase);
                dsInput.setShadowDatabase(shadowDatabase);
                dsInput.setShadowUserName(userName);
                dsInput.setShadowPassword(password);
                pressureResourceRelationDsMapper.insert(dsInput);
            } else {
                // update
                PressureResourceRelationDsEntity updateEntity = new PressureResourceRelationDsEntity();
                updateEntity.setShadowDatabase(shadowDatabase);
                updateEntity.setShadowUserName(userName);
                updateEntity.setShadowPassword(password);
                updateEntity.setGmtModified(new Date());

                QueryWrapper<PressureResourceRelationDsEntity> whereWrapper = new QueryWrapper<>();
                whereWrapper.eq("resource_id", resourceId);
                whereWrapper.eq("business_database", bussinessDatabase);
                pressureResourceRelationDsMapper.update(updateEntity, whereWrapper);
            }
        }
    }
}
