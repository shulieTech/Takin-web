package io.shulie.takin.sre.common.result;

import lombok.Data;

import java.util.List;

/**
 * @author zhangz
 * Created on 2023/11/29 17:27
 * Email: zz052831@163.com
 */
@Data
public class SrePageData<T> {
    private List<T> records;
    private String total;
    private String size;
    private String current;
    private List<Object> orders;
    private boolean optimizeCountSql;
    private boolean searchCount;
    private String countId;
    private Object maxLimit;
    private String pages;
}
