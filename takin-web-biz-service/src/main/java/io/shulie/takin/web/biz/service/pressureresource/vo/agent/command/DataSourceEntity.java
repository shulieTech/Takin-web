package io.shulie.takin.web.biz.service.pressureresource.vo.agent.command;

import java.io.Serializable;

public class DataSourceEntity implements Serializable {

    private String url;
    private String userName;
    private String password;
    private String driverClassName;
    private String connectionPool;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getConnectionPool() {
        return connectionPool;
    }

    public void setConnectionPool(String connectionPool) {
        this.connectionPool = connectionPool;
    }
}
