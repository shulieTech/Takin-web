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
import io.shulie.takin.web.biz.service.pressureresource.common.IsolateTypeEnum;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceDsVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelationAppVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelationDsVO;
import io.shulie.takin.web.biz.utils.xlsx.ExcelUtils;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mortbay.util.ajax.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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
