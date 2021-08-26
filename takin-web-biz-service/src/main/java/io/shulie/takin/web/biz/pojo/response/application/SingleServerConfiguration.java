package io.shulie.takin.web.biz.pojo.response.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/12/1 2:00 下午
 */
@Data
public class SingleServerConfiguration {
    private String master;
    private String nodes;
    private String password;
    private String database;
}
