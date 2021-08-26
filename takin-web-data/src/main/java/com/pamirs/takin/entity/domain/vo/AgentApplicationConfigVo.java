package com.pamirs.takin.entity.domain.vo;

/**
 * agent获取基础配置
 *
 * @author 298403
 * @date 2019-03-28
 */
public class AgentApplicationConfigVo {

    /**
     * sql检查异常 Y N
     */
    private String sqlCheck;

    /**
     * 防作弊检查异常 Y N
     */
    private String cheatCheck;

    public String getSqlCheck() {
        return sqlCheck;
    }

    public void setSqlCheck(String sqlCheck) {
        this.sqlCheck = sqlCheck;
    }

    public String getCheatCheck() {
        return cheatCheck;
    }

    public void setCheatCheck(String cheatCheck) {
        this.cheatCheck = cheatCheck;
    }

    @Override
    public String toString() {
        return "AgentApplicationConfigVo{" +
            "sqlCheck='" + sqlCheck + '\'' +
            ", cheatCheck='" + cheatCheck + '\'' +
            '}';
    }
}
