package io.shulie.takin.cloud.common.influxdb;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-20 14:25
 */
@Slf4j
@Component
public class InfluxWriter {

    /**
     * 连接地址
     */
    @Value("${cloud.influxdb.url:}")
    private String influxdbUrl;

    /**
     * 用户名
     */
    @Value("${cloud.influxdb.user:}")
    private String userName;

    /**
     * 密码
     */
    @Value("${cloud.influxdb.password:}")
    private String password;

    /**
     * 数据库库名
     */
    @Value("${cloud.influxdb.database:}")
    private String database;

    private InfluxDB influx;

    @PostConstruct
    public void init() {
        if (StringUtils.isBlank(influxdbUrl)) {
            return;
        }
        influx = InfluxDBFactory.connect(influxdbUrl, userName, password);
        influx.enableBatch(1000, 40, TimeUnit.MILLISECONDS);
    }

    /**
     * 查询数据
     *
     * @return -
     */
    public List<QueryResult.Result> select(String command) {
        QueryResult queryResult = influx.query(new Query(command, database));
        return queryResult.getResults();
    }

    /**
     * 封装查询结果
     *
     * @param command 命令
     * @param clazz   结果集合的泛型类
     * @param <T>     泛型
     * @return -
     */
    public <T> List<T> query(String command, Class<T> clazz) {
        List<QueryResult.Result> results = select(command);

        JSONArray resultArr = new JSONArray();
        for (QueryResult.Result result : results) {
            List<QueryResult.Series> series = result.getSeries();
            if (series == null) {
                continue;
            }
            for (QueryResult.Series serie : series) {
                List<List<Object>> values = serie.getValues();
                List<String> colums = serie.getColumns();
                Map<String, String> tags = serie.getTags();

                // 封装查询结果
                for (List<Object> value : values) {
                    JSONObject jsonData = new JSONObject();
                    if (tags != null && tags.keySet().size() > 0) {
                        jsonData.putAll(tags);
                    }
                    for (int j = 0; j < colums.size(); ++j) {
                        jsonData.put(colums.get(j), value.get(j));
                    }
                    resultArr.add(jsonData);
                }
            }
        }
        return JSONObject.parseArray(resultArr.toJSONString(), clazz);
    }

    public <T> T querySingle(String command, Class<T> clazz) {
        List<T> data = query(command, clazz);
        if (CollectionUtils.isNotEmpty(data)) {
            return data.get(0);
        }
        return null;
    }

    /**
     * 插入数据
     *
     * @param measurement 表名
     * @param tags        标签
     * @param fields      字段
     * @param time        时间
     * @return -
     */
    public boolean insert(String measurement, Map<String, String> tags, Map<String, Object> fields, long time) {
        Point.Builder builder = Point.measurement(measurement);
        builder.tag(tags);
        builder.fields(fields);
        if (time > 0) {
            builder.time(time, TimeUnit.MILLISECONDS);
        }
        return insert(builder.build());
    }

    /**
     * 插入数据
     */
    public boolean insert(Point point) {
        try {
            influx.write(database, "", point);
        } catch (Exception ex) {
            log.error("异常代码【{}】,异常内容：influxdb写数据异常 --> 异常信息: {}",
                TakinCloudExceptionEnum.TASK_RUNNING_RECEIVE_PT_DATA_ERROR, ex);
            return false;
        }
        return true;
    }

    @Async
    public void truncateMeasurement(String measurement) {
        if (StringUtils.isBlank(measurement)) {
            return;
        }
        influx.query(new Query(String.format("truncate %s", measurement), database));
    }
}
