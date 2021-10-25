package io.shulie.takin.web.amdb.bean.result.application;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 南风
 * @Date: 2021/9/16 10:02 上午
 */
@Data
public class ApplicationBizTableDTO implements Serializable {


    private Long id;

    private String appName;

    private String bizDatabase;

    private String tableName;

    private String tableUser;


}
