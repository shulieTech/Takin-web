package com.pamirs.takin.common.constant;

/**
 * 说明：数据源枚举类
 *
 * @author shulie
 * @version v1.0
 * @date Create in 2018/8/31 15:56
 */
public enum DataSourceTypeEnum {

    MYSQL("1"),

    ORACLE("2"),

    HIVE("3"),

    HBASE("4"),

    REDIS("5"),

    SQLSERVER("6");

    private String index;

    DataSourceTypeEnum(String index) {
        this.index = index;
    }

    /**
     * 说明：判断枚举值是否在定义范围内
     *
     * @param dataSourceEnum
     * @return
     * @author shulie
     * @time：2017年12月26日 上午10:16:18
     */
    public static boolean checkDataSourceEnum(DataSourceTypeEnum dataSourceEnum) {
        for (DataSourceTypeEnum dataSource : DataSourceTypeEnum.values()) {
            if (dataSource.equals(dataSourceEnum)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }

    /**
     * Gets the value of index.
     *
     * @return the value of index
     * @author shulie
     * @version 1.0
     */
    public String getIndex() {
        return index;
    }

    /**
     * Sets the index.
     *
     * <p>You can use getIndex() to get the value of index</p>
     *
     * @param index index
     * @author shulie
     * @version 1.0
     */
    public void setIndex(String index) {
        this.index = index;
    }
}
