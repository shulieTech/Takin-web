package io.shulie.takin.web.data.param.scriptmanage;

import java.util.List;

import io.shulie.takin.web.common.pojo.dto.PageBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分页查询调试记录入参
 *
 * @author liuchuan
 * @date 2021/5/12 9:40 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PageScriptDebugParam extends PageBaseDTO {

    /**
     * 脚本发布id
     */
    private Long scriptDeployId;

    /**
     * 状态列表
     */
    private List<Integer> statusList;

}
