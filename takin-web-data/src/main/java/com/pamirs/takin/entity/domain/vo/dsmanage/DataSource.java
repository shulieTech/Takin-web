package com.pamirs.takin.entity.domain.vo.dsmanage;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/3/12 下午3:49
 */
@Data
public class DataSource {
    private String id;
    private String driverClassName;
    private String url;
    private String username;
    private String schema;
    private String password;
    private String initialSize;
    private String minIdle;
    private String maxActive;
    private String maxWait;
    private String timeBetweenEvictionRunsMillis;
    private String minEvictableIdleTimeMillis;
    private String validationQuery;
    private String testWhileIdle;
    private String testOnBorrow;
    private String testOnReturn;
    private String poolPreparedStatements;
    private String maxPoolPreparedStatementPerConnectionSize;
    private String connectionProperties;

    private Map<String,String> extra;

    public DataSource() {
        extra = new HashMap<>();
    }
}
