package io.shulie.takin.web.data.common;

import java.util.List;
import java.util.Collection;

import org.slf4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.slf4j.LoggerFactory;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.QueryResult;
import org.springframework.stereotype.Service;
import org.influxdb.impl.InfluxDBResultMapper;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author xingchen
 * @date 2020/7/2717:03
 */
@Service
public class InfluxDatabaseManager implements AutoCloseable {
    private static final ThreadLocal<InfluxDB> CACHE = new ThreadLocal<>();
    private static final InfluxDBResultMapper RESULT_MAPPER = new InfluxDBResultMapper();
    private static final Logger logger = LoggerFactory.getLogger(InfluxDatabaseManager.class);
    @Value("${spring.influxdb.url}")
    private String influxdbUrl;
    @Value("${spring.influxdb.user}")
    private String userName;
    @Value("${spring.influxdb.password}")
    private String password;
    @Value("${spring.influxdb.database}")
    private String database;

    private InfluxDB createInfluxDatabase() {
        try {
            return InfluxDBFactory.connect(influxdbUrl, userName, password);
        } catch (Throwable e) {
            logger.error("influxdb init fail " + ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    private InfluxDB getInfluxDatabase() {
        InfluxDB influxDatabase = CACHE.get();
        if (influxDatabase == null) {
            influxDatabase = createInfluxDatabase();
            CACHE.set(influxDatabase);
        }
        return influxDatabase;
    }

    public <T> Collection<T> query(Class<T> clazz, String command) {
        return query(clazz, new Query(command, database));
    }

    public <T> Collection<T> query(Class<T> clazz, String command, String database) {
        return query(clazz, new Query(command, database));
    }

    public List<QueryResult.Result> query(String command) {
        return parseRecords(command);
    }

    public <T> Collection<T> query(Class<T> clazz, Query query) {
        InfluxDB influxDatabase = getInfluxDatabase();
        if (influxDatabase != null) {
            QueryResult queryResult = influxDatabase.query(query);
            if (queryResult != null) {
                return RESULT_MAPPER.toPOJO(queryResult, clazz);
            }
        }
        return null;
    }

    @Override
    public void close() {
        InfluxDB influxDatabase = CACHE.get();
        if (influxDatabase != null) {
            influxDatabase.close();
            CACHE.remove();
        }
    }

    private List<QueryResult.Result> parseRecords(String command) {
        QueryResult queryResult = getInfluxDatabase().query(new Query(command, database));
        List<QueryResult.Result> results = queryResult.getResults();
        if (results == null || results.size() == 0) {
            return null;
        }
        return results;
    }
}
