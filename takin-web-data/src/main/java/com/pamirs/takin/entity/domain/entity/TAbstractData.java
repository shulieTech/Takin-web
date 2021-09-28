package com.pamirs.takin.entity.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pamirs.takin.common.util.LongToStringFormatSerialize;

/**
 * 说明: 抽数实体类
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/9/4 14:30
 */
@JsonIgnoreProperties(value = {"handler"})
public class TAbstractData extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 抽数表id
     */
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private long tadId;

    /**
     * 　数据库表配置id
     */
    @JsonSerialize(using = LongToStringFormatSerialize.class)
    private long tdcId;

    /**
     * 数据表名
     */
    private String tableName;

    /**
     * 数据待插入的表
     */
    private String objTableName;

    /**
     * 建表语句
     */
    private String sqlDdl;

    /**
     * sql类型: 0表示sql类型为纯sql输入,1表示为文本类型
     */
    private String sqlType;

    /**
     * 取数逻辑sql(存储文件路径或者sql语句)
     */
    private String abstractSql;

    /**
     * 优化后sql
     */
    private String newSql;

    /**
     * 处理数据逻辑sql(存储文件路径或者sql语句)
     */
    private String dealSql;

    /**
     * 负责人工号
     */
    private String principalNo;

    /**
     * 数据状态是否删除
     */
    private String dbStatus;

    /**
     * 是否可用
     */
    private String useYn;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    /**
     * 用户id
     */
    private long userId;

    public long getTenantId() {
        return tenantId;
    }

    public void setTenantId(long tenantId) {
        this.tenantId = tenantId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getEnvCode() {
        return envCode;
    }

    public void setEnvCode(String envCode) {
        this.envCode = envCode;
    }

    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;

    public String getNewSql() {
        return newSql;
    }

    public void setNewSql(String newSql) {
        this.newSql = newSql;
    }

    public String getSqlDdl() {
        return sqlDdl;
    }

    public void setSqlDdl(String sqlDdl) {
        this.sqlDdl = sqlDdl;
    }

    public long getTadId() {
        return tadId;
    }

    public void setTadId(long tadId) {
        this.tadId = tadId;
    }

    public long getTdcId() {
        return tdcId;
    }

    public void setTdcId(long tdcId) {
        this.tdcId = tdcId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAbstractSql() {
        return abstractSql;
    }

    public void setAbstractSql(String abstractSql) {
        this.abstractSql = abstractSql;
    }

    public String getDealSql() {
        return dealSql;
    }

    public void setDealSql(String dealSql) {
        this.dealSql = dealSql;
    }

    public String getPrincipalNo() {
        return principalNo;
    }

    public void setPrincipalNo(String principalNo) {
        this.principalNo = principalNo;
    }

    public String getSqlType() {
        return sqlType;
    }

    public void setSqlType(String sqlType) {
        this.sqlType = sqlType;
    }

    public String getDbStatus() {
        return dbStatus;
    }

    public void setDbStatus(String dbStatus) {
        this.dbStatus = dbStatus;
    }

    public String getUseYn() {
        return useYn;
    }

    public void setUseYn(String useYn) {
        this.useYn = useYn;
    }

    public String getObjTableName() {
        return objTableName;
    }

    public void setObjTableName(String objTableName) {
        this.objTableName = objTableName;
    }

    @Override
    public String toString() {
        return "TAbstractData{" +
            "tadId=" + tadId +
            ", tdcId=" + tdcId +
            ", tableName='" + tableName + '\'' +
            ", objTableName='" + objTableName + '\'' +
            ", sqlDdl='" + sqlDdl + '\'' +
            ", sqlType='" + sqlType + '\'' +
            ", abstractSql='" + abstractSql + '\'' +
            ", newSql='" + newSql + '\'' +
            ", dealSql='" + dealSql + '\'' +
            ", principalNo='" + principalNo + '\'' +
            ", dbStatus='" + dbStatus + '\'' +
            ", useYn='" + useYn + '\'' +
            '}';
    }
}
