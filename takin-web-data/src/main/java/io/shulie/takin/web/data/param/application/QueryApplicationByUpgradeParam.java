package io.shulie.takin.web.data.param.application;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author liuchuan
 * @date 2021/12/8 3:04 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryApplicationByUpgradeParam extends PageBaseDTO {

    /**
     * 应用名称
     */
    private String applicationName;



    private List<Long> appIds;

    private List<Long> userIds;

    private Long tenantId;
    private String envCode;

}
