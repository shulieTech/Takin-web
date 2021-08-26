package io.shulie.takin.web.biz.pojo.request.application;

import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * @author fanxx
 * @date 2020/10/19 9:46 下午
 */
@Data
public class ApplicationNodeDashBoardQueryRequest {

    @NotNull
    private Long applicationId;

}
