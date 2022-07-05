package io.shulie.takin.web.amdb.bean.query.trace;

import lombok.Data;

/**
 * @author 无涯
 * @date 2021/6/10 12:14 下午
 */
@Data
public class EntranceRuleDTO {
    private String appName;
    private Integer businessType;
    private String entrance;

    public EntranceRuleDTO() {
    }

    public EntranceRuleDTO(String entrance) {
        this.entrance = entrance;
    }
}
