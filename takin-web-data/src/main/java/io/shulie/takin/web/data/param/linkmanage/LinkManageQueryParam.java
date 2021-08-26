package io.shulie.takin.web.data.param.linkmanage;

import java.util.List;

import io.shulie.takin.web.ext.entity.AuthQueryParamCommonExt;
import lombok.Data;

/**
 * @author fanxx
 * @date 2021/1/15 10:29 上午
 */
@Data
public class LinkManageQueryParam extends AuthQueryParamCommonExt {
    /**
     * 系统流程id
     */
    private List<Long> linkIdList;
    /**
     * 系统流程
     */
    private String systemProcessName;
}
