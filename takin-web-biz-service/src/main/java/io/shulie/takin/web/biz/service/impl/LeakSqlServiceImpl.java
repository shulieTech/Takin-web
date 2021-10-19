package io.shulie.takin.web.biz.service.impl;

import java.util.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pamirs.takin.common.util.AESUtil;
import com.pamirs.takin.common.util.JdbcConnection;
import com.pamirs.takin.common.util.SqlParseUtil;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneManageIdReq;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlBatchRefsRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlCreateRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlDetailRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.LeakSqlUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.leakcheck.SqlTestRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.VerifyTaskConfig;
import io.shulie.takin.web.biz.pojo.response.leakcheck.LeakSqlBatchRefsResponse;
import io.shulie.takin.web.biz.pojo.response.leakcheck.LeakSqlRefsResponse;
import io.shulie.takin.web.biz.pojo.response.leakcheck.SingleSqlDetailResponse;
import io.shulie.takin.web.biz.service.LeakSqlService;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.data.dao.datasource.DataSourceDAO;
import io.shulie.takin.web.data.dao.leakcheck.LeakCheckConfigDAO;
import io.shulie.takin.web.data.dao.leakcheck.LeakCheckConfigDetailDAO;
import io.shulie.takin.web.data.param.datasource.DataSourceQueryParam;
import io.shulie.takin.web.data.param.datasource.DataSourceSingleQueryParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigCreateParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigDeleteParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigDetailCreateParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigDetailDeleteParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigDetailQueryParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigQueryParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigSingleQueryParam;
import io.shulie.takin.web.data.param.leakcheck.LeakCheckConfigUpdateParam;
import io.shulie.takin.web.data.result.datasource.DataSourceResult;
import io.shulie.takin.web.data.result.leakcheck.LeakCheckConfigBatchDetailResult;
import io.shulie.takin.web.data.result.leakcheck.LeakCheckConfigResult;
import io.shulie.takin.web.data.result.leakcheck.LeakCheckConfigSingleDetailResult;
import io.shulie.takin.web.diff.api.scenemanage.SceneManageApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author fanxx
 * @date 2020/12/31 3:30 下午
 */
@Slf4j
@Component
public class LeakSqlServiceImpl implements LeakSqlService {

    @Autowired
    private LeakCheckConfigDAO checkConfigDAO;

    @Autowired
    private LeakCheckConfigDetailDAO checkConfigDetailDAO;

    @Autowired
    private DataSourceDAO dataSourceDAO;

    @Autowired
    private SceneManageApi sceneManageApi;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void createLeakCheckConfig(LeakSqlCreateRequest createRequest) {
        Long businessActivityId = createRequest.getBusinessActivityId();
        Long datasourceId = createRequest.getDatasourceId();
        List<String> sqlList = createRequest.getSqls();
        //验证sql是否合法
        sqlList.forEach(this::checkSql);

        //验证sql是否可正常运行
        SqlTestRequest sqlTestRequest = new SqlTestRequest();
        sqlTestRequest.setDatasourceId(datasourceId);
        sqlTestRequest.setSqls(sqlList);
        String message = testSqlConnection(sqlTestRequest);
        if (StringUtils.isNotBlank(message)) {
            throw new TakinWebException(ExceptionCode.LEAK_SQL_REF_RUN_ERROR, "sql验证失败");
        }

        LeakCheckConfigSingleQueryParam singleQueryParam = new LeakCheckConfigSingleQueryParam();
        singleQueryParam.setBusinessActivityId(businessActivityId);
        singleQueryParam.setDatasourceId(datasourceId);
        LeakCheckConfigResult oldLeakCheckConfigResult = checkConfigDAO.selectSingle(singleQueryParam);
        if (!Objects.isNull(oldLeakCheckConfigResult)) {
            LeakSqlUpdateRequest updateRequest = new LeakSqlUpdateRequest();
            updateRequest.setBusinessActivityId(businessActivityId);
            updateRequest.setDatasourceId(datasourceId);
            List<Long> oldSqlIdList = Arrays.stream(oldLeakCheckConfigResult.getLeakcheckSqlIds().split(",")).map(
                    Long::parseLong).distinct().collect(Collectors.toList());
            LeakCheckConfigDetailQueryParam detailQueryParam = new LeakCheckConfigDetailQueryParam();
            detailQueryParam.setIds(oldSqlIdList);
            LeakCheckConfigSingleDetailResult detailResult = checkConfigDetailDAO.selectSingle(detailQueryParam);
            if (!Objects.isNull(detailResult) && CollectionUtils.isNotEmpty(detailResult.getSqls())) {
                sqlList.addAll(detailResult.getSqls());

            }
            Map<String, String> sqlMap = Maps.newHashMap();
            sqlList.forEach(s -> sqlMap.put(s.replace(" ", ""), s));
            sqlList = new ArrayList<>(sqlMap.values());
            updateRequest.setSqls(sqlList);
            updateLeakCheckConfig(updateRequest);
        } else {
            sqlList = sqlList.stream().distinct().collect(Collectors.toList());
            LeakCheckConfigDetailCreateParam detailCreateParam = new LeakCheckConfigDetailCreateParam();
            detailCreateParam.setDatasourceId(datasourceId);
            detailCreateParam.setSqlList(sqlList);
            List<Long> sqlIdList = checkConfigDetailDAO.insert(detailCreateParam);

            LeakCheckConfigCreateParam checkConfigCreateParam = new LeakCheckConfigCreateParam();
            checkConfigCreateParam.setBusinessActivityId(businessActivityId);
            checkConfigCreateParam.setDatasourceId(datasourceId);
            checkConfigCreateParam.setSqlIdList(sqlIdList);
            checkConfigDAO.insert(checkConfigCreateParam);
        }
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void updateLeakCheckConfig(LeakSqlUpdateRequest updateRequest) {
        Long businessActivityId = updateRequest.getBusinessActivityId();
        Long datasourceId = updateRequest.getDatasourceId();
        List<String> sqlList = updateRequest.getSqls();
        //验证sql是否合法
        sqlList.forEach(this::checkSql);
        LeakCheckConfigSingleQueryParam singleQueryParam = new LeakCheckConfigSingleQueryParam();
        singleQueryParam.setBusinessActivityId(businessActivityId);
        singleQueryParam.setDatasourceId(datasourceId);
        LeakCheckConfigResult oldLeakCheckConfigResult = checkConfigDAO.selectSingle(singleQueryParam);
        if (!Objects.isNull(oldLeakCheckConfigResult)
                && StringUtils.isNotBlank(oldLeakCheckConfigResult.getLeakcheckSqlIds())) {
            //先清空原有sql详情配置
            LeakCheckConfigDetailDeleteParam detailDeleteParam = new LeakCheckConfigDetailDeleteParam();
            String oldSqlIdString = oldLeakCheckConfigResult.getLeakcheckSqlIds();
            List<Long> oldSqlIdList = Arrays.stream(oldSqlIdString.split(",")).map(Long::parseLong).distinct().collect(
                    Collectors.toList());
            detailDeleteParam.setIds(oldSqlIdList);
            checkConfigDetailDAO.delete(detailDeleteParam);
        }

        // 新增sql详情配置
        LeakCheckConfigDetailCreateParam detailCreateParam = new LeakCheckConfigDetailCreateParam();
        detailCreateParam.setDatasourceId(datasourceId);
        sqlList = sqlList.stream().distinct().collect(Collectors.toList());
        Map<String, String> sqlMap = Maps.newHashMap();
        sqlList.forEach(s -> sqlMap.put(s.replace(" ", ""), s));
        sqlList = new ArrayList<>(sqlMap.values());
        detailCreateParam.setSqlList(sqlList);
        List<Long> sqlIdList = checkConfigDetailDAO.insert(detailCreateParam);

        // 更新基础配置
        LeakCheckConfigUpdateParam checkConfigUpdateParam = new LeakCheckConfigUpdateParam();
        checkConfigUpdateParam.setBusinessActivityId(businessActivityId);
        checkConfigUpdateParam.setDatasourceId(datasourceId);
        checkConfigUpdateParam.setSqlIdList(sqlIdList);
        checkConfigDAO.update(checkConfigUpdateParam);
    }

    @Override
    public void deleteLeakCheckConfig(LeakSqlDeleteRequest deleteRequest) {
        Long businessActivityId = deleteRequest.getBusinessActivityId();
        Long datasourceId = deleteRequest.getDatasourceId();
        LeakCheckConfigSingleQueryParam singleQueryParam = new LeakCheckConfigSingleQueryParam();
        singleQueryParam.setBusinessActivityId(businessActivityId);
        singleQueryParam.setDatasourceId(datasourceId);
        LeakCheckConfigResult oldLeakCheckConfigResult = checkConfigDAO.selectSingle(singleQueryParam);
        if (!Objects.isNull(oldLeakCheckConfigResult)) {
            //先删除sql详情配置
            LeakCheckConfigDetailDeleteParam detailDeleteParam = new LeakCheckConfigDetailDeleteParam();
            String oldSqlIdString = oldLeakCheckConfigResult.getLeakcheckSqlIds();
            List<Long> oldSqlIdList = Arrays.stream(oldSqlIdString.split(",")).map(Long::parseLong).distinct().collect(
                    Collectors.toList());
            detailDeleteParam.setIds(oldSqlIdList);
            checkConfigDetailDAO.delete(detailDeleteParam);

            //再删除数据源基础配置
            LeakCheckConfigDeleteParam checkConfigDeleteParam = new LeakCheckConfigDeleteParam();
            checkConfigDeleteParam.setIds(Collections.singletonList(oldLeakCheckConfigResult.getId()));
            checkConfigDAO.delete(checkConfigDeleteParam);
        }
    }

    @Override
    public LeakSqlRefsResponse getLeakCheckConfigDetail(LeakSqlDetailRequest detailRequest) {
        Long businessActivityId = detailRequest.getBusinessActivityId();
        Long datasourceId = detailRequest.getDatasourceId();
        LeakCheckConfigSingleQueryParam singleQueryParam = new LeakCheckConfigSingleQueryParam();
        singleQueryParam.setBusinessActivityId(businessActivityId);
        singleQueryParam.setDatasourceId(datasourceId);
        LeakCheckConfigResult leakCheckConfigResult = checkConfigDAO.selectSingle(singleQueryParam);
        if (!Objects.isNull(leakCheckConfigResult)) {
            LeakSqlRefsResponse refsResponse = new LeakSqlRefsResponse();
            refsResponse.setBusinessActivityId(leakCheckConfigResult.getBusinessActivityId());
            refsResponse.setDatasourceId(leakCheckConfigResult.getDatasourceId());

            String sqlIdString = leakCheckConfigResult.getLeakcheckSqlIds();
            List<Long> sqlIdList = Arrays.stream(sqlIdString.split(",")).map(Long::parseLong).distinct().collect(
                    Collectors.toList());
            LeakCheckConfigDetailQueryParam detailQueryParam = new LeakCheckConfigDetailQueryParam();
            detailQueryParam.setIds(sqlIdList);
            LeakCheckConfigSingleDetailResult detailResult = checkConfigDetailDAO.selectSingle(detailQueryParam);
            if (!Objects.isNull(detailResult)) {
                refsResponse.setSqls(detailResult.getSqls());
            }
            return refsResponse;
        }
        return null;
    }

    @Override
    public List<LeakSqlBatchRefsResponse> getBatchLeakCheckConfig(LeakSqlBatchRefsRequest refsRequest) {
        List<LeakSqlBatchRefsResponse> refsResponseList = Lists.newArrayList();
        LeakCheckConfigQueryParam checkConfigQueryParam = new LeakCheckConfigQueryParam();
        checkConfigQueryParam.setBusinessActivityIds(refsRequest.getBusinessActivityIds());
        List<LeakCheckConfigResult> checkConfigResults = checkConfigDAO.selectList(checkConfigQueryParam);
        if (CollectionUtils.isNotEmpty(checkConfigResults)) {
            List<Long> datasourceIdList = checkConfigResults.stream().map(LeakCheckConfigResult::getDatasourceId)
                    .collect(Collectors.toList());
            List<Long> sqlIdToltalList = new ArrayList<>();
            for (LeakCheckConfigResult checkConfigResult : checkConfigResults) {
                String sqlIdString = checkConfigResult.getLeakcheckSqlIds();
                List<Long> sqlIdList = Arrays.stream(sqlIdString.split(",")).map(Long::parseLong).distinct().collect(
                        Collectors.toList());
                sqlIdToltalList.addAll(sqlIdList);
            }

            //查询数据源的基础信息
            DataSourceQueryParam dataSourceQueryParam = new DataSourceQueryParam();
            dataSourceQueryParam.setDataSourceIdList(datasourceIdList);
            List<DataSourceResult> dataSourceResultList = dataSourceDAO.selectList(dataSourceQueryParam);
            Map<Long, DataSourceResult> dataSourceResultMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(dataSourceResultList)) {
                dataSourceResultMap = dataSourceResultList.stream().collect(
                        Collectors.toMap(DataSourceResult::getId, dataSourceResult -> dataSourceResult));
            }

            //查询sql配置的信息
            LeakCheckConfigDetailQueryParam checkConfigDetailQueryParam = new LeakCheckConfigDetailQueryParam();
            checkConfigDetailQueryParam.setIds(sqlIdToltalList);
            List<LeakCheckConfigBatchDetailResult> sqlDetailResultList = checkConfigDetailDAO.selectList(
                    checkConfigDetailQueryParam);
            Map<Long, LeakCheckConfigBatchDetailResult> sqlDetailResultMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(sqlDetailResultList)) {
                sqlDetailResultMap = sqlDetailResultList.stream().collect(Collectors
                        .toMap(LeakCheckConfigBatchDetailResult::getId,
                                leakCheckConfigBatchDetailResult -> leakCheckConfigBatchDetailResult));
            }

            Map<Long, DataSourceResult> finalDataSourceResultMap = dataSourceResultMap;
            Map<Long, LeakCheckConfigBatchDetailResult> finalSqlDetailResultMap = sqlDetailResultMap;
            //同一压测场景，不同业务活动下相同数据源的sql需要合并
            Map<Long, LeakSqlBatchRefsResponse> refsResponseListGroupByDsMap = Maps.newHashMap();
            checkConfigResults.forEach(leakCheckConfigResult -> {
                LeakSqlBatchRefsResponse refsResponse;
                List<SingleSqlDetailResponse> sqlResponseList = Lists.newArrayList();
                Long datasourceId = leakCheckConfigResult.getDatasourceId();
                if (refsResponseListGroupByDsMap.containsKey(datasourceId)) {
                    refsResponse = refsResponseListGroupByDsMap.get(datasourceId);
                    sqlResponseList = refsResponse.getSqlResponseList();
                } else {
                    refsResponse = new LeakSqlBatchRefsResponse();
                    refsResponse.setDatasourceId(datasourceId);
                    if (finalDataSourceResultMap.containsKey(datasourceId)) {
                        DataSourceResult dataSourceResult = finalDataSourceResultMap.get(datasourceId);
                        refsResponse.setDatasourceName(dataSourceResult.getName());
                        refsResponse.setJdbcUrl(dataSourceResult.getJdbcUrl());
                    }
                }
                String sqlIdString = leakCheckConfigResult.getLeakcheckSqlIds();
                List<Long> sqlIdList = Arrays.stream(sqlIdString.split(",")).map(Long::parseLong).distinct().collect(
                        Collectors.toList());
                if (CollectionUtils.isNotEmpty(sqlIdList)) {
                    for (int i = 0; i < sqlIdList.size(); i++) {
                        Long sqlId = sqlIdList.get(i);
                        if (finalSqlDetailResultMap.containsKey(sqlId)) {
                            LeakCheckConfigBatchDetailResult detailResult = finalSqlDetailResultMap.get(sqlId);
                            SingleSqlDetailResponse sqlDetailResponse = new SingleSqlDetailResponse();
                            Integer order = sqlResponseList.size() + 1;
                            sqlDetailResponse.setOrder(order);
                            sqlDetailResponse.setSql(detailResult.getSql());
                            sqlResponseList.add(sqlDetailResponse);
                        }
                    }
                    //sql去重
                    sqlResponseList = sqlResponseList.stream().collect(
                            collectingAndThen(
                                    toCollection(() -> new TreeSet<>(Comparator.comparing(SingleSqlDetailResponse::getSql))),
                                    ArrayList::new)
                    );
                    List<SingleSqlDetailResponse> sorted = sqlResponseList.stream().sorted(
                            Comparator.comparing(SingleSqlDetailResponse::getOrder)).collect(Collectors.toList());
                    refsResponse.setSqlResponseList(sorted);
                }
                refsResponseListGroupByDsMap.put(datasourceId, refsResponse);
            });
            if (!refsResponseListGroupByDsMap.isEmpty()) {
                refsResponseList = refsResponseListGroupByDsMap.values().stream().collect(Collectors.toList());
            }
        }
        return refsResponseList;
    }

    @Override
    public List<VerifyTaskConfig> getVerifyTaskConfig(LeakSqlBatchRefsRequest refsRequest) {
        List<VerifyTaskConfig> verifyTaskConfigList = Lists.newArrayList();
        LeakCheckConfigQueryParam checkConfigQueryParam = new LeakCheckConfigQueryParam();
        checkConfigQueryParam.setBusinessActivityIds(refsRequest.getBusinessActivityIds());
        //查询基础配置
        List<LeakCheckConfigResult> checkConfigResults = checkConfigDAO.selectList(checkConfigQueryParam);
        if (CollectionUtils.isNotEmpty(checkConfigResults)) {
            List<Long> datasourceIdList = checkConfigResults.stream().map(LeakCheckConfigResult::getDatasourceId)
                    .collect(Collectors.toList());
            List<Long> sqlIdToltalList = new ArrayList<>();
            for (LeakCheckConfigResult checkConfigResult : checkConfigResults) {
                String sqlIdString = checkConfigResult.getLeakcheckSqlIds();
                List<Long> sqlIdList = Arrays.stream(sqlIdString.split(",")).map(Long::parseLong).distinct().collect(
                        Collectors.toList());
                sqlIdToltalList.addAll(sqlIdList);
            }

            //查询数据源的基础信息
            DataSourceQueryParam dataSourceQueryParam = new DataSourceQueryParam();
            dataSourceQueryParam.setDataSourceIdList(datasourceIdList);
            List<DataSourceResult> dataSourceResultList = dataSourceDAO.selectList(dataSourceQueryParam);
            Map<Long, DataSourceResult> dataSourceResultMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(dataSourceResultList)) {
                dataSourceResultMap = dataSourceResultList.stream().collect(
                        Collectors.toMap(DataSourceResult::getId, dataSourceResult -> dataSourceResult));
            }

            //查询sql配置详情
            LeakCheckConfigDetailQueryParam checkConfigDetailQueryParam = new LeakCheckConfigDetailQueryParam();
            checkConfigDetailQueryParam.setIds(sqlIdToltalList);
            List<LeakCheckConfigBatchDetailResult> sqlDetailResultList = checkConfigDetailDAO.selectList(
                    checkConfigDetailQueryParam);

            //组装漏数配置
            Map<Long, DataSourceResult> finalDataSourceResultMap = dataSourceResultMap;
            Map<Long, VerifyTaskConfig> verifyTaskConfigMap = Maps.newHashMap();
            checkConfigResults.forEach(leakCheckConfigResult -> {
                VerifyTaskConfig verifyTaskConfig = new VerifyTaskConfig();
                Long datasourceId = leakCheckConfigResult.getDatasourceId();
                verifyTaskConfig.setDatasourceId(datasourceId);
                if (finalDataSourceResultMap.containsKey(datasourceId)) {
                    DataSourceResult dataSourceResult = finalDataSourceResultMap.get(datasourceId);
                    verifyTaskConfig.setDatasourceName(dataSourceResult.getName());
                    verifyTaskConfig.setJdbcUrl(dataSourceResult.getJdbcUrl());
                    verifyTaskConfig.setUsername(dataSourceResult.getUsername());
                    verifyTaskConfig.setPassword(AESUtil.decrypt(dataSourceResult.getPassword()));
                    verifyTaskConfig.setType(dataSourceResult.getType());
                    verifyTaskConfig.setSqls(Lists.newArrayList());
                }
                if (CollectionUtils.isNotEmpty(sqlDetailResultList)) {
                    List<String> sqlList = sqlDetailResultList
                            .stream()
                            .filter(sqlDetail -> datasourceId.equals(sqlDetail.getDatasourceId()))
                            .map(LeakCheckConfigBatchDetailResult::getSql)
                            .distinct()
                            .collect(Collectors.toList());
                    verifyTaskConfig.setSqls(sqlList);
                }
                if (verifyTaskConfigMap.containsKey(verifyTaskConfig.getDatasourceId())) {
                    VerifyTaskConfig v = verifyTaskConfigMap.get(verifyTaskConfig.getDatasourceId());
                    List<String> sqlList = v.getSqls();
                    if (CollectionUtils.isNotEmpty(verifyTaskConfig.getSqls())) {
                        sqlList.addAll(verifyTaskConfig.getSqls());
                        sqlList = sqlList.stream().distinct().collect(Collectors.toList());
                        v.setSqls(sqlList);
                    }
                    verifyTaskConfigMap.put(verifyTaskConfig.getDatasourceId(), v);
                } else {
                    verifyTaskConfigMap.put(verifyTaskConfig.getDatasourceId(), verifyTaskConfig);
                }
            });
            if (!verifyTaskConfigMap.isEmpty()) {
                verifyTaskConfigList = new ArrayList<>(verifyTaskConfigMap.values());
            }
        }

        return verifyTaskConfigList;
    }

    @Override
    public Boolean getSceneLeakConfig(Long sceneId) {
        SceneManageIdReq req = new SceneManageIdReq();
        req.setId(sceneId);
        ResponseResult<SceneManageWrapperResp> sceneDetail = sceneManageApi.getSceneDetail(req);
        if (!Objects.isNull(sceneDetail.getData())) {
            SceneManageWrapperResp wrapperResp = JSON.parseObject(JSON.toJSONString(sceneDetail.getData()),
                    SceneManageWrapperResp.class);
            List<SceneManageWrapperResp.SceneBusinessActivityRefResp> refRespList = wrapperResp
                    .getBusinessActivityConfig();
            if (CollectionUtils.isNotEmpty(refRespList)) {
                List<Long> businessActivityIdList =
                        refRespList.stream().map(SceneManageWrapperResp.SceneBusinessActivityRefResp::getBusinessActivityId)
                                .collect(Collectors.toList());
                LeakCheckConfigQueryParam checkConfigQueryParam = new LeakCheckConfigQueryParam();
                checkConfigQueryParam.setBusinessActivityIds(businessActivityIdList);
                List<LeakCheckConfigResult> checkConfigResults = checkConfigDAO.selectList(checkConfigQueryParam);
                if (CollectionUtils.isNotEmpty(checkConfigResults)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public String testSqlConnection(SqlTestRequest sqlTestRequest) {
        StringBuilder msg = new StringBuilder();
        DataSourceSingleQueryParam queryParam = new DataSourceSingleQueryParam();
        queryParam.setId(sqlTestRequest.getDatasourceId());
        DataSourceResult dataSourceResult = dataSourceDAO.selectSingle(queryParam);
        String jdbcUrl = dataSourceResult.getJdbcUrl();
        String username = dataSourceResult.getUsername();
        String password = AESUtil.decrypt(dataSourceResult.getPassword());
        try {
            Connection connection = JdbcConnection.generateConnection(jdbcUrl, username, password);
            List<String> sqls = sqlTestRequest.getSqls();
            try {
                sqls.forEach(sql -> {
                    try {
                        if (StringUtils.isNotBlank(sql)) {
                            if (sql.lastIndexOf(";") != -1) {
                                sql = sql.split(";")[0];
                            }
                            this.checkSql(sql);
                            Statement statement = connection.createStatement();
                            statement.setQueryTimeout(30);
                            ResultSet resultSet = statement.executeQuery(sql);
                            resultSet.close();
                            statement.close();
                        }
                    } catch (SQLException throwables) {
                        String tmpMsg = throwables.getMessage();
                        String tableName = SqlParseUtil.parseAndGetTableName(sql);
                        tmpMsg = tmpMsg + ":" + tableName;
                        msg.append(tmpMsg).append(",");
                        log.error("Verify job run error:", throwables);
                    }
                });
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        log.error("error:", e);
                    }
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            msg.append(e.getMessage());
            log.error("testSqlConnection error:", e);
        }
        return msg.toString();
    }

    private void checkSql(String sql) {
        if (StringUtils.isBlank(sql)) {
            return;
        }
        if (!SqlParseUtil.checkIsSelect(sql) || !SqlParseUtil.checkHasLimit(sql)) {
            throw new TakinWebException(ExceptionCode.LEAK_SQL_REF_INVALID_ERROR, "验证命令格式不符合要求，请按照模板填写");
        }
    }
}
