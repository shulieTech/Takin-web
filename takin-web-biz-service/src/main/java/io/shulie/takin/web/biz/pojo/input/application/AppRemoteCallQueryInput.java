package io.shulie.takin.web.biz.pojo.input.application;

import java.util.List;

import io.shulie.takin.web.ext.entity.UserCommonExt;
import lombok.Data;

/**
 * @author 无涯
 * @date 2021/5/29 12:18 上午
 */
@Data
public class AppRemoteCallQueryInput extends UserCommonExt {
    private Integer type;
    private String interfaceName;
    private Integer status;
    private Long applicationId;
    private Long customerId;
    private List<String> appNames;
    private Boolean isAll = false;
}
