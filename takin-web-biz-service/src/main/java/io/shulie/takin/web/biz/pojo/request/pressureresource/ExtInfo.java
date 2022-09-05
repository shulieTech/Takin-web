package io.shulie.takin.web.biz.pojo.request.pressureresource;


import lombok.Data;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/1 10:10 AM
 */
@Data
public class ExtInfo {
    /**
     * 驱动名称
     */
    private String driver;

    private int maxActive;

    private int initalSize;

    private String schema;
}
