package io.shulie.takin.web.biz.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.pagehelper.PageHelper;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.common.util.PageInfo;
import com.pamirs.takin.entity.domain.entity.TShadowTableConfig;
import com.pamirs.takin.entity.domain.entity.TShadowTableDataSource;
import com.pamirs.takin.entity.domain.vo.TShadowTableConfigVo;
import com.pamirs.takin.entity.domain.vo.TShadowTableDatasourceVo;
import io.shulie.takin.web.biz.common.CommonService;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class ShadowTableConfigService extends CommonService {

    public static final int BATCH_SIZE = 200;

    /**
     * 提供给 pardar 插件  获取  影子表配置
     *
     * @param appName
     *
     * @return
     */
    public Map<String, Set<String>> agentGetShadowTable(String appName) {
        Map<String, Set<String>> appShadowTableMap = Maps.newHashMapWithExpectedSize(10);
        if (StringUtils.isEmpty(appName)) {
            appShadowTableMap.put("N|N|N", null);
            return appShadowTableMap;
        }
        appName = appName.trim();
        //给 pardar 影子表 Y|ip:port|schema  第一个代表 是否使用影子表 可能 用影子表 但是就是生产表 来搜索  不用去影子库  N 使用影子库
        ApplicationDetailResult applicationMnt = applicationDAO.getByName(appName);
        if (applicationMnt == null) {
            appShadowTableMap.put("N|N|N", null);
            return appShadowTableMap;
        }
        //先获取该数据源
        List<TShadowTableDataSource> shadowTableDataSourceList = tShadowTableDataSourceDao
                .queryShadowDataSourceByApplicationId(applicationMnt.getApplicationId());
        if (CollectionUtils.isEmpty(shadowTableDataSourceList)) {
            appShadowTableMap.put("N|N|N", null);
            return appShadowTableMap;
        }
        ;
        //在获取所有影子表
        List<TShadowTableConfig> shadowTableConfigList = tShadowTableConfigDao
                .queryShawdowTableConfigByShadowDatasourceIdList(
                        shadowTableDataSourceList.stream().map(TShadowTableDataSource::getShadowDatasourceId)
                                .collect(Collectors.toList()));
        shadowTableDataSourceList.forEach(shadowTableDataSource -> {
            boolean needShadowTable = false;
            StringBuilder sb = new StringBuilder();
            //判断该应用 某个数据源是否使用影子表
            if (Constants.INTEGER_USE.equals(shadowTableDataSource.getUseShadowTable())) {
                sb.append("Y");
                needShadowTable = true;
            } else {
                sb.append("N");
            }
            sb.append(Constants.SPLIT).append(shadowTableDataSource.getDatabaseIpport())
                    .append(Constants.SPLIT).append(shadowTableDataSource.getDatabaseName());
            //如果使用影子表  循环获取到的影子表 放入set ，否则就赛个空
            String shadowTableMapKey = sb.toString();
            appShadowTableMap.put(sb.toString(), Sets.newHashSetWithExpectedSize(1));
            if (needShadowTable) {
                shadowTableConfigList.forEach(shadowTable -> {
                    //如果数据源相等
                    if (shadowTable.getShadowDatasourceId().equals(shadowTableDataSource.getShadowDatasourceId())) {
                        if (appShadowTableMap.get(shadowTableMapKey) != null) {
                            appShadowTableMap.get(shadowTableMapKey).add(shadowTable.getShadowTableName());
                        } else {
                            Set<String> shadowTableNameSet = Sets.newHashSetWithExpectedSize(50);
                            shadowTableNameSet.add(shadowTable.getShadowTableName());
                            appShadowTableMap.put(shadowTableMapKey, shadowTableNameSet);
                        }
                    }
                });
            }
        });
        return appShadowTableMap;
    }


    public PageInfo<TShadowTableConfigVo> queryShadowTableConfigPage(Map<String, Object> paramMap) {
        PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        List<TShadowTableConfigVo> tableConfigVoList = tShadowTableConfigDao.queryShadowTableConfigPage(paramMap);
        return new PageInfo<>(CollectionUtils.isEmpty(tableConfigVoList) ? Lists.newArrayList() : tableConfigVoList);
    }

    /**
     * 更新 影子表配置
     *
     * @param shadowTableConfigVo
     *
     * @throws TakinModuleException
     */
    public void updateShadowTableConfig(TShadowTableConfigVo shadowTableConfigVo) throws TakinModuleException {
        if (StringUtils.isEmpty(shadowTableConfigVo.getId()) || StringUtils.isEmpty(
                shadowTableConfigVo.getShadowTableName())
                || shadowTableConfigVo.getEnableStatus() == null || StringUtils.isEmpty(
                shadowTableConfigVo.getShadowDatasourceId())) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_UPDATE_PARAM_EXCEPTION);
        }
        if (shadowTableConfigVo.getShadowTableName().length() > 127) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_TABLENAME_LONG_EXCEPTION);
        }
        TShadowTableConfig queryShadowTableConfig = tShadowTableConfigDao.selectByPrimaryKey(
                Long.parseLong(shadowTableConfigVo.getId()));
        if (queryShadowTableConfig == null) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_QUERY_ID_EXCEPTION);
        }
        TShadowTableDataSource queryShadowDatasource = tShadowTableDataSourceDao.selectByPrimaryKey(
                Long.parseLong(shadowTableConfigVo.getShadowDatasourceId()));
        if (queryShadowDatasource == null) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_DATASOURCE_ID_EXCEPTION);
        }
        List<TShadowTableConfig> existTableConfigs = tShadowTableConfigDao.queryShadowTableByDatasourceId(
                queryShadowDatasource.getShadowDatasourceId(),
                Arrays.asList(shadowTableConfigVo.getShadowTableName().toUpperCase()));
        //如果集合不为空 且 主键id 不相同  则认为是重复的
        if (CollectionUtils.isNotEmpty(existTableConfigs) && !existTableConfigs.get(0).getId().equals(
                Long.parseLong(shadowTableConfigVo.getId()))) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_UPDATE_SAME_EXCEPTION);
        }
        TShadowTableConfig shadowTableConfig = new TShadowTableConfig();
        BeanUtils.copyProperties(shadowTableConfigVo, shadowTableConfig);
        //特殊属性需要强转
        shadowTableConfig.setId(Long.parseLong(shadowTableConfigVo.getId()));
        shadowTableConfig.setShadowDatasourceId(Long.parseLong(shadowTableConfigVo.getShadowDatasourceId()));
        //入库的表明全是大写
        shadowTableConfig.setShadowTableName(shadowTableConfigVo.getShadowTableName().toUpperCase());
        tShadowTableConfigDao.updateByPrimaryKeySelective(shadowTableConfig);
    }

    /**
     * 通过主键批量删除影子表配置
     *
     * @param idList
     *
     * @throws TakinModuleException
     */
    public void deleteShadowTableByIdList(String idList) throws TakinModuleException {
        if (StringUtils.isEmpty(idList)) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_DELETE_EXCEPTION);
        }
        tShadowTableConfigDao.deleteByIdList(Splitter.on(",")
                .omitEmptyStrings()
                .trimResults()
                .splitToList(idList));
    }

    /**
     * 添加影子表时  获取该应用对应的 ip端口 和 库名
     *
     * @param applicationId
     *
     * @return
     */
    public List<TShadowTableConfigVo> queryApplicationDatabaseIpPortAndName(String applicationId) {
        return tShadowTableDataSourceDao.queryDatabaseIpPortAndName(applicationId);
    }

    /**
     * 新增影子表配置  批量
     *
     * @param shadowTableConfigVo
     *
     * @throws TakinModuleException
     */
    public void saveShadowTableConfig(TShadowTableConfigVo shadowTableConfigVo) throws TakinModuleException {
        if (StringUtils.isEmpty(shadowTableConfigVo.getShadowDatasourceId()) ||
                StringUtils.isEmpty(shadowTableConfigVo.getShadowTableName())
                || shadowTableConfigVo.getEnableStatus() == null) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_ADD_PARAM_EXCEPTION);
        }
        TShadowTableDataSource queryShadowDatasource = tShadowTableDataSourceDao.selectByPrimaryKey(
                Long.parseLong(shadowTableConfigVo.getShadowDatasourceId()));
        if (queryShadowDatasource == null) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_DATASOURCE_ID_EXCEPTION);
        }
        String[] talbeNames = shadowTableConfigVo.getShadowTableName().split(",");
        List<TShadowTableConfig> tableConfigList = new ArrayList<>(talbeNames.length);
        List<String> tableNameList = new ArrayList<>(talbeNames.length);
        for (String tableName : talbeNames) {
            if (tableName.length() > 127) {
                throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOWCONFIG_TABLENAME_LONG_EXCEPTION);
            }
            tableNameList.add(tableName.toUpperCase());
        }
        List<TShadowTableConfig> existTableConfigList = tShadowTableConfigDao.queryShadowTableByDatasourceId(
                queryShadowDatasource.getShadowDatasourceId(), tableNameList);
        for (String tableName : talbeNames) {
            boolean existTableName = false;
            for (TShadowTableConfig existTable : existTableConfigList) {
                if (existTable.getShadowTableName().equalsIgnoreCase(tableName)) {
                    existTableName = true;
                    break;
                }
            }
            if (!existTableName) {
                TShadowTableConfig tableConfig = new TShadowTableConfig(snowflake.next(),
                        queryShadowDatasource.getShadowDatasourceId(), tableName.toUpperCase(),
                        shadowTableConfigVo.getEnableStatus());
                tableConfigList.add(tableConfig);
            }
        }
        if (CollectionUtils.isNotEmpty(tableConfigList)) {
            tShadowTableConfigDao.insertList(tableConfigList);
        }
    }

    /**
     * 说明：查询应用下的数据库服务的IP端口
     *
     * @param applicationId 应用ID
     * @param dbName        数据库名称
     *
     * @return java.util.List<java.lang.String>
     *
     * @author shulie
     * @create 2019/3/14 10:43
     */
    public List<String> queryDatabaseIpPortList(String applicationId, String dbName) {
        return tShadowTableDataSourceDao.queryDatabaseIpPortList(applicationId, dbName);
    }

    /**
     * 说明：查询应用下的数据库服务的IP端口
     *
     * @param applicationId 应用ID
     * @param ipPort        数据库的IP端口号
     *
     * @return java.util.List<java.lang.String>
     *
     * @author shulie
     * @create 2019/3/14 10:43
     */
    public List<Map<String, Object>> queryDatabaseNameList(String applicationId, String ipPort) {
        List<Map<String, Object>> maps = tShadowTableDataSourceDao.queryDatabaseNameList(applicationId, ipPort);
        return maps;
    }

    /**
     * 查询使用影子库的 应用列表
     *
     * @param paramMap
     *
     * @return
     */
    public PageInfo<TShadowTableConfigVo> queryShadowDatabaseApplicationList(Map<String, Object> paramMap) {
        if (!StringUtils.equals("-1", MapUtils.getString(paramMap, "pageSize"))) {
            PageHelper.startPage(PageInfo.getPageNum(paramMap), PageInfo.getPageSize(paramMap));
        }
        List<TShadowTableConfigVo> shadowTableConfigVoList = tShadowTableDataSourceDao.queryShadowDBApplicationList(
                paramMap);
        return new PageInfo<>(shadowTableConfigVoList);
    }

    public void saveShadowDatasource(TShadowTableDatasourceVo shadowDatasourceVo) throws TakinModuleException {
        if (StringUtils.isEmpty(shadowDatasourceVo.getApplicationId()) || StringUtils.isEmpty(
                shadowDatasourceVo.getDatabaseIpport()) ||
                StringUtils.isEmpty(shadowDatasourceVo.getDatabaseName())
                || shadowDatasourceVo.getUseShadowTable() == null) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOW_DATASOURCE_SAVE_EXCEPTION);
        }
        //统一库名都是大写
        shadowDatasourceVo.setDatabaseName(shadowDatasourceVo.getDatabaseName().toUpperCase());
        Long datasourceId = tShadowTableDataSourceDao.queryShadowTableDatasourceId(
                Long.parseLong(shadowDatasourceVo.getApplicationId()), shadowDatasourceVo.getDatabaseName(),
                shadowDatasourceVo.getDatabaseIpport());
        if (datasourceId != null) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOW_DATASOURCE_EXIST_EXCEPTION);
        }
        TShadowTableDataSource shadowTableDataSource = new TShadowTableDataSource();
        BeanUtils.copyProperties(shadowDatasourceVo, shadowTableDataSource);
        shadowTableDataSource.setShadowDatasourceId(snowflake.next());
        shadowTableDataSource.setApplicationId(Long.parseLong(shadowDatasourceVo.getApplicationId()));
        tShadowTableDataSourceDao.insert(shadowTableDataSource);
    }

    public void updateShadowDatasource(TShadowTableDatasourceVo shadowDatasourceVo) throws TakinModuleException {
        if (StringUtils.isEmpty(shadowDatasourceVo.getShadowDatasourceId())) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOW_DATASOURCE_EXIST_EXCEPTION);
        }
        //如果属性都有看 是否更新为存在的数据源
        if (StringUtils.isNotEmpty(shadowDatasourceVo.getApplicationId()) && StringUtils.isNotEmpty(
                shadowDatasourceVo.getDatabaseName())
                && StringUtils.isNotEmpty(shadowDatasourceVo.getDatabaseIpport())) {
            Long datasourceId = tShadowTableDataSourceDao.queryShadowTableDatasourceId(
                    Long.parseLong(shadowDatasourceVo.getApplicationId()),
                    shadowDatasourceVo.getDatabaseName().toUpperCase(), shadowDatasourceVo.getDatabaseIpport());
            //id 不同 就报错 更新成一样的了
            if (datasourceId != null && !datasourceId.toString().equals(shadowDatasourceVo.getShadowDatasourceId())) {
                throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_SHADOW_DATASOURCE_EXIST_EXCEPTION);
            }
        }
        TShadowTableDataSource shadowTableDataSource = new TShadowTableDataSource();
        BeanUtils.copyProperties(shadowDatasourceVo, shadowTableDataSource);
        shadowTableDataSource.setShadowDatasourceId(Long.parseLong(shadowDatasourceVo.getShadowDatasourceId()));
        //如果有就大写下
        if (StringUtils.isNotEmpty(shadowDatasourceVo.getDatabaseName())) {
            shadowTableDataSource.setDatabaseName(shadowDatasourceVo.getDatabaseName().toUpperCase());
        }
        //如果有需要转换下
        if (StringUtils.isNotEmpty(shadowDatasourceVo.getApplicationId())) {
            shadowTableDataSource.setApplicationId(Long.parseLong(shadowDatasourceVo.getApplicationId()));
        }
        tShadowTableDataSourceDao.updateByPrimaryKeySelective(shadowTableDataSource);
    }

    /**
     * 获取使用影子库的 数据源 应用
     *
     * @return
     */
    public List<Map<String, Object>> getDatasourceApplication(String useShadowTable) {
        return transferElementToString(tShadowTableDataSourceDao.queryDatasourceApplicationdata(useShadowTable));
    }

}
