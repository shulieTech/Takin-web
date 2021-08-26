package com.pamirs.takin.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import com.pamirs.takin.common.constant.DataSourceVerifyTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fanxx
 * @date 2020/12/31 9:52 上午
 */
public class JdbcConnection {
    private static final Logger logger = LoggerFactory.getLogger(JdbcConnection.class);

    public static Long fetchCurrentTime(String url, String username, String password, DataSourceVerifyTypeEnum dbType) {
        Connection conn = null;
        String noPwUrl = url.trim().startsWith("jdbc") ? url.split("\\?")[0] : url;
        if (Objects.isNull(dbType)) {
            throw new RuntimeException("不支持的检查数据源[" + noPwUrl + "][" + dbType + "]");
        }
        try {
            switch (dbType) {
                case MYSQL:
                    conn = generateConnection(url, username, password);
                    Statement statement = conn.createStatement();
                    statement.setQueryTimeout(30);
                    try (ResultSet resultSet = statement.executeQuery("SELECT now()")) {
                        Long currentTime = resultSet.next() ? resultSet.getTimestamp(1).getTime() : null;
                        resultSet.close();
                        statement.close();
                        return currentTime;
                    }
                default:
                    throw new RuntimeException("不支持的检查数据源[" + noPwUrl + "][" + dbType + "]");
            }
        } catch (Exception ex1) {
            throw new RuntimeException("连接数据库[" + noPwUrl + "]失败，" + ex1.getMessage(), ex1);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    logger.error("error:", e);
                }
            }
        }
    }

    public static String findClassNameForCheckDb(String jdbcUrl) {
        String trim = jdbcUrl.trim();
        if (trim.startsWith("jdbc:mysql")) {
            return "com.mysql.jdbc.Driver";
        } else if (trim.startsWith("jdbc:oracle")) {
            return "oracle.jdbc.OracleDriver";
        } else if (trim.startsWith("jdbc:sqlserver")) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        } else if (trim.startsWith("jdbc:postgresql")) {
            return "org.postgresql.Driver";
        } else {
            return "";
        }
    }

    public static Connection generateConnection(String jdbcUrl, String username, String password) throws ClassNotFoundException, SQLException {
        if (!jdbcUrl.contains("mysql")) {
            throw new RuntimeException("不支持的 JDBC:" + jdbcUrl);
        }
        JdbcConnection.class.getClassLoader().loadClass(findClassNameForCheckDb(jdbcUrl));
        DriverManager.setLoginTimeout(20);
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    public static void main(String[] args) {
        System.out.println(fetchCurrentTime("jdbc:mysql://114.55.42.181:3306/taco_app", "canal1", "canal", DataSourceVerifyTypeEnum.MYSQL));
    }
}
