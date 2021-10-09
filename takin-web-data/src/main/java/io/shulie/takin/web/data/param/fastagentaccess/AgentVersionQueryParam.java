package io.shulie.takin.web.data.param.fastagentaccess;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/12 4:08 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AgentVersionQueryParam extends PageBaseDTO {

    private String version;

    private String firstVersion;
}
