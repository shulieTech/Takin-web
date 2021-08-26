package io.shulie.takin.web.data.param.application;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用中间件(ApplicationMiddleware) 分页列表入参类
 *
 * @author liuchuan
 * @date 2021-06-30 16:09:37
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageApplicationMiddlewareParam extends PageBaseDTO {

    /**
     * 应用id
     */
    private Long applicationId;

    /**
     * 搜索关键字, artifactId 或者 groupId
     */
    private String keywords;

    /**
     * 状态, 3 已支持, 2 未支持, 4 无需支持, 1 未知, 0 无
     */
    private Integer status;

}
