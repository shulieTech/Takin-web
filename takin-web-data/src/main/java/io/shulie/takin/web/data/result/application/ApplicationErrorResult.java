package io.shulie.takin.web.data.result.application;

import java.util.List;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/16 10:56 上午
 */
@Data
public class ApplicationErrorResult {

    private String exceptionId;
    private List<String> agentId;
    private String description;
    private String createTime;

}
