package io.shulie.takin.web.biz.service.dsManage.impl.v2;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.pamirs.attach.plugin.dynamic.one.Converter;
import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import io.shulie.takin.web.biz.convert.db.parser.DbTemplateParser;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsCreateInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationDsUpdateInputV2;
import io.shulie.takin.web.biz.pojo.response.application.ShadowDetailResponse;
import io.shulie.takin.web.biz.service.dsManage.AbstractShaDowManageService;
import io.shulie.takin.web.biz.service.pressureresource.common.DataSourceUtil;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbManageDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDsDbTableDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateDsDAO;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceRelateTableDAO;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbManageEntity;
import io.shulie.takin.web.data.model.mysql.ApplicationDsDbTableEntity;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntityV2;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntityV2;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;
import io.shulie.takin.web.data.result.application.ApplicationDsDbManageDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDsDbTableDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: 南风
 * @Date: 2021/9/2 5:51 下午
 */
@Service
public class ShaDowDbServiceImpl extends AbstractShaDowManageService {

    @Autowired
    private ApplicationDsDbManageDAO dbManageDAO;

    @Autowired
    private ApplicationDsDbTableDAO dsDbTableDAO;

    @Autowired
    private DbTemplateParser templateParser;

    @Autowired
    private PressureResourceRelateDsDAO pressureResourceRelateDsDAO;

    @Autowired
    private PressureResourceRelateTableDAO pressureResourceRelateTableDAO;

    /**
     * 更新影子配置方案
     *
     * @param inputV2
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateShadowProgramme(ApplicationDsUpdateInputV2 inputV2) {
        ApplicationDsDbManageEntity entity = this.buildEntity(inputV2, false);
        dbManageDAO.updateById(inputV2.getId(), entity);
        if (DsTypeEnum.SHADOW_TABLE.getCode().equals(entity.getDsType())) {
            this.saveShadowTable_V2(inputV2, inputV2.getId(), entity.getShaDowFileExtedn());
        }
    }


    /**
     * 创建影子配置方案
     *
     * @param inputV2
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createShadowProgramme(ApplicationDsCreateInputV2 inputV2, Boolean isJson) {
        //去重校验
        ApplicationDsDbManageDetailResult result = dbManageDAO.selectOne(inputV2.getApplicationName(),
                inputV2.getUrl(), inputV2.getUsername(), inputV2.getConnectionPool());
        boolean isInsertDb = true;
        if (Objects.nonNull(result)) {
            // 存在此数据了,但是从链路配置过来的,绑定一条关系
            if (inputV2.getResourceId() != null) {
                // 绑定关系
                bindPressourceDs(result.getId(), inputV2);
                isInsertDb = false; // 不需要新增了
            } else {
                throw new TakinWebException(TakinWebExceptionEnum.SHADOW_CONFIG_CREATE_ERROR, "业务数据源已存在");
            }
        }
        ApplicationDsDbManageEntity entity = this.buildEntity(inputV2, true);
        entity.setId(null); //去除原id
        if (isInsertDb) {
            dbManageDAO.saveOne(entity);
            // 新增的时候,直接绑定一条关系
            if (inputV2.getResourceId() != null) {
                // 绑定关系
                bindPressourceDs(entity.getId(), inputV2);
            }
        } else {
            entity.setId(result.getId());
        }
        if (DsTypeEnum.SHADOW_TABLE.getCode().equals(entity.getDsType())) {
            this.saveShadowTable_V2(inputV2, entity.getId(), entity.getShaDowFileExtedn());
        }
    }

    private void bindPressourceDs(Long relateId, ApplicationDsCreateInputV2 inputV2) {
        PressureResourceRelateDsEntityV2 dsEntity = new PressureResourceRelateDsEntityV2();
        dsEntity.setResourceId(inputV2.getResourceId());
        dsEntity.setDetailId(inputV2.getDetailId());
        dsEntity.setAppName(inputV2.getApplicationName());
        dsEntity.setBusinessUserName(inputV2.getUsername());
        dsEntity.setRelateId(relateId);
        dsEntity.setBusinessDatabase(inputV2.getUrl());
        dsEntity.setType(inputV2.getType());
        dsEntity.setTenantId(WebPluginUtils.traceTenantId());
        dsEntity.setEnvCode(WebPluginUtils.traceEnvCode());
        dsEntity.setGmtCreate(new Date());
        pressureResourceRelateDsDAO.add_v2(Arrays.asList(dsEntity));
    }

    private ApplicationDsDbManageEntity buildEntity(ApplicationDsCreateInputV2 inputV2, Boolean isCreate) {
        ApplicationDsDbManageEntity entity = new ApplicationDsDbManageEntity();
        WebPluginUtils.fillUserData(inputV2);
        BeanUtils.copyProperties(inputV2, entity);
        entity.setDsType(inputV2.getDsType());
        entity.setShaDowUrl(inputV2.getShaDowUrl());
        entity.setShaDowUserName(inputV2.getShaDowUserName());
        entity.setShaDowPwd(inputV2.getShaDowPassword());
        entity.setUrl(inputV2.getUrl());
        entity.setUserName(Objects.equals("-", inputV2.getUsername()) ? "" : inputV2.getUsername());
        entity.setConnPoolName(inputV2.getConnectionPool());
        entity.setStatus(0);
        String extInfo = inputV2.getExtInfo();
        if (DsTypeEnum.SHADOW_TABLE.getCode().equals(inputV2.getDsType())) {
            extInfo = JSONObject.parseObject(inputV2.getExtInfo()).get("shaDowTaleInfo").toString();
        }
        entity.setShaDowFileExtedn(extInfo);

        if (isCreate) {
            entity.setDbName("");
            entity.setPwd("");
            entity.setConfigJson("");
            entity.setSource(1);
            // 链路梳理而来
            if (inputV2.getType() != null && inputV2.getType() == 1) {
                entity.setSource(0);
            }
            entity.setFileExtedn(inputV2.getParseConfig());
            entity.setConfigJson(inputV2.getIsOld() ? "老转新" : "");
            if ("other".equals(entity.getConnPoolName())) {
                entity.setAgentSourceType(Converter.TemplateConverter.TemplateEnum._default.getKey());
            } else if ("兼容老版本(影子表)".equals(entity.getConnPoolName())
                    || "兼容老版本(影子库)".equals(entity.getConnPoolName())) {
                entity.setAgentSourceType(Converter.TemplateConverter.TemplateEnum._2.getKey());
            } else {
                Converter.TemplateConverter.TemplateEnum templateEnum = templateParser.convert(entity.getConnPoolName());
                if (templateEnum != null) {
                    entity.setAgentSourceType(templateEnum.getKey());
                }
            }
        }
        return entity;
    }

    /**
     * 保存影子表v2
     *
     * @param recordId
     * @param str
     */
    private void saveShadowTable_V2(ApplicationDsCreateInputV2 inputV2, Long recordId, String str) {
        if (StringUtils.isBlank(str)) {
            return;
        }
        ApplicationDsDbManageDetailResult detailResult = dbManageDAO.selectOneById(recordId);
        if (Objects.isNull(detailResult)) {
            return;
        }
        // 历史数据集合
        List<ApplicationDsDbTableDetailResult> list = dsDbTableDAO.getList(detailResult.getUrl(), detailResult.getApplicationId(),
                detailResult.getUserName());
        // 新数据集合
        List<ShadowDetailResponse.TableInfo> tableInfos = JSONArray.parseArray(str, ShadowDetailResponse.TableInfo.class);
        List<ApplicationDsDbTableDetailResult> collect = tableInfos.stream().map(x -> {
            ApplicationDsDbTableDetailResult result = new ApplicationDsDbTableDetailResult();
            result.setAppId(detailResult.getApplicationId());
            result.setUrl(detailResult.getUrl());
            result.setUserName(detailResult.getUserName());
            result.setBizDataBase(x.getBizDatabase());
            result.setBizTable(x.getBizTableName());
            result.setShadowTable(x.getShaDowTableName());
            result.setManualTag(x.getIsManual() ? 1 : 0);
            result.setIsCheck(x.getIsCheck() ? 1 : 0);
            return result;
        }).collect(Collectors.toList());

        Map<String, List<ApplicationDsDbTableDetailResult>> oldMap = list.stream().collect(Collectors.groupingBy(it -> fetchKey(it)));
        Map<String, List<ApplicationDsDbTableDetailResult>> newMap = collect.stream().collect(Collectors.groupingBy(it -> fetchKey(it)));

        // 自动梳理的不需要删除
        if (inputV2.getResourceId() == null) {
            // 删除，旧的数据不在新的数据里面的
            List<String> delKeyList = oldMap.keySet().stream().filter(nKey -> !newMap.keySet().contains(nKey)).collect(Collectors.toList());
            List<Long> delIds = Lists.newArrayList();
            if (CollectionUtils.isNotEmpty(delKeyList)) {
                delKeyList.stream().forEach(delKey -> {
                    oldMap.get(delKey).stream().forEach(old -> {
                        delIds.add(old.getId());
                    });
                });
                if (CollectionUtils.isNotEmpty(delIds)) {
                    dsDbTableDAO.batchDeleted_V2(delIds);

                    // 删除绑定关系
                    PressureResourceTableQueryParam queryParam = new PressureResourceTableQueryParam();
                    queryParam.setRelateIds(delIds);
                    pressureResourceRelateTableDAO.deleteByParam(queryParam);
                }
            }
        }
        // 新增,新的key数据不在旧的里面
        List<String> insertKeyList = newMap.keySet().stream().filter(nKey -> !oldMap.keySet().contains(nKey)).collect(Collectors.toList());
        List<ApplicationDsDbTableDetailResult> insertList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(insertKeyList)) {
            insertKeyList.stream().forEach(insertKey -> {
                insertList.addAll(newMap.get(insertKey));
            });
            List<ApplicationDsDbTableEntity> insertEntitys = dsDbTableDAO.batchSave_ext(insertList);
            // 新增Id,绑定关系
            if (inputV2.getResourceId() != null) {
                List<PressureResourceRelateTableEntityV2> relateTableList = insertEntitys.stream().map(table -> {
                    PressureResourceRelateTableEntityV2 tableEntityV2 = new PressureResourceRelateTableEntityV2();
                    tableEntityV2.setRelateId(table.getId());
                    tableEntityV2.setResourceId(inputV2.getResourceId());
                    tableEntityV2.setDsKey(DataSourceUtil.generateDsKey_ext(inputV2.getUrl(), inputV2.getUsername()));
                    tableEntityV2.setType(inputV2.getType() == null ? 0 : inputV2.getType());
                    tableEntityV2.setTenantId(WebPluginUtils.traceTenantId());
                    tableEntityV2.setEnvCode(WebPluginUtils.traceEnvCode());
                    tableEntityV2.setGmtCreate(new Date());
                    return tableEntityV2;
                }).collect(Collectors.toList());
                pressureResourceRelateTableDAO.add_V2(relateTableList);
            }
        }

        // 修改,交集,绑定关系
        List<String> updateKeyList = oldMap.keySet().stream().filter(nKey -> newMap.keySet().contains(nKey)).collect(Collectors.toList());
        List<PressureResourceRelateTableEntityV2> relateTableList = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(updateKeyList)) {
            if (inputV2.getResourceId() == null) {
                updateKeyList.stream().forEach(updateKey -> {
                    oldMap.get(updateKey).stream().forEach(oldValue -> {
                        ApplicationDsDbTableEntity update = new ApplicationDsDbTableEntity();
                        update.setId(oldValue.getId());
                        update.setGmtUpdate(new Date());
                        // 获取新的数据
                        List<ApplicationDsDbTableDetailResult> newValues = newMap.get(updateKey);
                        if (CollectionUtils.isNotEmpty(newValues)) {
                            update.setIsCheck(newValues.get(0).getIsCheck());
                            dsDbTableDAO.update_v2(update);
                        }
                    });
                });
            }
            if (inputV2.getResourceId() != null) {
                // 关系绑定
                updateKeyList.stream().forEach(updateKey -> {
                    List<PressureResourceRelateTableEntityV2> tmpList = oldMap.get(updateKey).stream().map(table -> {
                        PressureResourceRelateTableEntityV2 tableEntityV2 = new PressureResourceRelateTableEntityV2();
                        tableEntityV2.setResourceId(inputV2.getResourceId());
                        tableEntityV2.setRelateId(table.getId());
                        tableEntityV2.setDsKey(DataSourceUtil.generateDsKey_ext(inputV2.getUrl(), inputV2.getUsername()));
                        tableEntityV2.setType(inputV2.getType() == null ? 0 : inputV2.getType());
                        tableEntityV2.setTenantId(WebPluginUtils.traceTenantId());
                        tableEntityV2.setEnvCode(WebPluginUtils.traceEnvCode());
                        tableEntityV2.setGmtCreate(new Date());
                        return tableEntityV2;
                    }).collect(Collectors.toList());
                    relateTableList.addAll(tmpList);
                });
                pressureResourceRelateTableDAO.add_V2(relateTableList);
            }
        }
    }

    private String fetchKey(ApplicationDsDbTableDetailResult it) {
        return String.format("%d#%s#%s#%s", it.getAppId(), it.getUrl(), it.getBizDataBase(), it.getBizTable());
    }

    private void saveShadowTable(Long recordId, String str) {
        if (StringUtils.isBlank(str)) {
            return;
        }
        ApplicationDsDbManageDetailResult detailResult = dbManageDAO.selectOneById(recordId);
        if (Objects.isNull(detailResult)) {
            return;
        }
        List<ApplicationDsDbTableDetailResult> list = dsDbTableDAO.getList(detailResult.getUrl(), detailResult.getApplicationId(),
                detailResult.getUserName());
        if (CollectionUtils.isNotEmpty(list)) {
            dsDbTableDAO.batchDeleted(list);
        }
        List<ShadowDetailResponse.TableInfo> tableInfos = JSONArray.parseArray(str, ShadowDetailResponse.TableInfo.class);
        List<ApplicationDsDbTableDetailResult> collect = tableInfos.stream().map(x -> {
            ApplicationDsDbTableDetailResult result = new ApplicationDsDbTableDetailResult();
            result.setAppId(detailResult.getApplicationId());
            result.setUrl(detailResult.getUrl());
            result.setUserName(detailResult.getUserName());
            result.setBizDataBase(x.getBizDatabase());
            result.setBizTable(x.getBizTableName());
            result.setShadowTable(x.getShaDowTableName());
            result.setManualTag(x.getIsManual() ? 1 : 0);
            result.setIsCheck(x.getIsCheck() ? 1 : 0);
            return result;
        }).collect(Collectors.toList());
        dsDbTableDAO.batchSave(collect);
    }

}
