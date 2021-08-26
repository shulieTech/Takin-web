package io.shulie.takin.web.data.param.application;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/11/27 5:34 下午
 */
@Data
public class ApplicationDsUpdateParam {
    private Long id;
    private String url;
    private Integer status;
    private String config;
    private String parseConfig;
}
