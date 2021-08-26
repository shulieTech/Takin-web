package io.shulie.takin.web.biz.pojo.request.leakverify;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/5 3:17 下午
 */
@Data
public class VerifyTaskConfig {
    private Long datasourceId;
    private String datasourceName;
    private String jdbcUrl;
    private String username;
    private String password;
    private Integer type;
    private List<String> sqls;
}
