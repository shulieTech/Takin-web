package io.shulie.takin.web.amdb.bean.result.application;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: ει£
 * @Date: 2021/9/16 10:02 δΈε
 */
@Data
public class ApplicationBizTableDTO implements Serializable {


    private Long id;

    private String appName;

    private String bizDatabase;

    private String tableName;

    private String tableUser;


}
