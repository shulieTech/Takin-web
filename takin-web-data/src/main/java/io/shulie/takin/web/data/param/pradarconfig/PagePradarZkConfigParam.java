package io.shulie.takin.web.data.param.pradarconfig;

import java.util.List;

import lombok.Data;

/**
 * @author liuchuan
 * @date 2021/11/15 5:10 下午
 */
@Data
public class PagePradarZkConfigParam {

    /**
     * 租户ids
     */
    private List<Long> tenantIds;

    /**
     * 环境列表
     */
    private List<String> envCodeList;

}
