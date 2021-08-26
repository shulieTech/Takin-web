package io.shulie.takin.web.biz.pojo.response.leakcheck;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/13 10:08 上午
 */
@Data
@ApiModel("sql测试返回的对象")
public class SqlTestResponse {
    private Boolean passed;

    private String errorMessage;
}
