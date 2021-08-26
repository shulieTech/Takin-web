package io.shulie.takin.web.biz.pojo.input.whitelist;

import java.util.List;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/4/12 5:58 下午
 */
@Data
public class WhitelistUpdatePartAppNameInput {

    private List<String> effectiveAppName;

    private Long wlistId;
}
