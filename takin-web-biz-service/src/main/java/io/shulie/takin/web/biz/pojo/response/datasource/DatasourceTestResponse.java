package io.shulie.takin.web.biz.pojo.response.datasource;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-12-28
 */
@Data
@ApiModel("数据源测试返回的对象")
public class DatasourceTestResponse {

    private Boolean passed;

    private String errorMessage;

}
